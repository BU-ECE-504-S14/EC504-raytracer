package objects;

import java.util.Collection;

import javax.vecmath.Vector3d;

import raytracer.Ray;
import scene.BoundingBox;
import scene.Intersection;
import scene.Transformation;

/**
 * Superclass of all of the objects in the scene.  Contains attributes and abstract methods common to all objects.
 */
public abstract class AbstractSceneObject implements SceneObject {

	public Material material = new Material();
	
	public Material getMaterial() {
		return material;
	}

	public abstract Vector3d getNormalAt(Vector3d point);

	public abstract void transform(Transformation t);

	public abstract BoundingBox getBounds();

	public boolean intersectsBox(BoundingBox bb) {
		return bb.intersects(getBounds());
	}

	public abstract Intersection intersectsRay(Ray ray);

	public Collection<? extends SceneObject> getChildren() {
		return null;
	}

}
