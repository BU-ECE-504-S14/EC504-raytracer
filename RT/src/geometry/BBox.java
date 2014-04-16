package geometry;

import raytracer.Util;

public class BBox {
	

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
		pMin.x = Math.min(p1.x, p2.x);
		pMin.y = Math.min(p1.y, p2.y);
		pMin.z = Math.min(p1.z, p2.z);
		pMax.x = Math.max(p1.x, p2.x);
		pMax.y = Math.max(p1.y, p2.y);
		pMax.z = Math.max(p1.z, p2.z);
	}
	
	/**
	 * Create a copy of bounding box.
	 * 
	 * @param b BBox to be copied.
	 */
	public BBox(BBox b) {
		this.pMax = new Pt(b.pMax);
		this.pMin = new Pt(b.pMin);
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
		UBox.pMin.x = Math.min(b.pMin.x, p.x);
		UBox.pMin.y = Math.min(b.pMin.y, p.y);
		UBox.pMin.z = Math.min(b.pMin.z, p.z);
		UBox.pMax.x = Math.max(b.pMax.x, p.x);
		UBox.pMax.y = Math.max(b.pMax.y, p.y);
		UBox.pMax.z = Math.max(b.pMax.z, p.z);
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
		UBox.pMin.x = Math.min(b1.pMin.x, b2.pMin.x);
		UBox.pMin.y = Math.min(b1.pMin.y, b2.pMin.y);
		UBox.pMin.z = Math.min(b1.pMin.z, b2.pMin.z);
		UBox.pMax.x = Math.max(b1.pMax.x, b2.pMax.x);
		UBox.pMax.y = Math.max(b1.pMax.y, b2.pMax.y);
		UBox.pMax.z = Math.max(b1.pMax.z, b2.pMax.z);
		return UBox;
	}
	
	/**
	 * check whether this bounding box overlaps with bounding box b
	 * 
	 * @param b bounding box to check overlap against
	 * @return boolean true == overlap. false == no overlap.
	 */
	public boolean overlaps(BBox b) {
		boolean x = (pMax.x >= b.pMin.x) && (pMin.x <= b.pMax.x);
	    boolean y = (pMax.y >= b.pMin.y) && (pMin.y <= b.pMax.y);
	    boolean z = (pMax.z >= b.pMin.z) && (pMin.z <= b.pMax.z);
	    return (x&y&z);
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
		this.pMin = pMin;
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
		this.pMax = pMax;
	}
}

