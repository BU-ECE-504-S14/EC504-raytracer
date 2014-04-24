package scene;

import java.util.HashSet;

import javax.vecmath.Vector3d;

import objects.SceneObject;

/**
 * An abstract light object, which is defined by a position, color, radiosity value, and
 * attenuation.
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public interface Light
{

	/**
	 * Get the light's current radiosity value, which determines light diffusiveness.
	 * Higher numbers cause the light to spread more.
	 * 
	 * @return this Light's radiosity value.
	 */
	public double getRadio();

	/**
	 * Set the radiosity of this Light object, which determines light diffusiveness.
	 * 
	 * @param radio
	 *            is the desired radiosity value (>0) that this Light should be set to.
	 */
	public void setRadio(double radio);

	public Vector3d getPosition();
	
	public void setPosition(Vector3d position);
	
	public void setAttenuation(float[] attenuation);

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
	public Vector3d getColor(Vector3d point);
	
	public Vector3d getShadowColor(Vector3d point, HashSet<Intersection> hits);

}
