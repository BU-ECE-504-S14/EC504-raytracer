package objects;

import java.util.UUID;

import geometry.BBox;
import geometry.Normal;
import geometry.Pt;
import geometry.Ray;
import geometry.Transformation;
import geometry.Vec;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import raytracer.Util;
import scene.Intersection;

public class Sphere extends AbstractSceneObject {
	private float radius = 1;
	private Vector3d position = new Vector3d(0, 0, 0);
	private float zmin = -1f;
	private float zmax = 1f;
	private float thetaMin = 0;
	private float thetaMax = 360;
	public float phiMax = (float) Math.PI * 2; // Why is this in Radians when
												// everything
												// else isn't?
	private Vector3d scale = new Vector3d(1, 1, 1);
	private Vector3d pos = new Vector3d(0, 0, 0);
	private AxisAngle4d rot = new AxisAngle4d(0, 0, 0, 0);
	private Transformation trans = new Transformation(scale, pos, rot);

	public Sphere() {
		setName("New Sphere");
	}

	/**
	 * copy constructor for sphere.
	 * 
	 * @param s
	 *            sphere to be copied.
	 */
	public Sphere(Sphere s) {
		super();
		this.position = new Vector3d(s.position);
		this.zmin = s.zmin;
		this.zmax = s.zmax;
		this.thetaMin = s.thetaMin;
		this.thetaMax = s.thetaMax;
		this.phiMax = s.phiMax;
		this.scale = new Vector3d(s.scale);
		this.pos = new Vector3d(s.pos);
		this.rot = new AxisAngle4d(s.rot);
		this.trans = new Transformation(s.trans);
		this.material = new Material(s.getMaterial());
	}

	/**
	 * 
	 * @param radius
	 * @param z0
	 *            represents either the min or max height of sphere in object
	 *            space
	 * @param z1
	 *            represents either the min or max hieght of sphere in object
	 *            space
	 * @param pm
	 *            represents max "circle" of the sphere that will be traced
	 *            along the xz plane in object space
	 * @param trans
	 */
	public Sphere(float z0, float z1, float pm, Transformation t) {
		super();
		zmin = Util.clamp(Math.min(z0, z1), -radius, radius);
		zmax = Util.clamp(Math.max(z0, z1), -radius, radius);
		thetaMin = (float) Math.acos(Util.clamp(zmin / radius, -1f, 1f));
		thetaMax = (float) Math.acos(Util.clamp(zmax / radius, -1f, 1f));
		phiMax = (float) Math.toRadians(Util.clamp(pm, 0.0f, 360.0f));
		this.trans = new Transformation(t);

		setName("New Sphere");

	}

	public float getzMin() {
		return zmin;
	}

	public float getzMax() {
		return zmax;
	}

	public float getRadius() {
		if ((scale.x == scale.y) && (scale.y == scale.z)) {
			return (float) scale.x;
		} else { // find maximum scale to represent radius
			double approxRad = ((scale.x >= scale.y) && (scale.x >= scale.z)) ? scale.x
					: scale.y;
			approxRad = (approxRad > scale.z) ? approxRad : scale.z;
			return (float) approxRad;
		}
	}

	public Vector3d getPosition() {
		return new Vector3d(pos);
	}

	public void setPosition(Vector3d newPos) {
		pos = new Vector3d(newPos);
		trans.setTranslation(pos);
	}

	public Vector3d getScale() {
		return new Vector3d(scale);
	}

	/**
	 * Use this method to set radius/scale of world sphere. Because world space
	 * representation of sphere is based on transformation that includes scale.
	 * Radius of sphere is in essence set by setting the scale of the sphere.
	 * Note: set all scale values equal if you want to set the radius of the
	 * sphere.
	 * 
	 * @param newScaleAndRadius
	 */
	public void setScaleRad(Vector3d newScaleAndRadius) {
		scale = new Vector3d(newScaleAndRadius);
		trans.setScale(scale);
	}

	public AxisAngle4d getRotation() {
		return new AxisAngle4d(rot);
	}

	public void setRotation(AxisAngle4d newRot) {
		rot = new AxisAngle4d(newRot);
		trans.setRotation(rot);
	}

	/**
	 * Set the upper and lower bounds for sphere
	 * 
	 * @param z0
	 *            represents either the min or max height of sphere in object
	 *            space.
	 * @param z1
	 *            represents either the min or max hieght of sphere in object
	 *            space.
	 */
	public void setzMinMax(float z0, float z1) {
		zmin = Util.clamp(Math.min(z0, z1), -radius, radius);
		zmax = Util.clamp(Math.max(z0, z1), -radius, radius);
		thetaMin = (float) Math.acos(Util.clamp(zmin / radius, -1f, 1f));
		thetaMax = (float) Math.acos(Util.clamp(zmax / radius, -1f, 1f));
	}

