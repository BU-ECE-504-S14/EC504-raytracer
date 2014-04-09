package raytracer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import objects.Material;
import objects.SceneObject;
import objects.Sphere;
import scene.Intersection;
import scene.PointLight;
import scene.Scene;
/**
 * Simple ray tracer, for EC504 at Boston University
 * based on the work of Rafael Martin Bigio <rbigio@itba.edu.ar.
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class SimpleRayTracer{
	private static final int NOT_SHINY = -1;
	private static final int NOT_REFLECTIVE = -1;
	private static final int NOT_REFRACTIVE = -1;
	private static final int AIR_REFRACTIVE_INDEX = 1;

	private static final int MAX_LEVELS = 10;/** Maximum number of levels in the recursion of getColor */
	private static final double EPSILON_EQUALS = 0.000000000001;/** Margin of error when comparing doubles */
	private static final double FLOAT_CORRECTION = 0.001;

	private Scene scene;/** Desired scene to render */
	private Dimension imageSize;/** Size of the image to generate */
	private int antialiasing = 1;/** Antialiasing parameter */
	private int shadow;/** Shadow parameter */
	private int counter;
	public int totalRays;
	public int currentRay;

	/**
<<<<<<< HEAD
	 * Create the new ray tracer with the given parameters. The camera is set up to the
	 * size of the image to generate so that we can construct the rays.
=======
	 * Create the new ray tracer with the given parameters. The camera is set up
	 * to the size of the image to generate so that we can construct the rays.
	 * 
>>>>>>> Bryant
	 * @param scene
	 *            Scene to render
	 * @param imageSize
	 *            Size of the image to generate
	 * @param antialiasing
	 *            Antialiasing parameters (may be null)
	 * @param shadow
	 *            Shadow parameters (may be null)
	 * @param outputImage
	 *            TODO
	 */
	public SimpleRayTracer(Scene scene, Dimension imageSize, int antialiasing,
			int shadow) {
		super();
		this.scene = scene;
		this.antialiasing = antialiasing;
		this.shadow = shadow;
		this.imageSize = imageSize;
		counter = 0;
	}
	/**
	 * Perform the rendering of the provided scene in the constructor 
	 * @param showProgress
	 *            Flag indicating whether or not to show progress on the screen
	 * @return The generated image
	 */

	public BufferedImage render(boolean showProgress) {
		totalRays = imageSize.height * imageSize.width;
		currentRay = 1;

		BufferedImage image = new BufferedImage(imageSize.width,
				imageSize.height, BufferedImage.TYPE_INT_RGB);

		Vector3d color = new Vector3d();
		for (int i = 0; i < imageSize.height; i++) {
			for (int j = 0; j < imageSize.width; j++) {

				currentRay++;
				if (showProgress
						&& (i * imageSize.width + j)
								% (imageSize.width * imageSize.height / 80) == 0)
					System.out.print('*');

				Ray ray = constructRayThroughPixel(i, j);/* create this ray through pixel (i,j) */
				color.set(0, 0, 0);

				/* do ray trace */
				getColor(ray, 0, scene.getCamera().position, color, 1);

				/* set color into image at screen position (i,j) */
				image.setRGB(j, i, new Color((float) color.x, (float) color.y,
						(float) color.z).getRGB());

			}
		}
		return image;
	}
	/**
	 * Simple RayTracer. Calculates the color from intersected point of ray to
	 * lights in scene.
	 * 
	 * @param ray
	 *            Ray being shot
	 * @param currentLevel
	 *            Current level of recursion (0 at invocation) (not used yet)
	 * @param viewerPosition
	 *            Position of the observer. In the first invocation, it is the
	 *            origin (not used yet)
	 * @param color
	 *            Output parameter with the color found in the pixel
	 * @param currentRefraction
	 *            Refractive index of the current environment (not used yet)
	 * @return The first intersected object (may be null)
	 */

	private SceneObject getColor(Ray ray, int currentLevel,
			Vector3d viewerPosition, Vector3d color, double currentRefraction) {

		// Find the first intersected object and its material properties
		Intersection intersection = new Intersection();
		SceneObject intersectedObject = scene.getFirstIntersectedObject(ray,
				intersection);

		// If no object is intersected, set the color to black and return null
		if (intersectedObject == null) {
			color.set(new double[] { 0, 0, 0 });
			return null;
		}

		Material material = intersectedObject.getMaterial();

		// Running sum of ambient, diffuse, and specular lights
		Vector3d illumination = new Vector3d(0, 0, 0);

		// Cycle through all of the lights
		for (PointLight light : scene.getLights()) {

			// Lighting variables for each light
			Vector3d ambient = new Vector3d(0, 0, 0);
			Vector3d diffuse = new Vector3d(0, 0, 0);
			Vector3d specular = new Vector3d(0, 0, 0);

			// Calculate ambient lighting
			diffuse = calculateDiffuseColor(intersectedObject.getMaterial(),
					light, intersection.point);
			ambient = diffuse;
			ambient.scale(material.ambientIntensity);

			// Find the shadow ray
			Ray shadowRay = findShadowRay(intersectedObject, intersection,
					light);

			// Calculate the distance between the light and the intersection
			// point
			Vector3d lightVect = new Vector3d(light.getPosition());

			lightVect.sub(intersection.point);
			double lightDist = lightVect.length();
			lightVect.normalize();
			lightVect.negate();

			// Check if the intersection point is in shadow
			if (inShadow(shadowRay, lightDist)) {
				// Light contribution comes solely from ambient lighting
				// Update illumination and continue with the next light
				illumination.add(ambient);
				continue;
			}

			// If not in shadow, calculate diffuse and specular lighting

			// Calculate diffuse light using Lambertian shading
			// Equation: L_d = k_d * I * max(0, n dot l)
			// k_d: diffuse parameter (implicitly 1 here)
			// I: illumination of intensity of the light (its radiosity)
			// n: normal vector to the object at the point of intersection
			// l: the light ray emitted from the light that hits the point of
			// intersection

			double dotProd = intersection.normal.dot(lightVect);

			if (dotProd >= 0) {
				diffuse.scale(dotProd);
				diffuse.scale(light.getRadio());
				illumination.add(diffuse);
			}

			// Calculate specular light. View direction affects the intensity of
			// light contribution.
			// Equation: L_s = k_s * I * max(0, r dot v)^(k_e)
			// k_s: specular parameter (specular index)
			// I: illumination intensity of the light (radiosity)
			// n: normal vector to the object at the point of intersection
			// r: mirror reflection of the light ray hitting the intersection
			// point (reflection)
			// v: view ray (eyedirection; ray emanating from the camera and
			// hitting the object)
			// k_e: shininess parameter that controls the size of the specular
			// highlight (shininess*256)

			double shininess = material.shininess;
			Vector3d eyeDirection = new Vector3d(ray.position);
			Vector3d reflection = new Vector3d();

			eyeDirection.sub(intersection.point);
			eyeDirection.normalize();
			reflection = reflect(lightVect, intersection.normal);

			dotProd = eyeDirection.dot(reflection);
			if (dotProd > 0) {
				double specAmt = material.specularIndex
						* Math.pow(dotProd, (shininess * 128.0));

				// Use the attenuated light intensity at intersection point and
				// multiply by the calculated specular amount

				specular = calculateSpecularColor(
						intersectedObject.getMaterial(), light,
						intersection.point);
				
				specular.scale(specAmt);
				illumination.add(specular);
			}

			// Update illumination
			illumination.add(ambient);
		}

		// Maximum illumination is 1 for any given R,G,B value
		if (illumination.getX() > 1)
			illumination.setX(1);
		if (illumination.getY() > 1)
			illumination.setY(1);
		if (illumination.getZ() > 1)
			illumination.setZ(1);

		color.set(illumination);

		return intersectedObject;
	}
	/**
	 * Find reflected ray
	 * 
	 * @param vector
	 *            to be reflected
	 * @param normal
	 *            to the eye
	 * @return reflected vector
	 */
	public Vector3d reflect(Vector3d v1, Vector3d normal) {
		Vector3d reflected = new Vector3d(v1);
		Vector3d adjVect = new Vector3d(normal);

		double adj = v1.dot(normal) * 2;
		adjVect.scale(adj);
		reflected.sub(adjVect);
		reflected.normalize();

		return reflected;
	}

	/**
	 * Find shadow ray and correct for floating point imprecision
	 * 
	 * @param shadowRay
	 * @param distance
	 * @return ray
	 */
	public Ray findShadowRay(SceneObject intersectedObject,
			Intersection intersection, PointLight light) {
		Ray shadowRay;
		Vector3d lightDirection, objectNormal;

		/*
		 * correct for floating point imprecision of object surface detail by
		 * moving origin of shadow ray an epsilon factor in the direction of the
		 * object normal
		 */
		objectNormal = intersectedObject.getNormalAt(intersection.point);
		objectNormal.negate();
		Vector3d EPSILON = new Vector3d(objectNormal.x, objectNormal.y,
				objectNormal.z);
		EPSILON.scale(FLOAT_CORRECTION);
		intersection.point.add(EPSILON);

		lightDirection = new Vector3d(light.getPosition());
		lightDirection.sub(intersection.point);

		/* create shadow ray */
		shadowRay = new Ray(intersection.point, lightDirection);

		return shadowRay;
	}

	/**
	 * Check if an intersection point is in shadow relative to a specific light.
	 * The ray is shot from the object, ObjBeg, in the direction of the light.
	 * If an object is hit (ObjHit) and the distance between ObjBeg and ObjHit
	 * is less than the distance between the light and ObjBeg (parameter
	 * lightDist), then the object is in shadow relative to that light.
	 * Otherwise, the object is not in shadow.
	 * 
	 * @param shadowRay
	 * @param lightDist
	 * @return true if in shadow, false otherwise
	 */

	public boolean inShadow(Ray shadowRay, double lightDist) {
		Intersection intersection = new Intersection();
		SceneObject shadowIntersectedObject = scene.getFirstIntersectedObject(
				shadowRay, intersection);

		if (shadowIntersectedObject == null) {
			return false;
		} else {
			Vector3d shadowVec = new Vector3d(intersection.point);
			shadowVec.sub(shadowRay.position);
			double shadowDist = shadowVec.length();

			if (shadowDist >= lightDist) {
				return false;
			}

			return true;
		}
	}

	/**
	 * Calculates the specular color based on one light. NEED TO ADJUST FOR
	 * OTHER SCENE OBJECTS.
	 * 
	 * @param SceneObject
	 *            o
	 * @param PointLight
	 *            l
	 * @return RGB vector
	 */

	public Vector3d calculateSpecularColor(Material m, PointLight l,
			Vector3d intersect) {
		Vector3d lightColor = l.getColor(intersect);
		double newRColor = m.specularColor.getX() * lightColor.getX();
		double newGColor = m.specularColor.getY() * lightColor.getY();
		double newBColor = m.specularColor.getZ() * lightColor.getZ();

		return new Vector3d(newRColor, newGColor, newBColor);
	}

	/**
	 * Calculates the color based on one light. NEED TO ADJUST FOR OTHER SCENE
	 * OBJECTS.
	 * 
	 * @param SceneObject
	 *            o
	 * @param PointLight
	 *            l
	 * @return RGB vector
	 */

	public Vector3d calculateDiffuseColor(Material m, PointLight l,
			Vector3d intersect) {
		Vector3d lightColor = l.getColor(intersect);
		double newRColor = m.diffuseColor.getX() * lightColor.getX();
		double newGColor = m.diffuseColor.getY() * lightColor.getY();
		double newBColor = m.diffuseColor.getZ() * lightColor.getZ();

		return new Vector3d(newRColor, newGColor, newBColor);
	}
	/**

	 * Construct a ray that exits that camera and passes through the pixel (i,j)
	 * of the image plane.
	 * 

	 * @param i
	 *            Pixel row to traverse
	 * @param j
	 *            Pixel column to traverse
	 * @return A ray that leaves the camera and passes through the specified
	 *         pixel
	 */

	public Ray constructRayThroughPixel(int i, int j) {
		double xDir = (j - imageSize.width / 2f);
		double yDir = (i - imageSize.height / 2f);
		double zDir = (double) (Math.min(imageSize.width, imageSize.height) / (2 * Math
				.tan(scene.getCamera().fieldOfView / 2)));
		Vector4d dir = new Vector4d(xDir, -yDir, -zDir, 1); // why is image
															// inverted?

		dir.normalize();
		Vector4d result = Util.MultiplyMatrixAndVector(
				scene.getCamera().rotationMatrix, dir);
		Vector3d direction = new Vector3d(result.x, result.y, result.z);
		direction.normalize();
		return new Ray(scene.getCamera().position, direction);
	}
	Color getReflectiveRefractiveLighting(Intersection intersection, SceneObject o) {

		///////////////////////////////////////////////////////////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\DOUBLE CHECK THESE
		double reflectivity = o.getMaterial().reflectionIndex;//intersection.endMaterial->getReflectivity();
		double startRefractiveIndex = o.getMaterial().refractionIndex;//intersection.startMaterial->getRefractiveIndex();
		double endRefractiveIndex = o.getMaterial().refractionIndex;//intersection.endMaterial->getRefractiveIndex();
		int reflectionsRemaining =0;//intersection.ray.reflectionsRemaining;

		/**
		 * Don't perform lighting if the object is not reflective or refractive or we have
		 * hit our recursion limit.
		 */
		if (reflectivity == NOT_REFLECTIVE && endRefractiveIndex == NOT_REFRACTIVE || reflectionsRemaining <= 0) {
			return null;
		}

		// Default to exclusively reflective values.
		double reflectivePercentage = reflectivity;
		double refractivePercentage = 0;

		// Refractive index overrides the reflective property.
		if (endRefractiveIndex != NOT_REFRACTIVE) {
			reflectivePercentage = getReflectance(intersection.normal, intersection.ray.direction, startRefractiveIndex, endRefractiveIndex);

			refractivePercentage = 1 - reflectivePercentage;
		}

		// No ref{ra,le}ctive properties - bail early.
		if (refractivePercentage <= 0 && reflectivePercentage <= 0) {
			return null;
		}

		Color reflectiveColor = new Color(0,0,0);
		Color refractiveColor = new Color(0,0,0);

		if (reflectivePercentage > 0) {
			Vector3d reflected = reflectVector(intersection.ray.position, intersection.normal);
			Ray reflectedRay = new Ray(intersection.point, reflected);//, reflectionsRemaining - 1, intersection.ray.material);
			Color temp = new Color(0,0,0);
			//temp = castRay(reflectedRay);
			int red = (int) (temp.getRed()* reflectivePercentage);
			int green = (int) (temp.getGreen()* reflectivePercentage);
			int blue = (int) (temp.getBlue()* reflectivePercentage);
			reflectiveColor = new Color(red,green,blue);

		}

		if (refractivePercentage > 0) {
			Vector3d refracted = refractVector(intersection.normal,intersection.ray.direction, startRefractiveIndex, endRefractiveIndex);
			Ray refractedRay = new Ray(intersection.point, refracted);//, 1, intersection.endMaterial);
			//refractiveColor = castRay(refractedRay);// * refractivePercentage;
		}

		return new Color(reflectiveColor.getRed() + refractiveColor.getRed(),reflectiveColor.getGreen() +
				refractiveColor.getGreen(), reflectiveColor.getBlue() + refractiveColor.getBlue());
	}
	/*
	Color castRay(Ray ray) {
		//raysCast++;
		Intersection intersection = getClosestIntersection(ray);
		if (intersection.didIntersect) {
			return performLighting(intersection);
		} else {
			return null;
		}
	}

Color RayTracer::performLighting(const Intersection& intersection) {
   Color color = intersection.getColor();
   Color ambientColor = getAmbientLighting(intersection, color);
   Color diffuseAndSpecularColor = getDiffuseAndSpecularLighting(intersection, color);
   Color reflectedColor = getReflectiveRefractiveLighting(intersection);

   return ambientColor + diffuseAndSpecularColor + reflectedColor;
}
	 */
	double getReflectance( Vector3d normal, Vector3d incident, double n1, double n2) {
		double n = n1 / n2;
		double cosI = -normal.dot(incident);
		double sinT2 = n * n * (1.0 - cosI * cosI);

		if (sinT2 > 1.0) {
			// Total Internal Reflection.
			return 1.0;
		}

		double cosT = Math.sqrt(1.0 - sinT2);
		double r0rth = (n1 * cosI - n2 * cosT) / (n1 * cosI + n2 * cosT);
		double rPar = (n2 * cosI - n1 * cosT) / (n2 * cosI + n1 * cosT);
		return (r0rth * r0rth + rPar * rPar) / 2.0;
	}

	Vector3d refractVector( Vector3d normal, Vector3d incident, double n1, double n2) {
		double n = n1 / n2;
		double cosI = -normal.dot(incident);
		double sinT2 = n * n * (1.0 - cosI * cosI);

		if (sinT2 > 1.0) {
			System.out.print("Bad refraction vector!\n");
			System.exit(-1);
		}

		double cosT = Math.sqrt(1.0 - sinT2);
		incident.x *= n;
		incident.y *= n;
		incident.z *= n;
		normal.x *= (n * cosI - cosT);
		normal.y *= (n * cosI - cosT);
		normal.z *= (n * cosI - cosT);
		incident.add(normal);
		return incident;
	}

	Vector3d reflectVector(Vector3d vector, Vector3d normal) {
		normal.x *=2 * vector.dot(normal);
		normal.y *=2 * vector.dot(normal);
		normal.z *=2 * vector.dot(normal);
		normal.sub(vector);
		return normal;// - vector;
	}


}