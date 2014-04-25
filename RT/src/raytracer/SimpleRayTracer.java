package raytracer;

import geometry.Pt;
import geometry.Ray;
import geometry.Vec;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import accelerators.Octnode;
import accelerators.Octree;
import objects.Material;
import objects.SceneObject;
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
	private BufferedImage outputImage;

	/** Margin of error when comparing doubles */
	private static final double FLOAT_CORRECTION = 0.001;

	public int rayCounter;

	/**
	 * Perform the rendering of the provided scene in the constructor
	 * 
	 * @param showProgress
	 *            Flag indicating whether or not to show progress on the screen
	 * @return The generated image
	 */
	public BufferedImage render(Scene s) throws Exception
	{

		int height = s.settings.getHEIGHT();
		int width = s.settings.getWIDTH();
		double startTime = System.currentTimeMillis();

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Vector3d color = new Vector3d();
		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				if ((i * width + j) % (width * height / 80) == 0)
					System.out.print('*');
				color.set(0, 0, 0);

				if (s.settings.getANTIALIASING() <= 1)
				{
					Ray ray = constructRayThroughPixel(i, j, s);/*
																 * create this ray through
																 * pixel (i,j)
																 */
					/* do ray trace */
					color.set(getColor(ray, 0, 0, null, s));
				}
				else
				{
					List<Ray> rays = constructRaysThroughPixel(j, i, s);
					Vector3d newColor = new Vector3d(0, 0, 0);
					for (int ii = 0; ii < rays.size(); ii++)
					{
						newColor.add(getColor(rays.get(ii), 0, 0, null, s));
					}
					newColor.scale(1.0 / rays.size());
					color.set(newColor);
				}

				/* set color into image at screen position (i,j) */
				image.setRGB(j, i,
						new Color((float) color.x, (float) color.y, (float) color.z).getRGB());

			}
		}
		System.out.println("Total time: " + (System.currentTimeMillis() - startTime));

		return image;
	}

	/**
	 * Perform the rendering of the provided scene in the constructor
	 * 
	 * @param showProgress
	 *            Flag indicating whether or not to show progress on the screen
	 * @return The generated image
	 */
	public BufferedImage renderThreads(Scene s) throws Exception
	{
		int height = s.settings.getHEIGHT();
		int width = s.settings.getWIDTH();
		double start = System.currentTimeMillis();
		rayCounter = 0;

		double startTime = System.currentTimeMillis();
		int totalRays = height * width;
		int currentRay = 0;

		final int NUM_THREADS;
		if (s.settings.isMULTITHREADING())
		{
			NUM_THREADS = Runtime.getRuntime().availableProcessors() + 1;
		}
		else
		{
			NUM_THREADS = 1;
		}
		if (s.settings.isVERBOSE())
		{
			System.out.println("Using " + NUM_THREADS + " threads.");
		}

		// final int NUM_THREADS = 1;
		final ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		Set<Future<ColorPoint>> futureSet = new HashSet<Future<ColorPoint>>();
		Set<ColorPixel> queue = new HashSet<ColorPixel>();

		outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		double raysPerPixel = Math.pow(s.settings.getANTIALIASING(), 2);

		// Make all of the ColorPixel callables

		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				if (s.settings.getANTIALIASING() > 1)
				{
					List<Ray> rays = constructRaysThroughPixel(j, i, s);
					for (Ray r : rays)
					{
						queue.add(new ColorPixel(i, j, r, s, this, raysPerPixel));
					}
				}

				else
				{
					Ray r = constructRayThroughPixel(j, i, s);
					queue.add(new ColorPixel(i, j, r, s, this));

				}
			}
		}

		int max = queue.size();
		int step = max / 100;

		for (ColorPixel cp : queue)
		{
			Future<ColorPoint> future = executor.submit(cp);
			futureSet.add(future);
		}

		executor.shutdown();
		while (!executor.isTerminated())
		{
			if (s.settings.isVERBOSE())
			{
				double percent = (double) (rayCounter / (double) max) * 100;
				System.out.println("Computed " + rayCounter + " out of " + max + " primary rays. ("
						+ percent + "%)");

			}
			Thread.sleep(1000);
		}

		Vector3d[][] colors = new Vector3d[width][height];

		for (Future<ColorPoint> future : futureSet)
		{

			ColorPoint c = future.get();
			Vector3d newColor = colors[c.x][c.y];
			if (newColor == null)
			{
				colors[c.x][c.y] = (Vector3d) c;
			}
			else
			{
				newColor.add(c);
				newColor.clamp(0.0, 1.0);
			}
		}

		for (int xx = 0; xx < colors.length; xx++)
		{
			for (int yy = 0; yy < colors[0].length; yy++)
			{
				Vector3d c = colors[xx][yy];
				if (c == null)
				{
					outputImage.setRGB(xx, yy, new Color(0, 0, 0).getRGB());
				}
				else
				{
					outputImage.setRGB(xx, yy, new Color((float) c.getX(), (float) c.getY(),
							(float) c.getZ()).getRGB());
				}
			}
		}

		// executor.awaitTermination(100000, TimeUnit.SECONDS);
		double end = System.currentTimeMillis();
		System.out.println(start - end + "milliseconds");
		System.gc();

		System.out.println("Total time: " + (System.currentTimeMillis() - startTime));
		return outputImage;
	}

	/**
	 * Calculates the diffuse component of the Phong shading model for the given
	 * intersection, light, and eye position.
	 */
	private static Vector3d getDiffuseComponent(Intersection inter, Light light)
	{
		// ******** If not in shadow, calculate diffuse and specular lighting

		// ******** Calculate diffuse light using Lambertian shading
		// ******** Equation: L_d = k_d * I * max(0, n dot l)
		// ******** k_d: diffuse parameter (implicitly 1 here)
		// ******** I: illumination of intensity of the light (its radiosity)
		// ******** n: normal vector to the object at the point of intersection
		// ******** l: the light ray emitted from the light that hits the point
		// of
		// ******** intersection

		Vector3d diffuse = calculateDiffuseColor(inter, light);

		Vector3d lightVect = new Vector3d(light.getPosition());

		lightVect.sub(inter.p);
		lightVect.normalize();
		lightVect.negate();

		double dotProd = inter.nn.dot(lightVect);

		if (dotProd < 0)
		{
			lightVect.negate();
		}

		dotProd = inter.nn.dot(lightVect);

		diffuse.scale(dotProd);
		diffuse.scale(inter.shape.getMaterial().diffuseIndex);
		diffuse.scale(light.getRadio());
		diffuse.clamp(0.0, 1.0);

		return diffuse;
	}

	/**
	 * Calculates the ambient component of the Phong shading model for the given
	 * intersection, light, and eye position.
	 * 
	 * @param inter
	 *            is an Intersection describing the point where the ray hit an object.
	 * @param light
	 *            is the Light object that should be used in color calculations.
	 */
	private static Vector3d getAmbientComponent(Intersection inter, Light light)
	{
		Vector3d ambient = calculateDiffuseColor(inter, light);
		ambient.scale(inter.shape.getMaterial().ambientIntensity);
		ambient.clamp(0.0, 1.0);

		return ambient;
	}

	/**
	 * Calculates the specular component of the Phong shading model for the given
	 * intersection, light, and eye position.
	 */
	private static Vector3d getSpecularComponent(Intersection inter, Light light, Vector3d eye)
	{
		// ******** Calculate specular light. View direction affects the
		// intensity of
		// ******** light contribution.
		// ******** Equation: L_s = k_s * I * max(0, r dot v)^(k_e)
		// ******** k_s: specular parameter (specular index)
		// ******** I: illumination intensity of the light (radiosity)
		// ******** n: normal vector to the object at the point of intersection
		// ******** r: mirror reflection of the light ray hitting the
		// intersection
		// ******** point (reflection)
		// ******** v: view ray (eyedirection; ray emanating from the camera and
		// ******** hitting the object)
		// ******** k_e: shininess parameter that controls the size of the
		// specular
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
		reflection = Ray.reflectVector(lightVect, inter.nn);
		double dotProd = eye.dot(reflection);

		if (dotProd > 0)
		{
			double specAmt = material.specularIndex
					* Math.pow(dotProd, (material.shininess * 128.0));

			// ******** Use the attenuated light intensity at intersection point
			// and
			// ******** multiply by the calculated specular amount

			specular = calculateSpecularColor(inter, light);

			specular.scale(specAmt);
		}
		specular.clamp(0.0, 1.0);
		return specular;
	}

	/**
	 * Calculates the color for the given intersection and light based on the Phong
	 * shading model.
	 * 
	 * @throws Exception
	 */
	private static Vector3d getPhongColor(Ray ray, Intersection inter, Light light,
			int currentReflection, int currentRefraction, Scene s) throws Exception
	{
		int shadowType = s.settings.getSHADOW_TYPE();
		// ******** Color components for each light
		Vector3d ambient = new Vector3d(0, 0, 0);
		Vector3d diffuse = new Vector3d(0, 0, 0);
		Vector3d specular = new Vector3d(0, 0, 0);

		// ******** Calculate the ambient component
		ambient = getAmbientComponent(inter, light);

		// ******** Check to see whether in shadow, for diffuse lighting

		// Soft shadows:

		List<Intersection> shadowHits = new ArrayList<Intersection>();

		Ray chkRay = Ray.makeShadowRay(inter, light, FLOAT_CORRECTION);
		double alpha = inter.shape.getMaterial().alpha;

		if (shadowType == 2)
		{
			if (inShadow(chkRay, light, s)

			&& light.getSoftShadowOffset() > 0d)
			{
				Vector3d newDiffuse = new Vector3d(0, 0, 0);
				Vector3d newSpecular = new Vector3d(0, 0, 0);
				List<Ray> shadowRays = Ray.makeShadowRays(inter, light, FLOAT_CORRECTION,
						light.getSoftShadowOffset());

				for (Ray r : shadowRays)
				{
					if (!inShadow(r, light, s))
					{
						// ******** Calculate the diffuse component
						Vector3d sDiffuse = getDiffuseComponent(inter, light);

						Vector3d eyeDirection = new Vector3d(r.position);
						Vector3d sSpecular = getSpecularComponent(inter, light, eyeDirection);

						newDiffuse.clamp(0d, 1d);
						newSpecular.clamp(0d, 1d);

						newDiffuse.add(sDiffuse);
						newSpecular.add(sSpecular);
					}
				}
				double divisor = 1d / 5d;

				newDiffuse.scale(divisor);
				newSpecular.scale(divisor);
				diffuse = newDiffuse;
				specular = newSpecular;
			}
			else
			{
				Ray shadowRay = Ray.makeShadowRay(inter, light, FLOAT_CORRECTION);
				if (!inShadow(shadowRay, light, s))
				{
					diffuse = getDiffuseComponent(inter, light);
					Vector3d eyeDirection = new Vector3d(shadowRay.position);
					specular = getSpecularComponent(inter, light, eyeDirection);
				}
			}
		}

		else if (shadowType == 3)
		{

			Ray shadowRay = Ray.makeShadowRay(inter, light, FLOAT_CORRECTION);

			// ******** if object is in shadow for this light, only add ambient
			// color
			// ******** if (scene.getFirstIntersectedObject(shadowRay, new
			// Intersection()))
			ArrayList<SceneObject> shadowObjects = new ArrayList<SceneObject>();
			if (!inShadow(shadowRay, light, shadowHits, s))
			{
				if (shadowHits.size() > 0)
				{
					/*
					 * // ******** Calculate the diffuse component diffuse =
					 * getDiffuseShadowComponent(inter, light, shadowHits); Vector3d
					 * eyeDirection = new Vector3d(ray.position); specular =
					 * getSpecularShadowComponent(inter, light, eyeDirection, shadowHits);
					 */

					// Color of the object at the end of this shadow ray
					Vector3d shadowColor = getColor(shadowRay, currentReflection + 1,
							currentRefraction + 1, inter.shape, s);
					double shadowAlpha = shadowHits.get(0).shape.getMaterial().alpha;

					Vector3d alphaMask = new Vector3d(shadowAlpha, shadowAlpha, shadowAlpha);
					shadowColor.scale(shadowAlpha / 2);
					alphaMask.sub(shadowColor);

					diffuse = getDiffuseComponent(inter, light);
					Vector3d eyeDirection = new Vector3d(shadowRay.position);
					specular = getSpecularComponent(inter, light, eyeDirection);

					diffuse.clamp(0d, 1d);
					specular.clamp(0d, 1d);

					ambient.scale(1 - inter.shape.getMaterial().reflectionIndex);
					ambient.scale(alpha);
					diffuse.scale(1 - inter.shape.getMaterial().reflectionIndex);
					diffuse.scale(alpha);
					specular.scale(1 - inter.shape.getMaterial().reflectionIndex);

					Vector3d endColor = addColorComponents(ambient, diffuse, specular);
					endColor.sub(alphaMask);

					endColor.scale(alpha);
					return endColor;

				}
				else
				{
					diffuse = getDiffuseComponent(inter, light);
					Vector3d eyeDirection = new Vector3d(shadowRay.position);
					specular = getSpecularComponent(inter, light, eyeDirection);

					diffuse.clamp(0d, 1d);
					specular.clamp(0d, 1d);

					// specular.scale(1 -
					// inter.shape.getMaterial().reflectionIndex);
				}
			}
		}
		else
		{
			Ray shadowRay = Ray.makeShadowRay(inter, light, FLOAT_CORRECTION);
			if (!inShadow(shadowRay, light, s))
			{
				diffuse = getDiffuseComponent(inter, light);
				Vector3d eyeDirection = new Vector3d(shadowRay.position);
				specular = getSpecularComponent(inter, light, eyeDirection);
			}
		}
		ambient.scale(1 - inter.shape.getMaterial().reflectionIndex);
		ambient.scale(alpha);
		diffuse.scale(1 - inter.shape.getMaterial().reflectionIndex);
		diffuse.scale(alpha);
		specular.scale(1 - inter.shape.getMaterial().reflectionIndex);
		Vector3d endColor = addColorComponents(ambient, diffuse, specular);
		return endColor;

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
	 * @param currentReflection
	 *            , int currentRefraction Current level of recursion (0 at invocation)
	 *            (not used yet)
	 * @param viewerPosition
	 *            Position of the observer. In the first invocation, it is the origin (not
	 *            used yet)
	 * @param color
	 *            Output parameter with the color found in the pixel
	 * @param currentRefraction
	 *            Refractive index of the current environment (not used yet)
	 * @return The first intersected object (may be null)
	 * @throws Exception
	 */
	private static Vector3d getColor(Ray ray, int currentReflection, int currentRefraction,
			SceneObject lastObject, Scene s) throws Exception
	{

		Vector3d color = new Vector3d(0, 0, 0);
		Intersection inter = new Intersection();

		if (!s.getFirstIntersectedObject(ray, inter))
		{
			return color;

		}

		SceneObject nextObject = inter.shape;

		// ******** Cycle through all of the lights, adding the sum of the
		// diffuse,
		// ambient, and
		// ******** specular components to the color at this pixel
		for (Light light : s.getLights())
		{
			if (s.settings.isPHONG())
			{
				color.add(getPhongColor(ray, inter, light, currentReflection, currentRefraction, s));
			}
			else
			{
				color.add(calculateDiffuseColor(inter, light));
			}
		}

		if (!s.settings.isPHONG())
		{
			return color;
		}
		Material mat = inter.shape.getMaterial();
		double reflectionIndex = mat.reflectionIndex;
		double refractionIndex = mat.refractionIndex;
		double alpha = mat.alpha;

		/**
		 * If the reflectivity of the object is greater than 0, generate a reflection ray
		 * and get the color of the object it hits, recursively.
		 */

		{
			if (currentReflection < s.settings.getREFLECTION())
			{
				if (reflectionIndex > 0)
				{
					Ray reflection = Ray.reflectRay(ray, inter, FLOAT_CORRECTION);
					Vector3d refColor = getColor(reflection, currentReflection + 1,
							currentRefraction, inter.shape, s);
					refColor.scale(reflectionIndex);
					refColor.clamp(0.0, 1.0);
					color.add(refColor);
				}
			}
		}
		if (s.settings.isTRANSPARENCY())
		{

			if (alpha < 1)
			{
				if (currentRefraction < s.settings.getREFRACTION())
				{
					Ray refraction;
					if (refractionIndex == -1
							|| (lastObject != null && lastObject.getMaterial().refractionIndex == -1))
					{
						refraction = new Ray(Pt.fixPointIn(inter, FLOAT_CORRECTION), ray.direction);
					}
					else
					{
						double lastRefIndex = 1.0;
						if (lastObject != null)
						{
							lastRefIndex = lastObject.getMaterial().refractionIndex;
						}
						refraction = Ray.refractRay(ray, inter, lastRefIndex, FLOAT_CORRECTION);
					}
					Vector3d refractColor = getColor(refraction, currentReflection,
							currentRefraction + 1, nextObject, s);
					refractColor.clamp(0.0, 1.0);
					refractColor.scale(1 - alpha);
					refractColor.scale(1 - mat.reflectionIndex);
					refractColor.clamp(0.0, 1.0);
					color.add(refractColor);
				}
			}
		}

		color.clamp(0.0, 1.0);

		return color;
	}

	/**
	 * Check to see if the provided shadow Ray intersects an object in the scene before
	 * reaching the light.
	 * 
	 * @param shadowRay
	 * @param lightDist
	 * @return true if in shadow, false otherwise
	 * @throws Exception
	 */

	private static boolean inShadow(Ray shadowRay, Light l, List<Intersection> hitObjects, Scene s)
			throws Exception
	{
		Intersection inter = new Intersection();
		Vector3d lightPosition = new Vector3d(l.getPosition());

		if (!s.getFirstIntersectedObject(shadowRay, inter))
		{
			return false;
		}

		Vector3d shadowVec = new Vector3d(inter.p);
		shadowVec.sub(shadowRay.position);
		lightPosition.sub(shadowRay.position);

		if (shadowVec.length() < lightPosition.length())
		{
			if (inter.shape.getMaterial().alpha == 1)
			{
				return true;
			}
			else
			{
				hitObjects.add(inter);
				Ray nextShadow = Ray.makeShadowRay(inter, l, FLOAT_CORRECTION);
				return inShadow(nextShadow, l, hitObjects, s);
			}
		}

		return false;
	}

	/**
	 * Check to see if the provided shadow Ray intersects an object in the scene before
	 * reaching the light.
	 * 
	 * @param shadowRay
	 * @param lightDist
	 * @return true if in shadow, false otherwise
	 * @throws Exception
	 */

	private static boolean inShadow(Ray shadowRay, Light l, Scene s) throws Exception
	{
		Intersection inter = new Intersection();
		Vector3d lightPosition = new Vector3d(l.getPosition());

		if (!s.getFirstIntersectedObject(shadowRay, inter))
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

	private static Vector3d calculateSpecularColor(Intersection inter, Light light)
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

	public static Vector3d calculateDiffuseColor(Intersection inter, Light light)
	{
		Vector3d lightColor = light.getColor(inter.p);
		Material mat = inter.shape.getMaterial();
		double newRColor = mat.diffuseColor.getX() * lightColor.getX();
		double newGColor = mat.diffuseColor.getY() * lightColor.getY();
		double newBColor = mat.diffuseColor.getZ() * lightColor.getZ();

		return new Vector3d(newRColor, newGColor, newBColor);
	}

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

	public static Ray constructRayThroughPixel(double i, double j, Scene s)
	{
		int width = s.settings.getWIDTH();
		int height = s.settings.getHEIGHT();
		double xDir = (j - width / 2f);
		double yDir = (i - height / 2f);
		double zDir = Math.min(width, height) / (2 * Math.tan(s.getCamera().fieldOfView / 2));
		Vector4d dir = new Vector4d(xDir, -yDir, -zDir, 1); // ******** why is
															// image
															// ********
															// inverted?

		dir.normalize();
		Vector4d result = Util.MultiplyMatrixAndVector(s.getCamera().rotationMatrix, dir);
		Vector3d direction = new Vector3d(result.x, result.y, result.z);
		direction.normalize();
		return new Ray(s.getCamera().getPosition(), new Vec(direction), 0f);
	}

	public static List<Ray> constructRaysThroughPixel(double i, double j, Scene s)
	{
		int antiAlias = s.settings.getANTIALIASING();
		Random rand = new Random();
		rand.setSeed(0);
		rand.nextDouble();
		ArrayList<Ray> rays = new ArrayList<Ray>();
		// 1 pixel divided by the number of aa samples in i and j directions
		double offsetAmount = 1.0 / antiAlias;

		double[] js = new double[antiAlias];
		double[] is = new double[antiAlias];

		for (int ii = 0; ii < is.length; ii++)
		{
			double offset = rand.nextDouble() * offsetAmount;
			js[ii] = j + ii * offsetAmount + offset;
			offset = rand.nextDouble() * offsetAmount;
			is[ii] = i + ii * offsetAmount + offset;
		}

		for (int ii = 0; ii < is.length; ii++)
		{
			for (int jj = 0; jj < js.length; jj++)
			{
				rays.add(constructRayThroughPixel(is[ii], js[jj], s));
			}
		}
		return rays;
	}

	private class ColorPoint extends Vector3d
	{
		public int x;
		public int y;

		public ColorPoint(Vector3d vec, int xPos, int yPos)
		{
			super(vec);
			x = xPos;
			y = yPos;
		}
	}

	private class ColorPixel implements Callable
	{
		int x;
		int y;
		Scene pixelScene;
		Octnode node = null;
		Ray ray = null;
		List<Ray> rays = null;
		Vector3d color = new Vector3d(0, 0, 0);
		double divisor = 1;
		SimpleRayTracer myTracer;

		public ColorPixel(int i, int j, Ray r, Scene s, SimpleRayTracer rt)
		{
			myTracer = rt;
			pixelScene = s;
			x = i;
			y = j;
			ray = r;
		}

		public void setScene(Scene s)
		{
			pixelScene = s;
		}

		public ColorPixel(int i, int j, Ray r, Scene s, SimpleRayTracer rt, double totalRays)
		{
			myTracer = rt;
			pixelScene = s;
			x = i;
			y = j;
			ray = r;
			divisor = 1.0 / totalRays;
		}

		@Override
		public Vector3d call()
		{

			try
			{
				color = getColor(ray, 0, 0, null, this.pixelScene);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			color.scale(divisor);
			myTracer.rayCounter++;
			return new ColorPoint(color, x, y);
		}
	}
}
