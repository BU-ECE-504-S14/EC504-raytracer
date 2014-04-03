package raytracer;

import javax.vecmath.Vector3d;

/**
 * Ray class.  Contains the origin position of the ray and its direction (a standard vector)
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class Ray {

	/** The origin point of the ray */
	public Vector3d position;

	/** Direction of the ray (vector is normalized) */
	public Vector3d direction;

	public Ray(Vector3d origin, Vector3d direction) {
		super();
		direction.normalize();
		this.position = origin;
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "position: " + position + ", direction: " + direction;
	}
}
