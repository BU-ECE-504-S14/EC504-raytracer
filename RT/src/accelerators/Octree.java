/**
 * 
 */
package accelerators;

import geometry.BBox;
import geometry.Ray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
public class Octree implements AbstractAccelerator, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Octnode root;
	private final float scnBoxEpsilon = 0f;
	private final float objectBoxEpsilon = 0f;

	public Octree(Scene scn, int maxdepth) throws RefinementException, SplitBeyondMaxDepthException
	{

		// create bounding box for root node
		BBox rootBox = new BBox();
		ArrayList<BBox> scnBoxes = new ArrayList<BBox>();
		ArrayList<SceneObject> objs = new ArrayList<SceneObject>();
		ArrayList<SceneObject> tmpObjs = new ArrayList<SceneObject>();

		for (SceneObject obj : scn.getObjects())
		{
			fillBoxesAndObjs(obj, objs, tmpObjs, scnBoxes);
			rootBox = BBox.union(rootBox, obj.getWorldBound());
		}
		rootBox.expand(scnBoxEpsilon);
		root = new Octnode(rootBox, 0, maxdepth); // 0 is root's depth
		for (int i = 0; i < objs.size(); i++)
		{
			root.insert(objs.get(i), scnBoxes.get(i));
		}
	}

	public boolean intersectTraverse(Ray ray, Intersection inter) throws NotIntersectableException
	{
		return root.intersectTraverse(ray, inter);
	}

	public static boolean intersectTraverse(Ray ray, Intersection inter, Octnode node)
			throws NotIntersectableException
	{
		return node.intersectTraverse(ray, inter);
	}

	public static boolean Intersect(Ray ray, Intersection inter, Octnode node)
			throws NotIntersectableException
	{
		ArrayList<Octleaf> intersectedLeaves = new ArrayList<Octleaf>();
		node.IntersectP(ray, intersectedLeaves);

		HashSet<SceneObject> allObjects = new HashSet<SceneObject>();
		for (int ii = 0; ii < intersectedLeaves.size(); ii++)
		{
			Octleaf currentLeaf = intersectedLeaves.get(ii);
			ArrayList<SceneObject> leafObjects = currentLeaf.getObjects();
			for (int i = 0; i < leafObjects.size(); i++)
			{
				allObjects.add(leafObjects.get(i));
			}
		}

		SceneObject nearest = null;

		if (allObjects.size() != 0)
		{
			for (SceneObject obj : allObjects)
			{
				if (obj.IntersectP(ray))
				{
					nearest = obj;
				}
			}
			if (nearest != null)
			{
				return nearest.Intersect(ray, inter);
			}
		}

		return false;
	}

	@Override
	public boolean Intersect(Ray ray, Intersection inter) throws NotIntersectableException
	{
		return Intersect(ray, inter, root);
		//return intersectTraverse(ray, inter, root);
	}

	/*
	 * (non-Javadoc)
	 * @see accelerators.AbstractAccelerator#insert(objects.SceneObject)
	 */
	@Override
	public void insert(SceneObject object)
	{
		// TODO implement insert (if time allows)
	}

	/*
	 * (non-Javadoc)
	 * @see accelerators.AbstractAccelerator#delete(int)
	 */
	@Override
	public void delete(int id)
	{
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
			ArrayList<SceneObject> tmpObjs, ArrayList<BBox> scnBoxes) throws RefinementException
	{
		BBox correctedBox;
		if (obj.isIntersectable())
		{
			scnBoxes.add(obj.getWorldBound());
			objs.add(obj);
		}
		else
		{
			obj.refine(tmpObjs);
			for (SceneObject tmp : tmpObjs)
			{
				objs.add(tmp);
				correctedBox = tmp.getWorldBound();
				correctedBox.expand(objectBoxEpsilon);
				scnBoxes.add(new BBox(correctedBox));
			}
			tmpObjs.clear();
		}
	}

}