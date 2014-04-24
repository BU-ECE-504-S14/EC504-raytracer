/**
 *
 */

package scene;

import geometry.Pt;
import geometry.Transformation;
import geometry.Vec;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import parser.ObjectParser;
import raytracer.Camera;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class PreviewScene extends Scene
{
	public PreviewScene(Sphere o)
	{
		super();

		camera = new Camera(new Pt(3, 3, 3), new Pt(-5, -5, -5), new Vec(0, 1, 0), Math.PI / 4);
		PointLight demoLight = new PointLight();
		demoLight.setColor(new Vector3d(1, 1, 1));
		demoLight.setRadio(1);
		demoLight.setPosition(new Vector3d(5, 5, 2));

		lights.add(demoLight);

		Sphere demoSphere = new Sphere();

		demoSphere.material = o.material;
		demoSphere.setzMinMax(o.getzMin(), o.getzMax());
		demoSphere.phiMax = o.phiMax;
		demoSphere.setTransform(new Transformation());		
				
		TriangleMesh previewBox = null;

		try
		{
			previewBox = ObjectParser.parseObjectsFromFile("previewSkybox.obj").get(0);
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TriangleMesh demoBox = null;
		
		try
		{
			demoBox = ObjectParser.parseObjectsFromFile("preBox.obj").get(0);
		}
		catch (SceneObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		demoBox.material = demoSphere.material;
		Transformation boxTrans = new Transformation();
		//boxTrans.setRotation(new AxisAngle4d(0,1,0,Math.PI/4));
		demoBox.updateTransform(boxTrans);

		Transformation preTrans = new Transformation();
		
		// The box that surrounds the preview object
		//preTrans.setScale(new Vector3d(10, 10, 10));
		//preTrans.setRotation(new AxisAngle4d(0, 1, 0, Math.PI / 4));
	//	preTrans.setTranslation(new Vector3d(-2.5, -2.5, -2.5));
		previewBox.updateTransform(preTrans);

		
		
		objects.add(demoSphere);
		//objects.add(demoBox);
		objects.add(previewBox);
		buildOctree(3);
		
	}
}
