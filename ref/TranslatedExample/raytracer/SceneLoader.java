package raytracer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import objects.Material;
import objects.SceneObject;
import objects.Sphere;
import objects.TriangleSet;



import scene.PointLight;
import scene.Scene;
import scene.Transformation;

/**
 * Given the name of the x3d file, this class parses the file and generates the scene.  To use it,
 * invoke the method <code>loadScene</code> with the x3d file name.  This returns the built scene to be passed to the ray tracer.
 */
public class SceneLoader {

	/**
	 * Loads the x3d file and constructs the object <code>Scene</code> so it can be rendered by the ray tracer
	 * 
	 * @param fileName File name to parse 
	 * @return The constructed scene
	 * @throws IOException If there is any problem parsing the file
	 */
	public Scene loadScene(String fileName) throws IOException {
		X3DLoader loader = new X3DLoader(X3DLoader.LOAD_ALL);
		try {
			loader.load(fileName);
		} catch (Exception e) {
			throw new IOException(e.getMessage()); // , e);
		}
		VRMLNode node = loader.getVRMLScene().getRootNode();
		Scene scene = new Scene();
		parseScene(node, scene);
		return scene;
	}

	/**
	 * Given the root node of the x3d and an empty scene, navigate through the nodes and construct and add
	 * all of the objects necessary for the scene. n
	 * 
	 * @param node Root node of the x3d
	 * @param scene Scene with the aggregated nodes
	 */
	private void parseScene(VRMLNode node, Scene scene) {
		switch (node.getPrimaryType()) {
		case TypeConstants.ViewpointNodeType:
			parseViewpointNode((J3DViewpoint) node, scene);
			break;
		case TypeConstants.LightNodeType:
			parseLightNode((J3DLightNode) node, scene);
			break;
		case TypeConstants.GroupingNodeType:
			if (node instanceof J3DTransform) {
				parseTransformNode((J3DTransform) node, scene);
			}
			break;
		case TypeConstants.WorldRootNodeType:
			for (VRMLNode v : ((J3DWorldRoot) node).getChildren()) {
				parseScene(v, scene);
			}
		}
	}

	/**
	 * Given a node viewpoint, construct the corresponding object <code>Camera</code>
	 * and add it to the scene.  Set the position, orientation, and rotation.
	 * 
	 * @param viewpoint Viewpoint node that contains the camera 
	 * @param scene Scene with the camera
	 */
	private void parseViewpointNode(J3DViewpoint viewpoint, Scene scene) {
		VRMLFieldData data;
		Vector3d position = new Vector3d(0, 0, 0);
		AxisAngle4d orientation = new AxisAngle4d(0, 0, 1, 0);
		if ((data = getField(viewpoint, "position")) != null) {
			position = new Vector3d(new Vector3f(data.floatArrayValue));
		}
		if ((data = getField(viewpoint, "orientation")) != null) {
			orientation = new AxisAngle4d(new AxisAngle4f(data.floatArrayValue));
		}
		scene.setCamera(new Camera(position, orientation, viewpoint
				.getFieldOfView()));
	}

	/**
	 * Process the transform node.  Take the scale, rotation, and translation and 
	 * propagate it through the objects to be defined within it, through the object <code>Transform</code>.
	 * 
	 * @param transformNode Node type transformed from x3d 
	 * @param scene Scene to be constructed
	 */
	private Set<Object> parseTransformNode(J3DTransform transformNode, Scene scene) {
		Set<Object> children = new HashSet<Object>();
		
		Transformation transform = new Transformation();
		transform.rotation = new AxisAngle4d(new AxisAngle4f(transformNode
				.getRotation()));
		transform.translation = new Vector3d(new Vector3f(transformNode
				.getTranslation()));
		transform.scale = new Vector3d(new Vector3f(transformNode.getScale()));
		for (VRMLNode node : transformNode.getChildren()) {
			if (node instanceof J3DShape) {
				J3DShape shapeNode = (J3DShape) node;
				SceneObject object = parseShapeNode(shapeNode);
				if (object != null) {
					object.transform(transform);
					scene.addSceneObject(object);
					children.add(object);
				} else {
					System.err.println("Unsupported shape geometry: "
							+ shapeNode.getGeometry().getVRMLNodeName());
				}
			} else if (node instanceof J3DViewpoint) {
				parseViewpointNode((J3DViewpoint)node, scene);
				scene.getCamera().transform(transform);
				children.add(scene.getCamera());
			} else if (node instanceof J3DTransform) {
				Set<Object> group = parseTransformNode((J3DTransform)node, scene);
				for (Object obj : group) {
					if (obj instanceof SceneObject) {
						((SceneObject)obj).transform(transform);
					} else if (obj instanceof Camera) {
						((Camera)obj).transform(transform);
					}
				}
				children.addAll(group);
			}
		}
		
		return children;
	}

