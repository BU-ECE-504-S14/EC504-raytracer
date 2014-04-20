/**
 * 
 */
package accelerators;

import java.util.ArrayList;

import objects.AbstractSceneObject.NotIntersectableException;
import objects.SceneObject;
import geometry.BBox;
import geometry.Ray;

/**
 * @author DavidsMac
 * This has an extra field which contains an arraylist of scene objects.
 */
public class Octleaf extends Octnode {
	

	private ArrayList<SceneObject> containedObjects = null;
	
	public Octleaf(BBox bBox, int i, int maxdepth) {
		super(bBox, i, maxdepth);
	}
	
	@Override  
	public void split() throws SplitBeyondMaxDepthException {
		throw new SplitBeyondMaxDepthException(); //You should not be splitting leaf nodes.
	}
	
	@Override 
	public void insert(SceneObject scnobj, BBox objbb){
		if(!occupied){
			occupied = true;
			containedObjects = new ArrayList<SceneObject>();
		}
		containedObjects.add(scnobj);
	}
	
	@Override
	public boolean IntersectP(Ray ray, ArrayList<SceneObject> lastIntersectedObject) throws NotIntersectableException{
		
		boolean intersected = false;
	    
		if(occupied && bbox.IntersectP(ray, new float[2])){
	    	for(SceneObject obj : containedObjects){
	    		if (obj.IntersectP(ray)){
	    			lastIntersectedObject.add(obj);
	    			intersected = true;
	    		}
	    	}
	    }
	    
	   	return intersected; 
	}

}