	public float getThetaMin() {
		return thetaMin;
	}

	public float getThetaMax() {
		return thetaMax;
	}

	/**
	 * Because Sphere's exist only in object space, updating the transformation
	 * of a sphere is the equivalent of scaling a radius 1 sphere, then
	 * translating and rotating that sphere in world space.
	 * 
	 * @param t
	 *            updated transformation for this sphere.
	 */
	public void setTransform(Transformation t) {
		trans = new Transformation(t);
	}

	public Transformation getTransform() {
		return new Transformation(trans);
	}

	/**
	 * Because Sphere's exist only in object space, updating the transformation
	 * of a sphere is the equivalent of scaling a radius 1 sphere, then
	 * translating and rotating that sphere in world space.
	 * 
	 * @param newScale
	 *            updated scale of transformation
	 * @param newPos
	 *            updated position of transformation
	 * @param newRot
	 *            updated rotation of transformation
	 */
	public void setTransform(Vector3d newScale, Vector3d newPos,
			AxisAngle4d newRot) {
		scale.set(newScale);
		pos.set(newPos);
		rot.set(newRot);
		trans = new Transformation(scale, pos, rot);
	}

	public Sphere getCopy() {
		return new Sphere(this);
	}

	@Override
	public boolean IntersectP(Ray ray) {
		float phi;
		Pt pHit;

		// transform ray to obj space
		Ray o_ray = this.trans.world2Object(ray);

		// calculate quadratic sphere coeffs
		float A = (float) o_ray.direction.dot(o_ray.direction); // dx^2 + dy^2 +
																// dz^2
		float B = (float) (2f * o_ray.direction.dot(o_ray.position));
		float C = (float) (o_ray.position.dot(o_ray.position)) - radius
				* radius;

		float[] t = { 0, 0 };
		if (!Quadratic(A, B, C, t))
			return false;

		// compute intersection distance along ray
		if (t[0] > o_ray.maxt || t[1] < o_ray.mint)
			return false;
		float thit = t[0];
		if (t[0] < o_ray.mint) {
			thit = t[1];
			if (thit > o_ray.maxt)
				return false;
		}

		// compute sphere hit position and phi
		pHit = o_ray.getPointAt(thit);
		phi = computePhi(pHit);

		// test sphere intersection against clipping parameters
		if ((zmin > -radius && pHit.z < zmin)
				|| (zmax < radius && pHit.z > zmax) || (phi > phiMax)) {

			if (thit == t[1])
				return false;
			if (t[1] > o_ray.maxt)
				return false;

			thit = t[1];
			pHit = o_ray.getPointAt(thit);
			phi = computePhi(pHit);
			if ((zmin > -radius && pHit.z < zmin)
					|| (zmax < radius && pHit.z > zmax) || (phi > phiMax))
				return false;
		}

		ray.maxt = thit;
		return true;
	}

