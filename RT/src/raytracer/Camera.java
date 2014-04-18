package raytracer;

import geometry.Pt;
import geometry.Transformation;
import geometry.Vec;

import java.io.Serializable;

import javax.vecmath.*;
import javax.media.j3d.Transform3D;


/**
 * Camera representation.  A camera is created using its position, its orientation 
 * (using a 4d vector: the first three components create the orientation axis and the fourth component is the angle that rotates around the axis),
 * and a field of view. Additionally, the construction of a camera calculates the rotation matrix 
 * (to apply to rays that are created) and a transformation matrix (combined translation and rotation)
 * 
 * A camera can also be initialized using a Pt representing the camera's position "eye," at look at Pt representing a center of 
 * focus of the camera "lookAt," and a Vec "up" representing an approximate up axis. Together eye lookAt and up are used to create
 * a coordinate system trueUp, center, and right used to direct the camera's focus.
 * 
 * camera position and orientation can be adjusted after initialization using public functions.
 *  
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class Camera implements Serializable{

	/**
	 * Serial Identification for camera
	 */
	private static final long serialVersionUID = 1L;

	/** Camera position can be set with changePostion */
	private Pt position;
	
	/** Transformation matrix (rotation and translation) */  
	private Matrix4d transformationMatrix;

	/** Orientation.  An axis and an angle about said axis. 
	 *  the axis is the axis that you want to rotate around (right hand rule)
	 *  the angle is the angle in radians that you rotate about that axis
	 *  example: (1,0,0,.55) means rotate in the x-z plane .55 radians counterclockwise (again right hand)
	 *  can be set with lookAt*/
	public AxisAngle4d orientation;

	/** angle representing the field of view of camera. 
	 *  The closer the camera is to the viewing screen 
	 *  the wider the fov angle is.
	 *  see Raytracer for use of this value */
	public double fieldOfView;
	
	/** Rotation matrix */
	public Matrix4d rotationMatrix;
	
	public Camera(Camera c){
		position = new Pt(c.position);
		fieldOfView = c.fieldOfView;
		orientation = new AxisAngle4d(c.orientation);
		rotationMatrix = new Matrix4d(c.rotationMatrix);
		transformationMatrix = new Matrix4d(c.transformationMatrix);
	}
	
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
		this.position = new Pt(position);
		this.fieldOfView = fieldOfView;
		this.orientation = orientation;
		
		Transformation t = new Transformation(new Vector3d(1,1,1), position, orientation);
		this.rotationMatrix = new Matrix4d();
		this.rotationMatrix.setIdentity();
		this.rotationMatrix.set(orientation);
		
		this.transformationMatrix = t.o2w;
	}
	
	/**
	 * create a new camera facing the direction of interest"lookAt" from the base position
	 * "eye," with up equal to "up." This is essentially constructing the camera using
	 * a "look-at" rotation matrix.
	 * 
	 * @param eye The base translated position of the camera.
	 * @param lookAt The forward point of the camera axis system (the spot at which the eye is looking/centered).
	 * @param up The up point of the camera axis system (does not have to be orthogonal.
	 * @param fieldOfView Viewing angle width.
	 */
	public Camera(Pt eye, Pt lookAt, Vec up, double fieldOfView){
		super();
		this.position = new Pt(eye);
		this.fieldOfView = fieldOfView;

		lookAt(lookAt, up);
	}
	
	/**
	 *  change orientation of camera to look at a desired position "lookAt".
	 *  
	 * @param lookAt point of interest
	 * @param up relative up axis (does not have to be orthogonal)
	 */
	public void lookAt(Pt lookAt, Vec up) {
		Vec center = findDirectionOfLookAt(lookAt);
		Vec trueUp = calculateTrueUp(up, center);
		Vec right = correctCoordinateSystem(trueUp,center); //not currently used can be used to manually create rotation matrix
		
		/* create matrix to look at point center from eye */
		Transform3D findOrientation = new Transform3D();
		findOrientation.lookAt(new Point3d(position), new Point3d(center), new Vector3d(trueUp));
		findOrientation.invert();
		
		/* set orientation */
		Matrix3d rotation = new Matrix3d();
		findOrientation.getRotationScale(rotation);
		orientation = new AxisAngle4d();
		orientation.set(rotation);
		
		Transformation t = new Transformation(new Vector3d(1,1,1), position, orientation);
		this.rotationMatrix = new Matrix4d();
		this.rotationMatrix.setIdentity();
		this.rotationMatrix.set(orientation);
		
		this.transformationMatrix = t.o2w;	
	}
	
	/** 
	 * change current position of camera to desired position "newPos."
	 * 
	 * @param newPos desired position of camera.
	 */
	public void setPostion(Pt newPos) {
		this.position = new Pt(newPos);
		Transformation t = new Transformation(new Vector3d(1,1,1), position, orientation);
		this.transformationMatrix = t.o2w;
	}
	
	/**
	 * get current camera position.
	 * 
	 * @return Pt representing the current camera position.
	 */
	public Pt getPosition(){
		return new Pt(position);
	}
	
	public Matrix4d getTransformationMatrix(){
		return new Matrix4d(transformationMatrix);
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
	
	/**
	 * correct trueUp and Center to ensure coordinate system is orthogonal. Return right coordinate vector for coord system
	 * 
	 * @param trueUp true up of coordinate system (may be modified by function call)
	 * @param center center of the coordinate system
	 * @return vector representing the right coordinate axis of coord system.
	 */
	private Vec correctCoordinateSystem(Vec trueUp, Vec center) {
		
		//check to see if up was initialized to center if so make arbitrary up
		if(	trueUp.x == center.x &&
			trueUp.y == center.y &&
			trueUp.z == center.z) {
			trueUp.set(0f,1f,0f);
		}
		
		//create right handed coordinate system
		Vec right = new Vec();
		right.cross(trueUp, center);
		trueUp.cross(right, center); //ensure that coordinate system is orthogonal
		
		return right;
	}
	
	/**
	 * use vector up and point look at to calculate the true up for the coordinate system of this camera
	 * 
	 * @param up vector representing approximate up.
	 * @param lookAt point representing point of interest for camera.
	 * @return a vector representing the true up based on the coordinate System generated from center up and worldUp.
	 */
	private Vec calculateTrueUp(Vec up, Vec c) {
		Vec projection = new Vec(c);
		Vec trueUp = new Vec(up);
		
		//project up onto center = (up dot center)*center
		double magnitude = Util.dotProduct(up, c);
		projection.scale(magnitude);
		
		//subtract projection from up to find true up for coordinate system
		trueUp.sub(projection);
		
		return trueUp;
	}
	
	/**
	 * remove translation bias from lookAt to generate a vector in the direction of lookAt from the camera's world position.
	 *  
	 * @param lookAt point of interest for the camera
	 * @return normalized vector representing the direction that the camera is looking "center."
	 */
	private Vec findDirectionOfLookAt(Pt lookAt) {
		Vec center = new Vec(lookAt);
		center.sub(this.position);
		center.normalize();
		return center;
	}
	
	@Override
	public String toString() {
		return "Camera: position = " + position + ", orientation = " + orientation
				+ ", fieldOfView = " + fieldOfView;
	}
}
