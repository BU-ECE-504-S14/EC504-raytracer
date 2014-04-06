package objects;

/**
 * Ray class.  Contains the origin position of the ray and its direction (a standard vector)
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class Ray {

	/** The origin point of the ray */
	public Pt position;

	/** Direction of the ray (vector is normalized) */
	public Vec direction;

	public Ray(Pt origin, Vec direction) {
		super();
		direction.normalize();
		this.position = new Pt(origin);
		this.direction = new Vec(direction);
	}
	
	public Ray(Ray r){
		super();
		this.position=r.position;
		this.direction=r.direction;
	}

	@Override
	public String toString() {
		return "position: " + position + ", direction: " + direction;
	}
}
