/**
 *
 */

package raytracer;

import java.io.File;
import java.io.Serializable;

import util.RenderSettingException;

/**
 * Used for storing rendering settings.
 * 
 * @author Aaron Heuckroth
 */
public class RenderSettings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean PROGRESS = false;

	private boolean MULTITHREADING = false;
	private int ANTIALIASING = 1;
	private int WIDTH = 100;
	private int HEIGHT = 100;
	private int SHADOW_TYPE = 0;
	private int REFRACTION = 1;
	private int REFLECTION = 1;
	private boolean ACCELERATE = true;
	private boolean TRANSPARENCY = true;
	private boolean PHONG = true;
	private boolean VERBOSE = true;
	private File OUTPUT_PATH = null;
	private int OCTREE_DEPTH = 3;

	public boolean checkOctreeDepth(int d) {
		if (d > 0 && d <= 8) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getOCTREE_DEPTH(){
		return OCTREE_DEPTH;
	}
	
	public void setOCTREE_DEPTH(int d) throws RenderSettingException{
		if (!checkOctreeDepth(d)){
			throw new RenderSettingException("Invalid octree depth! Must be between 1 and 8.");
		}
		else {
			OCTREE_DEPTH = d;
		}
	}

	public boolean checkRefraction(int r) {
		if (r >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkReflection(int r) {
		if (r >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkAntialiasing(int aa) {
		if (aa >= 1 && aa <= 3) {
			return true;
		} else
			return false;
	}

	public boolean checkWidth(int w) {
		if (w > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkHeight(int h) {
		if (h > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkShadow(int s) {
		if (s == 1 || s == 2 || s == 0) {
			return true;
		} else {
			return false;

		}
	}

	public boolean isPROGRESS() {
		return PROGRESS;
	}

	public void setPROGRESS(boolean pROGRESS) {
		PROGRESS = pROGRESS;
	}

	public boolean isMULTITHREADING() {
		return MULTITHREADING;
	}

	public void setMULTITHREADING(boolean mULTITHREADING) {
		MULTITHREADING = mULTITHREADING;
	}

	public int getANTIALIASING() {
		return ANTIALIASING;
	}

	public void setANTIALIASING(int aNTIALIASING) throws RenderSettingException {
		if (!checkAntialiasing(aNTIALIASING)) {
			throw new RenderSettingException(
					"Antialiasing must be 1,2,3, or 4!");
		} else {
			ANTIALIASING = aNTIALIASING;
		}
	}

	public int getWIDTH() {
		return WIDTH;
	}

	public void setWIDTH(int wIDTH) throws RenderSettingException {
		if (!checkWidth(wIDTH)) {
			throw new RenderSettingException("Width must be > 0!");
		}
		WIDTH = wIDTH;
	}

	public int getHEIGHT() {
		return HEIGHT;
	}

	public void setHEIGHT(int hEIGHT) throws RenderSettingException {
		if (!checkHeight(hEIGHT)) {
			throw new RenderSettingException("Height must be > 0!");
		} else {
			HEIGHT = hEIGHT;
		}
	}

	public int getSHADOW_TYPE() {
		return SHADOW_TYPE;
	}

	public void setSHADOW_TYPE(int sHADOW_TYPE) throws RenderSettingException {
		if (!checkShadow(sHADOW_TYPE)) {
			throw new RenderSettingException("Reflections must be >= 0!");
		} else {
			SHADOW_TYPE = sHADOW_TYPE;
		}
	}

	public int getREFRACTION() {
		return REFRACTION;
	}

	public void setREFRACTION(int rEFRACTION) throws RenderSettingException {
		if (!checkRefraction(rEFRACTION)) {
			throw new RenderSettingException("Refractions must be >= 0!");
		} else {
			REFRACTION = rEFRACTION;
		}

	}

	public int getREFLECTION() {
		return REFLECTION;
	}

	public void setREFLECTION(int rEFLECTION) throws RenderSettingException {
		if (!checkReflection(rEFLECTION)) {
			throw new RenderSettingException("Relfections must be >= 0!");
		} else {
			REFLECTION = rEFLECTION;
		}
	}

	public boolean isACCELERATE() {
		return ACCELERATE;
	}

	public void setACCELERATE(boolean aCCELERATE) {
		ACCELERATE = aCCELERATE;
	}

	public boolean isTRANSPARENCY() {
		return TRANSPARENCY;
	}

	public void setTRANSPARENCY(boolean tRANSPARENCY) {
		TRANSPARENCY = tRANSPARENCY;
	}

	public boolean isPHONG() {
		return PHONG;
	}

	public void setPHONG(boolean pHONG) {
		PHONG = pHONG;
	}

	public boolean isVERBOSE() {
		return VERBOSE;
	}

	public void setVERBOSE(boolean vERBOSE) {
		VERBOSE = vERBOSE;
	}

	public File getOUTPUT_PATH() {
		return OUTPUT_PATH;
	}

	public void setOUTPUT_PATH(File f) {
		OUTPUT_PATH = f;
	}

}
