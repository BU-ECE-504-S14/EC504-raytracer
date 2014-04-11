package scene;

import java.io.Serializable;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import raytracer.Util;

/**
 * A simple point light, which casts light rays in all directions.
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class PointLight extends Light implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PointLight()
	{
	}

	public PointLight(PointLight light)
	{
		this.position.set(light.position);
		this.color.set(light.color);
		this.radiosity = light.radiosity;

		this.attenuation[0] = light.attenuation[0];
		this.attenuation[1] = light.attenuation[1];
		this.attenuation[2] = light.attenuation[2];
	}
}
