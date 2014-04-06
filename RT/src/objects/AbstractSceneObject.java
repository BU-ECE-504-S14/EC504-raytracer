package objects;

import java.util.Collection;

import javax.vecmath.Vector3d;

import scene.DifferentialGeometry;
import scene.Intersection;
import scene.Transformation;


public abstract class AbstractSceneObject implements SceneObject {

	public Material material = new Material();
	
	public Material getMaterial() {
		return material;
	}

	public abstract Vector3d getNormalAt(Vector3d point);

	public abstract boolean intersect(Ray ray, DifferentialGeometry dg);

	public Collection<? extends SceneObject> getChildren() {
		return null;
	}

}
