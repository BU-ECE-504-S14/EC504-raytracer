package geometry;


import javax.vecmath.Vector3d;

import scene.Intersection;
import scene.Light;

/**
 * Ray class. Contains the origin position of the ray and its direction (a standard
 * vector)
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class Ray
{

	/** The origin point of the ray */
	public Pt position;

	/** Direction of the ray (vector is normalized) */
	public Vec direction;

	/** parametric information for ray **/
	public float maxt; // current farthest distance from origin (parametric)
	public float mint; // base position on parametric line (any point less than rMin is
						// behind ray)
	public int depth; // numbers of times ray has been reflected/refracted

	public Ray(Pt origin, Vec dir)
	{
		this.position = new Pt(origin);
		this.direction = new Vec(dir);
		this.direction.normalize();
		maxt = Float.POSITIVE_INFINITY;
		this.mint = 0;
		depth = 0;
	}

	public Ray(Pt origin, Vec dir, float mint)
	{
		this.position = new Pt(origin);
		this.direction = new Vec(dir);
		this.direction.normalize();
		maxt = Float.POSITIVE_INFINITY;
		this.mint = mint;
		depth = 0;
	}

	public Ray(Pt origin, Vec dir, float mint, float maxt)
	{
		this.position = new Pt(origin);
		this.direction = new Vec(dir);
		this.direction.normalize();
		this.maxt = maxt;
		this.mint = mint;
		depth = 0;
	}

	public Ray(Pt origin, Vec dir, Ray r, float mint)
	{
		this.position = new Pt(origin);
		this.direction = new Vec(dir);
		this.direction.normalize();
		this.maxt = Float.POSITIVE_INFINITY;
		this.mint = mint;
		depth = r.depth + 1;
	}

	public Ray(Pt origin, Vec dir, Ray r, float mint, float maxt)
	{
		this.position = new Pt(origin);
		this.direction = new Vec(dir);
		this.direction.normalize();
		this.maxt = maxt;
		this.mint = mint;
		depth = r.depth + 1;
	}

	public Ray(Ray r)
	{
		this.position = new Pt(r.position);
		this.direction = new Vec(r.direction);
		maxt = r.maxt;
		mint = r.mint;
		depth = r.depth;
	}

	public static Ray refractRay(Ray ray, Intersection i, double refractionIndex, double correction)
	{
		Vector3d refraction = refractVector(ray.direction, i.nn, refractionIndex,
				i.shape.getMaterial().refractionIndex);
		Vector3d direction = new Vector3d(refraction);
		direction.scale(correction);
		Pt offsetPoint = new Pt(i.p);
		offsetPoint.add(direction);
		return new Ray(offsetPoint, new Vec(refraction));
	}

	public static Vector3d refractVector(Vector3d incident, Vector3d normal, double incidentIndex,
			double normalIndex)
	{
		Vector3d inc = new Vector3d(incident);
		Vector3d norm = new Vector3d(normal);

		double n = incidentIndex / normalIndex;

		if (norm.dot(inc) > 0)
		{
			norm.negate();
		}
		double cosI = -norm.dot(inc);
		double sinT2 = n * n * (1.0 - cosI * cosI);

		if (sinT2 > 1.0)
		{
			System.out.print("Bad refraction vector!\n");
			System.exit(-1);
		}

		double cosT = Math.sqrt(1.0 - sinT2);

		inc.scale(n);
		norm.scale(n * cosI - cosT); //
		inc.add(norm);
		return inc;
	}

	/**
	 * Create a shadow ray from the given intersection point towards the provided light
	 * source.
	 * 
	 * @param shadowRay
	 * @param distance
	 * @return ray
	 */
	public static Ray makeShadowRay(Intersection inter, Light light, double correction)
	{
		// ******** Construct the shadow ray
		Vec lightDir = new Vec(new Vector3d(light.getPosition()));
		lightDir.sub(inter.p);
		Vector3d direction = new Vector3d(lightDir);
		direction.scale(correction);
		Pt offsetPoint = new Pt(inter.p);
		offsetPoint.add(direction);

		return new Ray(offsetPoint, lightDir, 0);
	}

	public static Ray reflectRay(Ray ray, Intersection i, double correction)
	{
		Vector3d reflection = reflectVector((Vector3d) ray.direction, i.nn);
		Vector3d direction = new Vector3d(reflection);
		direction.scale(correction);
		Pt offsetPoint = new Pt(i.p);
		offsetPoint.add(direction);
		return new Ray(offsetPoint, new Vec(reflection));
	}

	public static Vector3d reflectVector(Vector3d incident, Vector3d normal)
	{

		Vector3d in = new Vector3d(incident);

		Vector3d nor = new Vector3d(normal);

		double dotProduct = in.dot(nor);

		dotProduct *= 2;
		nor.scale(dotProduct);

		in.sub(nor);

		return in;

		// in is now -v, nor is 2N(v,n);

	}

	public Pt getPointAt(float x)
	{
		Pt p = new Pt(position);
		Vec d_copy = new Vec(direction);
		d_copy.scale(x);
		p.add(d_copy);
		return p;
	}

	@Override
	public String toString()
	{
		return "position: " + position + ", direction: " + direction;
	}
}