	/**
	 * Process a shape node. Calculate the position and dimensions of the received object to be transformed.  
	 * (contains the accumulated transformations up to the moment for the shape being processed).
	 * 
	 * @param shapeNode Node type shape to process
	 * @param transform Transformations accumulated thus far
	 */
	private SceneObject parseShapeNode(J3DShape shapeNode) {
		SceneObject shape = null;
		VRMLNode geometry = shapeNode.getGeometry();

		if (geometry instanceof J3DSphere) {
			shape = parseSphere((J3DSphere) geometry);
		} else if (geometry instanceof J3DTriangleSet) {
			shape = parseTriangleSet((J3DTriangleSet) geometry);
		} else if (geometry instanceof J3DIndexedTriangleFanSet) {
			shape = parseTriangleSet((J3DIndexedTriangleFanSet) geometry);
		} else if (geometry instanceof J3DIndexedTriangleSet) {
			shape = parseTriangleSet((J3DIndexedTriangleSet) geometry);
		} else if (geometry instanceof J3DIndexedTriangleStripSet) {
			shape = parseTriangleSet((J3DIndexedTriangleStripSet) geometry);
		}

		// Figura no reconocida
		if (shape == null) {
			return null;
		}

		J3DAppearance appearance = (J3DAppearance) shapeNode.getAppearance();
		if (appearance != null) {
			J3DMaterial material = (J3DMaterial) appearance.getMaterial();
			if (material != null) {
				shape.getMaterial().set(parseMaterial(material));
			}
		}
		return shape;
	}

	private SceneObject parseSphere(J3DSphere sphereNode) {
		VRMLFieldData data;
		Sphere sphere = new Sphere();
		data = getField(sphereNode, "radius");
		if (data != null) {
			sphere.radius = data.floatValue;
		}
		return sphere;
	}

	private SceneObject parseTriangleSet(J3DTriangleSet triangleSetNode) {
		TriangleSet triangleSet = new TriangleSet();
		for (VRMLNodeType node : triangleSetNode.getComponents()) {
			if (node instanceof J3DCoordinate) {
				J3DCoordinate coordinate = (J3DCoordinate) node;
				float[] points = coordinate.getPointRef();
				for (int i = 0; i < points.length; i += 9) {
					Vector3d p1 = new Vector3d(points[i], points[i + 1],
							points[i + 2]);
					Vector3d p2 = new Vector3d(points[i + 3], points[i + 4],
							points[i + 5]);
					Vector3d p3 = new Vector3d(points[i + 6], points[i + 7],
							points[i + 8]);
					triangleSet.addTriangle(p1, p2, p3);
				}
			}
		}
		return triangleSet;
	}

	private Vector3d getIndexedPoint(float[] points, int i)
			throws IndexOutOfBoundsException {
		int j = 3 * i;
		if (!(j >= 0 && j + 2 < points.length)) {
			throw new IndexOutOfBoundsException("No triangle coords at index "
					+ j);
		}
		return new Vector3d(points[j], points[j + 1], points[j + 2]);
	}

