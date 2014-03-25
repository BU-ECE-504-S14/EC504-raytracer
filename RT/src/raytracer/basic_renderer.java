package raytracer;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import scene.Scene;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix4d;

import objects.Material;
import objects.SceneObject;
import objects.Sphere;
import scene.PointLight;
import scene.Scene;
import scene.Transformation;

public class basic_renderer {

	private static boolean optionProgress = true;
	private static int optionAntialiasing = 1;
	private static String optionOutputFile;
	private static String optionInputFile;
	private static int optionWidth = 400;
	private static int optionHeight = 300;
	private static int optionShadow = 20;
	
	
	/*basic setup for rendering simple scene*/
	public static void main(String[] args) {
		Transformation t = new Transformation();
		Transformation t1 = new Transformation();
		Transformation t2 = new Transformation();
		Transformation t3 = new Transformation();
		
		SceneObject shape = new Sphere();
		SceneObject shape1 = new Sphere();
		SceneObject shape2 = new Sphere();
		SceneObject shape3 = new Sphere();
		
		Material m = new Material();
		Material m1 = new Material();
		Material m2 = new Material();
		Material m3 = new Material();
		
		/*set poisitions and material of sphere objects */
		/*shape*/
		t.translation = new Vector3d(15, 0, 0);
		t.scale = new Vector3d(3,3,3);
		shape.transform(t);
		m.diffuseColor = new Vector3d(1,0,0);
		m.reflectionIndex = 0.5;
		m.diffuseIndex=1;
		shape.getMaterial().set(m);
		
		/*shape1*/
		t1.translation = new Vector3d(-6, 0, 0);
		t1.scale = new Vector3d(3,3,3);
		shape1.transform(t1);
		m1.diffuseColor = new Vector3d(0,1,0);
		m1.reflectionIndex = 0.5;
		shape1.getMaterial().set(m1);
		
		/*shape2*/
		t2.translation = new Vector3d(0, 0, -10);
		t2.scale = new Vector3d(3,3,3);
		shape2.transform(t2);
		m2.diffuseColor = new Vector3d(0,0,1);
		m2.reflectionIndex = 0.5;
		shape2.getMaterial().set(m2);
		
		/*shape3*/
		t3.translation = new Vector3d(30, 0, 0);
		t3.scale = new Vector3d(3,3,3);
		shape3.transform(t3);
		m3.diffuseColor = new Vector3d(1,1,1);
		m3.reflectionIndex = 0.5;
		shape3.getMaterial().set(m3);
		
		/*set up camera*/
		double fieldofView = .5;
		Point3d position = new Point3d(0,0,1);
		Point3d sphereLocation = new Point3d(1,0,1);
		Point3d takePos = new Point3d(position.x,position.y,position.z);
		Point3d takeSL = new Point3d(sphereLocation.x,sphereLocation.y,sphereLocation.z);
		takePos.sub(takeSL);

		Vector3d up = new Vector3d(0,0,1);

		/* uses axisangle to create camera (I hate axisangle) 
		 * you will need to modify the position to get this to work
		 * it may be instructive to try to get this camera to work to test if you understand camera operations
		 */
		//Camera cam = new Camera(position, new AxisAngle4d(1,0,0,0.55), fieldofView);	
		
		/* uses look at type constructor to create camera */
		Camera cam = new Camera(position, sphereLocation, up, fieldofView);	
		System.out.println(cam.toString()); //debug
		
		/* set lights */
		PointLight l1 = new PointLight();
		l1.setColor(new Vector3d(0,1,0));
		l1.setPosition(new Vector3d(14,-2,5));
		PointLight l2 = new PointLight();
		l2.setColor(new Vector3d(0,.5,0));
		l2.setPosition(new Vector3d(14,2,5));
		PointLight l3 = new PointLight();
		l3.setPosition(new Vector3d(16,0,5));
		l3.setColor(new Vector3d(0,0,1));
		
		
		/* add objects & lights & camera to scene */
		Scene scene = new Scene();
		scene.addSceneObject(shape);
		scene.addSceneObject(shape1);
		scene.addSceneObject(shape2);
		scene.addSceneObject(shape3);
		scene.addLight(l1);
		scene.addLight(l2);
		scene.addLight(l3);
		scene.setCamera(cam);
		
		try {
			basic_renderer.renderScene(scene);
		} catch (IOException e) {
			System.err.println("Error rendering scene: " + e.getMessage());
		}		
		
	}
	
	/**
	 * Does the scene rendering. Create the ray tracer object 
	 * called with the generated scene, and then visualize the rendered scene.
	 * 
	 * @throws IOException if there are errors in the file access input/output
	 */
	public static void renderScene(Scene scene) throws IOException {
		

		Dimension imageSize = new Dimension(optionWidth, optionHeight);
		
		/* Ultra simple raytracer */
		Simple_RayTracer rayTracer = new Simple_RayTracer(scene, imageSize, optionAntialiasing, optionShadow);
		
		/* more complex ray tracer specular lighting, anti aliasing, refraction, reflection, etc. */
		//RayTracer rayTracer = new RayTracer(scene, imageSize, optionAntialiasing, optionShadow);
		
		/* final raytracer: all features and fast */
		//RayTracer rayTracer = new RayTracer(new OctreeScene(scene), imageSize, optionAntialiasing, optionShadow);
		
		BufferedImage result = rayTracer.render(optionProgress);
		new RenderViewer(result);
	}
}
