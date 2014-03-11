package raytracer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import objects.Material;
import objects.SceneObject;

import scene.Intersection;
import scene.PointLight;
import scene.Scene;

/**
 * The class that implements the principal ray tracing algorithm.  The parameters are specified through
 * the constructor and the generated image is obtained from the render method
  */
public class RayTracer {

	/** Maximum number of levels in the recursion of getColor */
	private static final int MAX_LEVELS = 10;

	/** Margin of error when comparing doubles */
	private static final double EPSILON_EQUALS = 0.000000000001;

	/** Desired scene to render */
	private Scene scene;

	/** Size of the image to generate */
	private Dimension imageSize;

	/** Antialiasing parameter */
	private int antialiasing = 1;

	/** Shadow parameter */
	private int shadow;

	/**
	 * Create the new ray tracer with the given parameters.  The camera is set up to the size 
	 * of the image to generate so that we can construct the rays. 
	 * 
	 * @param scene Scene to render
	 * @param imageSize Size of the image to generate
	 * @param antialiasing Antialiasing parameters (may be null)
	 * @param shadow Shadow parameters (may be null)
	 */
	public RayTracer(Scene scene, Dimension imageSize, int antialiasing, int shadow) {
		super();
		this.scene = scene;
		this.antialiasing = antialiasing;
		this.shadow = shadow;
		this.imageSize = imageSize;
	}

	/**
	 * Perform the rendering of the provided scene in the constructor 
	 * 
	 * @param showProgress Flag indicating whether or not to show progress on the screen
	 * @return The generated image
	 */
	public BufferedImage render(boolean showProgress) {
		BufferedImage image = new BufferedImage(imageSize.width, imageSize.height,
				BufferedImage.TYPE_INT_RGB);
		if (showProgress) {
			for (int i = 0; i < 80; i++) {
				System.out.print('-');
			}
			System.out.print("\n");
		}
		for (int i = 0; i < imageSize.height; i++) {
			for (int j = 0; j < imageSize.width; j++) {
				if (showProgress
						&& (i * imageSize.width + j) % (imageSize.width * imageSize.height / 80) == 0) {
					System.out.print('*');
				}
				SceneObject intersected, lastIntersected = null;
				Ray ray = constructRayThroughPixel(i, j);
				Vector3d color = new Vector3d();
				intersected = getColor(ray, 0, scene.getCamera().position, color, 1);
				if (intersected != lastIntersected) {
					lastIntersected = intersected;
					color.set(0, 0, 0);
					for (int m = -(antialiasing / 2); m <= antialiasing / 2; m++) {
						for (int n = -(antialiasing / 2); n <= antialiasing / 2; n++) {
							Vector3d colorAcum = new Vector3d();
							Ray antialiasRay = constructRayThroughPixel(i, j, m, n);
							getColor(antialiasRay, 0, scene.getCamera().position, colorAcum, 1);
							color.add(colorAcum);
						}
					}
					color.scale(1.0 / (antialiasing * antialiasing));
				}
				image.setRGB(j, i, new Color((float) color.x, (float) color.y, (float) color.z)
						.getRGB());
			}
		}
		System.out.print("\n");
		return image;
	}

