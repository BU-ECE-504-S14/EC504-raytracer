package scene;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import raytracer.Util;

/**
 * A simple point light, which casts light rays in all directions.
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class PointLight {

	private Vector3d position = new Vector3d(0, 10, 0);
	private Vector3d color = new Vector3d(1, 1, 1);
	// private double fallConstant = 0.05;
	
	/* used to determine the diffuseness of ie radiosity of light around object */
	private double radio = 1;
	private float[] attenuation = new float[] { 1, 0, 0 };

	public PointLight () {
		
	}
	
	public PointLight(PointLight light){
		this.position.set(light.position);
		this.color.set(light.color);
		this.radio = light.radio;
		
		this.attenuation[0] = light.attenuation[0];
		this.attenuation[1] = light.attenuation[1];
		this.attenuation[2] = light.attenuation[2];
	}
	
	public Vector3d getPosition() {
		return position;
	}

	public void setColor(Vector3d color) {
		if (color != null)
			this.color = color;
	}

	public void setPosition(Vector3d position) {
		this.position = position;
	}

	public void setAttenuation(float[] attenuation) {
		this.attenuation = attenuation;
	}

	public Vector3d getColor(Vector3d currentPosition) {
		double r = Math.sqrt(Math.pow(position.x - currentPosition.x, 2.0)
				+ Math.pow(position.y - currentPosition.y, 2.0)
				+ Math.pow(position.z - currentPosition.z, 2.0));
		double factor = 1 / Math.max(attenuation[0] + attenuation[1] * r
				+ attenuation[2] * r * r, 1);

		Vector3d result = new Vector3d(color);
		result.scale(factor);
		// return result;
		return new Vector3d(color);
	}

	public void transform(Matrix4d transformationMatrix) {
		Vector4d aux = new Vector4d(position.getX(), position.getY(), position
				.getZ(), 1);
		aux = Util.MultiplyMatrixAndVector(transformationMatrix, aux);
		position.set(aux.getX(), aux.getY(), aux.getZ());
	}

	public double getRadio() {
		return radio;
	}

	public void setRadio(double radio) {
		this.radio = radio;
	}

	@Override
	public String toString() {
		return "PointLight(position=" + position + ", color=" + color + ")";
	}
}
