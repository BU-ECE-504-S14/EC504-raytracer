package geometry;

import java.io.Serializable;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/*
 * simple wrapper for Vector3d class. Reduces potential for performing an incorrect tranformation.
 */
public class Vec extends Vector3d implements Serializable{

	private static final long serialVersionUID = 2L;
	public final double w = 0;
	
	public Vec(Vec n){
		super(n);
	}
	
	public Vec() {
		super();
	}
	
	public Vec(double x,double y, double z) {
		super(x,y,z);
	}
	
	public Vec(Tuple3d t1) {
		super(t1);
	}
	
	public Vec(Tuple3f t1) {
		super(t1);
	}
	
	public Vec(Vector3d v1) {
		super(v1);
	}
	
	public Vec(Vector3f v1) {
		super(v1);
	}
	

}
