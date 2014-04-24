/**
 *
 */

package raytracer;

import java.io.Serializable;

/**
 * Used for storing rendering settings.
 * @author Aaron Heuckroth 
 */
public class RenderSettings implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean PROGRESS = false;
	public boolean MULTITHREADING = false;
	public int ANTIALIASING = 1;
	public int WIDTH = 100;
	public int HEIGHT = 100;
	public int SHADOW_TYPE = 0;
	public int REFRACTION = 1;
	public int REFLECTION = 1;
	public boolean ACCELERATE = true;
	public boolean TRANSPARENCY = true;
	public boolean PHONG = true;
	
	public RenderSettings(){
		
	}
	
	
}
