package geometry;

import java.io.Serializable;

import javax.vecmath.Tuple3d;

import raytracer.Util;

public class BBox implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Pt pMin, pMax;
	
	/**
	 * basic constructor sets pMin to negative infiinity and pMax to positive infinity. This ensures that bounding
	 * box is degenerate and that any operation performed on the bounding box will compute correctly. For example, 
	 * any union of a possible point will be less than the current pMin and greater than the current pMax for 
	 * pMin.x ... .y ... .z and etc.
	 */
	public BBox() {
		pMin = new Pt(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
		pMax = new Pt(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY);
	}
	
	/**
	 * Initialize a bounding box to surround a single point.
	 * 
	 * @param p point (Pt) to be enclosed in bounding box.
	 */
	public BBox(Pt p) {
		pMin = new Pt(p);
		pMax = new Pt(p);
	}
	
	/**
	 * Initialize bounding box based on two points. This function will find the minimal and maxmial x,y,z of the two points.
	 *  
	 * @param p1 first point (Pt) of bounding box (not necessarily min or max).
	 * @param p2 second point (Pt) of the bounding box (not necessarily min or max).
	 */
	public BBox(Pt p1, Pt p2) {
		float xm,ym,zm,xM,yM,zM;
		xm = (float) Math.min(p1.x, p2.x);
		ym = (float) Math.min(p1.y, p2.y);
		zm = (float) Math.min(p1.z, p2.z);
		xM = (float) Math.max(p1.x, p2.x);
		yM = (float) Math.max(p1.y, p2.y);
		zM = (float) Math.max(p1.z, p2.z);
		pMin = new Pt(xm,ym,zm);
		pMax = new Pt(xM,yM,zM);
	}
	
	/**
	 * Create a copy of bounding box.
	 * 
	 * @param b BBox to be copied.
	 */
	public BBox(BBox b) {
		this.pMax = new Pt(b.getpMax());
		this.pMin = new Pt(b.getpMin());
	}
	
	/**
	 * create a new Bounding Box based on the maximal/minimal of Bounding box b and point p.
	 * 
	 * @param b bounding box used to find maximal and minimal points of union BBox.
	 * @param p point used to find maximal and minimal points of union BBox.
	 * @return BBox containing the maximal and minimal point of the union of p and b.
	 */
	public static BBox union(BBox b, Pt p) {
		BBox UBox = new BBox(b);
		UBox.pMin.x = Math.min(b.getpMin().x, p.x);
		UBox.pMin.y = Math.min(b.getpMin().y, p.y);
		UBox.pMin.z = Math.min(b.getpMin().z, p.z);
		UBox.pMax.x = Math.max(b.getpMax().x, p.x);
		UBox.pMax.y = Math.max(b.getpMax().y, p.y);
		UBox.pMax.z = Math.max(b.getpMax().z, p.z);
		return UBox;
	}
	
	/**
	 * create a new Bounding Box based on the maximal/minimal of bounding box b1 and bounding box b2.
	 * 
	 * @param b1 bounding box used to find maximal and minimal points of union BBox.
	 * @param b2 bounding box used to find maximal and minimal points of union BBox.
	 * @return BBox containing the maximal and minimal point of the union of b1 and b2.
	 */
	public static BBox union(BBox b1, BBox b2) {
		BBox UBox = new BBox(b1);
		UBox.pMin.x = Math.min(b1.getpMin().x, b2.getpMin().x);
		UBox.pMin.y = Math.min(b1.getpMin().y, b2.getpMin().y);
		UBox.pMin.z = Math.min(b1.getpMin().z, b2.getpMin().z);
		UBox.pMax.x = Math.max(b1.getpMax().x, b2.getpMax().x);
		UBox.pMax.y = Math.max(b1.getpMax().y, b2.getpMax().y);
		UBox.pMax.z = Math.max(b1.getpMax().z, b2.getpMax().z);
		return UBox;
	}
	
	/**
	 * check whether this bounding box overlaps with bounding box b
	 * 
	 * @param b bounding box to check overlap against
	 * @param epsilon allows for epsilon difference between bounding boxes.
	 * @return boolean true == overlap. false == no overlap.
	 */
	public boolean overlaps(BBox b, float epsilon) {
		boolean x = (pMax.x >= b.getpMin().x - epsilon) && (pMin.x <= b.getpMax().x + epsilon);
	    boolean y = (pMax.y >= b.getpMin().y - epsilon) && (pMin.y <= b.getpMax().y + epsilon);
	    boolean z = (pMax.z >= b.getpMin().z - epsilon) && (pMin.z <= b.getpMax().z + epsilon);
	    return (x&&y&&z);
	}
	
	/**
	 * less narrow comparison range. Checks whether this BBox is has a common min max in any axis x, y, z
	 * 
	 * @param b bounding box with inclusive relationship
	 * @param epsilon allows for epsilon difference between bounding boxes in x, y, or z axis.
	 * @return boolean true == inclusive. false == not inclusive.
	 */
	public boolean inclusive(BBox b, float epsilon) {
		boolean x = (pMax.x >= b.getpMin().x - epsilon) && (pMin.x <= b.getpMax().x + epsilon);
	    boolean y = (pMax.y >= b.getpMin().y - epsilon) && (pMin.y <= b.getpMax().y + epsilon);
	    boolean z = (pMax.z >= b.getpMin().z - epsilon) && (pMin.z <= b.getpMax().z + epsilon);
	    return (x||y||z);
	}
	
	/**
	 * Check whether or not point pt lies inside of this BBox's bounding box
	 * 
	 * @param pt point to check inside outside
	 * @return true == pt is inside bounding box. false == pt is not inside bounding box.
	 */
	public boolean inside(Pt pt) {
		return (   (pt.x >= pMin.x) && (pt.x <= pMax.x) &&
	               (pt.y >= pMin.y) && (pt.y <= pMax.y) &&
	               (pt.z >= pMin.z) && (pt.z <= pMax.z)    );
	}
	
	/**
	 * Expand bounding box by float delta.
	 * 
	 * @param delta the factor by which the bounding box will be expanded
	 */
	public void expand(float delta) {
		pMin.sub(new Pt(delta,delta,delta));
		pMax.add(new Pt(delta,delta,delta));
	}
	
	/** 
	 * Calculates and returns the Surface area of this bounding box.
	 * 
	 * @return surface area of bounding box (float).
	 */
	public float surfaceArea() {
		Vec d = new Vec(pMax); //diagonal vector of BBox
		d.sub(pMin);
		
		//add xy square, xz square, yz square twice. 
		return (float) (2.0f * (d.x * d.y + d.x * d.z + d.y * d.z));
	}
	
	/**
	 * Calculates and returns the Volume of this bounding box
	 * 
	 * @return Volume of the bounding box (float)
	 */
	public float volume() {
		Vec d = new Vec(pMax);	//Diagonal vector of BBox
		d.sub(pMin);
		
		return (float) (d.x*d.y*d.z);
	}

	/**
	 * Interpolate a point xI%, yI%, and zI% inside of this BBox.
	 * 
	 * @param xI percent interpolation along x of this BBox.
	 * @param yI percent interpolation along y of this BBox.
	 * @param zI percent interpolation along z of this BBox.
	 * @return point xI%, yI%, zI% inside this BBox.
	 */
	public Pt lerp(float xI, float yI, float zI) {
		return new Pt(	Util.lerp(xI, (float) pMin.x, (float) pMax.x),
						Util.lerp(yI, (float) pMin.y, (float) pMax.y),
						Util.lerp(zI, (float) pMin.z, (float) pMax.z)   );
	}
	
	/**
	 * Find normalized offset from point p to edges of bounding box in x, y, and z direction.
	 * 
	 * @param p point to find BBox offset from.
	 * @return a Vec containing the normalized offset of point from pMin.x, pMin.y, pMin.z.
	 */
	public Vec offset(Pt p) {
		return new Vec(	(p.x - pMin.x) / (pMax.x - pMin.x),
                		(p.y - pMin.y) / (pMax.y - pMin.y),
                		(p.z - pMin.z) / (pMax.z - pMin.z)   );
	}
	
	/**
	 * checks if ray hits bounding box. Computes hitT[0] = parameterized first contact with BB 
	 * and hitT1[1] = parameterized final contact with BB. Returns whether or not box was intersected. 
	 * 
	 * @param Ray ray that is striking bounding box
	 * @param hitT 
	 * 			float array that will receive the parametric beginning and end points of bound.
	 * 			Set hitT[0] != 0 if hitT[0] is supposed to be set.
	 * 			Set hitT[1] != 0 if hitT[1] is supposed to be set. 
	 * @return boolean true == box intersected. false == box not intersected.
	 */
	public boolean IntersectP(Ray Ray, float[] hitT) {
		float t0 = new Float (Ray.mint);
		float t1 = new Float (Ray.maxt);
		
		float[] o = setFloatArrayFromTuple(Ray.position);
		float[] d = setFloatArrayFromTuple(Ray.direction);
		float[] mins = setFloatArrayFromTuple(pMin);
		float[] maxs = setFloatArrayFromTuple(pMax);
		
		for(int i = 0; i < 3; i++){
			
			//compute near and far for x, y, and z bbox planes based on simplified para/intr solution pharr 195
			float invRayDir = 1f/d[i];
			float tNear = (mins[i] - o[i])*invRayDir;
			float tFar = (maxs[i] - o[i])*invRayDir;
			
			//swap tNear and tFar if near is greater than far
			if(tNear > tFar) {
				float tmp = tNear;
				tNear = tFar;
				tFar = tmp;
			}
			
			//update t0 t1
			t0 = tNear > t0 ? tNear : t0;
			t1 = tFar < t1 ? tFar : t1;
			
			//if t0 is greater than t1 ray does not intersect bounding box
			if(t0 > t1) {return false;}
		}
		
		if(hitT[0] != 0) hitT[0] = t0;
		if(hitT[1] != 0) hitT[1] = t1;
		
		return true;
	}
	
	/**
	 * 
	 * @param t tuple used to set float array
	 * @return a float array with f[0] set to t.x, f[1] set to t.y, f[2] set to t.z.
	 */
	private float[] setFloatArrayFromTuple(Tuple3d t ) {
		float[] f = new float[3];
		f[0] = new Float(t.x);
		f[1] = new Float(t.y);
		f[2] = new Float(t.z);
		return f;
	}
	
	
	
	/**
	 * get the variable that represents the minimal point of this bounding box
	 * 
	 * @return pMin the minimal point of bounding box
	 */
	public Pt getpMin() {
		return pMin;
	}

	/**
	 * Set the variable pMin that represents the minimal point of this bounding box 
	 * 
	 * @param pMin the new minimal point of this bounding box
	 */
	public void setpMin(Pt pMin) {
		this.pMin = new Pt(pMin);
	}

	/**
	 * get the variable that represents the maximal point of this bounding box
	 * 
	 * @return pMax Maximal point of bounding box
	 */
	public Pt getpMax() {
		return pMax;
	}

	/**
	 * Set the variable pMin that represents the maximal point of this bounding box 
	 * 
	 * @param pMax	the new maximal point of this bounding box
	 */
	public void setpMax(Pt pMax) {
		this.pMax = new Pt(pMax);
	}
	/**
	 * Returns the corner points of the bounding box
	 * @return contains 8 corner points of the bounding box
	 */
	public Pt[] getCorners(){
		Pt[] P = new Pt[8];
		P[0] = new Pt( pMin.x, pMin.y, pMin.z ); //000
	    P[4] = new Pt( pMax.x, pMin.y, pMin.z ); //100
	    P[2] = new Pt( pMin.x, pMax.y, pMin.z ); //010
	    P[1] = new Pt( pMin.x, pMin.y, pMax.z ); //001
	    P[6] = new Pt( pMax.x, pMax.y, pMin.z ); //110
	    P[5] = new Pt( pMax.x, pMin.y, pMax.z ); //101
	    P[3] = new Pt( pMin.x, pMax.y, pMax.z ); //011
	    P[7] = new Pt( pMax.x, pMax.y, pMax.z ); //111
	    return P;
	}
}

