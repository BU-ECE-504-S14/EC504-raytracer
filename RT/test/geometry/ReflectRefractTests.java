/**
 *
 */

package geometry;

import static org.junit.Assert.*;

import javax.vecmath.Vector3d;

import objects.Sphere;

import org.junit.Test;

import raytracer.Util;
import scene.Intersection;

/**
@author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist */
public class ReflectRefractTests
{

	@Test
	public void reflectRay()
	{
		Pt ori = new Pt(new Vector3d(0, 0, 0));
		Pt tar = new Pt(new Vector3d(1, 1, 0));

		Vec nor = new Vec(new Vector3d(0, -1, 0));
		Vec inc = new Vec(new Vector3d(1, 1, 0));

		Vector3d reflectVector = Ray.reflectVector(inc, nor);

		assertEquals("Reflect vector calculated wrong!", true,
				Util.checkEqual(reflectVector, new Vector3d(1, -1, 0), .001));

		Vec out = new Vec(new Vector3d(1, -1, 0));
		out.normalize();

		Intersection i = new Intersection();
		nor.normalize();
		i.nn = new Vec(nor);
		i.p = new Pt(tar);

		Ray in = new Ray(ori, inc);
		Ray ref = Ray.reflectRay(in, i, .00001);
		assertEquals("Expected: " + out + ", but vector was: " + ref.direction, true,
				Util.checkEqual(ref.direction, out, .001));
		assertEquals(true, Util.checkEqual(ref.position, i.p, .001));
	}

