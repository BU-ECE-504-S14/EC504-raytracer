/**
 *
 */

package scene;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.Pt;
import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import raytracer.Camera;

/**
@author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class PreviewScene extends Scene
{
	public PreviewScene(Sphere o)
	{
		super();
		camera = new Camera(new Vector3d(0, 0, 10), new AxisAngle4d(0, 0, -1, 0),
				(float) (Math.PI / 4));
		PointLight demoLight = new PointLight();
		PointLight demoLight2 = new PointLight();
		demoLight.setPosition(new Vector3d(100, 100, 50));
		demoLight2.setPosition(new Vector3d(.5, .5, 3));
		demoLight2.setColor(new Vector3d(1, 1, 1));
		demoLight.setColor(new Vector3d(1,1,1));
		demoLight.setRadio(.5);
		demoLight2.setRadio(1);

		//lights.add(demoLight);
		lights.add(demoLight2);
		Sphere demoSphere = new Sphere();
		Sphere demoSphere2 = new Sphere();
		demoSphere2.material.ambientIntensity = .3;
		demoSphere2.material.diffuseIndex = 1;
		demoSphere2.material.specularIndex = 1;
		demoSphere2.trans.setTranslation( new Vector3d(0,0,-200));
		demoSphere2.trans.setScale(new Vector3d(100,100,100));
		demoSphere2.material.reflectionIndex = 0;
		
		TriangleMesh demoTri;
		/*shape4*/
		Vector3d scale = new Vector3d(10,10,10);
		Vector3d pos = new Vector3d(0, 0, -25);
		AxisAngle4d rot = new AxisAngle4d(1,0,0,180);
		Pt[] P = new Pt[6];
		P[0] = new Pt(1,0,0);
		P[1] = new Pt(0,0,0);
		P[2] = new Pt(0,0,1);
		P[3] = new Pt(0,0,-1);
		P[4] = new Pt(0,0,0);
		P[5] = new Pt(-1,0,0);
		
		int[] vi = {0,1,2,3,4,5};
		Transformation t4 = new Transformation(scale,pos,rot);
		demoTri = new TriangleMesh(t4, 2, 6, vi,P, null, null, null);
		demoTri.material.diffuseColor = new Vector3d(1, 1, 1);
		demoTri.material.reflectionIndex = 1;
		demoTri.material.diffuseIndex = 1;
		demoTri.material.ambientIntensity = 1;
		demoTri.material.specularIndex = 1;
		
		
		demoSphere.position = new Vector3d(0,0,0);
		demoSphere.radius = o.radius;
		demoSphere.material = o.material;
		demoSphere.zmin = o.zmin;
		demoSphere.zmax = o.zmax;
		demoSphere.thetaMin = o.thetaMin;
		demoSphere.thetaMax = o.thetaMax;
		demoSphere.phiMax = o.phiMax;
		demoSphere.trans = o.trans;

		objects.add(demoSphere);
		objects.add(demoSphere2);
		//objects.add(demoTri);
	}
}
