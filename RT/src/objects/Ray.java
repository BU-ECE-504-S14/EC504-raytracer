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
	
	/** parametric information for ray **/
	public float maxt; //current farthest distance from origin (parametric)
	public float mint; //base position on parametric line (any point less than rMin is behind ray)
	public float tHit; //parametric distance from ray origin to nearest object intersection
	public int depth;  //numbers of times ray has been reflected/refracted

	public Ray(Pt origin, Vec direction, float mint) {
		direction.normalize();
		this.position = new Pt(origin);
		this.direction = new Vec(direction);
		maxt = Float.POSITIVE_INFINITY;
		this.mint = mint;
		depth = 0;
	}
	
	public Ray(Pt origin, Vec direction, float mint, float maxt) {
		this.position = new Pt(origin);
		this.direction = new Vec(direction);
		this.direction.normalize();
		this.maxt = maxt;
		this.mint = mint;
		depth = 0;
	}
	
	public Ray(Pt origin, Vec direction, Ray r, float mint){
		this.position = new Pt(origin);
		this.direction = new Vec(direction);
		this.direction.normalize();
		this.maxt = Float.POSITIVE_INFINITY;
		this.mint = mint;
		depth = r.depth+1;
	}
	
	public Ray(Pt origin, Vec direction, Ray r, float mint, float maxt){
		this.position = new Pt(origin);
		this.direction = new Vec(direction);
		this.direction.normalize();
		this.maxt = maxt;
		this.mint = mint;
		depth = r.depth+1;
	}
	
	public Ray(Ray r){
		this.position= new Pt(r.position);
		this.direction= new Vec(r.direction);
		maxt = r.maxt;
		mint = r.mint;
		tHit = r.tHit;
		depth = r.depth;
	}

	public Pt getPointAt(float x){
		Pt p = new Pt(position);
		Vec d_copy = new Vec(direction);
		d_copy.scale(x);
		p.add(d_copy);
		return p;
	}
	@Override
	public String toString() {
		return "position: " + position + ", direction: " + direction;
	}
}