	@Test
	public void refractRay()
	{
		Vector3d v1 = new Vector3d(Math.sqrt(3)/2, -.5, 0);
		Vector3d norm1 = new Vector3d(-1, 0, 0);
		double n1 = 1;
		double n2 = .7071;
		Vector3d vOut = new Vector3d(1, -1, 0);
		
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));

		Intersection i = new Intersection();
		i.nn = new Vec(norm1);
		i.p = new Pt(new Vector3d(0,0,0));
		i.shape = new Sphere();
		i.shape.getMaterial().refractionIndex = n2;
		
		Pt inPoint = new Pt(new Vector3d(-Math.sqrt(3)/2, .5, 0));
		Vec inVec = new Vec(v1);

		Ray in = new Ray(inPoint, inVec);
		Ray ref = Ray.refractRay(in, i, n1, .00001);
		assertEquals("Expected: " + vOut + ", but vector was: " + ref.direction, true,
				Util.checkEqual(ref.direction, vOut, .001));
		assertEquals(true, Util.checkEqual(ref.position, i.p, .001));
	}

	public boolean testRefraction(Vector3d v1, Vector3d v2, double n1, double n2, Vector3d result)
	{
		v1.normalize();
		v2.normalize();
		Vector3d refract = Ray.refractVector(v1, v2, n1, n2);
		result.normalize();

		if (Util.checkEqual(refract, result, .001))
		{
			return true;
		}
		else
			return false;
	}
	
	@Test
	public void refract7()
	{
		Vector3d v1 = new Vector3d(1, -1, 0);
		Vector3d norm1 = new Vector3d(0, 1, 0);
		double n1 = 1;
		double n2 = 1;
		Vector3d vOut = new Vector3d(1, -1, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract8()
	{
		Vector3d v1 = new Vector3d(1, -1, 0);
		Vector3d norm1 = new Vector3d(0, -1, 0);
		double n1 = 1;
		double n2 = 1;
		Vector3d vOut = new Vector3d(1, -1, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract1()
	{
		Vector3d v1 = new Vector3d(1, -1, 0);
		Vector3d norm1 = new Vector3d(0, 1, 0);
		double n1 = 1;
		double n2 = 1.4142;
		Vector3d vOut = new Vector3d(.5, -Math.sqrt(3) / 2, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract2()
	{
		Vector3d v1 = new Vector3d(1, -1, 0);
		Vector3d norm1 = new Vector3d(0, -1, 0);
		double n1 = 1;
		double n2 = 1.4142;
		Vector3d vOut = new Vector3d(.5, -Math.sqrt(3) / 2, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}

	@Test
	public void refract5()
	{
		Vector3d v1 = new Vector3d(0, -1, 0);
		Vector3d norm1 = new Vector3d(0, 1, 0);
		double n1 = 1;
		double n2 = 1.4142;
		Vector3d vOut = new Vector3d(0, -1, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract6()
	{
		Vector3d v1 = new Vector3d(0, -1, 0);
		Vector3d norm1 = new Vector3d(0, -1, 0);
		double n1 = 1;
		double n2 = 1.4142;
		Vector3d vOut = new Vector3d(0, -1, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract3()
	{
		Vector3d v1 = new Vector3d(Math.sqrt(3)/2, -.5, 0);
		Vector3d norm1 = new Vector3d(0, 1, 0);
		double n1 = 1;
		double n2 = 1.732;
		Vector3d vOut = new Vector3d(.5, -Math.sqrt(3) / 2, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract4()
	{
		Vector3d v1 = new Vector3d(Math.sqrt(3)/2, -.5, 0);
		Vector3d norm1 = new Vector3d(0, -1, 0);
		double n1 = 1;
		double n2 = 1.732;
		Vector3d vOut = new Vector3d(.5, -Math.sqrt(3) / 2, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract9()
	{
		Vector3d v1 = new Vector3d(.5, -Math.sqrt(3)/2, 0);
		Vector3d norm1 = new Vector3d(0, 1, 0);
		double n1 = 1;
		double n2 = .7071;
		Vector3d vOut = new Vector3d(1, -1, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract10()
	{
		Vector3d v1 = new Vector3d(.5, -Math.sqrt(3)/2, 0);
		Vector3d norm1 = new Vector3d(0, -1, 0);
		double n1 = 1;
		double n2 = .7071;
		Vector3d vOut = new Vector3d(1, -1, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract11()
	{
		Vector3d v1 = new Vector3d(.5, -Math.sqrt(3)/2, 0);
		Vector3d norm1 = new Vector3d(0, 1, 0);
		double n1 = 1;
		double n2 = .5774;
		Vector3d vOut = new Vector3d(Math.sqrt(3)/2, -.5, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract12()
	{
		Vector3d v1 = new Vector3d(.5, -Math.sqrt(3)/2, 0);
		Vector3d norm1 = new Vector3d(0, -1, 0);
		double n1 = 1;
		double n2 = .5774;
		Vector3d vOut = new Vector3d(Math.sqrt(3)/2, -.5, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}
	
	@Test
	public void refract13()
	{
		Vector3d v1 = new Vector3d(Math.sqrt(3)/2, -.5, 0);
		Vector3d norm1 = new Vector3d(-1, 0, 0);
		double n1 = 1;
		double n2 = .7071;
		Vector3d vOut = new Vector3d(1, -1, 0);
		assertEquals(true, testRefraction(v1, norm1, n1, n2, vOut));
	}

	@Test
	public void checkEqual()
	{
		Vector3d inc = new Vector3d(.5, -.5, 0);

		Vector3d norm = new Vector3d(.5, -.5, 0);

		Vector3d not = new Vector3d(0, 0, 0);
		assertEquals(true, Util.checkEqual(inc, norm, .001));
		assertEquals(false, Util.checkEqual(inc, not, .001));
	}

	@Test
	public void reflect()
	{
		Vector3d inc = new Vector3d(.707, -.707, 0);
		Vector3d inc2 = new Vector3d(.707, .707, 0);
		Vector3d norm = new Vector3d(0, 1, 0);
		Vector3d norm2 = new Vector3d(0, -1, 0);

		Vector3d reflect = Ray.reflectVector(inc, norm);
		Vector3d reflect2 = Ray.reflectVector(inc, norm2);
		Vector3d reflect3 = Ray.reflectVector(inc2, norm);
		Vector3d reflect4 = Ray.reflectVector(inc2, norm2);

		assertEquals("Expected (.5,.5,0), got: " + reflect.toString(), true,
				Util.checkEqual(reflect, new Vector3d(.707, .707, 0), .001));
		assertEquals("Expected2 (.5,.5,0), got: " + reflect.toString(), true,
				Util.checkEqual(reflect2, new Vector3d(.707, .707, 0), .001));
		assertEquals("Expected (.5,-.5,0), got: " + reflect.toString(), true,
				Util.checkEqual(reflect3, new Vector3d(.707, -.707, 0), .001));
		assertEquals("Expected2 (.5,-.5,0), got: " + reflect.toString(), true,
				Util.checkEqual(reflect4, new Vector3d(.707, -.707, 0), .001));
	}

}
