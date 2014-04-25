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
public class BuddhaScene extends Scene
{
	public BuddhaScene()
	{
		super();
		
		//Lights
		PointLight demoLight1 = new PointLight();
		PointLight demoLight2 = new PointLight();
		PointLight demoLight3 = new PointLight();
		PointLight demoLight4 = new PointLight();

		demoLight1.setPosition(new Vector3d(0, 27, 60));
		demoLight2.setPosition(new Vector3d(0, 0, 0));
		demoLight3.setPosition(new Vector3d(0, 0, -60));
		demoLight4.setPosition(new Vector3d(10, 30, -60));
		
		
		demoLight1.setColor(new Vector3d(1, 1, 1));
		demoLight2.setColor(new Vector3d(1, 0.3, 0.3));
		demoLight2.setSoftShadowOffset(.6);
		demoLight3.setColor(new Vector3d(0.3, 1, 0.3));
		demoLight4.setColor(new Vector3d(0.3, 0.2, 1));

		demoLight1.setRadio(1);
		demoLight2.setRadio(1);
		demoLight3.setRadio(1);
		demoLight4.setRadio(1);

		lights.add(demoLight1);
		lights.add(demoLight2);
		lights.add(demoLight3);
		lights.add(demoLight4);

		//Reference sphere
		Sphere demoSphere2 = new Sphere();
		
		Vector3d scale = new Vector3d(10, 10, 10);
		Vector3d position = new Vector3d(0, -10, 0);
		AxisAngle4d rotation = new AxisAngle4d(0, 0, 1, 0);
		demoSphere2.setTransform(scale, position, rotation);
		
		demoSphere2.material.diffuseColor = new Vector3d(0.6, 0.6, 1);
		demoSphere2.material.ambientIntensity = .1;
		demoSphere2.material.diffuseIndex = .5;
		demoSphere2.material.specularIndex = 1;
		demoSphere2.material.reflectionIndex = 0;
		
		//Camera
		camera = new Camera(new Vector3d(0, 0, 10), new AxisAngle4d(0, 0, -1, 0),
				(float)(Math.PI / 4));
		camera.setPosition(new Pt(-12f, 0f, 17f));
		camera.lookAt(new Pt(-10,1,0), new Vec(0, 1, 0));
		camera.fieldOfView = 1.05;
		
		
		//Meshes
		
		//plane
		List<TriangleMesh> meshes = null;
		try
		{
			meshes = ObjectParser.parseObjectsFromFile("plane_ofbuddha.obj");
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TriangleMesh parse1 = new TriangleMesh(meshes.get(0));
		parse1.material.alpha = 1;
		parse1.material.reflectionIndex = 0;
		parse1.material.diffuseColor = new Vector3d(0.6,0.6,1);
		parse1.material.specularIndex = 0.1;
		
		
		Transformation target = new Transformation(demoSphere2.getTransform());
		target.setScale(new Vector3d(10, 10, 10));
		target.setRotation(new AxisAngle4d(0,1,0,-5.5*Math.PI/14));
		parse1.setTransform(target);

		//sphere
		try
		{
			meshes = ObjectParser.parseObjectsFromFile("sphere_ofbuddha.obj");
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TriangleMesh parse2 = new TriangleMesh(meshes.get(0));
		parse2.material.alpha = 1;
		parse2.material.reflectionIndex = 0.5;
		parse2.material.diffuseColor = new Vector3d(1,1,1);
		parse2.material.specularIndex = 0.5;
		
		target = new Transformation(demoSphere2.getTransform());
		target.setScale(new Vector3d(10, 10, 10));
		target.setRotation(new AxisAngle4d(0,1,0,-5.5*Math.PI/14));
		parse2.setTransform(target);

		
		//snowflakes
		try
		{
			meshes = ObjectParser.parseObjectsFromFile("snow.obj");
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TriangleMesh parse3 = new TriangleMesh(meshes.get(0));
		parse3.material.alpha = 0.3;
		parse3.material.reflectionIndex = 0.3;
		parse3.material.refractionIndex = 1;
		parse3.material.diffuseColor = new Vector3d(1,0.6,0);
		parse3.material.specularIndex = 0.2;
		
		target = new Transformation(demoSphere2.getTransform());
		target.setScale(new Vector3d(10, 10, 10));
		target.setRotation(new AxisAngle4d(0,1,0,-5.5*Math.PI/14));
		parse3.setTransform(target);
		
		//buddha
		try
		{
			meshes = ObjectParser.parseObjectsFromFile("buddha_onsphere.obj");
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TriangleMesh parse4 = new TriangleMesh(meshes.get(0));
		parse4.material.alpha = 1;
		parse4.material.reflectionIndex = 0;
		parse4.material.diffuseColor = new Vector3d(0.8,0.9,0.8);
		parse4.material.specularIndex = 0.2;
		
		target = new Transformation(demoSphere2.getTransform());
		target.setScale(new Vector3d(10, 10, 10));
		target.setRotation(new AxisAngle4d(0,1,0,-5.5*Math.PI/14));
		parse4.setTransform(target);
		
		//objects.add(parse1);
		//objects.add(parse2);
		//objects.add(parse3);
		objects.add(parse4);
		
		// Prints out all object ids
		/*
		 * for(SceneObject tmpobj: objects){ if(tmpobj.isIntersectable()){
		 * System.out.println(tmpobj.getID()); } else{ System.out.println(tmpobj.getID());
		 * ArrayList<SceneObject> soa = new ArrayList<SceneObject>(); try { tmpobj.refine(soa); }
		 * catch (RefinementException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * for(SceneObject tmptmpobj:soa) System.out.println(tmptmpobj.getID()); } }
		 */
		
		this.buildOctree(5);

	}
}