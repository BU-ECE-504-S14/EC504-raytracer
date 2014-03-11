package raytracer;

import javax.vecmath.*;

import scene.Transformation;


/**
 * Camera representation.  A camera is created using its position, its orientation 
 * (using a 4d vector: the first three components create the orientation axis and the fourth component is the angle that rotates around the axis),
 * and a field of view. Additionally, the construction of a camera calculates the rotation matrix 
 * (to apply to rays that are created) and a transformation matrix (combined translation and rotation)
 */
public class Camera {

	/** Camera position */
	public Vector3d position;

	/** Orientation.  An axis and an angle about said axis. */
	public AxisAngle4d orientation;

	/** Field of view */
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
//		t.rotation = orientation;
//		Vector4d o = Util.MultiplyMatrixAndVector(t.getRotationMatrix(), new Vector4d(0, 0, -1, 1));
//		
//		Vector3d orientationAxis = new Vector3d(o.x, o.y, o.z);
//		orientationAxis.normalize();
//		this.orientation = new Vector4d(orientationAxis.x, orientationAxis.y, orientationAxis.z, orientation.w);
//		
		//orientation.angle = Math.PI - orientation.angle;
		this.rotationMatrix = new Matrix4d();
		this.rotationMatrix.setIdentity();
		this.rotationMatrix.set(orientation);
		
		t.translation = this.position;
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
