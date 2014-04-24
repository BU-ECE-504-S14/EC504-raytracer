/**
 *
 */

package raytracer;

import java.io.Serializable;

/**
 * Used for storing rendering settings.
 * @author Aaron Heuckroth 
 */
public class MatSettings extends RenderSettings
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public MatSettings(int width, int height){
		PROGRESS = false;
		MULTITHREADING = true;
		ANTIALIASING = 1;
		WIDTH = width;
		HEIGHT = height;
		SHADOW_TYPE = 2;
		REFRACTION = 5;
		REFLECTION = 3;
		ACCELERATE = true;
		TRANSPARENCY = true;
		PHONG = true;
	}
	
	
}
