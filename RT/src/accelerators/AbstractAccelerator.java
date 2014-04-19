package accelerators;

import geometry.Ray;
import objects.SceneObject;
import objects.AbstractSceneObject.NotIntersectableException;
import scene.Intersection;

public interface AbstractAccelerator {

	/**
	 * Checks to see whether or not ray intersects an object in the Octree. this method should be called to initialize
	 * Octree's intersect function
	 * 
	 * @param ray Intersection test ray
	 * @return true if intersection occurs. False otherwise.
	 * @throws NotIntersectableException 
	 */
	public abstract boolean IntersectP(Ray ray)
			throws NotIntersectableException;

	/**
	 * Populates the Intersection for the intersected object. If not called after IntersectP, 
	 * it will first call IntersectP to find the object.
	 * 
	 * @param ray Intersection test ray
	 * @param inter Intersection to be filled with object intersection information
	 * @return true if intersect occurs. False otherwise.
	 * @throws NotIntersectableException 
	 */
	public abstract boolean Intersect(Ray ray, Intersection inter)
			throws NotIntersectableException;

	/**
	 * FILL IN THIS METHOD!!!
	 * @param object
	 */
	public abstract void insert(SceneObject object);

	/**
	 * 
	 *FILL IN THIS METHOD!!! 
	 * @param id
	 */
	public abstract void delete(int id);

}