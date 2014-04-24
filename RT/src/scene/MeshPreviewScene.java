/**
 *
 */

package scene;

import geometry.Pt;
import geometry.Transformation;
import geometry.Vec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.Material;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.AbstractSceneObject.RefinementException;
import objects.BBoxObject;
import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import parser.ObjectParser;
import raytracer.Camera;
import util.SceneObjectException;

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
			meshes = ObjectParser.parseObjectsFromFile("box.obj");
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		camera = new Camera(new Vector3d(0, 0, 10), new AxisAngle4d(0, 0, -1, 0),
				(float)(Math.PI / 4));
		PointLight demoLight = new PointLight();
		PointLight demoLight2 = new PointLight();
		PointLight demoLight3 = new PointLight();

		demoLight.setPosition(new Vector3d(100, 100, 50));
		demoLight3.setPosition(new Vector3d(0, 0, -50));

		demoLight2.setPosition(new Vector3d(0, 0, 20));
		demoLight2.setColor(new Vector3d(1, 1, 1));
		demoLight2.setSoftShadowOffset(.6);
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
		demoSphere2.material.ambientIntensity = .1;
		demoSphere2.material.diffuseIndex = .5;
		demoSphere2.material.specularIndex = 1;
		
		
		Vector3d scale = new Vector3d(10, 10, 10);
		Vector3d position = new Vector3d(0, 0, 100);
		AxisAngle4d rotation = new AxisAngle4d(0, 0, 1, 0);

		demoSphere2.setTransform(scale, position, rotation);
		demoSphere2.material.reflectionIndex = 0;
		
		BBoxObject BBo = new BBoxObject(demoSphere2.getWorldBound());
		BBo.material.diffuseColor = new Vector3d(1,.1,.1);
		BBo.material.ambientIntensity = .1;
		BBo.material.diffuseIndex = .5;
		BBo.material.specularIndex = 1;

		camera.setPostion(new Pt(0f, 0f, 4f));
		camera.lookAt(new Pt(demoSphere2.getPosition()), new Vec(0, 1, 0));

		TriangleMesh parse = meshes.get(0);
		parse.material = new Material(demoSphere2.material);
		parse.material.alpha = 1;
		parse.material.diffuseColor = new Vector3d(1,0,0);
		parse.material.reflectionIndex = .5;
		Transformation target = new Transformation(demoSphere.getTransform());
		target.setScale(new Vector3d(1, 1, 1));
		target.setRotation(new AxisAngle4d(1,0,0,Math.PI/2));
		parse.updateTransform(target);

		// objects.add(plane);
		//objects.add(parse);
		objects.add(demoSphere);
		//objects.add(demoSphere2);
		objects.add(BBo);

		// Prints out all object ids
		/*
		 * for(SceneObject tmpobj: objects){ if(tmpobj.isIntersectable()){
		 * System.out.println(tmpobj.getID()); } else{ System.out.println(tmpobj.getID());
		 * ArrayList<SceneObject> soa = new ArrayList<SceneObject>(); try { tmpobj.refine(soa); }
		 * catch (RefinementException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * for(SceneObject tmptmpobj:soa) System.out.println(tmptmpobj.getID()); } }
		 */
		
		this.buildOctree(1);

	}
}