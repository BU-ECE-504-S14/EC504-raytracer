package objects;

import java.util.Collection;

import javax.vecmath.Vector3d;

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
	boolean IntersectP(Ray ray);

	/**
	 * Test if a ray intersects an object.  If an intersection occurs, update inter object.
	 * with information about the intersection.
	 * 
	 * @param ray Ray that is shooting.
	 * @param inter differential geometry to fill with intersection information
	 * @return boolean as to whether or not object was intersected
	 */
	public boolean Intersect(Ray ray, Intersection inter);

	/**
	 * Return the normal at a point on the object's surface.   It is the responsibility of the
	 * user to ensure that the point belongs to the object's surface. 
	 * 
	 * @param point The point whose normal is desired
	 * @return The normal at the point
	 */
	public Vector3d getNormalAt(Vector3d point);

	/**
	 * @param bb BoundingBox to intersect
	 * @return boolean Return true if the object intersects the box
	 */

	/**
	 * @return Set of child objects (e.g. triangles in TriangleSets)
	 */
	public Collection<? extends SceneObject> getChildren();


}
