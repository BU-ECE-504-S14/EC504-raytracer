package scene;

import javax.vecmath.Vector3d;

import objects.AbstractSceneObject;
import objects.SceneObject;

import raytracer.Ray;

/**
 * This class represents an Octree node used to efficiently find objects 
 * of the scene by location
 * 
 * An OctreeNode is able to a terminal node (when it contains a list of SceneObjects) or a branch
 * (when it contains other OctreeNodes children).
 */

public class OctreeNode extends BoundingBox {

	private static final int MAX_OBJECTS_PER_NODE = 8;
	private static final int MAX_OCTREE_DEPTH = 16;

	private OctreeNode parent, child[];
	private SceneObject obj[];
	private double midx, midy, midz;
	private int depth;

	/**
	 * @param nodeBounds BoundingBox of the node
	 * @param objects Node objects
	 * @param bb BoundingBoxes corresponding to the objects
	 */
	public OctreeNode(BoundingBox nodeBounds, SceneObject objects[],
			BoundingBox bb[]) {
		this(nodeBounds, objects, bb, null, 0);
	}

	/** Constructor to be wrapped in the case of the root node */
	private OctreeNode(BoundingBox nodeBounds, SceneObject objects[],
			BoundingBox bb[], OctreeNode parentNode, int nodeDepth) {
		super(nodeBounds);
		boolean inside[] = new boolean[objects.length];
		int count, i;

		parent = parentNode;
		depth = nodeDepth;

		// Find the objects in this node
		for (i = 0, count = 0; i < objects.length; i++) {
			if (bb[i].intersects(this))
				if (objects[i].intersectsBox(this)) {
					inside[i] = true;
					count++;
				}
		}

		// Construct the list of objects in this node and its BoundingBoxes 
		obj = new AbstractSceneObject[count];
		if (count == 0)
			return;
		BoundingBox objBounds[] = new BoundingBox[count];
		for (i = 0, count = 0; i < objects.length; i++) {
			if (inside[i]) {
				obj[count] = objects[i];
				objBounds[count++] = bb[i];
			}
		}

		// Split this node
		split(objBounds);
	}

	/**
	 * Splits if the current node does not have too many octree objects and if it is not too deep 
	 * In this case, construct up to 8 children according to their 
	 * BoundingBoxes of their objects and mark as a terminal node.
	 */
	private void split(BoundingBox objBounds[]) {
		if (obj.length <= MAX_OBJECTS_PER_NODE || depth >= MAX_OCTREE_DEPTH) {
			return;
		}

		boolean splitx, splity, splitz;
		findMidpoints(objBounds);
		splitx = (midx != xmax);
		splity = (midy != ymax);
		splitz = (midz != zmax);
		if (!(splitx || splity || splitz))
			return;

		child = new OctreeNode[8];
		int d = depth + 1;
		// near SW
		BoundingBox bb = new BoundingBox(xmin, midx, ymin, midy, zmin, midz);
		child[0] = new OctreeNode(bb, obj, objBounds, this, d);
		if (splitz) { // far SW
			bb = new BoundingBox(xmin, midx, ymin, midy, zmax, midz);
			child[1] = new OctreeNode(bb, obj, objBounds, this, d);
		}
		if (splity) { // near NW
			bb = new BoundingBox(xmin, midx, ymax, midy, zmin, midz);
			child[2] = new OctreeNode(bb, obj, objBounds, this, d);
			if (splitz) { // far NW
				bb = new BoundingBox(xmin, midx, ymax, midy, zmax, midz);
				child[3] = new OctreeNode(bb, obj, objBounds, this, d);
			}
		}
		if (splitx) { // near SE
			bb = new BoundingBox(xmax, midx, ymin, midy, zmin, midz);
			child[4] = new OctreeNode(bb, obj, objBounds, this, d);
			if (splitz) { // far SE
				bb = new BoundingBox(xmax, midx, ymin, midy, zmax, midz);
				child[5] = new OctreeNode(bb, obj, objBounds, this, d);
			}
			if (splity) { // near NE
				bb = new BoundingBox(xmax, midx, ymax, midy, zmin, midz);
				child[6] = new OctreeNode(bb, obj, objBounds, this, d);
				if (splitz) { // far NE
					bb = new BoundingBox(xmin, midx, ymax, midy, zmax, midz);
					child[7] = new OctreeNode(bb, obj, objBounds, this, d);
				}
			}
		}
		obj = null; // Mark the splitting node (not terminal)
	}

	/**
	 * The method should be invoked on the terminal node of the octree 
	 * 
	 * @return SceneObjects belonging to this node
	 */
	public SceneObject[] getObjects() {
		if (obj == null)
			throw new RuntimeException("OctreeNode.getObjects "
					+ "invocado sobre un nodo no terminal.");
		return obj;
	}

	/**
	 * This method shood be invoked on the root of the octree.
	 * 
	 * @returns OctreeNode terminal that contains the point (if not, then null)
	 */
	public OctreeNode findNode(Vector3d pos) {
		OctreeNode current;

		if (!contains(pos))
			return null;
		current = this;
		while (current.obj == null) {
			if (pos.x > current.midx) {
				if (pos.y > current.midy) {
					if (pos.z > current.midz)
						current = current.child[7];
					else
						current = current.child[6];
				} else {
					if (pos.z > current.midz)
						current = current.child[5];
					else
						current = current.child[4];
				}
			} else {
				if (pos.y > current.midy) {
					if (pos.z > current.midz)
						current = current.child[3];
					else
						current = current.child[2];
				} else {
					if (pos.z > current.midz)
						current = current.child[1];
					else
						current = current.child[0];
				}
			}
		}
		return current;
	}

