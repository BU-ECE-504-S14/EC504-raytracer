package objects;

import geometry.BBox;
import geometry.Ray;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import scene.Intersection;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and
 *         Bryant Moquist
 */
public abstract class AbstractSceneObject implements SceneObject, Serializable {

	UUID ID;

	private static final long serialVersionUID = 1L;

	public class RefinementException extends Exception {
		private static final long serialVersionUID = 1L;

	}

	public class NotIntersectableException extends Exception {

		private static final long serialVersionUID = 1L;

	}

	public Material material = new Material();
	public String name = "N/A";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public UUID getUUID() {
		return ID;
	}

	/** Sets the name of this SceneObject, for identification in the GUI. */
	public void setName(String s) {
		name = s;
	}

	@Override
	public Material getMaterial() {
		return material;
	}

	@Override
	public boolean isIntersectable() {
		return true;
	}

	@Override
	public void refine(ArrayList<SceneObject> SOA) throws RefinementException {
		throw new RefinementException();
	}

	@Override
	public boolean IntersectP(Ray ray) throws NotIntersectableException {
		throw new NotIntersectableException();
	}

	@Override
	public boolean Intersect(Ray ray, Intersection inter)
			throws NotIntersectableException {
		throw new NotIntersectableException();
	}

	@Override
	public abstract BBox getWorldBound();

	@Override
	public Collection<? extends SceneObject> getChildren() {
		return null;
	}

}
