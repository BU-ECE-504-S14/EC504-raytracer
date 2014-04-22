/**
 * 
 */
package accelerators;

import geometry.BBox;
import geometry.Ray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import accelerators.Octnode.SplitBeyondMaxDepthException;
import objects.AbstractSceneObject.NotIntersectableException;
import objects.AbstractSceneObject.RefinementException;
import objects.SceneObject;
import scene.Intersection;
import scene.Scene;

/**
 * Octree contains the scene
 */
public class Octree implements AbstractAccelerator {

	private Octnode root;
	private float scnBoxEpsilon = 0;
	private float objectBoxEpsilon = 0;

	public Octree(Scene scn, int maxdepth) throws RefinementException,
			SplitBeyondMaxDepthException {

		// create bounding box for root node
		BBox rootBox = new BBox();
		ArrayList<BBox> scnBoxes = new ArrayList<BBox>();
		ArrayList<SceneObject> objs = new ArrayList<SceneObject>();
		ArrayList<SceneObject> tmpObjs = new ArrayList<SceneObject>();

		for (SceneObject obj : scn.getObjects()) {
			fillBoxesAndObjs(obj, objs, tmpObjs, scnBoxes);
			rootBox = BBox.union(rootBox, obj.getWorldBound());
		}
		rootBox.expand(scnBoxEpsilon);
		root = new Octnode(rootBox, 0, maxdepth); // 0 is root's depth
		for (int i = 0; i < objs.size(); i++) {
			root.insert(objs.get(i), scnBoxes.get(i));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerators.AbstractAccelerator#Intersect(geometry.Ray,
	 * scene.Intersection)
	 * 
	 * @Override public boolean Intersect(Ray ray, Intersection inter) throws
	 * NotIntersectableException{
	 * 
	 * ArrayList<SceneObject> lastIntersectedObject = new
	 * ArrayList<SceneObject>(); boolean intersected = false;
	 * root.IntersectP(ray, lastIntersectedObject);
	 * 
	 * if(!lastIntersectedObject.isEmpty()) { intersected =
	 * lastIntersectedObject.get(0).Intersect(ray, inter); }
	 * 
	 * return intersected; }
	 */

	@Override
	public boolean Intersect(Ray ray, Intersection inter)
			throws NotIntersectableException {

		ArrayList<Octleaf> intersectedLeaves = new ArrayList<Octleaf>();
		root.IntersectP(ray, intersectedLeaves);

		Comparator<Octleaf>comp = new Comparator<Octleaf>() {

			@Override
			public int compare(Octleaf o1, Octleaf o2) {
				Octleaf l1 =  o1;
				Octleaf l2 =  o2;

				if (l1.distanceToBBoxIn() < l2.distanceToBBoxIn()) {
					return -1;
				} else if (l1.distanceToBBoxIn() == l2.distanceToBBoxIn()) {
					return 0;
				} else
					return 1;
			}

		};

		Collections.sort(intersectedLeaves, comp);
		int leaves = intersectedLeaves.size();
		SceneObject nearest = null;
		for (int ii = 0; ii < leaves; ii++) {
			
			
			Octleaf currentLeaf = intersectedLeaves.get(ii);
			/*
			ArrayList<SceneObject> objs = currentLeaf.getObjects();
			int children = objs.size();

			for (int jj = 0; jj < children; jj++){
				if (objs.get(jj).IntersectP(ray)){
					nearest = objs.get(jj);
				}
			}
			*/
			
			nearest = currentLeaf.nearestIntersect(ray);
			if(nearest != null) {
				nearest.Intersect(ray, inter);
				return true;
			}
		}
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerators.AbstractAccelerator#insert(objects.SceneObject)
	 */
	@Override
	public void insert(SceneObject object) {
		// TODO implement insert (if time allows)
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see accelerators.AbstractAccelerator#delete(int)
	 */
	@Override
	public void delete(int id) {
		// TODO implement delete (if time allows)
	}

	/**
	 * helper function used to initialize objs and scnBoxes
	 * 
	 * @param obj
	 * @param objs
	 * @param tmpObjs
	 * @param scnBoxes
	 * @throws RefinementException
	 */
	private void fillBoxesAndObjs(SceneObject obj, ArrayList<SceneObject> objs,
			ArrayList<SceneObject> tmpObjs, ArrayList<BBox> scnBoxes)
			throws RefinementException {
		BBox correctedBox;
		if (obj.isIntersectable()) {
			scnBoxes.add(obj.getWorldBound());
			objs.add(obj);
		} else {
			obj.refine(tmpObjs);
			for (SceneObject tmp : tmpObjs) {
				objs.add(tmp);
				correctedBox = tmp.getWorldBound();
				correctedBox.expand(objectBoxEpsilon);
				scnBoxes.add(new BBox(correctedBox));
			}
			tmpObjs.clear();
		}
	}

}