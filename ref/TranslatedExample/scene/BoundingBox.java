package scene;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import raytracer.Util;

public class BoundingBox {

	public double xmin, xmax, ymin, ymax, zmin, zmax;

	/** Create a BoundingBox with the coordinate limits in each direction */
	public BoundingBox(double x1, double x2, double y1, double y2, double z1,
			double z2) {
		xmin = Math.min(x1, x2);
		xmax = Math.max(x1, x2);
		ymin = Math.min(y1, y2);
		ymax = Math.max(y1, y2);
		zmin = Math.min(z1, z2);
		zmax = Math.max(z1, z2);
	}

	/** Create a BoundingBox with the points of two opposite corners*/
	public BoundingBox(Vector3d p1, Vector3d p2) {
		xmin = Math.min(p1.x, p2.x);
		xmax = Math.max(p1.x, p2.x);
		ymin = Math.min(p1.y, p2.y);
		ymax = Math.max(p1.y, p2.y);
		zmin = Math.min(p1.z, p2.z);
		zmax = Math.max(p1.z, p2.z);
	}

	/** Create an identical BoundingBox to the other*/
	public BoundingBox(BoundingBox b) {
		xmin = b.xmin;
		ymin = b.ymin;
		zmin = b.zmin;
		xmax = b.xmax;
		ymax = b.ymax;
		zmax = b.zmax;
	}

	/** @returns Vector with the width, height, and length of the BoundingBox. */
	public Vector3d getSize() {
		return new Vector3d(xmax - xmin, ymax - ymin, zmax - zmin);
	}

	/** @returns Midpoint of the BoundingBox. */
	public Vector3d getCenter() {
		return new Vector3d((xmax + xmin) / 2.0, (ymax + ymin) / 2.0,
				(zmax + zmin) / 2.0);
	}

	/** @returns Array with the 8 corner points of the BoundingBox. */
	public Vector3d[] getCorners() {
		return new Vector3d[] { new Vector3d(xmin, ymin, zmin),
				new Vector3d(xmin, ymin, zmax), new Vector3d(xmin, ymax, zmin),
				new Vector3d(xmin, ymax, zmax), new Vector3d(xmax, ymin, zmin),
				new Vector3d(xmax, ymin, zmax), new Vector3d(xmax, ymax, zmin),
				new Vector3d(xmax, ymax, zmax) };
	}

	/** @returns New BoundingBox containing this and the other */
	public BoundingBox merge(BoundingBox b) {
		return new BoundingBox(Math.min(xmin, b.xmin), Math.max(xmax, b.xmax),
				Math.min(ymin, b.ymin), Math.max(ymax, b.ymax), Math.min(zmin,
						b.zmin), Math.max(zmax, b.zmax));
	}

	/** Extend this BoundingBox (modifies it) to cover the other */
	public void extend(BoundingBox b) {
		if (b.xmin < xmin)
			xmin = b.xmin;
		if (b.ymin < ymin)
			ymin = b.ymin;
		if (b.zmin < zmin)
			zmin = b.zmin;
		if (b.xmax > xmax)
			xmax = b.xmax;
		if (b.ymax > ymax)
			ymax = b.ymax;
		if (b.zmax > zmax)
			zmax = b.zmax;
	}

	/** @returns Determine if the point is contained in the BoundingBox*/
	public final boolean contains(Vector3d p) {
		if (p.x < xmin || p.x > xmax || p.y < ymin || p.y > ymax || p.z < zmin
				|| p.z > zmax)
			return false;
		return true;
	}

	/** @returns Determine if this BoundingBox intersects the other*/
	public final boolean intersects(BoundingBox b) {
		if (xmin > b.xmax || xmax < b.xmin || ymin > b.ymax || ymax < b.ymin
				|| zmin > b.zmax || zmax < b.zmin)
			return false;
		return true;
	}

	/** @returns Distance between the point and the closest point in the BoundingBox */
	public final double distanceToPoint(Vector3d p) {
		double x, y, z;

		if (p.x < xmin)
			x = xmin - p.x;
		else if (p.x > xmax)
			x = p.x - xmax;
		else
			x = 0.0;
		if (p.y < ymin)
			y = ymin - p.y;
		else if (p.y > ymax)
			y = p.y - ymax;
		else
			y = 0.0;
		if (p.z < zmin)
			z = zmin - p.z;
		else if (p.z > zmax)
			z = p.z - zmax;
		else
			z = 0.0;
		return Math.sqrt(x * x + y * y + z * z);
	}

	/** Extends this BoundingBox a fixed distance in each direction */
	public final void outset(double dist) {
		xmin -= dist;
		ymin -= dist;
		zmin -= dist;
		xmax += dist;
		ymax += dist;
		zmax += dist;
	}

	/** @returns New BoundingBox displaced by the given delta in each direction*/
	public final BoundingBox translate(double dx, double dy, double dz) {
		return new BoundingBox(xmin + dx, xmax + dx, ymin + dy, ymax + dy, zmin
				+ dz, zmax + dz);
	}

	/**
	 * Apply a transformation (matrix m) to each of the 8 corners of the BoundingBox
	 * and generate a new BoundingBox that contains it.
	 * 
	 * @return New BoundingBox that contains the applied transformation
	 */
	public final BoundingBox transformAndOutset(Matrix4d m) {
		double newxmin, newxmax, newymin, newymax, newzmin, newzmax;

		// Convert the corners to Vector4d by multiplying it with m
		Vector4d p, corners[];
		Vector3d tmp[] = getCorners();
		corners = new Vector4d[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			corners[i] = new Vector4d(tmp[i].x, tmp[i].y, tmp[i].z, 1.0);
		}
		
		p = Util.MultiplyMatrixAndVector(m, corners[0]);
		newxmin = newxmax = p.x;
		newymin = newymax = p.y;
		newzmin = newzmax = p.z;
		for (int i = 1; i < 8; i++) {
			p = Util.MultiplyMatrixAndVector(m, corners[i]);
			if (p.x < newxmin)
				newxmin = p.x;
			if (p.x > newxmax)
				newxmax = p.x;
			if (p.y < newymin)
				newymin = p.y;
			if (p.y > newymax)
				newymax = p.y;
			if (p.z < newzmin)
				newzmin = p.z;
			if (p.z > newzmax)
				newzmax = p.z;
		}
		return new BoundingBox(newxmin, newxmax, newymin, newymax, newzmin,
				newzmax);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "BoundingBox(xmin=" + xmin + ", xmax=" + xmax + ", ymin=" + ymin
				+ ", ymax=" + ymax + ", zmin=" + zmin + ", zmax=" + zmax + ")";
	}

}
