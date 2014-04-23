/**
 * 
 */
package accelerators;

import java.util.ArrayList;

import objects.AbstractSceneObject.NotIntersectableException;
import objects.SceneObject;
import objects.Triangle;
import geometry.BBox;
import geometry.Ray;

/**
 * @author DavidsMac This has an extra field which contains an arraylist of
 *         scene objects.
 */
public class Octleaf extends Octnode {

	private float disToBBoxIn = Float.POSITIVE_INFINITY;
	private float disToBBoxOut = Float.POSITIVE_INFINITY;

	private ArrayList<SceneObject> containedObjects = null;

	public Octleaf(Octleaf o){
		super(o.bbox, o.depth, o.maxdepth);
		disToBBoxIn = o.distanceToBBoxOut();
		disToBBoxOut = o.distanceToBBoxOut();
		containedObjects = o.containedObjects;
	}
	
	public Octleaf(BBox bBox, int i, int maxdepth) {
		super(bBox, i, maxdepth);
	}
	
	
	@Override
	public void split() throws SplitBeyondMaxDepthException {
		 // You should not be splitting leaf nodes!
		throw new SplitBeyondMaxDepthException();
	}

	@Override
	public void insert(SceneObject scnobj, BBox objbb) {
		if (!occupied) {
			occupied = true;
			containedObjects = new ArrayList<SceneObject>();
		}
		containedObjects.add(scnobj);
	}

	public float distanceToBBoxIn() {
		return this.disToBBoxIn;
	}

	public float distanceToBBoxOut() {
		return this.disToBBoxOut;
	}
	
	public ArrayList<SceneObject> getContents(){
		return containedObjects;
	}

	@Override
	synchronized public boolean IntersectP(Ray ray, ArrayList<Octleaf> IntersectedLeaves)
			throws NotIntersectableException {

		boolean intersected = false;
		float[] inOut = { 1, 1 };

		 if(occupied && bbox.IntersectP(ray, inOut)){
			this.disToBBoxIn = inOut[0];
			this.disToBBoxOut = inOut[1];
			IntersectedLeaves.add(new Octleaf(this));
			intersected = true;
		}

		return intersected;
	}

	public ArrayList<SceneObject> getObjects() {
		return this.containedObjects;
	}

	public SceneObject nearestIntersect(Ray ray)
			throws NotIntersectableException {

		SceneObject nearest = null;

		if (containedObjects != null) {
			for (SceneObject obj : containedObjects) {
				if (obj.IntersectP(ray)) {
					nearest = obj;
				}
			}
		}

		return nearest;

	}

}
