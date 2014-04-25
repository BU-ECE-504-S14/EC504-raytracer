package scene;

import geometry.Ray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import accelerators.AbstractAccelerator;
import accelerators.Octnode;
import accelerators.Octnode.SplitBeyondMaxDepthException;
import accelerators.Octree;
import objects.AbstractSceneObject.RefinementException;
import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import raytracer.Camera;
import raytracer.RenderSettings;

/**
 * A representation of a scene, which contains various objects which can be
 * intersected and illuminated by rays.
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and
 *         Bryant Moquist
 */
public class Scene implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String SCENE_PATH = "./scn";

	protected ArrayList<SceneObject> objects;
	protected Collection<Light> lights = new HashSet<Light>(); // TODO possibly
																// need to
																// change data
																// type
	protected Camera camera;
	public boolean accelFlag; // true when the accelerator is updated and
									// usable
	protected AbstractAccelerator accelerator;
	protected String name;

	public RenderSettings settings;

	public Scene() {
		camera = new Camera(new Vector3d(0, 0, 10),
				new AxisAngle4d(0, 0, -1, 0), (float) (Math.PI / 4));
		objects = new ArrayList<SceneObject>();
		lights = new HashSet<Light>();
		settings = new RenderSettings();
		name = "New Scene";
	}

	public Scene(Scene s) {
		camera = new Camera(s.camera);
		objects = new ArrayList<SceneObject>();
		for (SceneObject so : s.objects) {
			if (so instanceof TriangleMesh) {
				objects.add(((TriangleMesh) so).getCopy());
			} else if (so instanceof Sphere) {
				objects.add(((Sphere) so).getCopy());
			}
		}
		lights = new HashSet<Light>();
		for (Light l : s.lights) {
			if (l instanceof PointLight) {
				lights.add(((PointLight) l).getCopy());
			}
		}
	}

	public void setName(String s) {
		name = s;
	}

	public static void writeSceneToFile(Scene targetScene, File f) {
		Scene s = targetScene;
		try {
			FileOutputStream fOut = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fOut);
			out.writeObject(s);
			out.close();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeSceneToFile(Scene targetScene, String filePath) {
		File f = new File(SCENE_PATH + filePath);
		writeSceneToFile(targetScene, f);
	}

	public static Scene readSceneFromFile(File f) {
		Scene s = null;
		try {
			FileInputStream fIn = new FileInputStream(f);
			ObjectInputStream in = new ObjectInputStream(fIn);
			s = (Scene) in.readObject();
			in.close();
			fIn.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static Scene readSceneFromFile(String fileName) {
		return readSceneFromFile(new File(SCENE_PATH + fileName));
	}

	protected Scene(ArrayList<SceneObject> objects, Collection<Light> lights,
			Camera camera) {
		this.objects = objects;
		this.lights = lights;
		this.camera = camera;
	}

	public void addSceneObject(SceneObject obj) {
		this.objects.add(obj);
		accelFlag = false;
	}

	public void removeSceneObject(SceneObject obj) {
		this.objects.remove(obj);
		accelFlag = false;
	}

	public SceneObject[] getObjectArray() {
		SceneObject[] objs = new SceneObject[objects.size()];
		for (int i = 0; i < objects.size(); i++) {
			objs[i] = objects.get(i);
		}
		return objs;
	}

	public void addLight(Light light) {
		this.lights.add(light);
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public ArrayList<SceneObject> getObjects() {
		return objects;
	}

	public Collection<Light> getLights() {
		return lights;
	}

	public Camera getCamera() {
		return camera;
	}

	/**
	 * looks for the first object that a ray intersects, and the point at which
	 * the ray intersects that object. If no intersection return null.
	 * 
	 * @param ray
	 *            , ray that you want to analyze.
	 * @param intersection
	 *            , output parameter.
	 * @return intersected object.
	 */
	public boolean getFirstIntersectedObject(Ray ray, Intersection inter)
			throws Exception {
		return getFirstIntersectedObject(ray, inter, objects);
	}

	/* ask aaron about this coding practice. Is this overloading. */
	public boolean getFirstIntersectedObject(Ray ray, Intersection inter,
			Collection<SceneObject> objs) throws Exception {
		boolean intersectedFlag = false;

		if (accelFlag == false) { // accelerator is not ready
			SceneObject nearest = null;
			ArrayList<SceneObject> refinedObject = new ArrayList<SceneObject>();

			for (SceneObject o : objs) {
				if (o.isIntersectable()) {
					if (o.IntersectP(ray))
						nearest = o;
				} else {
					refinedObject.clear();
					o.refine(refinedObject);
					for (SceneObject ro : refinedObject) {
						if (ro.IntersectP(ray))
							nearest = ro;
					}
				}
			}
			if (nearest != null) {
				intersectedFlag = nearest.Intersect(ray, inter);
			}
		} else {

			intersectedFlag = accelerator.Intersect(ray, inter);

		}
		return intersectedFlag;
	}

	public void dumpScene() {
		System.out.println("SCENE");
		System.out.println("Objects:");
		for (SceneObject s : objects) {
			System.out.println("  " + s.toString());
		}
		System.out.println("Lights:");
		for (Light p : lights) {
			System.out.println("  " + p.toString());
		}
		System.out.println(camera.toString());
	}

	public void buildOctree(int maxdepth) {
		try {
			accelerator = new Octree(this, maxdepth);
			accelFlag = true;
		} catch (RefinementException e) {
			System.out.println("Octree build failed: Refinement Error"); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			e.printStackTrace();
		} catch (SplitBeyondMaxDepthException e) {
			System.out
					.println("Octree build failed: Split beyond maximum depth");
			e.printStackTrace();
		}
	}
	
	public void buildOctree(){
		buildOctree(settings.getOCTREE_DEPTH());
	}

}
