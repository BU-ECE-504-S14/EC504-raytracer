package scene;

import javax.vecmath.Vector3d;

public class Intersection {

	/** Point of intersection*/
	public Vector3d point;

	/** Surface normal at the point of intersection */
	public Vector3d normal;

	/** Distance from the ray's origin to the point of intersection*/
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
