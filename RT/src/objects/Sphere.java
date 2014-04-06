package objects;

import javax.vecmath.Vector3d;

import raytracer.Util;
import scene.Intersection;
import scene.Transformation;

public class Sphere extends AbstractSceneObject {

	private float radius = 1;
	private Vector3d position = new Vector3d(0, 0, 0);
	private float zmin,zmax;
	private float thetaMin,thetaMax,phiMax;
	public Transformation t;
	
	public Sphere(float radius, float z0, float z1, float pm ){
		this.radius = radius;
		zmin = Util.clamp(Math.min(z0, z1), -radius, radius);
		zmax = Util.clamp(Math.max(z0, z1), -radius, radius);
		thetaMin = (float) Math.acos(Util.clamp(zmin/radius, -1f, 1f));
		thetaMax = (float) Math.acos(Util.clamp(zmax/radius, -1f, 1f));
		phiMax = (float) Math.toRadians(Util.clamp(pm, 0.0f, 360.0f));
	}

	public Vector3d getNormalAt(Vector3d pointOfIntersection) {
		Vector3d ret = new Vector3d(pointOfIntersection);
		ret.sub(position);	//ret =  point of intersection - sphere center position
		ret.scale(-1);
		ret.normalize();	//make into unit normal vector
		return ret;
	}

	@Override
	public void transform(Transformation t) {
		position.add(t.translation);
		radius *= Math.min(Math.min(t.scale.x, t.scale.y), t.scale.z);
	}

	@Override
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

		/* determine point of intersection */
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
	}

	@Override
	public String toString() {
		return "Sphere(radius=" + radius + ", position=" + position + ")" +
		material.toString();
	}
}