	@Override
	public boolean Intersect(Ray ray, Intersection inter) {
		float phi;
		Pt pHit;

		// transform ray to obj space
		Ray o_ray = this.trans.world2Object(ray);

		// calculate quadratic sphere coeffs
		float A = (float) o_ray.direction.dot(o_ray.direction); // dx^2 + dy^2 +
																// dz^2
		float B = (float) (2f * o_ray.direction.dot(o_ray.position));
		float C = (float) (o_ray.position.dot(o_ray.position)) - radius
				* radius;

		float[] t = { 0, 0 };
		if (!Quadratic(A, B, C, t))
			return false;

		// compute intersection distance along ray
		if (t[0] > o_ray.maxt || t[1] < o_ray.mint)
			return false;
		float thit = t[0];
		if (t[0] < o_ray.mint) {
			thit = t[1];
			if (thit > o_ray.maxt)
				return false;
		}

		// compute sphere hit position and phi
		pHit = o_ray.getPointAt(thit);
		phi = computePhi(pHit);

		// test sphere intersection against clipping parameters
		if ((zmin > -radius && pHit.z < zmin)
				|| (zmax < radius && pHit.z > zmax) || (phi > phiMax)) {

			if (thit == t[1])
				return false;
			if (t[1] > o_ray.maxt)
				return false;

			thit = t[1];
			pHit = o_ray.getPointAt(thit);
			phi = computePhi(pHit);
			if ((zmin > -radius && pHit.z < zmin)
					|| (zmax < radius && pHit.z > zmax) || (phi > phiMax))
				return false;
		}

		// find parametric representation of sphere hit
		float u = phi / phiMax;
		float theta = (float) Math.acos(Util.clamp((float) (pHit.z / radius),
				-1f, 1f));
		float v = (theta - thetaMin) / (thetaMax - thetaMin);

		// compute dpdu dpdv
		float zradius = (float) Math.sqrt(pHit.x * pHit.x + pHit.y * pHit.y);
		float invzradius = 1f / zradius;
		float cosphi = (float) (pHit.x * invzradius);
		float sinphi = (float) (pHit.y * invzradius);
		Vec dpdu = new Vec(-phiMax * pHit.y, phiMax * pHit.x, 0);
		Vec dpdv = new Vec(pHit.z * cosphi, pHit.z * sinphi, -radius
				* Math.sin(theta));
		dpdv.scale((thetaMax - thetaMin));

		// compute dndu dndv based on Weingarten equations
		Vec d2Pduu = new Vec(pHit.x, pHit.y, 0f);
		d2Pduu.scale(-phiMax * phiMax);
		Vec d2Pduv = new Vec(-sinphi, cosphi, 0f);
		d2Pduv.scale((thetaMax - thetaMin) * pHit.z * phiMax);
		Vec d2Pdvv = new Vec(pHit.x, pHit.y, pHit.z);
		d2Pdvv.scale(-(thetaMax - thetaMin) * (thetaMax - thetaMin));

		// compute coeffs for fundamental forms
		float E = (float) dpdu.dot(dpdu);
		float F = (float) dpdu.dot(dpdv);
		float G = (float) dpdv.dot(dpdv);
		Vec N = new Vec();
		N.cross(dpdu, dpdv);
		N.normalize();
		float e = (float) N.dot(d2Pduu);
		float f = (float) N.dot(d2Pduv);
		float g = (float) N.dot(d2Pdvv);

		float invEGF2 = 1f / (E * G - F * F);
		Vec sdpdu = new Vec(dpdu);
		Vec sdpdv = new Vec(dpdv);
		sdpdu.scale((f * F - e * G) * invEGF2);
		sdpdv.scale((e * F - f * E) * invEGF2);
		sdpdu.add(sdpdv);
		Normal dndu = new Normal(sdpdu);

		sdpdu = new Vec(dpdu);
		sdpdv = new Vec(dpdv);
		sdpdu.scale((g * F - f * G) * invEGF2);
		sdpdv.scale((f * F - g * E) * invEGF2);
		sdpdu.add(sdpdv);
		Normal dndv = new Normal(sdpdu);

		// Differential geometry initialization
		inter.update(this.trans.object2World(pHit),
				this.trans.object2World(dpdu), this.trans.object2World(dpdv),
				this.trans.object2World(dndu), this.trans.object2World(dndv),
				u, v, this);

		// update hit parameter
		ray.maxt = thit;

		return true;
	}

	private float computePhi(Pt pHit) {
		if (pHit.x == 0 && pHit.y == 0)
			pHit.x = 1E-5f * radius; // make pHit a small number to avoid 0/0
										// division
		float phi = (float) Math.atan2(pHit.y, pHit.x);
		if (phi < 0f)
			phi += 2f * Math.PI;
		return phi;
	}

	private boolean Quadratic(float A, float B, float C, float[] t) {

		// find quadratic discriminant
		float discrim = B * B - 4f * A * C;
		if (discrim <= 0)
			return false;
		float rootdiscrim = (float) Math.sqrt(discrim);

		// compute t0,t1 i.e. the roots page no 119
		float q;
		if (B < 0)
			q = -0.5f * (B - rootdiscrim);
		else
			q = -0.5f * (B + rootdiscrim);
		t[0] = new Float(q / A);
		t[1] = new Float(C / q);

		// swap if t1<t0
		if (t[0] >= t[1]) {
			float tmp = t[0];
			t[0] = t[1];
			t[1] = tmp;
		}
		return true;
	}

	@Override
	public BBox getWorldBound() {
		return trans.object2World(getobjectBound());
	}

	/**
	 * @return a BBox representing the bounding box of this sphere in object
	 *         space
	 */
	private BBox getobjectBound() {
		return new BBox(new Pt(-radius, -radius, zmin), new Pt(radius, radius,
				zmax));
	}

	@Override
	public String toString() {
		return "Sphere: " + name;
	}

	public String paramsToString() {
		return "Sphere name= " + name + ", radius=" + radius + ", position="
				+ position + ")" + material.toString() + trans.toString();
	}

}
