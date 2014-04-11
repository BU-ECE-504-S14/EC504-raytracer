package raytracer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import objects.Material;
import objects.Normal;
import objects.Pt;
import objects.Ray;
import objects.SceneObject;
import objects.Sphere;
import objects.Vec;
import scene.Intersection;
import scene.Light;
import scene.Scene;

/**
 * Simple ray tracer, for EC504 at Boston University based on the work of Rafael Martin
 * Bigio <rbigio@itba.edu.ar.
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class SimpleRayTracer
{
	private static final int NOT_SHINY = -1;
	private static final int NOT_REFLECTIVE = -1;
	private static final int NOT_REFRACTIVE = -1;
	private static final int AIR_REFRACTIVE_INDEX = 1;

	private static final int MAX_LEVELS = 2;
	/** Maximum number of levels in the recursion of getColor */
	private static final double EPSILON_EQUALS = 0.000000000001;
	/** Margin of error when comparing doubles */
	private static final double FLOAT_CORRECTION = 0.001;

	private Scene scene;
	/** Desired scene to render */
	private Dimension imageSize;
	/** Size of the image to generate */
	private int antialiasing = 1;
	/** Antialiasing parameter */
	private int shadow;
	/** Shadow parameter */
	private int counter;
	public int totalRays;
	public int currentRay;

	/**
	 * <<<<<<< HEAD Create the new ray tracer with the given parameters. The camera is set
	 * up to the size of the image to generate so that we can construct the rays. =======
	 * Create the new ray tracer with the given parameters. The camera is set up to the
	 * size of the image to generate so that we can construct the rays.
	 * 
	 * >>>>>>> Bryant
	 * 
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
	public SimpleRayTracer(Scene scene, Dimension imageSize, int antialiasing, int shadow)
	{
		super();
		this.scene = scene;
		this.antialiasing = antialiasing;
		this.shadow = shadow;
		this.imageSize = imageSize;
		counter = 0;
	}

	/**
	 * Perform the rendering of the provided scene in the constructor
	 * 
	 * @param showProgress
	 *            Flag indicating whether or not to show progress on the screen
	 * @return The generated image
	 */

	public BufferedImage render(boolean showProgress)
	{
		totalRays = imageSize.height * imageSize.width;
		currentRay = 1;

		BufferedImage image = new BufferedImage(imageSize.width, imageSize.height,
				BufferedImage.TYPE_INT_RGB);

		Vector3d color = new Vector3d();
		for (int i = 0; i < imageSize.height; i++)
		{
			for (int j = 0; j < imageSize.width; j++)
			{

				currentRay++;
				if (showProgress
						&& (i * imageSize.width + j) % (imageSize.width * imageSize.height / 80) == 0)
					System.out.print('*');

				Ray ray = constructRayThroughPixel(i, j);/*
														 * create this ray through pixel
														 * (i,j)
														 */
				color.set(0, 0, 0);

				/* do ray trace */
				color.add(getColor(ray, 0, scene.getCamera().position));

				/* set color into image at screen position (i,j) */
				image.setRGB(j, i,
						new Color((float) color.x, (float) color.y, (float) color.z).getRGB());

			}
		}
		return image;
	}

	/**
	 * Calculates the diffuse component of the Phong shading model for the given
	 * intersection, light, and eye position.
	 */
	private Vector3d getDiffuseComponent(Intersection inter, Light light)
	{
		// ******** If not in shadow, calculate diffuse and specular lighting

		// ******** Calculate diffuse light using Lambertian shading
		// ******** Equation: L_d = k_d * I * max(0, n dot l)
		// ******** k_d: diffuse parameter (implicitly 1 here)
		// ******** I: illumination of intensity of the light (its radiosity)
		// ******** n: normal vector to the object at the point of intersection
		// ******** l: the light ray emitted from the light that hits the point of
		// ******** intersection

		Vector3d diffuse = calculateDiffuseColor(inter, light);

		Vector3d lightVect = new Vector3d(light.getPosition());

		lightVect.sub(inter.p);
		lightVect.normalize();
		lightVect.negate();

		double dotProd = inter.nn.dot(lightVect);

		if (dotProd < 0)
		{
			dotProd = 0;
		}

		diffuse.scale(dotProd);
		diffuse.scale(inter.shape.getMaterial().diffuseIndex);
		diffuse.scale(light.getRadio());
		return diffuse;
	}

	/**
	 * Calculates the ambient component of the Phong shading model for the given
	 * intersection, light, and eye position.
	 */
	private Vector3d getAmbientComponent(Intersection inter, Light light)
	{
		Vector3d newColor = calculateDiffuseColor(inter, light);
		newColor.scale(inter.shape.getMaterial().ambientIntensity);
		return newColor;
	}

	/**
	 * Calculates the specular component of the Phong shading model for the given
	 * intersection, light, and eye position.
	 */
	private Vector3d getSpecularComponent(Intersection inter, Light light, Vector3d eye)
	{
		// ******** Calculate specular light. View direction affects the intensity of
		// ******** light contribution.
		// ******** Equation: L_s = k_s * I * max(0, r dot v)^(k_e)
		// ******** k_s: specular parameter (specular index)
		// ******** I: illumination intensity of the light (radiosity)
		// ******** n: normal vector to the object at the point of intersection
		// ******** r: mirror reflection of the light ray hitting the intersection
		// ******** point (reflection)
		// ******** v: view ray (eyedirection; ray emanating from the camera and
		// ******** hitting the object)
		// ******** k_e: shininess parameter that controls the size of the specular
		// ******** highlight (shininess*256)

		Vector3d lightVect = new Vector3d(light.getPosition());

		lightVect.sub(inter.p);
		lightVect.normalize();
		lightVect.negate();

		Vector3d specular = new Vector3d(0, 0, 0);
		Material material = inter.shape.getMaterial();
		Vector3d reflection = new Vector3d();
		eye.sub(inter.p);
		eye.normalize();
		reflection = reflect(lightVect, inter.nn);
		double dotProd = eye.dot(reflection);

		if (dotProd > 0)
		{
			double specAmt = material.specularIndex
					* Math.pow(dotProd, (material.shininess * 128.0));

			// ******** Use the attenuated light intensity at intersection point and
			// ******** multiply by the calculated specular amount

			specular = calculateSpecularColor(inter, light);

			specular.scale(specAmt);
		}
		return specular;
	}

	/**
	 * Calculates the color for the given intersection and light based on the Phong
	 * shading model.
	 */
	private Vector3d getPhongColor(Ray ray, Intersection inter, Light light)
	{
		// ******** Color components for each light
		Vector3d ambient = new Vector3d(0, 0, 0);
		Vector3d diffuse = new Vector3d(0, 0, 0);
		Vector3d specular = new Vector3d(0, 0, 0);

		// ******** Calculate the ambient component
		ambient = getAmbientComponent(inter, light);

		// ******** Check to see whether in shadow, for diffuse lighting
		Ray shadowRay = makeShadowRay(inter, light);

		// ******** if object is in shadow for this light, only add ambient color
		// ******** if (scene.getFirstIntersectedObject(shadowRay, new Intersection()))
		if (!inShadow(shadowRay, light))
		{
			// ******** Calculate the diffuse component
			diffuse = getDiffuseComponent(inter, light);

			Vector3d eyeDirection = new Vector3d(ray.position);
			specular = getSpecularComponent(inter, light, eyeDirection);

		}

		return addColorComponents(ambient, diffuse, specular);

	}

	/**
	 * Returns the sum of the ambient, diffuse, and specular components provided.
	 */
	private static Vector3d addColorComponents(Vector3d ambient, Vector3d diffuse, Vector3d specular)
	{
		Vector3d newColor = new Vector3d(0, 0, 0);
		newColor.add(ambient);
		newColor.add(diffuse);
		newColor.add(specular);

		newColor.clamp(0.0, 1.0);
		return newColor;
	}

	/**
	 * Simple RayTracer. Calculates the color from intersected point of ray to lights in
	 * scene.
	 * 
	 * @param ray
	 *            Ray being shot
	 * @param currentLevel
	 *            Current level of recursion (0 at invocation) (not used yet)
	 * @param viewerPosition
	 *            Position of the observer. In the first invocation, it is the origin (not
	 *            used yet)
	 * @param color
	 *            Output parameter with the color found in the pixel
	 * @param currentRefraction
	 *            Refractive index of the current environment (not used yet)
	 * @return The first intersected object (may be null)
	 */
	private Vector3d getColor(Ray ray, int currentLevel, Vector3d viewerPosition)
	{

		Vector3d color = new Vector3d(0, 0, 0);
		Intersection inter = new Intersection();

		if (!scene.getFirstIntersectedObject(ray, inter))
		{
			return color;
		}

		// ******** Cycle through all of the lights, adding the sum of the diffuse,
		// ambient, and
		// ******** specular components to the color at this pixel
		for (Light light : scene.getLights())
		{
			color.add(getPhongColor(ray, inter, light));
		}

		// ******** Restrict the color of this pixel to the range [0,1]

		if (currentLevel < MAX_LEVELS)
		{
			double refIndex = inter.shape.getMaterial().reflectionIndex;
			if (refIndex > 0)
			{
				Ray reflection = makeReflectionRay(inter, ray.position);
				Vector3d refColor = getColor(reflection, currentLevel + 1, inter.p);
				refColor.scale(refIndex);
				color.add(refColor);
			}
		}

		color.clamp(0.0, 1.0);

		return color;
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
	private Vector3d reflect(Vector3d v1, Vector3d normal)
	{
		Vector3d reflected = new Vector3d(v1);
		Vector3d adjVect = new Vector3d(normal);

		double adj = v1.dot(normal) * 2;
		adjVect.scale(adj);
		reflected.sub(adjVect);
		reflected.normalize();

		return reflected;
	}

	/**
	 * Create a shadow ray from the given intersection point towards the provided light
	 * source.
	 * 
	 * @param shadowRay
	 * @param distance
	 * @return ray
	 */
	private Ray makeShadowRay(Intersection inter, Light light)
	{
		// ******** Construct the shadow ray
		Pt interP = fixPoint(inter);
		Vec lightDir = new Vec(new Vector3d(light.getPosition()));
		lightDir.sub(interP);

		return new Ray(interP, lightDir, 0);
	}

	private Pt fixPoint(Intersection inter)
	{
		Pt newPt = new Pt(inter.p);
		Vec EPSILON = new Vec(inter.nn.x, inter.nn.y, inter.nn.z);
		EPSILON.negate();
		EPSILON.scale(FLOAT_CORRECTION);
		newPt.add(EPSILON);
		return newPt;
	}

	/**
	 * Create a reflection ray from the given intersection poiint towards the provided
	 * light source.
	 * 
	 * @param shadowRay
	 * @param distance
	 * @return ray
	 */
	private Ray makeReflectionRay(Intersection inter, Vector3d eye)
	{
		Vector3d ref = reflectVector(eye, inter.nn);
		ref.negate();
		Vec reflected = new Vec(ref);
		// ******** Find the shadow ray
		Pt reflectPoint = fixPoint(inter);

		return new Ray(reflectPoint, reflected, 0);
	}

	/**
	 * Check to see if the provided shadow Ray intersects an object in the scene before
	 * reaching the light.
	 * 
	 * @param shadowRay
	 * @param lightDist
	 * @return true if in shadow, false otherwise
	 */

	private boolean inShadow(Ray shadowRay, Light l)
	{
		Intersection inter = new Intersection();
		Vector3d lightPosition = new Vector3d(l.getPosition());

		if (!scene.getFirstIntersectedObject(shadowRay, inter))
		{
			return false;
		}

		Vector3d shadowVec = new Vector3d(inter.p);
		shadowVec.sub(shadowRay.position);
		lightPosition.sub(shadowRay.position);

		if (shadowVec.length() < lightPosition.length())
		{
			return true;
		}

		return false;
	}

	/**
	 * Calculates the specular color based on one light. NEED TO ADJUST FOR OTHER SCENE
	 * OBJECTS.
	 * 
	 * @param SceneObject
	 *            o
	 * @param Light
	 *            l
	 * @return RGB vector
	 */

	private Vector3d calculateSpecularColor(Intersection inter, Light light)
	{
		Vector3d lightColor = light.getColor(inter.p);
		Material mat = inter.shape.getMaterial();
		double newRColor = mat.specularColor.getX() * lightColor.getX();
		double newGColor = mat.specularColor.getY() * lightColor.getY();
		double newBColor = mat.specularColor.getZ() * lightColor.getZ();

		return new Vector3d(newRColor, newGColor, newBColor);
	}

	/**
	 * Calculates the color based on one light. NEED TO ADJUST FOR OTHER SCENE OBJECTS.
	 * 
	 * @param SceneObject
	 *            o
	 * @param Light
	 *            l
	 * @return RGB vector
	 */

	private Vector3d calculateDiffuseColor(Intersection inter, Light light)
	{
		Vector3d lightColor = light.getColor(inter.p);
		Material mat = inter.shape.getMaterial();
		double newRColor = mat.diffuseColor.getX() * lightColor.getX();
		double newGColor = mat.diffuseColor.getY() * lightColor.getY();
		double newBColor = mat.diffuseColor.getZ() * lightColor.getZ();

		return new Vector3d(newRColor, newGColor, newBColor);
	}

	// private Vector3d getReflectiveRefractiveLighting(Ray ray, SceneObject
	// intersectedObject,
	// Intersection intersection, Vector3d ambientIntensity, int currentLevel,
	// double currentRefraction)
	// {
	// double reflectivity = intersectedObject.getMaterial().reflectionIndex;
	// double startRefractiveIndex = intersectedObject.getMaterial().refractionIndex;//
	// intersection.startMaterial->getRefractiveIndex();
	// double endRefractiveIndex = intersectedObject.getMaterial().refractionIndex;
	// int reflectionsRemaining = intersection.ray.remainingReflections;
	//
	// /**
	// * Don't perform lighting if the object is not reflective or refractive or we have
	// * hit our recursion limit.
	// */
	// if (reflectivity == NOT_REFLECTIVE && endRefractiveIndex == NOT_REFRACTIVE
	// || reflectionsRemaining <= 0)
	// {
	// return new Vector3d(0, 0, 0);
	// }
	//
	// // Default to exclusively reflective values.
	// double reflectivePercentage = reflectivity;
	// double refractivePercentage = 0;
	//
	// // Refractive index overrides the reflective property.
	// if (endRefractiveIndex != NOT_REFRACTIVE)
	// {
	// reflectivePercentage = getReflectance(intersection.normal,
	// intersection.ray.direction,
	// startRefractiveIndex, endRefractiveIndex);
	// refractivePercentage = 1 - reflectivePercentage;
	// }
	//
	// // No ref{ra,le}ctive properties - bail early.
	// if (refractivePercentage <= 0 && reflectivePercentage <= 0)
	// {
	// return new Vector3d(0, 0, 0);
	// }
	//
	// Vector3d reflectiveColor = new Vector3d(0, 0, 0);
	// Vector3d refractiveColor = new Vector3d(0, 0, 0);
	//
	// if (reflectivePercentage > 0)
	// {
	// Vector3d reflected = reflect(intersection.ray.position, intersection.normal);
	// Ray reflectedRay = new Ray(intersection.point, intersection.ray.origin, reflected,
	// reflectionsRemaining - 1);
	// reflectiveColor.x = (reflectiveColor.x * reflectivePercentage);
	// reflectiveColor.y = (reflectiveColor.y * reflectivePercentage);
	// reflectiveColor.z = (reflectiveColor.z * reflectivePercentage);
	// if (getColor(reflectedRay, currentLevel + 1, reflectedRay.position,
	// reflectiveColor,
	// currentRefraction) != null)
	// {
	// Material material = intersectedObject.getMaterial();
	// Util.multiplyVectors(reflectiveColor, material.diffuseColor);
	// reflectiveColor.scale(material.reflectionIndex);
	// ambientIntensity.add(reflectiveColor);
	// }
	// }
	//
	// if (refractivePercentage > 0)
	// {
	// Vector3d refracted = refract(intersection.normal, intersection.ray.direction,
	// startRefractiveIndex, endRefractiveIndex);
	// Ray refractedRay = new Ray(intersection.point, intersection.ray.origin, refracted,
	// reflectionsRemaining - 1);// , 1, intersection.endMaterial);
	// // refractiveColor = castRay(refractedRay);// * refractivePercentage;
	// refractiveColor.x = (refractiveColor.x * refractivePercentage);
	// refractiveColor.y = (refractiveColor.y * refractivePercentage);
	// refractiveColor.z = (refractiveColor.z * refractivePercentage);
	// if (refractedRay != null)
	// {
	// // TODO
	// if (refractedRay.direction.dot(intersection.normal) > 0)
	// {
	// currentRefraction = intersectedObject.getMaterial().refractionIndex;
	// }
	// else
	// {
	// currentRefraction = 1;
	// }
	// Vector3d refractedColor = new Vector3d();
	// SceneObject instersectedObject2 = getColor(refractedRay, currentLevel + 1,
	// refractedRay.position, refractedColor, currentRefraction);
	// if (instersectedObject2 != null)
	// {
	//
	// Vector3d pointOfIntersection =
	// instersectedObject2.intersectsRay(refractedRay).point;
	// pointOfIntersection.sub(intersection.point);
	// double distancia = intersection.distance;
	//
	// Material material = intersectedObject.getMaterial();
	// Vector3d absorbance = new Vector3d(material.diffuseColor);
	// absorbance.scale(-0.15 * distancia);
	// Vector3d transparency = new Vector3d(Math.exp(absorbance.x),
	// Math.exp(absorbance.y), Math.exp(absorbance.z));
	//
	// Util.multiplyVectors(refractedColor, transparency);
	// refractedColor.scale(material.transparency);
	// ambientIntensity.add(refractedColor);
	// }
	// }
	//
	// }
	// refractiveColor.add(reflectiveColor);
	// return refractiveColor;
	// }

	/**
	 * 
	 * Construct a ray that exits that camera and passes through the pixel (i,j) of the
	 * image plane.
	 * 
	 * 
	 * @param i
	 *            Pixel row to traverse
	 * @param j
	 *            Pixel column to traverse
	 * @return A ray that leaves the camera and passes through the specified pixel
	 */

	public Ray constructRayThroughPixel(int i, int j)
	{
		double xDir = (j - imageSize.width / 2f);
		double yDir = (i - imageSize.height / 2f);
		double zDir = (double) (Math.min(imageSize.width, imageSize.height) / (2 * Math.tan(scene
				.getCamera().fieldOfView / 2)));
		Vector4d dir = new Vector4d(xDir, -yDir, -zDir, 1); // ******** why is image
															// ******** inverted?

		dir.normalize();
		Vector4d result = Util.MultiplyMatrixAndVector(scene.getCamera().rotationMatrix, dir);
		Vector3d direction = new Vector3d(result.x, result.y, result.z);
		direction.normalize();
		return new Ray(scene.getCamera().position, new Vec(direction), 0f);
	}

	double getReflectance(Vector3d normal, Vector3d incident, double n1, double n2)
	{
		double n = n1 / n2;
		double cosI = -normal.dot(incident);
		double sinT2 = n * n * (1.0 - cosI * cosI);

		if (sinT2 > 1.0)
		{
			// ******** Total Internal Reflection.
			return 1.0;
		}

		double cosT = Math.sqrt(1.0 - sinT2);
		double r0rth = (n1 * cosI - n2 * cosT) / (n1 * cosI + n2 * cosT);
		double rPar = (n2 * cosI - n1 * cosT) / (n2 * cosI + n1 * cosT);
		return (r0rth * r0rth + rPar * rPar) / 2.0;
	}

	Vector3d refractVector(Vector3d normal, Vector3d incident, double n1, double n2)
	{
		double n = n1 / n2;
		double cosI = -normal.dot(incident);
		double sinT2 = n * n * (1.0 - cosI * cosI);

		if (sinT2 > 1.0)
		{
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

	Vector3d reflectVector(Vector3d vector, Vector3d normal)
	{
		normal.x *= 2 * vector.dot(normal);
		normal.y *= 2 * vector.dot(normal);
		normal.z *= 2 * vector.dot(normal);
		normal.sub(vector);
		return normal;// ******** - vector;
	}

}
