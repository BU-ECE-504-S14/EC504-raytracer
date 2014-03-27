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

public class Simple_RayTracer
{

	/** Maximum number of levels in the recursion of getColor */
	private static final int MAX_LEVELS = 10;

	/** Margin of error when comparing doubles */
	private static final double EPSILON_EQUALS = 0.000000000001;

	private static final double FLOAT_CORRECTION = 0.001;

	/** Desired scene to render */
	private Scene scene;

	/** Size of the image to generate */
	private Dimension imageSize;

	/** Antialiasing parameter */
	private int antialiasing = 1;

	/** Shadow parameter */
	private int shadow;

	private int counter;

	public int totalRays;
	public int currentRay;

	/**
	 * Create the new ray tracer with the given parameters. The camera is set up to the
	 * size of the image to generate so that we can construct the rays.
	 * 
	 * @param scene
	 *            Scene to render
	 * @param imageSize
	 *            Size of the image to generate
	 * @param antialiasing
	 *            Antialiasing parameters (may be null)
	 * @param shadow
	 *            Shadow parameters (may be null)
	 * @param outputImage TODO
	 */
	public Simple_RayTracer(Scene scene, Dimension imageSize, int antialiasing, int shadow, BufferedImage outputImage)
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

				/* create this ray through pixel (i,j) */
				Ray ray = constructRayThroughPixel(i, j);
				color.set(0, 0, 0);

				/* do ray trace */
				getColor(ray, 0, scene.getCamera().position, color, 1);

				/* set color into image at screen position (i,j) */
				image.setRGB(j, i,
						new Color((float) color.x, (float) color.y, (float) color.z).getRGB());
			}
		}

		return image;
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
	private SceneObject getColor(Ray ray, int currentLevel, Vector3d viewerPosition,
			Vector3d color, double currentRefraction)
	{

		Intersection intersection = new Intersection();
		SceneObject intersectedObject = scene.getFirstIntersectedObject(ray, intersection);
		if (intersectedObject == null)
		{
			color.set(new double[] { 0, 0, 0 });
			return null;
		}

		Material material = intersectedObject.getMaterial();
		Vector3d ptColor = new Vector3d(1, 1, 1);
		Vector3d lightColor = new Vector3d(0, 0, 0);
		;
		Vector3d totalLightColor = new Vector3d(0, 0, 0);
		boolean lit;

		double lightCount = 0;
		for (PointLight light : scene.getLights())
		{
			Ray shadowRay;
			Vector3d lightPosition, lightDirection;
			lit = false;

			/* create shadow ray */
			lightPosition = new Vector3d(light.getPosition());
			lightDirection = new Vector3d(light.getPosition());

			/*
			 * correct for floating point imprecision of object surface detail by moving
			 * origin of shadow ray an epsilon factor closer to light
			 */
			Vector3d EPSILON = new Vector3d(lightDirection.x, lightDirection.y, lightDirection.z);
			EPSILON.scale(FLOAT_CORRECTION);
			intersection.point.add(EPSILON);

			lightDirection.sub(intersection.point);
			shadowRay = new Ray(intersection.point, lightDirection);

			/* check to see if shadow ray intersects object (ie the point is in shadow) */
			Intersection lightIntersection = new Intersection();
			SceneObject shadowIntersectedObject = scene.getFirstIntersectedObject(shadowRay,
					lightIntersection);

			/* if shadow ray does not intersect another object make color appear */
			if (shadowIntersectedObject == null)
			{
				ptColor.set(material.diffuseColor);
				color.set(ptColor);
				return intersectedObject;
			}

		}

		color.set(new double[] { 0, 0, 0 });
		return intersectedObject;

	}

	/**
	 * Construct a ray that exits that camera and passes through the pixel (i,j) of the
	 * image plane.
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
		Vector4d dir = new Vector4d(xDir, -yDir, -zDir, 1); // why is image inverted?
		dir.normalize();
		Vector4d result = Util.MultiplyMatrixAndVector(scene.getCamera().rotationMatrix, dir);
		Vector3d direction = new Vector3d(result.x, result.y, result.z);
		direction.normalize();
		return new Ray(scene.getCamera().position, direction);
	}

}
