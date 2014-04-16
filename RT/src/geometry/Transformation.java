package geometry;

import java.io.Serializable;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import raytracer.Util;

/**
 * Utility class used for Matrix conversions.
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class Transformation implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector3d translation = new Vector3d(0, 0, 0);

	private AxisAngle4d rotation = new AxisAngle4d(0, 0, 1, 0);

	private Vector3d scale = new Vector3d(1, 1, 1);

	public Matrix4d o2w;

	public Matrix4d w2o;

	/**
	 * 
	 * @param scale
	 *            (1,1,1) for no scale
	 * @param translation
	 *            (0,0,0) for no translation
	 * @param rotation
	 *            (0,0,1,0) for no rotation
	 */
	public Transformation(Vector3d scale, Vector3d translation, AxisAngle4d rotation)
	{
		this.translation.set(translation);
		this.scale.set(scale);
		this.rotation.set(rotation);
		o2w = getTransformationMatrix();
		w2o = getInverseTransformationMatrix();
	}

	public Vector3d getTranslation()
	{
		return translation;
	}

	public Vector3d getScale()
	{
		return scale;
	}

	public AxisAngle4d getRotation()
	{
		return rotation;
	}

	public void setTranslation(Vector3d newTrans)
	{
		translation = newTrans;
		o2w = getTransformationMatrix();
		w2o = getInverseTransformationMatrix();
	}

	public void setRotation(AxisAngle4d newRot)
	{
		rotation = newRot;
		o2w = getTransformationMatrix();
		w2o = getInverseTransformationMatrix();
	}

	public void setScale(Vector3d newScale)
	{
		scale = newScale;
		o2w = getTransformationMatrix();
		w2o = getInverseTransformationMatrix();
	}

	public Transformation(Transformation t)
	{
		translation = new Vector3d(t.translation);
		scale = new Vector3d(t.scale);
		rotation = new AxisAngle4d(t.rotation);
		o2w = new Matrix4d(t.o2w);
		w2o = new Matrix4d(t.w2o);
	}

	/** Create the transformation pipeline */
	public Matrix4d getTransformationMatrix()
	{
		Matrix4d rotationMatrix = getRotationMatrix();
		Matrix4d translationMatrix = new Matrix4d(1, 0, 0, translation.x, 0, 1, 0, translation.y,
				0, 0, 1, translation.z, 0, 0, 0, 1);
		Matrix4d scaleMatrix = new Matrix4d(scale.x, 0, 0, 0, 0, scale.y, 0, 0, 0, 0, scale.z, 0,
				0, 0, 0, 1);
		rotationMatrix.mul(scaleMatrix);
		translationMatrix.mul(rotationMatrix);
		return translationMatrix;
	}

	public Matrix4d getInverseTransformationMatrix()
	{
		Matrix4d rotationMatrix = getRotationMatrix();
		Matrix4d translationMatrix = new Matrix4d(1, 0, 0, translation.x, 0, 1, 0, translation.y,
				0, 0, 1, translation.z, 0, 0, 0, 1);
		Matrix4d scaleMatrix = new Matrix4d(scale.x, 0, 0, 0, 0, scale.y, 0, 0, 0, 0, scale.z, 0,
				0, 0, 0, 1);
		rotationMatrix.mul(scaleMatrix);
		translationMatrix.mul(rotationMatrix);
		translationMatrix.invert();
		return translationMatrix;
	}

	public Matrix4d getRotationMatrix()
	{
		Matrix4d ret = new Matrix4d();
		ret.setIdentity();
		ret.set(rotation);
		return ret;
	}

	public Pt world2Object(Pt p)
	{
		Vector4d v4 = new Vector4d(p.x, p.y, p.z, p.w);
		v4 = Util.MultiplyMatrixAndVector(w2o, v4);
		Pt p_copy = new Pt(v4.x, v4.y, v4.z);
		return p_copy;
	}

	public Pt object2World(Pt p)
	{
		Vector4d v4 = new Vector4d(p.x, p.y, p.z, p.w);
		v4 = Util.MultiplyMatrixAndVector(o2w, v4);
		Pt p_copy = new Pt(v4.x, v4.y, v4.z);
		return p_copy;
	}

	public Vec object2World(Vec v)
	{
		Vector4d v4 = new Vector4d(v.x, v.y, v.z, v.w);
		v4 = Util.MultiplyMatrixAndVector(o2w, v4);
		Vec v_copy = new Vec(v4.x, v4.y, v4.z);
		return v_copy;
	}

	public Vec world2Object(Vec v)
	{
		Vector4d v4 = new Vector4d(v.x, v.y, v.z, v.w);
		v4 = Util.MultiplyMatrixAndVector(w2o, v4);
		Vec v_copy = new Vec(v4.x, v4.y, v4.z);
		return v_copy;
	}

	public Ray world2Object(Ray r)
	{
		Ray r_copy = new Ray(r);
		r_copy.direction = this.world2Object(r_copy.direction);
		r_copy.position = this.world2Object(r_copy.position);
		return r_copy;
	}

	public Ray object2World(Ray r)
	{
		Ray r_copy = new Ray(r);
		r_copy.direction = this.object2World(r_copy.direction);
		r_copy.position = this.object2World(r_copy.position);
		return r_copy;

	}

	public String toString()
	{
		return "Position: " + translation + ", rotation: " + rotation + ", scale: " + scale;
	}

	/**
	 * Implements object space to world space normal transformation based on page 88 Pharr
	 * book.
	 * 
	 * @param n
	 *            normal vector to be transformed
	 * @return normal vector in world space
	 */
	public Normal object2World(Normal n)
	{
		float x = (float) n.x;
		float y = (float) n.y;
		float z = (float) n.z;
		return new Normal(w2o.m00 * x + w2o.m10 * y + w2o.m20 * z, w2o.m01 * x + w2o.m11 * y
				+ w2o.m21 * z, w2o.m02 * x + w2o.m12 * y + w2o.m22 * z);
	}

	/**
	 * Implements world space to object space normal transformation based on page 88 Pharr
	 * book.
	 * 
	 * @param n
	 *            normal vector to be transformed
	 * @return normal vector in world space
	 */
	public Normal world2Object(Normal n)
	{
		float x = (float) n.x;
		float y = (float) n.y;
		float z = (float) n.z;
		return new Normal(o2w.m00 * x + o2w.m10 * y + o2w.m20 * z, o2w.m01 * x + o2w.m11 * y
				+ o2w.m21 * z, o2w.m02 * x + o2w.m12 * y + o2w.m22 * z);
	}

	/*
	public BBox object2World(BBox b) {
		
		BBox wBox = new BBox(this.object2World(	new Pt( (float) b.getpMin().x,
														(float) b.getpMin().y,
														(float) b.getpMin().z )  );
		wBox = BBox.union(wBox, this.object2World( new Pt(b.getpMax().x,b.getpMin().y,b.getpMin().z));
		wBox = BBox.union(wBox, new Pt(b.getpMin().x,b.getpMax().y,b.getpMin().z));
		wbox = 
		
	}
	*/
}
