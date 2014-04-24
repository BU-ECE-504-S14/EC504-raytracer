package scene;

import java.io.Serializable;

import javax.vecmath.Vector3d;
import objects.Sphere;

/**
 * A light shaped like a sphere. Allows ray intersections.
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class SphereLight extends Sphere implements Light, Serializable
{

	Sphere mySphere;

	public SphereLight(SphereLight s)
	{
		this.mySphere = new Sphere(s.mySphere);
	}

	@Override
	public Vector3d getPosition()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector3d getColor(Vector3d point)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * A sphere the represents the size and shape of this light. private Sphere mySphere;
	 * /**
	 * 
	 */

	@Override
	public SphereLight getCopy()
	{
		return new SphereLight(this);
	}

	@Override
	public double getRadio()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRadio(double radio)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPosition(Vector3d position)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setAttenuation(float[] attenuation)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setSoftShadowOffset(double SS) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getSoftShadowOffset() {
		// TODO Auto-generated method stub
		return 0;
	}
}
