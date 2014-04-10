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

	public Vector3d origin;
	
	public int remainingReflections;
	
	//completely new ray.. set position and origin as the same.. remainingReflections is initialized to 8
	public Ray(Vector3d origin, Vector3d direction) {
		super();
		direction.normalize();
		this.position = origin;
		this.origin = origin;
		this.direction = direction;
		this.remainingReflections = 8;
	}
	//used in reflection and refractions.. creates new ray with specification for remainingReflections and an
	//optionally different origin and position..
	public Ray(Vector3d position, Vector3d origin, Vector3d direction, int remainingReflections) {
		super();
		direction.normalize();
		this.position = position;
		this.origin = origin;
		this.direction = direction;
		this.remainingReflections = remainingReflections;
	}

	@Override
	public String toString() {
		return "position: " + position + ", direction: " + direction;
	}
}
