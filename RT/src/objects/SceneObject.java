package objects;

import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector3d;

import objects.AbstractSceneObject.NotIntersectableException;
import objects.AbstractSceneObject.RefinementException;
import scene.Intersection;
import scene.Transformation;


/**
 * Object interface for the scene
 */
public interface SceneObject {
	
	/** Get the name of this SceneObject, if it has one. 
	 * 
	 * @return a String representing the name of this SceneObject.
	 */
	public String getName();

	/** @return Material properties of the object */
	public Material getMaterial();
	
	/**
	 * Simplified intersection test. If ray intersects object simply return true.
	 * 
	 * @param ray that is shooting
	 * @return boolean as to whether or not object was intersected
	 */
	public boolean IntersectP(Ray ray) throws NotIntersectableException;
	
	/**
	 * tests whether or not object is intersectable
	 * 
	 * @return true if object is intersectable
	 */
	public boolean isIntersectable();
	
	/**
	 * Breaks unintersectable object into an ArrayList of smaller intersectable objects
	 * 
	 * @param SOA empty ArrayList to be filled with intersectable objects
	 * @throws RefinementException
	 */
	public void refine(ArrayList<SceneObject> SOA) throws RefinementException;

	/**
	 * Test if a ray intersects an object.  If an intersection occurs, update inter object.
	 * with information about the intersection.
	 * 
	 * @param ray Ray that is shooting.
	 * @param inter differential geometry to fill with intersection information
	 * @return boolean as to whether or not object was intersected
	 */
	public boolean Intersect(Ray ray, Intersection inter) throws NotIntersectableException;

	/**
	 * @param bb BoundingBox to intersect
	 * @return boolean Return true if the object intersects the box
	 */

	/**
	 * @return Set of child objects (e.g. triangles in TriangleSets)
	 */
	public Collection<? extends SceneObject> getChildren();


}
