package scene;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class Transformation {

	public Vector3d translation = new Vector3d(0, 0, 0);

	public AxisAngle4d rotation = new AxisAngle4d(0, 0, 1, 0);

	public Vector3d scale = new Vector3d(1, 1, 1);

	/** Create the transformation pipeline*/
	public Matrix4d getTransformationMatrix() {
		Matrix4d rotationMatrix = getRotationMatrix();
		Matrix4d translationMatrix = new Matrix4d(1, 0, 0, -translation.x, 0, 1, 0, -translation.y,
				0, 0, 1, -translation.z, 0, 0, 0, 1);
		Matrix4d scaleMatrix = new Matrix4d(scale.x, 0, 0, 0, 0, scale.y, 0, 0, 0, 0, scale.z, 0,
				0, 0, 0, 1);
		rotationMatrix.mul(scaleMatrix);
		translationMatrix.mul(rotationMatrix);
		return translationMatrix;
	}

	public Matrix4d getTransformationMatrix(boolean inverseRotation) {
		Matrix4d rotationMatrix = getRotationMatrix();

		Matrix4d translationMatrix = new Matrix4d(1, 0, 0, -translation.x, 0, 1, 0, -translation.y,
				0, 0, 1, -translation.z, 0, 0, 0, 1);
		if (inverseRotation) {
			translationMatrix.invert();
		}
		Matrix4d scaleMatrix = new Matrix4d(scale.x, 0, 0, 0, 0, scale.y, 0, 0, 0, 0, scale.z, 0,
				0, 0, 0, 1);
		rotationMatrix.mul(scaleMatrix);
		translationMatrix.mul(rotationMatrix);
		return translationMatrix;
	}
	
	public Matrix4d getRotationMatrix() {
		Matrix4d ret = new Matrix4d();
		ret.setIdentity();
		ret.set(rotation);
		return ret;
	}
}
