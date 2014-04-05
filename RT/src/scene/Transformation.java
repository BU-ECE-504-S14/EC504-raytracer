package scene;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import objects.Pt;
import objects.Vec;
import raytracer.Util;

/**
 * Utility class used for Matrix conversions.
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class Transformation {

	public Vector3d translation = new Vector3d(0, 0, 0);

	public AxisAngle4d rotation = new AxisAngle4d(0, 0, 1, 0);

	public Vector3d scale = new Vector3d(1, 1, 1);
	
	public Matrix4d o2w;
	
	public Matrix4d w2o;
	
	public Transformation(Vector3d scale, Vector3d translation,AxisAngle4d rotation){
		this.translation.set(translation);
		this.scale.set(scale);
		this.rotation.set(rotation);
		o2w = getTransformationMatrix();
		w2o = getInverseTransformationMatrix();
	}

	/** Create the transformation pipeline*/
	public Matrix4d getTransformationMatrix() {
		Matrix4d rotationMatrix = getRotationMatrix();
		Matrix4d translationMatrix = new Matrix4d(1, 0, 0, -translation.x, 0, 1, 0, -translation.y,
				0, 0, 1, -translation.z, 0, 0, 0, 1);
		translationMatrix.mul(rotationMatrix);
		return translationMatrix;
	}

	public Matrix4d getInverseTransformationMatrix() {
		Matrix4d rotationMatrix = getRotationMatrix();
		Matrix4d translationMatrix = new Matrix4d(1, 0, 0, -translation.x, 0, 1, 0, -translation.y,
				0, 0, 1, -translation.z, 0, 0, 0, 1);
		translationMatrix.mul(rotationMatrix);
		translationMatrix.invert();
		return translationMatrix;
	}
	
	public Matrix4d getRotationMatrix() {
		Matrix4d ret = new Matrix4d();
		ret.setIdentity();
		ret.set(rotation);
		return ret;
	}
	
	/*public void scaleObject(){
		Matrix4d scaleMatrix = new Matrix4d(scale.x, 0, 0, 0, 0, scale.y, 0, 0, 0, 0, scale.z, 0,
				0, 0, 0, 1);
		rotationMatrix.mul(scaleMatrix);
	}*/
	
	public Pt world2Object(Pt p){
		Vector4d v4 = new Vector4d(p.x,p.y,p.z,p.w);
		Util.MultiplyMatrixAndVector(w2o, v4);
		Pt p_copy = new Pt(v4.x,v4.y,v4.z);
		return p_copy;
	}
	
	public Pt object2World(Pt p){
		Vector4d v4 = new Vector4d(p.x,p.y,p.z,p.w);
		Util.MultiplyMatrixAndVector(o2w, v4);
		Pt p_copy = new Pt(v4.x,v4.y,v4.z);
		return p_copy;
	} 
	
	public Vec object2World(Vec v){
		Vector4d v4 = new Vector4d(v.x,v.y,v.z,v.w);
		Util.MultiplyMatrixAndVector(o2w, v4);
		Vec v_copy = new Vec(v4.x,v4.y,v4.z);
		return v_copy;
	} 
	
	public Vec world2Object(Vec v){
		Vector4d v4 = new Vector4d(v.x,v.y,v.z,v.w);
		Util.MultiplyMatrixAndVector(w2o, v4);
		Vec v_copy = new Vec(v4.x,v4.y,v4.z);
		return v_copy;
	} 
	
}
