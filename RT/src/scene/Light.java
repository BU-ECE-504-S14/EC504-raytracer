package scene;

import java.io.Serializable;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import raytracer.Util;

/**
 * An abstract light object, which is defined by a position, color, radiosity value, and
 * attenuation.
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public abstract class Light implements Serializable
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

	public Light()
	{
	}

	/**
	 * Copy constructor.
	 * 
	 * @param light
	 */
	public Light(Light light)
	{
		this.position.set(light.position);
		this.color.set(light.color);
		this.radiosity = light.radiosity;

		this.attenuation[0] = light.attenuation[0];
		this.attenuation[1] = light.attenuation[1];
		this.attenuation[2] = light.attenuation[2];
	}

	public Vector3d getPosition()
	{
		return position;
	}

	public void setColor(Vector3d color)
	{
		if (color != null)
			this.color = color;
	}

	public void setPosition(Vector3d position)
	{
		this.position = position;
	}

	public void setAttenuation(float[] attenuation)
	{
		this.attenuation = attenuation;
	}

	/**
	 * Get the color of the light given off by this object once it has reached the
	 * specified position, as affected by attentuation.
	 * 
	 * @param currentPosition
	 *            is the Scene position to which the light has traveled since leaving this
	 *            object. Usually, this is the point of intersection with another Scene
	 *            object.
	 * @return a Vector3d representing the color of the light at the given position.
	 */
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

	/**
	 * Get the light's current radiosity value, which determines light diffusiveness.
	 * Higher numbers cause the light to spread more.
	 * 
	 * @return this Light's radiosity value.
	 */
	public double getRadio()
	{
		return radiosity;
	}

	/**
	 * Set the radiosity of this Light object, which determines light diffusiveness.
	 * @param radio is the desired radiosity value (>0) that this Light should be set to.
	 */
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
