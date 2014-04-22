package objects;

import geometry.BBox;
import geometry.Ray;
import geometry.Transformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.vecmath.Vector3d;

import scene.Intersection;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public abstract class AbstractSceneObject implements SceneObject, Serializable
{
	protected final static AtomicInteger NEXT_ID = new AtomicInteger();
	protected final Integer id;
	private static final long serialVersionUID = 1L;

	public AbstractSceneObject(){
		id = NEXT_ID.getAndIncrement();
	}
	
	public AbstractSceneObject(int parentId){
		id = parentId;
	}
	
	public class RefinementException extends Exception {
		private static final long serialVersionUID = 1L;
		
	}
	
	public class NotIntersectableException extends Exception {

		private static final long serialVersionUID = 1L;
		
	}
	
	public Material material = new Material();
	public String name = "N/A";

	@Override
	public String getName()
	{
		return name;
	}
	
	/** Sets the name of this SceneObject, for identification in the GUI. */
	public void setName(String s){
		name = s;
	}

	@Override
	public Material getMaterial()
	{
		return material;
	}
	
	public boolean isIntersectable(){
		return true;
	}
	
	public void refine(ArrayList<SceneObject> SOA) throws RefinementException {
		throw new RefinementException();
	}
	
	public boolean IntersectP(Ray ray) throws NotIntersectableException {
		throw new NotIntersectableException();
	}

	public boolean Intersect(Ray ray, Intersection inter) throws NotIntersectableException {
		throw new NotIntersectableException();
	}
	
	public abstract BBox getWorldBound();

	@Override
	public Collection<? extends SceneObject> getChildren()
	{
		return null;
	}
	
	public int getID(){
		return id.intValue();
	}
}