	private SceneObject parseTriangleSet(J3DIndexedTriangleSet setNode) {
		TriangleSet triangleSet = new TriangleSet();
		VRMLNode[] components = setNode.getComponents();

		// Coordenadas
		J3DCoordinate coordinates = (J3DCoordinate) components[0];
		float[] points = new float[coordinates.getNumPoints()];
		coordinates.getPoint(points);

		// Puntos
		int[] indexes = setNode.getFieldValue(setNode.getFieldIndex("index")).intArrayValue;
		for (int i = 0; i < indexes.length; i += 3) {
			try {
				Vector3d p1 = getIndexedPoint(points, indexes[i]);
				Vector3d p2 = getIndexedPoint(points, indexes[i + 1]);
				Vector3d p3 = getIndexedPoint(points, indexes[i + 2]);
				triangleSet.addTriangle(p1, p2, p3);
				//triangleSet.addTriangle(p1, p3, p2);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		return triangleSet;
	}

	private SceneObject parseTriangleSet(J3DIndexedTriangleFanSet setNode) {
		TriangleSet triangleSet = new TriangleSet();
		VRMLNode[] components = setNode.getComponents();

		// Coordinates
		J3DCoordinate coordinates = (J3DCoordinate) components[0];
		float[] points = new float[coordinates.getNumPoints()];
		coordinates.getPoint(points);

		// Points
		int[] indexes = setNode.getFieldValue(setNode.getFieldIndex("index")).intArrayValue;
		Vector3d p1 = getIndexedPoint(points, indexes[0]);
		Vector3d p2 = getIndexedPoint(points, indexes[1]);
		for (int i = 2; i < indexes.length; i++) {
			try {
				if (indexes[i] == -1) {
					if (i + 2 < indexes.length) {
						p1 = getIndexedPoint(points, indexes[i + 1]);
						p2 = getIndexedPoint(points, indexes[i + 2]);
						i += 2;
					}
				} else {
					Vector3d p3 = getIndexedPoint(points, indexes[i]);
					triangleSet.addTriangle(p1, p2, p3);
					//triangleSet.addTriangle(p1, p3, p2);
					p2 = p3;
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		return triangleSet;
	}

	private SceneObject parseTriangleSet(J3DIndexedTriangleStripSet setNode) {
		TriangleSet triangleSet = new TriangleSet();
		VRMLNode[] components = setNode.getComponents();

		// Coordinates
		J3DCoordinate coordinates = (J3DCoordinate) components[0];
		float[] points = new float[coordinates.getNumPoints()];
		coordinates.getPoint(points);

		// Points
		int[] indexes = setNode.getFieldValue(setNode.getFieldIndex("index")).intArrayValue;
		Vector3d p1 = getIndexedPoint(points, indexes[0]);
		Vector3d p2 = getIndexedPoint(points, indexes[1]);
		for (int i = 2; i < indexes.length; i++) {
			try {
				if (indexes[i] == -1) {
					if (i + 2 < indexes.length) {
						p1 = getIndexedPoint(points, indexes[i + 1]);
						p2 = getIndexedPoint(points, indexes[i + 2]);
						i += 2;
				}
				} else {
					Vector3d p3 = getIndexedPoint(points, indexes[i]);
					triangleSet.addTriangle(p1, p2, p3);
					//triangleSet.addTriangle(p1, p3, p2);
					p1 = p2;
					p2 = p3;
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		return triangleSet;
	}

	/**
	 * @param material
	 * @param shape
	 */
	private Material parseMaterial(J3DMaterial material) {
		Material m = new Material();
		// Material Data
		m.diffuseColor = new Vector3d(new Vector3f(material.getDiffuseColor()));
		m.specularColor = new Vector3d(
				new Vector3f(material.getSpecularColor()));
		m.ambientIntensity = material.getAmbientIntensity();
		m.transparency = material.getTransparency();
		m.shininess = material.getShininess();
		
		// Material Metadata
		J3DMetadataSet metadata = (J3DMetadataSet) material.getMetadataObject();
		if (metadata != null) {
			VRMLFieldData data = metadata.getFieldValue(metadata
					.getFieldIndex("value"));
			VRMLNode[] metadataNodes = data.nodeArrayValue;
			for (int i = 0; i < metadataNodes.length; i++) {
				J3DMetadataFloat f = (J3DMetadataFloat) metadataNodes[i];
				VRMLFieldData d = f.getFieldValue(f.getFieldIndex("value"));
				if (f.getName().equals("reflection")) {
					m.reflectionIndex = d.floatArrayValue[0];
				} else if (f.getName().equals("refraction")) {
					m.refractionIndex = d.floatArrayValue[0];
				} else if (f.getName().equals("diffuse")) {
					m.diffuseIndex = d.floatArrayValue[0];
				} else if (f.getName().equals("specular")) {
					m.specularIndex = d.floatArrayValue[0];
				}
			}
		}
		return m;
	}

	/**
	 * Process the light node type.  Set the location and color.  Create the
	 * point light object and add it to the scene.  
	 * 
	 * @param node Light node type to process 
	 * @param scene Scene with the added light
	 */
	private void parseLightNode(J3DLightNode node, Scene scene) {
		PointLight light = new PointLight();
		VRMLFieldData data = getField(node, "location");
		light.setColor(new Vector3d(new Vector3f(node.getColor())));
		if (data != null) {
			light.setPosition(new Vector3d(new Vector3f(data.floatArrayValue)));
		}
		if ((data = getField(node, "on")) != null) {
			if (!data.booleanValue)
				return;
		}
		if ((data = getField(node, "radio")) != null) {
			light.setRadio(data.floatValue);
		}
		if ((data = getField(node, "attenuation")) != null) {
			light.setAttenuation(data.floatArrayValue);
		}
		scene.addLight(light);
	}

	/**
	 * Gets a field from a vrml node. If the field does not exist, return null.
	 * 
	 * @param node Node in which we are looking for a field
	 * @param field Name of the field we're looking for
	 * @return The data node when the field exists.  Null if it does not exist.
	 */
	private VRMLFieldData getField(J3DVRMLNode node, String field) {
		for (int i = 0; i < node.getNumFields(); i++) {
			VRMLFieldDeclaration declaration = node.getFieldDeclaration(i);
			if (declaration != null && declaration.getName().equals(field)) {
				return node.getFieldValue(i);
			}
		}
		return null;
	}
}
