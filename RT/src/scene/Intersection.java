package scene;

import javax.vecmath.Vector3d;


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
	
	public Intersection() {
		point = new Vector3d();
		normal = new Vector3d();
	}

	public Intersection(Vector3d point, Vector3d normal, double distance) {
		this.point = new Vector3d(point);
		this.normal = new Vector3d(normal);
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Intersection(point=" + point.toString() + ", normal="
				+ normal.toString() + ", distance=" + distance + ")";
	}

}
