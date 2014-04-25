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

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.AbstractSceneObject.RefinementException;
import objects.Material;
import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import parser.ObjectParser;
import raytracer.Camera;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class SpheresInRoom extends Scene
{
	public SpheresInRoom()
	{
		super();

		Vector3d lightGreen = new Vector3d(0.65f, 1.0f, 0.30f);
        Vector3d green = new Vector3d(0.0f, 0.6f, 0.3f);
        Vector3d darkGreen = new Vector3d(0.0f, 0.41f, 0.41f);

        Vector3d yellow = new Vector3d(1.0f, 1.0f, 0.0f);
        Vector3d darkYellow = new Vector3d(0.61f, 0.61f, 0.0f);

        Vector3d lightPurple = new Vector3d(0.65f, 0.3f, 1.0f);
        Vector3d darkPurple = new Vector3d(0.5f, 0.0f, 1.0f);

        Vector3d brown = new Vector3d(0.71f, 0.40f, 0.16f);
        Vector3d orange = new Vector3d(1.0f, 0.75f, 0.0f);
        
        Vector3d scale;
		Vector3d position;
		AxisAngle4d rotation = new AxisAngle4d(0, 0, 1, 0);
		
		//Spheres
		Sphere sphereArray[] = new Sphere[5];
		for(int i = 0; i < sphereArray.length ; i++){
			sphereArray[i] = new Sphere();
		}
		
		scale = new Vector3d(30, 30, 30);
		position = new Vector3d(100, -50, 0);
		sphereArray[0].setTransform(scale, position, rotation);
		sphereArray[0].material.diffuseColor = yellow;
		//sphereArray[0].material.reflectionIndex = 0;
		
		scale = new Vector3d(20, 20, 20);
		position = new Vector3d(0, 0, 0);
		sphereArray[1].setTransform(scale, position, rotation);
		sphereArray[1].material.diffuseColor = brown;
		
		scale = new Vector3d(20, 20, 20);
		position = new Vector3d(50, 100, -100);
		sphereArray[2].setTransform(scale, position, rotation);
		sphereArray[2].material.diffuseColor = darkGreen;
		
		scale = new Vector3d(20, 20, 20);
		position = new Vector3d(-100, -50, 400);
		sphereArray[3].setTransform(scale, position, rotation);
		sphereArray[3].material.diffuseColor = orange;
		
		scale = new Vector3d(27, 27, 27);
		position = new Vector3d(-40, -50, -100);
		sphereArray[4].setTransform(scale, position, rotation);
		sphereArray[4].material.diffuseColor = green;
		
		for(int i = 0; i<sphereArray.length ; i++){
			objects.add(sphereArray[i]);
		}
		
		//Lights
		PointLight demoLight1 = new PointLight();
		demoLight1.setPosition(new Vector3d(100f, 100f, 400f));
		demoLight1.setColor(new Vector3d(1, 1, 1));
		demoLight1.setRadio(1);
		lights.add(demoLight1);

		//Planes
		
		List<TriangleMesh> meshes = null;
		try
		{
			meshes = ObjectParser.parseObjectsFromFile("planeAtOrigin.obj");
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TriangleMesh planeArray[] = new TriangleMesh[4];
		for(int i = 0; i < planeArray.length ; i++){
			planeArray[i] = new TriangleMesh(meshes.get(0));
		}
		
		scale = new Vector3d(1000, 1000, 1000);
		position = new Vector3d(0, 0, -1000);
		rotation = new AxisAngle4d(1,0,0,Math.PI/2);
		planeArray[0].updateTransform(new Transformation(scale,position,rotation));
		planeArray[0].material.alpha = 1;
		planeArray[0].material.diffuseColor = new Vector3d(.5,.5,.5);
		planeArray[0].material.reflectionIndex = 0.2;
		
		scale = new Vector3d(1000, 1000, 1000);
		position = new Vector3d(150, 0, 0);
		rotation = new AxisAngle4d(0,0,1,Math.PI/2);
		planeArray[1].updateTransform(new Transformation(scale,position,rotation));
		planeArray[1].material.alpha = 1;
		planeArray[1].material.diffuseColor = new Vector3d(1,0,0);
		planeArray[1].material.reflectionIndex = 0.2;
		
		scale = new Vector3d(1000, 1000, 1000);
		position = new Vector3d(-150, 0, 0);
		rotation = new AxisAngle4d(0,0,1,Math.PI/2);
		planeArray[2].updateTransform(new Transformation(scale,position,rotation));
		planeArray[2].material.alpha = 1;
		planeArray[2].material.diffuseColor = new Vector3d(0,0,1);
		planeArray[2].material.reflectionIndex = 0.2;
		
		scale = new Vector3d(1000, 1000, 1000);
		position = new Vector3d(0, -85, 0);
		rotation = new AxisAngle4d(1,0,0,0);
		planeArray[3].updateTransform(new Transformation(scale,position,rotation));
		planeArray[3].material.alpha = 1;
		planeArray[3].material.diffuseColor = new Vector3d(1,1,0);
		planeArray[3].material.reflectionIndex = 0.2;
		
		for(int i = 0; i<planeArray.length ; i++){
			objects.add(planeArray[i]);
		}
		
		//Camera
		camera = new Camera(new Vector3d(0, 0, 10), new AxisAngle4d(0, 0, -1, 0),
				(float)(Math.PI / 4));
		camera.setPostion(new Pt(0f, 50f, 1400f));
		camera.lookAt(new Pt(0f, 0f, 0f), new Vec(0, 1, 0));
		camera.fieldOfView = 0.3f;
		// Prints out all object ids
		/*
		 * for(SceneObject tmpobj: objects){ if(tmpobj.isIntersectable()){
		 * System.out.println(tmpobj.getID()); } else{ System.out.println(tmpobj.getID());
		 * ArrayList<SceneObject> soa = new ArrayList<SceneObject>(); try { tmpobj.refine(soa); }
		 * catch (RefinementException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * for(SceneObject tmptmpobj:soa) System.out.println(tmptmpobj.getID()); } }
		 */
		this.buildOctree(3);

	}
}