/**
 * 
 */
package accelerators;

import geometry.BBox;
import geometry.Ray;

import java.util.ArrayList;

import objects.AbstractSceneObject.NotIntersectableException;
import objects.AbstractSceneObject.RefinementException;
import objects.SceneObject;
import scene.Intersection;
import scene.Scene;

/**
 * @author DavidsMac
 * Octree contains the scene
 */
public class Octree {
	
	private Octnode root;
	private ArrayList<SceneObject> lastIntersectedObject = new ArrayList<SceneObject>();
	
	public Octree(Scene scn,int maxdepth) throws RefinementException {
		
		//create bounding box for root node
		BBox rootBox = new BBox();
		ArrayList<BBox> scnBoxes = new ArrayList<BBox>();
		ArrayList<SceneObject> objs = new ArrayList<SceneObject>();
		ArrayList<SceneObject> tmpObjs = new ArrayList<SceneObject>();
		
		for(SceneObject obj: scn.getObjects()) {
			fillBoxesAndObjs(obj, objs, tmpObjs, scnBoxes);
			rootBox = BBox.union(rootBox, obj.getWorldBound());
		}
		root = new Octnode(rootBox, 0, maxdepth); //0 is root's depth
		for(int i = 0; i< objs.size(); i++){
			root.insert(objs.get(i), scnBoxes.get(i));
		}
	}
	
	/**
	 * Checks to see whether or not ray intersects an object in the Octree. this method should be called to initialize
	 * Octree's intersect function
	 * 
	 * @param ray Intersection test ray
	 * @return true if intersection occurs. False otherwise.
	 */
	public boolean IntersectP(Ray ray){
		lastIntersectedObject.clear(); //protection against users misusing IntersectP and filling lastIntersectedObject list
		return root.IntersectP(ray, lastIntersectedObject);
	}
	
	/**
	 * Populates the Intersection for the intersected object. If not called after IntersectP, 
	 * it will first call IntersectP to find the object.
	 * 
	 * @param ray Intersection test ray
	 * @param inter Intersection to be filled with object intersection information
	 * @return true if intersect occurs. False otherwise.
	 * @throws NotIntersectableException 
	 */
	public boolean Intersect(Ray ray, Intersection inter) throws NotIntersectableException{
		if(lastIntersectedObject.isEmpty()){ //Protection from people misusing IntersectP->Intersect call sequence
			if(!root.IntersectP(ray, lastIntersectedObject)){
				return false;
			}
		}
		
		SceneObject IntersectedObject = lastIntersectedObject.remove(0);
		lastIntersectedObject.clear();
		
		return IntersectedObject.Intersect(ray, inter);
	}
	
	/**
	 * FILL IN THIS METHOD!!!
	 * @param object
	 */
	public void insert(SceneObject object){
		
	}
	
	
	/**
	 * 
	 *FILL IN THIS METHOD!!! 
	 * @param id
	 */
	public void delete(int id){
		
	}

	/**
	 *  helper function used to initialize objs and scnBoxes
	 *  
	 * @param obj
	 * @param objs
	 * @param tmpObjs
	 * @param scnBoxes
	 * @throws RefinementException
	 */
	private void fillBoxesAndObjs(SceneObject obj, ArrayList<SceneObject> objs, 
									ArrayList<SceneObject> tmpObjs, ArrayList<BBox> scnBoxes) throws RefinementException{
		if(obj.isIntersectable()) {
			scnBoxes.add(obj.getWorldBound());
			objs.add(obj);
		} else {
			obj.refine(tmpObjs);
			for(SceneObject tmp: tmpObjs){
				objs.add(tmp);
				scnBoxes.add(tmp.getWorldBound());
			}
			tmpObjs.clear();
		}
	}
	
	

}