	/**
	 * @return Terminal neighbor node where the ray passes through (If not, then null)
	 */
	public OctreeNode findNextNode(Ray r) {
		double t1, t2, tmax = Double.MAX_VALUE;
		Vector3d orig = r.position, dir = r.direction;
		OctreeNode current;

		if (parent == null)
			return null;

		// Find the final point where the ray passes in this node 
		if (dir.x != 0.0) {
			t1 = (xmin - orig.x) / dir.x;
			t2 = (xmax - orig.x) / dir.x;
			if (t1 < t2) {
				if (t2 < tmax)
					tmax = t2;
			} else {
				if (t1 < tmax)
					tmax = t1;
			}
		}
		if (dir.y != 0.0) {
			t1 = (ymin - orig.y) / dir.y;
			t2 = (ymax - orig.y) / dir.y;
			if (t1 < t2) {
				if (t2 < tmax)
					tmax = t2;
			} else {
				if (t1 < tmax)
					tmax = t1;
			}
		}
		if (dir.z != 0.0) {
			t1 = (zmin - orig.z) / dir.z;
			t2 = (zmax - orig.z) / dir.z;
			if (t1 < t2) {
				if (t2 < tmax)
					tmax = t2;
			} else {
				if (t1 < tmax)
					tmax = t1;
			}
		}

		// Put it inside the node
		Vector3d nextPos = new Vector3d(orig.x + dir.x * tmax, orig.y + dir.y
				* tmax, orig.z + dir.z * tmax);
		nextPos.x += (dir.x > 0.0 ? OctreeScene.TOLERANCE
				: -OctreeScene.TOLERANCE);
		nextPos.y += (dir.y > 0.0 ? OctreeScene.TOLERANCE
				: -OctreeScene.TOLERANCE);
		nextPos.z += (dir.z > 0.0 ? OctreeScene.TOLERANCE
				: -OctreeScene.TOLERANCE);

		// Go up the octree to find the node that contains it
		current = parent;
		while (!current.contains(nextPos)) {
			current = current.parent;
			if (current == null)
				return null;
		}

		// Now go back down the octree until a terminal node is encountered
		while (current.obj == null) {
			if (nextPos.x > current.midx) {
				if (nextPos.y > current.midy) {
					if (nextPos.z > current.midz)
						current = current.child[7];
					else
						current = current.child[6];
				} else {
					if (nextPos.z > current.midz)
						current = current.child[5];
					else
						current = current.child[4];
				}
			} else {
				if (nextPos.y > current.midy) {
					if (nextPos.z > current.midz)
						current = current.child[3];
					else
						current = current.child[2];
				} else {
					if (nextPos.z > current.midz)
						current = current.child[1];
					else
						current = current.child[0];
				}
			}
		}
		return current;
	}

	/**
	 * This method should be invoked on the root of the octree.
	 * 
	 * @return Terminal node containing the point where the ray coming from outside of this node passes through. 
	 * (If it does not pass through, then null).
	 */
	public OctreeNode findFirstNode(Ray r) {
		double t1, t2, tmin = -Double.MAX_VALUE, tmax = Double.MAX_VALUE;
		Vector3d orig = r.position, dir = r.direction;

		// Find the point (if any) where the ray enters the node
		if (dir.x == 0.0) {
			if (orig.x < xmin || orig.x > xmax)
				return null;
		} else {
			t1 = (xmin - orig.x) / dir.x;
			t2 = (xmax - orig.x) / dir.x;
			if (t1 < t2) {
				if (t1 > tmin)
					tmin = t1;
				if (t2 < tmax)
					tmax = t2;
			} else {
				if (t2 > tmin)
					tmin = t2;
				if (t1 < tmax)
					tmax = t1;
			}
			if (tmin > tmax || tmax < 0.0)
				return null;
		}
		if (dir.y == 0.0) {
			if (orig.y < ymin || orig.y > ymax)
				return null;
		} else {
			t1 = (ymin - orig.y) / dir.y;
			t2 = (ymax - orig.y) / dir.y;
			if (t1 < t2) {
				if (t1 > tmin)
					tmin = t1;
				if (t2 < tmax)
					tmax = t2;
			} else {
				if (t2 > tmin)
					tmin = t2;
				if (t1 < tmax)
					tmax = t1;
			}
			if (tmin > tmax || tmax < 0.0)
				return null;
		}
		if (dir.z == 0.0) {
			if (orig.z < zmin || orig.z > zmax)
				return null;
		} else {
			t1 = (zmin - orig.z) / dir.z;
			t2 = (zmax - orig.z) / dir.z;
			if (t1 < t2) {
				if (t1 > tmin)
					tmin = t1;
				if (t2 < tmax)
					tmax = t2;
			} else {
				if (t2 > tmin)
					tmin = t2;
				if (t1 < tmax)
					tmax = t1;
			}
			if (tmin > tmax || tmax < 0.0)
				return null;
		}

		// Put it inside the node
		tmin += OctreeScene.TOLERANCE;
		Vector3d nextPos = new Vector3d(orig.x + dir.x * tmin, orig.y + dir.y
				* tmin, orig.z + dir.z * tmin);

		// Return the terminal node containing the point
		return findNode(nextPos);
	}

	/** Sets the midpoints of the node  */
	private void findMidpoints(BoundingBox objBounds[]) {
		// TODO: The efficiency of the octree can be improved if these middle points are not 
		// set to the half of the BoundingBox, but with planes, other planes can be conveniently used (Spanish-English translation not great...)
		Vector3d size = getSize();
		midx = size.x / 2;
		midy = size.y / 2;
		midz = size.z / 2;
	}

}