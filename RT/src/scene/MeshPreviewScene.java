/**
 *
 */

package scene;

import geometry.Pt;
import geometry.Transformation;

import java.io.IOException;
import java.util.List;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

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
		PointLight demoLight3 = new PointLight();

		demoLight.setPosition(new Vector3d(100, 100, 50));
		demoLight3.setPosition(new Vector3d(-100, -100, 50));

		demoLight2.setPosition(new Vector3d(.5, .5, 10));
		demoLight2.setColor(new Vector3d(1, 1, 1));
		demoLight3.setColor(new Vector3d(.8, .3, .8));
		demoLight.setColor(new Vector3d(.3, .8, .8));
		demoLight.setRadio(1);
		demoLight2.setRadio(1);
		demoLight3.setRadio(1);
		

		lights.add(demoLight);
		lights.add(demoLight2);
		lights.add(demoLight3);
		Sphere demoSphere = new Sphere();
		demoSphere.material.diffuseColor = new Vector3d(1, .3, .3);
		Sphere demoSphere2 = new Sphere();
		demoSphere2.material.ambientIntensity = .2;
		demoSphere2.material.diffuseIndex = .5;
		demoSphere2.material.specularIndex = 1;
		
		Vector3d scale = new Vector3d(100,100,100);
		Vector3d position = new Vector3d(0,0,-200);
		AxisAngle4d rotation = new AxisAngle4d(0,0,0,.5);
		
		demoSphere2.setTransform(scale, position, rotation);
		demoSphere2.material.reflectionIndex = 0;

		TriangleMesh parse = meshes.get(0);
		parse.material = demoSphere2.material;
		parse.material.reflectionIndex = 0;
		Transformation target = new Transformation(demoSphere.getTransform());
		target.setScale(new Vector3d(1.5, 1.5, 1.5));
		// target.setRotation(new AxisAngle4d(0,0,1,Math.PI));
		parse.updateTransform(target);

		// objects.add(plane);
		objects.add(parse);
		this.buildOctree(4);
		// objects.add(demoSphere);
		//objects.add(demoSphere2);

	}
}
