/**
 *
 */

package scene;

import java.io.IOException;
import java.util.List;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.Pt;
import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import parser.ObjectParser;
import raytracer.Camera;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class MeshPreviewScene extends Scene
{
	public MeshPreviewScene()
	{
		super();

		List<TriangleMesh> meshes = null;
		try
		{
			meshes = ObjectParser.findObjects("./res/box.obj");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		camera = new Camera(new Vector3d(0, 0, 10), new AxisAngle4d(0, 0, -1, 0),
				(float) (Math.PI / 4));
		PointLight demoLight = new PointLight();
		PointLight demoLight2 = new PointLight();
		demoLight.setPosition(new Vector3d(100, 100, 50));
		demoLight2.setPosition(new Vector3d(.5, .5, 10));
		demoLight2.setColor(new Vector3d(1, 1, 1));
		demoLight.setColor(new Vector3d(1, 1, 1));
		demoLight.setRadio(.5);
		demoLight2.setRadio(1);

		// lights.add(demoLight);
		lights.add(demoLight2);
		Sphere demoSphere = new Sphere();
		Sphere demoSphere2 = new Sphere();
		demoSphere2.material.ambientIntensity = .3;
		demoSphere2.material.diffuseIndex = 1;
		demoSphere2.material.specularIndex = 1;
		demoSphere2.trans.setTranslation(new Vector3d(0, 0, -200));
		demoSphere2.trans.setScale(new Vector3d(100, 100, 100));
		demoSphere2.material.reflectionIndex = 0;

		TriangleMesh parse = meshes.get(0);
		parse.material = demoSphere2.material;
		Transformation target = new Transformation(demoSphere.trans);
		target.setScale(new Vector3d(1.5, 1.5, 1.5));
		// target.setRotation(new AxisAngle4d(0,0,1,Math.PI));
		parse.updateTransform(target);

		// objects.add(plane);
		objects.add(parse);
		// objects.add(demoSphere);
		objects.add(demoSphere2);
	}
}
