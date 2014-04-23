package geometry;

import java.io.Serializable;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/*
 * simple wrapper for Vector3d class. Reduces potential for performing an incorrect tranformation.
 */
public class Normal extends Vector3d implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	public final double w = 0;
	
	public Normal(Normal n){
		super(n);
	}
	
	public Normal() {
		super();
	}
	
	public Normal(double x,double y, double z) {
		super(x,y,z);
	}
	
	public Normal(Tuple3d t1) {
		super(t1);
	}
	
	public Normal(Tuple3f t1) {
		super(t1);
	}
	
	public Normal(Vector3d v1) {
		super(v1);
	}
	
	public Normal(Vector3f v1) {
		super(v1);
	}
	

}
