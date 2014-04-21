/**
 * 
 */
package accelerators;

import java.util.ArrayList;
import java.util.Arrays;

import objects.AbstractSceneObject.NotIntersectableException;
import objects.SceneObject;
import geometry.BBox;
import geometry.Pt;
import geometry.Ray;

/**
 * contains 8 children octnodes/leafs is intersectable
 */
public class Octnode {
	
	public class SplitBeyondMaxDepthException extends Exception {
		private static final long serialVersionUID = 1L;
		
	}
	
	private float octBoxEpsilon = 0;
	private Octnode children[] = null;
	protected BBox bbox;
	protected boolean occupied = false;
	protected int depth;
	public final int maxdepth;
	
	public Octnode(BBox bb,int dep, int mdep){
		bbox = new BBox(bb);
		depth = dep;
		maxdepth = mdep;
	}
	
	public void split() throws SplitBeyondMaxDepthException{
		Pt centerpt = bbox.lerp(0.5f,0.5f, 0.5f);
		Pt[] corners = bbox.getCorners();
		boolean maxDepthReached = (depth == maxdepth - 1);
		
		//if depth is less than max depth initialize to ocnode, if on last level initialize to ocleaf
		if(!maxDepthReached) {children = new Octnode[8];}
		else if(maxDepthReached) {children = new Octleaf[8];}
		else {throw new SplitBeyondMaxDepthException();}
		
		for(int i=0 ; i<8; i++){
			if(!maxDepthReached)
				{children[i] = new Octnode(new BBox(corners[i], centerpt), depth+1, maxdepth);}
			else
				{children[i] = new Octleaf(new BBox(corners[i], centerpt), depth+1, maxdepth);}
		}
	}
	
	public void insert(SceneObject scnobj, BBox objbb) throws SplitBeyondMaxDepthException{
		
		if(occupied == false){
			occupied = true; //I have something in me!
			split();
		}

		for(Octnode child: children){
			if(child.bbox.overlaps(objbb, octBoxEpsilon)) 
				{ child.insert(scnobj, objbb); }
		}

	}
	
	
	public boolean IntersectP(Ray ray, ArrayList<SceneObject> lastIntersectedObject) throws NotIntersectableException{
		boolean intersected[] = new boolean[8];
		
		/* David's Java is smarter than your code note:
		 * Guess what everyone; Java is sooooooo smart. Its much smarter than me, or my code. In fact, Its so smart that it 
		 * even knows the future. OOOOOooooooOOOOOooooo.
		 * Like when I wanted to write the below function like this:
		 * 
		 * for(int ii = 0; ii <= 8; ii++) { //TODO figure out why reverse iteration is required.		
	     *		inter = inter || children[ii].IntersectP(ray, lastIntersectedObject);
	     *	}
	     * 
	     *  Java was sooo smart and already knew the future anyway and it just knew I didn't want 
	     *  to call IntersectP every. single. time. I ran the for loop. 
	     *  
	     *  Uggg... it makes me tired just thinking about doing all of those function calls yeash! So Java just never called
	     *  IntersectP after inter was set to true. It just... never... called... it..........!!!!!!!!!!!!!!!!!!!
	     *  
	     *  Which was great! I didn't want my scenes to render correctly. No, I wanted confusing impossible to understand visual
	     *  gibberish that was practically impossible to debug because nothing was actually wrong with my code.
	     *  
	     *  Thanks for the "compiler optimization" Java!
	     *  
	     *  Love, David
		 */
		
		for(int i = 0; i < 8; i++) {
			intersected[i] = false;
		}
		
	    if(occupied && bbox.IntersectP(ray, new float[2])){
	    	//for(Octnode child: children){
	    	for(int ii = 7; ii >= 0; ii--) { //TODO figure out why reverse iteration is required.
	    		//intersected = child.IntersectP(ray, lastIntersectedObject);
	    		intersected[ii] = children[ii].IntersectP(ray, lastIntersectedObject);
	    	}
	    }
	    
	    boolean inter = false;
	    for(int i = 0; i < 8; i++) {
	    	inter = inter || intersected[i];
	    }
	    
	   	return inter; 
	}
	
}
