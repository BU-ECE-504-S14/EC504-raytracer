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
public class BoxTestScene extends Scene
{
	public BoxTestScene()
	{
		super();

		List<TriangleMesh> meshes = null;
		try
		{
			meshes = ObjectParser.parseObjectsFromFile("boxes.obj");
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		camera = new Camera(new Pt(new Vector3d(0,0,0)), new Pt(new Vector3d(10,0,0)), new Vec(0,1,0), Math.PI);
		PointLight demoLight = new PointLight();

		demoLight.setPosition(new Vector3d(100, 100, 50));

		demoLight.setColor(new Vector3d(1, 1, 1));
		demoLight.setRadio(3);

		lights.add(demoLight);

		camera.setPostion(new Pt(new Vector3d(0,0,50)));
		camera.lookAt(new Pt(new Vector3d(0,0,0)), new Vec(0, 1, 0));
		
		for (TriangleMesh tm : meshes){
			tm.material.reflectionIndex = .8;
			tm.material.diffuseColor = new Vector3d(Math.random(), Math.random(), Math.random());
			this.addSceneObject(tm);
			System.out.println(tm);
		}

		// Prints out all object ids
		/*
		 * for(SceneObject tmpobj: objects){ if(tmpobj.isIntersectable()){
		 * System.out.println(tmpobj.getID()); } else{ System.out.println(tmpobj.getID());
		 * ArrayList<SceneObject> soa = new ArrayList<SceneObject>(); try { tmpobj.refine(soa); }
		 * catch (RefinementException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * for(SceneObject tmptmpobj:soa) System.out.println(tmptmpobj.getID()); } }
		 */
		//this.buildOctree(3);

	}
}