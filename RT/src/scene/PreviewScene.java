/**
 *
 */

package scene;

import geometry.Pt;
import geometry.Vec;

import java.io.IOException;
import java.util.List;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import parser.ObjectParser;
import raytracer.Camera;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class PreviewScene extends Scene
{
	public PreviewScene(Sphere o)
	{
		super();

		camera = new Camera(new Pt(0, 0, -5), new Pt(0, 0, 300), new Vec(0, 1, 0),
				(double) (Math.PI / 4));
		PointLight demoLight = new PointLight();
		PointLight demoLight2 = new PointLight();
		demoLight.setPosition(new Vector3d(100, 100, 50));
		demoLight2.setPosition(new Vector3d(1, 1, -5));
		demoLight2.setColor(new Vector3d(1, 1, 1));
		demoLight.setColor(new Vector3d(1, 1, 1));
		demoLight.setRadio(.5);
		demoLight2.setRadio(1);

		// lights.add(demoLight);
		lights.add(demoLight2);
		Sphere demoSphere = new Sphere();
		Sphere demoSphere2 = new Sphere();
		demoSphere2.material.ambientIntensity = .3;
		demoSphere2.material.diffuseIndex = .5;
		demoSphere2.material.specularIndex = .5;
		demoSphere2.material.shininess = 1;
		demoSphere2.material.diffuseColor = new Vector3d(0,0,0);
		demoSphere2.setTransform(new Vector3d(100, 100, 10), new Vector3d(0, 0, -20), new AxisAngle4d(0,0,0,0));
		demoSphere2.material.reflectionIndex = 1;

		demoSphere.material = o.material;
		demoSphere.setzMinMax(o.getzMin(), o.getzMax());
		demoSphere.phiMax = o.phiMax;
		demoSphere.setTransform(o.getTransform());

		objects.add(demoSphere);
		objects.add(demoSphere2);
	}
}
