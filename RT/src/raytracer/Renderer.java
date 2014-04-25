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
import scene.SpheresInRoom;
import scene.SpheresInSpace;


/**
 * Ray tracing renderer, for EC504 at Boston University based on the work of Rafael Martin
 * Bigio <rbigio@itba.edu.ar>
 * 
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class Renderer
{

	private boolean optionProgress = false;
	private boolean optionMultithreading = false;
	private int optionAntialiasing = 1;
	private String optionOutputFile;
	private String optionInputFile;
	private int optionWidth = 100;
	private int optionHeight = 100;
	private int optionShadow = 0;
	private int optionRefraction = 1;
	private int optionReflection = 1;
	private boolean optionPhong = true;

	public static double progress = 0.0;
	public static boolean done = false;

	/* basic setup for rendering simple scene */
	public static void main(String[] args)
	{
		Renderer r = new Renderer();
		r.optionProgress = true;

		r.optionAntialiasing = 0;

		r.optionWidth = 12*128;
		r.optionHeight = 12*72;

		r.optionShadow = 0;
		r.showSampleScene();
	}

	public void showSampleScene()
	{
		try
		{

			// new RenderViewer(renderScene(constructSampleScene()));
			new RenderViewer(renderScene(new SpheresInRoom()));

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setOptionProgress(boolean b)
	{
		optionProgress = b;
	}

	public void setOptionAntialiasing(int i)
	{
		optionAntialiasing = i;
	}

	public void setOptionOutputFile(String s)
	{
		optionOutputFile = s;
	}

	public void setOptionInputFile(String s)
	{
		optionInputFile = s;
	}

	public void setOptionWidth(int w)
	{
		optionWidth = w;
	}

	public void setOptionHeight(int h)
	{
		optionHeight = h;
	}

	public void setOptionShadow(int s)
	{
		optionShadow = s;
	}

	public void setOptionMultithreading(boolean b)
	{
		optionMultithreading = b;
	}

	public void setOptionRefraction(int s)
	{
		optionRefraction = s;
	}

	public void setOptionPhong(boolean b)
	{
		optionPhong = b;
	}

	public void setOptionReflection(int s)
	{
		optionReflection = s;
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
		Dimension imageSize = new Dimension(optionWidth, optionHeight);

		/* Ultra simple raytracer */
		SimpleRayTracer rayTracer = new SimpleRayTracer(scene, imageSize, optionAntialiasing);
		long time = System.currentTimeMillis();
		//BufferedImage result = rayTracer.render(optionProgress);
		 BufferedImage result = rayTracer.renderThreads(optionProgress);

		return result;
	}
}
