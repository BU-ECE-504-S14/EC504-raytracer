/**
 * 
 */
package accelerators;

import geometry.BBox;

/**
 * @author DavidsMac
 * This has an extra field which contains an arraylist of scene objects.
 */
public class Octleaf extends Octnode {

	public Octleaf(BBox bBox, int i, int maxdepth) {
		super(bBox, i, maxdepth);
	}

}
