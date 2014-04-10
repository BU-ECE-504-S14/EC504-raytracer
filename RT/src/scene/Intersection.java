package scene;

import javax.vecmath.Vector3d;

import raytracer.Ray;


/**
 * Representation of the intersection points of rays with SceneObjects.
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class Intersection {

	/* point of intersection from the point of view of the scene (ie not camera or object)*/
	public Vector3d point;

	public Vector3d normal;

	/**distance from rays origin to the point of intersection*/
	public double distance;
	
	
	public Ray ray;

	public Intersection() {
		point = new Vector3d();
		normal = new Vector3d();
	}

	public Intersection(Vector3d point, Vector3d normal, double distance, Ray ray) {
		this.point = new Vector3d(point);
		this.normal = new Vector3d(normal);
		this.distance = distance;
		this.ray = new Ray(ray.position, ray.origin, ray.direction, ray.remainingReflections);
	}

	@Override
	public String toString() {
		return "Intersection(point=" + point.toString() + ", normal="
				+ normal.toString() + ", distance=" + distance + ")";
	}

}
