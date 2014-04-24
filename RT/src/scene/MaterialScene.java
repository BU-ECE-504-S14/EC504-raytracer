/**
 *
 */

package scene;

import java.util.ArrayList;

import geometry.Pt;
import geometry.Transformation;
import geometry.Vec;

import javax.swing.JFrame;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import GUI.MeshPanel;
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
public class MaterialScene extends Scene
{
	static Sphere demoSphere;
	static TriangleMesh demoBox;
	static TriangleMesh previewBox;
	
	public static void main(String[] args){
		MaterialScene m = new MaterialScene(new Sphere());
		JFrame frame = new JFrame();
		frame.add(new MeshPanel(m.demoBox));
		frame.setVisible(true);
	}

	public MaterialScene(SceneObject o)
	{
		super();

		camera = new Camera(new Pt(3, 3, 3), new Pt(-5, -5, -5), new Vec(0, 1, 0), Math.PI / 4);
		PointLight demoLight = new PointLight();
		demoLight.setColor(new Vector3d(1, 1, 1));
		demoLight.setRadio(1.5);
		demoLight.setPosition(new Vector3d(3, 3, 2));

		lights.add(demoLight);

		demoSphere = new Sphere();

		demoSphere.material = new Material(o.getMaterial());
		demoSphere.setTransform(new Transformation());

		previewBox = null;

		try
		{
			previewBox = ObjectParser.parseObjectsFromFile("previewSkybox.obj").get(0);
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		demoBox = null;

		try
		{
			demoBox = ObjectParser.parseObjectsFromFile("preBox.obj").get(0);
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		demoBox.material = new Material(demoSphere.material);
		Transformation boxTrans = new Transformation();
		boxTrans.setRotation(new AxisAngle4d(1, .3, 1, .17));
		demoBox.updateTransform(boxTrans);

		Transformation preTrans = new Transformation();

		previewBox.updateTransform(preTrans);
		previewBox.material.diffuseIndex = .9;

		if (o instanceof Sphere)
		{
			setSphere();
		}
		else
		{
			setBox();
		}
	}

	public void updateScene(SceneObject o)
	{
		demoSphere.material = new Material(o.getMaterial());
		demoBox.material = new Material(o.getMaterial());
	}

	public void setBox()
	{
		removeSceneObject(demoSphere);
		this.objects = new ArrayList<SceneObject>();

		addSceneObject(previewBox);
		addSceneObject(demoBox);
		buildOctree(3);
	}

	public void setSphere()
	{
		this.objects = new ArrayList<SceneObject>();
		addSceneObject(previewBox);
		addSceneObject(demoSphere);
		buildOctree(3);
	}
}
