package raytracer;

import geometry.Pt;
import geometry.Ray;
import geometry.Transformation;
import geometry.Vec;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import scene.Scene;

import javax.imageio.ImageIO;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.Material;
import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import scene.BoxTestScene;
import scene.Intersection;
import scene.MeshPreviewScene;
import scene.PointLight;
import scene.MaterialScene;
import util.RenderSettingException;

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
		try
		{
			s.settings.setANTIALIASING(1);
			s.settings.setMULTITHREADING(true);
			s.settings.setWIDTH(400);
			s.settings.setHEIGHT(600);
		}
		catch (RenderSettingException e1)
		{
			e1.printStackTrace();
		}

		try
		{

			// new RenderViewer(renderScene(constructSampleScene()));
			new RenderViewer(renderScene(s));
			// new RenderViewer(renderScene(new MeshPreviewScene()));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeToOutput(BufferedImage bi, Scene s)
	{

		File f = s.settings.getOUTPUT_PATH();
		try
		{
			ImageIO.write(bi, "PNG", f);
		}
		catch (IOException e)
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
	public static BufferedImage renderScene(Scene scene) throws Exception
	{

		progress = 0.0;
		done = false;

		/* Ultra simple raytracer */
		SimpleRayTracer rayTracer = new SimpleRayTracer();
		BufferedImage result;

		Ray.rayConstructionCount = 0;
		Intersection.updateInterCount = 0;

		if (scene.settings.isACCELERATE() && !scene.accelFlag)
		{
			if (scene.settings.isVERBOSE())
			{
				System.out.println("Building octree of depth: " + scene.settings.getOCTREE_DEPTH());
			}
			scene.buildOctree(scene.settings.getOCTREE_DEPTH());

		}

		if (!scene.settings.isACCELERATE() && scene.accelFlag)
		{
			if (scene.settings.isVERBOSE())
			{
				System.out.println("Disabling acceleration...");
			}
			scene.accelFlag = false;
		}

		result = rayTracer.renderThreads(scene);

		if (scene.settings.getOUTPUT_PATH() != null)
		{
			writeToOutput(result, scene);
			if (scene.settings.isVERBOSE())
			{
				System.out.println("Writing rendered image to: " + scene.settings.getOUTPUT_PATH());
			}
		}

		if (scene.settings.isVERBOSE())
		{
			System.out.println("Total rays generated: " + Ray.rayConstructionCount);
			System.out.println("Total rays generated: " + Intersection.updateInterCount);
		}
		return result;
	}
}
