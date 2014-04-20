/**
 * 
 */
package accelerators;

import geometry.BBox;
import geometry.Ray;

import java.util.ArrayList;

import accelerators.Octnode.SplitBeyondMaxDepthException;
import objects.AbstractSceneObject.NotIntersectableException;
import objects.AbstractSceneObject.RefinementException;
import objects.SceneObject;
import scene.Intersection;
import scene.Scene;

/**
 * Octree contains the scene
 */
public class Octree implements AbstractAccelerator {
	
	private Octnode root;
	private ArrayList<SceneObject> lastIntersectedObject = new ArrayList<SceneObject>();
	
	public Octree(Scene scn,int maxdepth) throws RefinementException, SplitBeyondMaxDepthException {
		
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
	
	/* (non-Javadoc)
	 * @see accelerators.AbstractAccelerator#IntersectP(geometry.Ray)
	 */
	@Override
	public boolean IntersectP(Ray ray) throws NotIntersectableException{
		lastIntersectedObject.clear(); //protection against users misusing IntersectP and filling lastIntersectedObject list
		return root.IntersectP(ray, lastIntersectedObject);
	}
	
	/* (non-Javadoc)
	 * @see accelerators.AbstractAccelerator#Intersect(geometry.Ray, scene.Intersection)
	 */
	@Override
	public boolean Intersect(Ray ray, Intersection inter) throws NotIntersectableException{
		if(lastIntersectedObject.isEmpty()){ //Protection from people misusing IntersectP->Intersect call sequence
			if(!root.IntersectP(ray, lastIntersectedObject)){
				return false;
			}
		}
        if(lastIntersectedObject.isEmpty()){
        	System.out.println("h");
        }
		SceneObject IntersectedObject = lastIntersectedObject.remove(0);
		lastIntersectedObject.clear();
		
		return IntersectedObject.Intersect(ray, inter);
	}
	
	/* (non-Javadoc)
	 * @see accelerators.AbstractAccelerator#insert(objects.SceneObject)
	 */
	@Override
	public void insert(SceneObject object){
		
	}
	
	
	/* (non-Javadoc)
	 * @see accelerators.AbstractAccelerator#delete(int)
	 */
	@Override
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