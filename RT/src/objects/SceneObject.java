package objects;

import java.util.Collection;

import javax.vecmath.Vector3d;

import scene.DifferentialGeometry;
import scene.Intersection;
import scene.Transformation;


/**
 * Object interface for the scene
 */
public interface SceneObject {

	/** @return Material properties of the object */
	public Material getMaterial();

	/**
	 * Test if a ray intersects an object.  If an intersection occurs, return the
	 * relevant point on the surface of the object where the ray intersects.
	 * If no intersection occurs, return null. 
	 * 
	 * @param ray Ray that is shooting.
	 * @return point of intersection or null
	 */
	public DifferentialGeometry intersectsRay(Ray ray);

	/**
	 * Return the normal at a point on the object's surface.   It is the responsibility of the
	 * user to ensure that the point belongs to the object's surface. 
	 * 
	 * @param point The point whose normal is desired
	 * @return The normal at the point
	 */
	public Vector3d getNormalAt(Vector3d point);

	/**
	 * Transform the object's vertices in accordance with the determined 
	 * transformation matrix
	 * 
	 * @param transformationMatrix The matrix to be applied
	 */
	public void transform(Transformation t);

	/**
	 * @param bb BoundingBox to intersect
	 * @return boolean Return true if the object intersects the box
	 */

	/**
	 * @return Set of child objects (e.g. triangles in TriangleSets)
	 */
	public Collection<? extends SceneObject> getChildren();

}
