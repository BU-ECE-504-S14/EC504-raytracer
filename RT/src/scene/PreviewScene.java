/**
 *
 */

package scene;

import javax.vecmath.Vector3d;

import objects.SceneObject;
import objects.Sphere;

/**
@author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class PreviewScene extends Scene
{
	public PreviewScene(Sphere o)
	{
		super();
		PointLight demoLight = new PointLight();
		PointLight demoLight2 = new PointLight();
		demoLight.setPosition(new Vector3d(100, 100, 50));
		demoLight2.setPosition(new Vector3d(-100, -100, -50));
		demoLight2.setColor(new Vector3d(.5, .5, .5));

		lights.add(demoLight);
		lights.add(demoLight2);
		Sphere demoSphere = new Sphere();
		
		demoSphere.position = new Vector3d(0,0,0);
		demoSphere.radius = 3;
		demoSphere.material = o.material;

		objects.add(demoSphere);
		
	}
}
