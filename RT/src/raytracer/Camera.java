package raytracer;

import java.io.Serializable;

import javax.vecmath.*;
import javax.media.j3d.Transform3D;

import scene.Transformation;


/**
 * Camera representation.  A camera is created using its position, its orientation 
 * (using a 4d vector: the first three components create the orientation axis and the fourth component is the angle that rotates around the axis),
 * and a field of view. Additionally, the construction of a camera calculates the rotation matrix 
 * (to apply to rays that are created) and a transformation matrix (combined translation and rotation)
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class Camera implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Camera position */
	public Vector3d position;

	/** Orientation.  An axis and an angle about said axis. 
	 *  the axis is the axis that you want to rotate around (right hand rule)
	 *  the angle is the angle in radians that you rotate about that axis
	 *  example: (1,0,0,.55) means rotate in the x-z plane .55 radians counterclockwise (again right hand)*/
	public AxisAngle4d orientation;

	/** angle representing the field of view of camera. 
	 *  The closer the camera is to the viewing screen 
	 *  the wider the fov angle is.
	 *  see Raytracer for use of this value */
	public double fieldOfView;
	
	/** Rotation matrix */
	public Matrix4d rotationMatrix;

	/** Transformation matrix (rotation and translation) */  
	public Matrix4d transformationMatrix;

	/**
	 * Create a new camera with the data parameters
	 * 
	 * @param position Location of the camera
	 * @param orientation Orients where the camera is looking
	 * @param rotation Rotation of the camera on the orientation axis 
	 * @param fieldOfView Viewing angle width
	 */
	public Camera(Vector3d position, AxisAngle4d orientation, double fieldOfView) {
		super();
		this.position = position;
		this.fieldOfView = fieldOfView;
		this.orientation = orientation;
		
		Transformation t = new Transformation();
		this.rotationMatrix = new Matrix4d();
		this.rotationMatrix.setIdentity();
		this.rotationMatrix.set(orientation);
		
		t.translation = this.position;
		this.transformationMatrix = t.getTransformationMatrix();
		this.transformationMatrix.mul(this.rotationMatrix);
		
	}
	
	/**
	 * create a new camera facing the direction of "center" from the base position
	 * "eye," with up equal to "up." This is essentially constructing the camera using
	 * a "look-at" rotation matrix.
	 * 
	 * @param eye The base translated position of the camera.
	 * @param center The forward point of the camera axis system (the spot at which the eye is looking/centered).
	 * @param up The up point of the camera axis system.
	 * @param fieldOfView Viewing angle width.
	 */
	public Camera(Point3d eye, Point3d center, Vector3d up, double fieldOfView){
		super();
		this.position = new Vector3d(eye.x,eye.y,eye.z);
		this.fieldOfView = fieldOfView;
		
		/* create matrix to look at point center from eye */
		Transform3D findOrientation = new Transform3D();
		findOrientation.lookAt(eye, center, up);
		findOrientation.invert();
		
		/* set matrix */
		Matrix3d rotation = new Matrix3d();
		findOrientation.getRotationScale(rotation);
		
		orientation = new AxisAngle4d();
		orientation.set(rotation);
		
		Transformation t = new Transformation();
		this.rotationMatrix = new Matrix4d();
		this.rotationMatrix.setIdentity();
		this.rotationMatrix.set(orientation);
		//this.rotationMatrix = rotation;
		
		//System.out.println(this.rotationMatrix.toString());
		t.translation = position;
		this.transformationMatrix = t.getTransformationMatrix();
		this.transformationMatrix.mul(this.rotationMatrix);		
	}

	public void transform(Transformation t) {
		Vector4d aux = new Vector4d(position.x, position.y, position.z, 1);
		Matrix4d matrix = t.getTransformationMatrix();
		aux = Util.MultiplyMatrixAndVector(matrix, aux);
		this.position.set(aux.x, aux.y, aux.z);
		
		aux = new Vector4d(orientation.x, orientation.y, orientation.z, 1);
		aux = Util.MultiplyMatrixAndVector(matrix, aux);
		
		this.orientation.x = aux.x;
		this.orientation.y = aux.y;
		this.orientation.z = aux.z;		
	}
	
	@Override
	public String toString() {
		return "Camera: position = " + position + ", orientation = " + orientation
				+ ", fieldOfView = " + fieldOfView;
	}
}
