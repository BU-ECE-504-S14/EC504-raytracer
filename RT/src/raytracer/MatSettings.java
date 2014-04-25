/**
 *
 */

package raytracer;

import java.io.Serializable;

import util.RenderSettingException;

/**
 * Used for storing rendering settings.
 * 
 * @author Aaron Heuckroth
 */
public class MatSettings extends RenderSettings
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MatSettings(int width, int height)
	{
		setPROGRESS(false);
		setMULTITHREADING(true);

		try
		{
			setANTIALIASING(1);
			setWIDTH(width);
			setHEIGHT(height);
			setSHADOW_TYPE(2);
			setREFRACTION(5);
			setREFLECTION(3);
		}
		catch (RenderSettingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setACCELERATE(true);
		setTRANSPARENCY(true);
		setPHONG(true);
	}

}
