/**
 *
 */

package scene;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.SceneObject;
import objects.Sphere;
import raytracer.Camera;

/**
@author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class PreviewScene extends Scene
{
	public PreviewScene(Sphere o)
	{
		super();
		camera = new Camera(new Vector3d(0, 0, 3), new AxisAngle4d(0, 0, -1, 0),
				(float) (Math.PI / 4));
		PointLight demoLight = new PointLight();
		PointLight demoLight2 = new PointLight();
		demoLight.setPosition(new Vector3d(100, 100, 50));
		demoLight2.setPosition(new Vector3d(-100, -100, -50));
		demoLight2.setColor(new Vector3d(.5, .5, .5));

		lights.add(demoLight);
		lights.add(demoLight2);
		Sphere demoSphere = new Sphere();
		
		demoSphere.position = new Vector3d(0,0,0);
		demoSphere.radius = o.radius;
		demoSphere.material = o.material;
		demoSphere.zmin = o.zmin;
		demoSphere.zmax = o.zmax;
		demoSphere.thetaMin = o.thetaMin;
		demoSphere.thetaMax = o.thetaMax;
		demoSphere.phiMax = o.phiMax;
		demoSphere.trans = o.trans;

		objects.add(demoSphere);
		
	}
}