	/**
	 * Calculates the color that is reflected for a ray in the scene.  This is called recursively for reflections with the 
	 * currentLevel counter incremented at each level of the recursion.   Get the location of the viewer for the specular part.
	 * In the first invocation, this location will be (0,0,0), but then it will be the point of intersection for each ray
	 * (specular effect calculated recursively).  
	 * 
	 * @param ray Ray being shot
	 * @param currentLevel Current level of recursion (0 at invocation)
	 * @param viewerPosition Position of the observer.  In the first invocation, it is the origin. 
	 * @param color Output parameter with the color found in the pixel
	 * @param currentRefraction Refractive index of the current environment 
	 * @return The first intersected object (may be null) 
	 */
	private SceneObject getColor(Ray ray, int currentLevel, Vector3d viewerPosition,
			Vector3d color, double currentRefraction) {

		if (currentLevel > MAX_LEVELS) {
			color.set(new double[] { 0, 0, 0 });
			return null;
		}
		Intersection intersection = new Intersection();
		SceneObject intersectedObject = scene.getFirstIntersectedObject(ray, intersection);
		if (intersectedObject == null) {
			color.set(new double[] { 0, 0, 0 });
			return null;
		}
		Material material = intersectedObject.getMaterial();
		double nShiny = material.shininess * 128.0;
		
		Vector3d ambientIntensity = new Vector3d(1, 1, 1);
		Vector3d specularIntensity = new Vector3d(0, 0, 0);
		Vector3d diffuseIntensity = new Vector3d(0, 0, 0);

		for (PointLight light : scene.getLights()) {

			Ray lightRay;
			Vector3d lightPosition, lightDirection;

			/* Calculate the light shading */
			double shade = 0;
			for (int i = 0; i < shadow; i++) {
				lightPosition = new Vector3d(light.getPosition());
				if (shadow > 1) {
					lightPosition.x += (Math.random() - 1) * light.getRadio();
					lightPosition.y += (Math.random() - 1) * light.getRadio();
					lightPosition.z += (Math.random() - 1) * light.getRadio();
				}

				lightDirection = new Vector3d(intersection.point);
				lightDirection.sub(lightPosition);

				lightRay = new Ray(lightPosition, lightDirection);

				Intersection lightIntersection = new Intersection();
				SceneObject lightIntersectedObject = scene.getFirstIntersectedObject(lightRay,
						lightIntersection);
				if (lightIntersectedObject == null
						|| !lightIntersectedObject.equals(intersectedObject)
						|| !lightIntersection.point.epsilonEquals(intersection.point,
								EPSILON_EQUALS)) {
					continue;
				}
				shade += 1.0 / shadow;
			}
			if (shade < EPSILON_EQUALS) {
				continue;
			}

			/* Calculate the ray for the counts (counter?) */
			lightDirection = new Vector3d(intersection.point);
			lightDirection.sub(light.getPosition());
			lightRay = new Ray(light.getPosition(), lightDirection);

			/* Calculate the specular term */
			Ray lightReflectedRay = reflectRay(lightRay, intersectedObject, intersection, 0.005);
			Vector3d specular = new Vector3d(viewerPosition);
			specular.sub(intersection.point);
			specular.normalize();
			double aux = specular.dot(lightReflectedRay.direction);
			if (aux < 0) {
				aux = 0;
			}
			double specularTerm = material.specularIndex * Math.pow(aux, nShiny);
			if (specularTerm < 0.001) {
				specularTerm = 0;
			}
			Vector3d lightIntensity = light.getColor(intersection.point);
			lightIntensity.scale(specularTerm);
			specularIntensity.add(lightIntensity);

			/* Calculate the diffusion term */
			Vector3d diffuse = new Vector3d(intersection.normal);
			diffuse.normalize();
			// lightRay.direction.scale(-1);
			double diffuseTerm = diffuse.dot(lightRay.direction) * material.diffuseIndex;
			if (diffuseTerm < 0) {
				diffuseTerm = 0;
			}
			lightIntensity = light.getColor(intersection.point);
			lightIntensity.scale(diffuseTerm * shade);
			diffuseIntensity.add(lightIntensity);
		}

		/* Calculate the contribution of each light color */
		ambientIntensity.scale((float) material.ambientIntensity);
		Util.multiplyVectors(ambientIntensity, material.diffuseColor);
		Util.multiplyVectors(diffuseIntensity, material.diffuseColor);
		Util.multiplyVectors(specularIntensity, material.specularColor);

		ambientIntensity.add(diffuseIntensity);
		ambientIntensity.add(specularIntensity);

		/*The final result remains in ambient intensity (Spanish-English translation not great) */
		Util.cropVector(ambientIntensity);

		/* Reflection */
		if (material.reflectionIndex > 0)
			computeReflection(ray, intersectedObject, intersection, ambientIntensity, currentLevel,
					currentRefraction);

		/* Refraction */
		if (material.transparency > 0)
			computeRefraction(ray, intersectedObject, intersection, ambientIntensity, currentLevel,
					currentRefraction);

		/* Final color */
		Util.cropVector(ambientIntensity);
		color.set(ambientIntensity);

		return intersectedObject;
	}

	private void computeReflection(Ray ray, SceneObject intersectedObject,
			Intersection intersection, Vector3d ambientIntensity, int currentLevel,
			double currentRefraction) {
		Ray reflectedRay = reflectRay(ray, intersectedObject, intersection, 0.00001);
		Vector3d reflectedColor = new Vector3d();
		if (getColor(reflectedRay, currentLevel + 1, reflectedRay.position, reflectedColor,
				currentRefraction) != null) {

			Material material = intersectedObject.getMaterial();
			Util.multiplyVectors(reflectedColor, material.diffuseColor);
			reflectedColor.scale(material.reflectionIndex);
			ambientIntensity.add(reflectedColor);
		}
	}

	private void computeRefraction(Ray ray, SceneObject intersectedObject,
			Intersection intersection, Vector3d ambientIntensity, int currentLevel,
			double currentRefraction) {
		Ray refractedRay = refractRay(ray, intersectedObject, intersection, 0.00001,
				currentRefraction);
		if (refractedRay != null) {

			// TODO
			if (refractedRay.direction.dot(intersection.normal) > 0) {
				currentRefraction = intersectedObject.getMaterial().refractionIndex;
			} else {
				currentRefraction = 1;
			}

			Vector3d refractedColor = new Vector3d();
			SceneObject instersectedObject2 = getColor(refractedRay, currentLevel + 1,
					refractedRay.position, refractedColor, currentRefraction);
			if (instersectedObject2 != null) {

				Vector3d pointOfIntersection = instersectedObject2.intersectsRay(refractedRay).point;
				pointOfIntersection.sub(intersection.point);
				double distancia = intersection.distance;

				Material material = intersectedObject.getMaterial();
				Vector3d absorbance = new Vector3d(material.diffuseColor);
				absorbance.scale(-0.15 * distancia);
				Vector3d transparency = new Vector3d(Math.exp(absorbance.x),
						Math.exp(absorbance.y), Math.exp(absorbance.z));

				Util.multiplyVectors(refractedColor, transparency);
				refractedColor.scale(material.transparency);
				ambientIntensity.add(refractedColor);
			}
		}
	}

