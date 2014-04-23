/**
 * 
 */
package accelerators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import objects.AbstractSceneObject.NotIntersectableException;
import objects.SceneObject;
import scene.Intersection;
import geometry.BBox;
import geometry.Pt;
import geometry.Ray;

/**
 * contains 8 children octnodes/leafs is intersectable
 */
public class Octnode implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class SplitBeyondMaxDepthException extends Exception
	{
		private static final long serialVersionUID = 1L;

	}

	private final float octBoxEpsilon = 0f;
	private Octnode children[] = null;
	protected final BBox bbox;
	protected boolean occupied = false;
	protected final int depth;
	public final int maxdepth;

	protected float disToBBoxIn = Float.POSITIVE_INFINITY;
	protected float disToBBoxOut = Float.POSITIVE_INFINITY;

	public Octnode(BBox bb, int dep, int mdep)
	{
		bbox = new BBox(bb);
		depth = dep;
		maxdepth = mdep;
	}

	public void split() throws SplitBeyondMaxDepthException
	{
		Pt centerpt = bbox.lerp(0.5f, 0.5f, 0.5f);
		Pt[] corners = bbox.getCorners();
		boolean maxDepthReached = (depth == maxdepth - 1);

		// if depth is less than max depth initialize to octnode, if on last level initialize to
		// ocleaf
		if (!maxDepthReached)
		{
			children = new Octnode[8];
		}
		else if (maxDepthReached)
		{
			children = new Octleaf[8];
		}
		else
		{
			throw new SplitBeyondMaxDepthException();
		}

		for (int i = 0; i < 8; i++)
		{
			if (!maxDepthReached)
			{
				children[i] = new Octnode(new BBox(corners[i], centerpt), depth + 1, maxdepth);
			}
			else
			{
				children[i] = new Octleaf(new BBox(corners[i], centerpt), depth + 1, maxdepth);
			}
		}
	}

	public boolean isHit(Ray ray)
	{
		float[] hits = new float[2];
		if (bbox.IntersectP(ray, hits))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public ArrayList<Octnode> getIntersectedChildren(Ray ray)
	{
		ArrayList<Octnode> octnodes = new ArrayList<Octnode>();
		if (!occupied)
		{
			return null;
		}

		for (int i = 0; i < children.length; i++)
		{
			if (children[i].isHit(ray))
			{
				octnodes.add(children[i]);
			}
		}
		if (octnodes.size() > 0)
		{
			return octnodes;
		}
		else
		{
			return null;
		}
	}

	public void insert(SceneObject scnobj, BBox objbb) throws SplitBeyondMaxDepthException
	{

		if (occupied == false)
		{
			occupied = true;
			split();
		}

		for (Octnode child : children)
		{
			if (child.bbox.overlaps(objbb, octBoxEpsilon))
			{
				child.insert(scnobj, objbb);
			}
		}
	}

	public boolean IntersectPQuick(Ray ray)
	{
		float[] hits = new float[2];
		if (occupied && bbox.IntersectP(ray, hits))
		{
			return true;
		}
		else
			return false;
	}

	public float distanceToBBoxIn()
	{
		return this.disToBBoxIn;
	}

	public float distanceToBBoxOut()
	{
		return this.disToBBoxOut;
	}

	public boolean intersectTraverse(Ray ray, Intersection inter) throws NotIntersectableException
	{
		ArrayList<Octnode> hitChildren = new ArrayList<Octnode>();
		for (Octnode o : children)
		{
			if (o.IntersectPQuick(ray))
			{
				hitChildren.add(o);
			}
		}

		Collections.sort(hitChildren, new OctnodeComparator());

		for (Octnode o : hitChildren)
		{
			if (o.intersectTraverse(ray, inter))
			{
				return true;
			}
		}
		return false;
	}

	public boolean IntersectP(Ray ray, ArrayList<Octleaf> IntersectedLeaves)
			throws NotIntersectableException
	{

		boolean intersected[] = new boolean[8];

		for (int i = 0; i < 8; i++)
		{
			intersected[i] = false;
		}

		if (occupied && bbox.IntersectP(ray, new float[2]))
		{
			for (int ii = 0; ii < 8; ii++)
			{
				intersected[ii] = children[ii].IntersectP(ray, IntersectedLeaves);
			}
		}

		boolean inter = false;
		for (int i = 0; i < 8; i++)
		{
			inter = inter || intersected[i];
		}

		return inter;

	}

}
