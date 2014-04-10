package objects;

import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Vector3d;

import scene.Intersection;
import scene.Transformation;


public abstract class AbstractSceneObject implements SceneObject {

	public class RefinementException extends Exception {
		private static final long serialVersionUID = 1L;
		
	}
	public Material material = new Material();
	
	public Material getMaterial() {
		return material;
	}

	public abstract Vector3d getNormalAt(Vector3d point);
	
	public boolean isIntersectable(){
		return true;
	}
	
	public void refine(ArrayList<SceneObject> SOA) throws RefinementException {
		throw new RefinementException();
	}
	
	public abstract boolean IntersectP(Ray ray);

	public abstract boolean Intersect(Ray ray, Intersection inter);

	public Collection<? extends SceneObject> getChildren() {
		return null;
	}

}