	/**
	 * Construct a ray that exits that camera and passes through the pixel (i,j) of the image plane.  
	 * 
	 * @param i Pixel row to traverse
	 * @param j Pixel column to traverse
	 * @return A ray that leaves the camera and passes through the specified pixel 
	 */
	public Ray constructRayThroughPixel(int i, int j) {
		double xDir = (j - imageSize.width / 2f);
		double yDir = (i - imageSize.height / 2f);
		double zDir = (double) (
				Math.min(imageSize.width, imageSize.height)
				/ (2 * Math.tan(scene.getCamera().fieldOfView / 2)));
		Vector4d dir = new Vector4d(xDir, -yDir, -zDir, 1);
		dir.normalize();
		Vector4d result = Util.MultiplyMatrixAndVector(scene.getCamera().rotationMatrix,
				dir);
		Vector3d direction = new Vector3d(result.x, result.y, result.z);
		direction.normalize();
		return new Ray(scene.getCamera().position, direction);
	}

	/**
	 * Construct a ray for antialiasing that leaves the camera and passes through pixel (i,j) of the image plane
	 * In the pixel, constuct a grid with the antialiasing parameter and make the ray pass through 
	 * through the element (m,n) of the grid. 
	 * 
	 * @param i Pixel row to traverse 
	 * @param j Pixel column to traverse
	 * @param m Row of the constructed grid on the pixel 
	 * @param n Column of the constructed grid on the pixel 
	 * @return A ray that exits the camera and passes through the specified pixel
	 */
	public Ray constructRayThroughPixel(int i, int j, int m, int n) {
		double xDir = antialiasing * (j - imageSize.width / 2f);
		double yDir = antialiasing * (i - imageSize.height / 2f);
		double zDir = antialiasing * (double) (
				Math.min(imageSize.width, imageSize.height)
				/ (2 * Math.tan(scene.getCamera().fieldOfView / 2)));

//		double zDir = antialiasing
//				* (double) (Math.sqrt(Math.pow(imageSize.width, 2) + Math.pow(imageSize.height, 2)) / (2 * Math
//						.tan(scene.getCamera().fieldOfView)));

		xDir += Util.randomBetween(n, n + 1);
		yDir += Util.randomBetween(m, m + 1);

		Vector4d result = Util.MultiplyMatrixAndVector(scene.getCamera().rotationMatrix,
				new Vector4d(xDir, -yDir, -zDir, 1));
		Vector3d direction = new Vector3d(result.x, result.y, result.z);
		direction.normalize();
		return new Ray(scene.getCamera().position, direction);

	}

	/**
	 * Calculate the reflection of the ray on the object.  Use the object normal at the point of intersection 
	 * to construct the reflection and return it. 
	 * 
	 * @param ray Ray the hits the object
	 * @param intersectedObject Object hit by the ray
	 * @param pointOfIntersection Point where the ray hits the object
	 * @param delta The amount that the origin of the ray should be displaced relative to its direction 
	 * @return Reflection ray at that point
	 */
	private Ray reflectRay(Ray ray, SceneObject intersectedObject, Intersection intersection,
			double delta) {
		Vector3d direction = new Vector3d(intersection.normal);
		double aux = direction.dot(ray.direction) * -1;
		direction.scale(2 * aux);
		direction.add(ray.direction);
		Vector3d position = new Vector3d(direction);
		position.scale(delta);
		position.add(intersection.point);
		return new Ray(position, direction);
	}

	/**
	 * Calculate the ray refraction through an object
	 * 
	 * @param ray Ray that hits the object
	 * @param intersectedObject Object hit by the ray
	 * @param pointOfIntersection Point where the ray hits the object
	 * @param delta The amount that the origin of the ray should be displaced relative to the normal
	 * @return Refracted ray at that point 
	 */
	private Ray refractRay(Ray ray, SceneObject intersectedObject, Intersection intersection,
			double delta, double currentRefraction) {

		Material material = intersectedObject.getMaterial();
		double rindex = material.refractionIndex;
		double n = currentRefraction / rindex;
		Vector3d normal = new Vector3d(intersection.normal);

		if (normal.dot(ray.direction) > 0) {
			normal.scale(-1);
		}

		double cosI = -normal.dot(ray.direction);
		double cosT2 = 1.0 - n * n * (1.0 - cosI * cosI);
		if (cosT2 > 0) {
			Vector3d direction = new Vector3d(ray.direction);
			direction.scale(n);
			normal.scale(n * cosI - Math.sqrt(cosT2));
			direction.add(normal);
			direction.normalize();

			Vector3d position = new Vector3d(direction);
			position.scale(delta);
			position.add(intersection.point);

			return new Ray(position, direction);
		}
		return null;

	}
}
