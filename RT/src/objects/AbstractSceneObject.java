package objects;

import java.util.Collection;

import javax.vecmath.Vector3d;

import raytracer.Ray;
import scene.Intersection;
import scene.Transformation;


public abstract class AbstractSceneObject implements SceneObject {

	public Material material = new Material();
	
	public Material getMaterial() {
		return material;
	}

	public abstract Vector3d getNormalAt(Vector3d point);

	public abstract void transform(Transformation t);

	public abstract Intersection intersectsRay(Ray ray);

	public Collection<? extends SceneObject> getChildren() {
		return null;
	}

}
