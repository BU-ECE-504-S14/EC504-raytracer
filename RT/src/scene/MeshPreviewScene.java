/**
 *
 */

package scene;

import geometry.Pt;
import geometry.Transformation;
import geometry.Vec;

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

		camera = new Camera(new Pt(0,0, 20), new Pt(0,0,0), new Vec(0,1,0), 1f);
		
		Vector3d SphereS = new Vector3d(100, 100, 100);
		Vector3d SphereP = new Vector3d(0,0,-200);
		AxisAngle4d SphereR = new AxisAngle4d(0,0,1,0);
		Transformation t = new Transformation(SphereS, SphereP, SphereR);
		Sphere demoSphere = new Sphere(1,1,360f,t);
		
		demoSphere.getMaterial().ambientIntensity = .1;
		demoSphere.getMaterial().diffuseIndex = .5;
		demoSphere.getMaterial().specularIndex = 1;
		demoSphere.getMaterial().reflectionIndex = 0;
		
		Vector3d parseS = new Vector3d(3,3,3);
		Vector3d parseP = new Vector3d(0,0,0);
		AxisAngle4d parseR = new AxisAngle4d(0,0,1,0);
		Transformation target = new Transformation(parseS, parseP, parseR);
		TriangleMesh parse = meshes.get(0);
		parse.material = demoSphere.getMaterial();
		parse.material.reflectionIndex = 0;
		parse.updateTransform(target);
		
		PointLight demoLight = new PointLight();
		PointLight demoLight2 = new PointLight();
		PointLight demoLight3 = new PointLight();

		demoLight.setPosition(new Vector3d(100, 100, 50));
		demoLight2.setPosition(new Vector3d(.5, .5, 10));
		demoLight3.setPosition(new Vector3d(0, 0, -50));
		
		demoLight.setColor(new Vector3d(.3, .8, .8));
		demoLight2.setColor(new Vector3d(1, 1, 1));
		demoLight3.setColor(new Vector3d(.8, .3, .8));

		demoLight.setRadio(1);
		demoLight2.setRadio(1);
		demoLight3.setRadio(1);
		
		lights.add(demoLight);
		lights.add(demoLight2);
		lights.add(demoLight3);

		// objects.add(plane);
		objects.add(parse);
		objects.add(demoSphere);
		this.buildOctree(3);

	}
}
