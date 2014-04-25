/**
 * 
 */
package accelerators;

import java.io.Serializable;
import java.util.ArrayList;

import objects.AbstractSceneObject.NotIntersectableException;
import objects.SceneObject;
import objects.Triangle;
import scene.Intersection;
import geometry.BBox;
import geometry.Ray;

/**
 * @author DavidsMac This has an extra field which contains an arraylist of scene objects.
 */
public class Octleaf extends Octnode implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<SceneObject> containedObjects = null;

	public Octleaf(BBox bBox, int i, int maxdepth)
	{
		super(bBox, i, maxdepth);
	}

	public Octleaf(Octleaf o)
	{
		super(o);
		containedObjects = o.containedObjects;
	}

	@Override
	public void split() throws SplitBeyondMaxDepthException
	{
		// You should not be splitting leaf nodes!
		throw new SplitBeyondMaxDepthException();
	}

	@Override
	public void insert(SceneObject scnobj, BBox objbb)
	{
		if (!occupied)
		{
			occupied = true;
			containedObjects = new ArrayList<SceneObject>();
		}
		containedObjects.add(scnobj);
	}

	public ArrayList<SceneObject> getContents()
	{
		return containedObjects;
	}

	@Override
	public boolean intersectTraverse(Ray ray, Intersection inter) throws NotIntersectableException
	{
		SceneObject nearest = nearestIntersect(ray);
		if (nearest != null)
		{
			nearest.Intersect(ray, inter);
			return true;
		}

		return false;
	}

	@Override
	synchronized public boolean IntersectP(Ray ray, ArrayList<Octleaf> IntersectedLeaves)
			throws NotIntersectableException
	{

		boolean intersected = false;
		float[] inOut = { 1, 1 };

		if (occupied && bbox.IntersectP(ray, inOut))
		{
			this.disToBBoxIn = inOut[0];
			this.disToBBoxOut = inOut[1];
			IntersectedLeaves.add(new Octleaf(this));
			intersected = true;
		}

		return intersected;
	}

	public ArrayList<SceneObject> getObjects()
	{
		return this.containedObjects;
	}

	public SceneObject nearestIntersect(Ray ray) throws NotIntersectableException
	{
		SceneObject nearest = null;

		if (containedObjects != null)
		{
			for (SceneObject obj : containedObjects)
			{
				if (obj.IntersectP(ray))
				{
					nearest = obj;
				}
			}
		}

		return nearest;

	}

}
