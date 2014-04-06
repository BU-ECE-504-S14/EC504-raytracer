package objects;

import javax.vecmath.Vector3d;

import raytracer.Util;
import scene.DifferentialGeometry;
import scene.Intersection;
import scene.Transformation;

public class Sphere extends AbstractSceneObject {

	private float radius = 1;
	private Vector3d position = new Vector3d(0, 0, 0);
	private float zmin,zmax;
	private float thetaMin,thetaMax,phiMax;
	public Transformation t;
	
	public Sphere(float radius, float z0, float z1, float pm, Transformation t){
		this.radius = radius;
		zmin = Util.clamp(Math.min(z0, z1), -radius, radius);
		zmax = Util.clamp(Math.max(z0, z1), -radius, radius);
		thetaMin = (float) Math.acos(Util.clamp(zmin/radius, -1f, 1f));
		thetaMax = (float) Math.acos(Util.clamp(zmax/radius, -1f, 1f));
		phiMax = (float) Math.toRadians(Util.clamp(pm, 0.0f, 360.0f));
		this.t = new Transformation(t);
	}

	public Vector3d getNormalAt(Vector3d pointOfIntersection) {
		Vector3d ret = new Vector3d(pointOfIntersection);
		ret.sub(position);	//ret =  point of intersection - sphere center position
		ret.scale(-1);
		ret.normalize();	//make into unit normal vector
		return ret;
	}

	@Override
	public boolean Intersect(Ray ray, DifferentialGeometry dg){
		float phi;
		Pt pHit;
		
		//transform ray to obj space
		Ray o_ray = t.world2Object(ray);
		
		//calculate quadratic sphere coeffs
		
		float A = (float) o_ray.direction.dot(o_ray.direction); //dx^2 + dy^2 + dz^2
		float B = (float) (2f*o_ray.direction.dot(position));
		float C = (float) (o_ray.position.dot(o_ray.position))-radius*radius;
		
		float[] t = {0,0};
		if(!Quadratic(A,B,C,t)) return false;
		
		//compute intersection distance along ray
		if(t[0] > o_ray.maxt || t[1] < o_ray.mint) return false;
		
		
		return true;
	}
	
	private boolean Quadratic(float A, float B, float C, float[] t){
		
		//find quadratic discriminant
		float discrim = B*B - 4f*A*C;
		if(discrim <= 0) return false;
		float rootdiscrim = (float) Math.sqrt(discrim);
		
		//compute t0,t1 i.e. the roots page no 119
		float q;
		if (B < 0) q = -0.5f * (B-rootdiscrim);
		else	   q = -0.5f * (B+rootdiscrim);
		t[0] = new Float(q / A);
		t[1] = new Float(C / q);
		
		//swap if t1<t0
		if (t[0] >= t[1]){
			float tmp = t[0];
			t[0] = t[1];
			t[1] = tmp;
		}
		return true;
	}
	/*@Override
	public DifferentialGeometry intersectsRay(Ray ray) {
		Vector3d aux = new Vector3d(ray.position);
		aux.sub(position);
		double a = 1.0; // == ray.direction.dot(ray.direction);
		double b = 2 * ray.direction.dot(aux); //parameterized ray line of distance center of sphere to beginning of ray
		double c = Math.pow(Util.Norm(aux), 2) - Math.pow(radius, 2); //unit normal

		double discriminant = Math.pow(b, 2) - 4 * a * c;
		if (discriminant < 0) {
			return null;
		}

		/* determine point of intersection 
		double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
		double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
		double t;

		if (t1 < 0 && t2 < 0) {
			return null;
		} else if (t1 < 0) {
			t = t2;
		} else if (t2 < 0) {
			t = t1;
		} else if (Math.abs(t1) < Math.abs(t2)) {
			t = t1;
		} else {
			t = t2;
		}

		Intersection x = new Intersection();
		x.point = new Vector3d(ray.direction);
		x.point.scale(t);
		x.point.add(ray.position);	
		x.normal = getNormalAt(x.point);
		x.distance = t;
		return x;
	}*/
	
	@Override
	public String toString() {
		return "Sphere(radius=" + radius + ", position=" + position + ")" +
		material.toString();
	}
}
