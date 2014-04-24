package scene;

import geometry.Ray;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import objects.SceneObject;
import raytracer.SimpleRayTracer;
import raytracer.Util;

/**
 * A simple point light, which casts light rays in all directions.
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class PointLight implements Light, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The position of this Light object, in Scene coordinates.
	 */
	protected Vector3d position = new Vector3d(0, 10, 0);
	/**
	 * The color of the light given off by this object.
	 */
	protected Vector3d color = new Vector3d(1, 1, 1);
	// private double fallConstant = 0.05;

	/**
	 * Determines the radiosity/diffusiveness of light given off by this object.
	 */
	protected double radiosity = 1;

	/**
	 * Determines the rate at which light intensity decreases with distance from the light
	 * source.
	 */
	protected float[] attenuation = new float[] { 1, 0, 0 };

	public PointLight()
	{
	}

	/**
	 * Copy constructor.
	 * 
	 * @param light
	 */
	public PointLight(PointLight light)
	{
		this.position.set(new Vector3d(light.position));
		this.color.set(new Vector3d(light.color));
		this.radiosity = light.radiosity;

		this.attenuation[0] = light.attenuation[0];
		this.attenuation[1] = light.attenuation[1];
		this.attenuation[2] = light.attenuation[2];
	}

	@Override
	public Vector3d getPosition()
	{
		return position;
	}

	public PointLight getCopy()
	{
		return new PointLight(this);
	}

	public void setColor(Vector3d color)
	{
		if (color != null)
			this.color = color;
	}

	@Override
	public void setPosition(Vector3d position)
	{
		this.position = position;
	}

	@Override
	public void setAttenuation(float[] attenuation)
	{
		this.attenuation = attenuation;
	}

	@Override
	public Vector3d getColor(Vector3d currentPosition)
	{
		double r = Math.sqrt(Math.pow(position.x - currentPosition.x, 2.0)
				+ Math.pow(position.y - currentPosition.y, 2.0)
				+ Math.pow(position.z - currentPosition.z, 2.0));
		double factor = 1 / Math.max(attenuation[0] + attenuation[1] * r + attenuation[2] * r * r,
				1);

		Vector3d result = new Vector3d(color);
		result.scale(factor);
		// return result;
		return new Vector3d(color);
	}

	@Override
	public Vector3d getShadowColor(Vector3d currentPosition, HashSet<Intersection> hits)
	{
		Vector3d baseColor = getColor(currentPosition);
		HashMap<SceneObject, Intersection> objs = new HashMap<SceneObject, Intersection>();

		for (Intersection i : hits)
		{
			SceneObject o = i.shape;
			Vector3d transColor = SimpleRayTracer.calculateDiffuseColor(i, this);
			transColor.negate();
			baseColor.sub(transColor);
			double aSq = 1 - o.getMaterial().alpha;
			baseColor.scale(aSq);
		}
		baseColor.clamp(0.0, 1.0);
		return baseColor;
	}

	/**
	 * Transforms the position of this Light based on the provided Matrix.
	 * 
	 * @param transformationMatrix
	 *            is the Matrix which should be used to transform the position of this
	 *            Light.
	 */
	public void transform(Matrix4d transformationMatrix)
	{
		Vector4d aux = new Vector4d(position.getX(), position.getY(), position.getZ(), 1);
		aux = Util.MultiplyMatrixAndVector(transformationMatrix, aux);
		position.set(aux.getX(), aux.getY(), aux.getZ());
	}

	@Override
	public double getRadio()
	{
		return radiosity;
	}

	@Override
	public void setRadio(double radio)
	{
		this.radiosity = radio;
	}

	@Override
	public String toString()
	{
		return "PointLight(position=" + position + ", color=" + color + ")";
	}
}
