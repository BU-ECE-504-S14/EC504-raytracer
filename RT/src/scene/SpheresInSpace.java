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
import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import parser.ObjectParser;
import raytracer.Camera;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class SpheresInSpace extends Scene
{
	public SpheresInSpace()
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
		Sphere sphereArray[] = new Sphere[17];
		for(int i = 0; i < sphereArray.length ; i++){
			sphereArray[i] = new Sphere();
		}
		
		scale = new Vector3d(30, 30, 30);
		position = new Vector3d(100, -50, 0);
		sphereArray[0].setTransform(scale, position, rotation);
		sphereArray[0].material.diffuseColor = yellow;
		
		scale = new Vector3d(20, 20, 20);
		position = new Vector3d(0, 0, 0);
		sphereArray[1].setTransform(scale, position, rotation);
		sphereArray[1].material.diffuseColor = brown;
		
		scale = new Vector3d(20, 20, 20);
		position = new Vector3d(50, 100, -100);
		sphereArray[2].setTransform(scale, position, rotation);
		sphereArray[2].material.diffuseColor = darkGreen;

		scale = new Vector3d(100, 100, 100);
		position = new Vector3d(-100, -50, 400);
		sphereArray[3].setTransform(scale, position, rotation);
		sphereArray[3].material.diffuseColor = orange;

		scale = new Vector3d(27, 27, 27);
		position = new Vector3d(-40, -50, -100);
		sphereArray[4].setTransform(scale, position, rotation);
		sphereArray[4].material.diffuseColor = green;
      
		scale = new Vector3d(20, 20, 20);
		position = new Vector3d(-100, -80, 250);
		sphereArray[5].setTransform(scale, position, rotation);
		sphereArray[5].material.diffuseColor = lightPurple;

		scale = new Vector3d(100, 100, 100);
		position = new Vector3d(-200, -200, -400);
		sphereArray[6].setTransform(scale, position, rotation);
		sphereArray[6].material.diffuseColor = darkPurple;
		
		scale = new Vector3d(50, 50, 50);
		position = new Vector3d(-150, 100, 400);
		sphereArray[7].setTransform(scale, position, rotation);
		sphereArray[7].material.diffuseColor = darkGreen;

		scale = new Vector3d(27, 27, 27);
		position = new Vector3d(300, 200, 150);
		sphereArray[8].setTransform(scale, position, rotation);
		sphereArray[8].material.diffuseColor = green;

		scale = new Vector3d(27, 27, 27);
		position = new Vector3d(400, 200, 100);
		sphereArray[9].setTransform(scale, position, rotation);
		sphereArray[9].material.diffuseColor = darkPurple;

		scale = new Vector3d(27, 27, 27);
		position = new Vector3d(-100, 100, 150);
		sphereArray[10].setTransform(scale, position, rotation);
		sphereArray[10].material.diffuseColor = green;
          
		scale = new Vector3d(200, 200, 200);
		position = new Vector3d(200, -300, -200);
		sphereArray[11].setTransform(scale, position, rotation);
		sphereArray[11].material.diffuseColor = lightPurple;
  
		scale = new Vector3d(40, 40, 40);
		position = new Vector3d(-150, 100, -300);
		sphereArray[12].setTransform(scale, position, rotation);
		sphereArray[12].material.diffuseColor = orange;
		
		scale = new Vector3d(100, 100, 100);
		position = new Vector3d(100, 250, -150);
		sphereArray[13].setTransform(scale, position, rotation);
		sphereArray[13].material.diffuseColor = green;
 
		scale = new Vector3d(30, 30, 30);
		position = new Vector3d(200, -100, 100);
		sphereArray[14].setTransform(scale, position, rotation);
		sphereArray[14].material.diffuseColor = yellow;
		
		scale = new Vector3d(30, 30, 30);
		position = new Vector3d(300, -200, 20);
		sphereArray[15].setTransform(scale, position, rotation);
		sphereArray[15].material.diffuseColor = yellow;
		
		scale = new Vector3d(500, 500, 500);
		position = new Vector3d(-200, -500, -2000);
		sphereArray[16].setTransform(scale, position, rotation);
		sphereArray[16].material.diffuseColor = yellow;
		
		for(int i = 0; i<sphereArray.length ; i++){
			objects.add(sphereArray[i]);
		}
		
		camera = new Camera(new Vector3d(0, 0, 10), new AxisAngle4d(0, 0, -1, 0),
				(float)(Math.PI / 4));
		
		//Lights
		PointLight demoLight1 = new PointLight();
		demoLight1.setPosition(new Vector3d(100f, 100f, 400f));
		demoLight1.setColor(new Vector3d(1, 1, 1));
		demoLight1.setRadio(1);
		lights.add(demoLight1);

		/*demoSphere2.material.diffuseColor = new Vector3d(0.6, 0.6, 1);
		demoSphere2.material.ambientIntensity = .1;
		demoSphere2.material.diffuseIndex = .5;
		demoSphere2.material.specularIndex = 1;
		 */
		
		//Camera
		camera.setPostion(new Pt(600f, 1000f, 2000f));
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