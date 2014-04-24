package raytracer;

import geometry.Pt;
import geometry.Transformation;
import geometry.Vec;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import scene.Scene;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.Material;
import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import scene.BoxTestScene;
import scene.MeshPreviewScene;
import scene.PointLight;
import scene.MaterialScene;

/**
 * Ray tracing renderer, for EC504 at Boston University based on the work of Rafael Martin
 * Bigio <rbigio@itba.edu.ar>
 * 
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class Renderer
{

	public static double progress = 0.0;
	public static boolean done = false;
	public String outputFile;
	public String inputFile;

	/* basic setup for rendering simple scene */
	public static void main(String[] args)
	{
		Renderer r = new Renderer();
	}

	public void showSampleScene()
	{
		Scene s = new MaterialScene(new Sphere());
		s.settings.ANTIALIASING = 1;
		s.settings.MULTITHREADING = true;
		s.settings.WIDTH = 400;
		s.settings.HEIGHT = 600;
		try
		{

			// new RenderViewer(renderScene(constructSampleScene()));
			new RenderViewer(renderScene(s));
			//new RenderViewer(renderScene(new MeshPreviewScene()));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Does the scene rendering. Create the ray tracer object called with the generated
	 * scene, and then visualize the rendered scene.
	 * 
	 * @throws IOException
	 *             if there are errors in the file access input/output
	 */
	public BufferedImage renderScene(Scene scene) throws Exception
	{

		progress = 0.0;
		done = false;

		/* Ultra simple raytracer */
		SimpleRayTracer rayTracer = new SimpleRayTracer();
		long time = System.currentTimeMillis();
		//BufferedImage result = rayTracer.render(optionProgress);
		 BufferedImage result = rayTracer.renderThreads(scene);
		return result;
	}
}
