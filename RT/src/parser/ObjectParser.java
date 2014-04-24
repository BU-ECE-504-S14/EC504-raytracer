/**
 *
 */

package parser;

import geometry.Pt;
import geometry.Transformation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.Material;
import objects.TriangleMesh;
import util.SceneObjectException;

/**
 * @author Aaron Heuckroth
 */
public class ObjectParser
{
	static String RESOURCE_PATH = "./res/";

	public static void main(String[] args)
	{
		System.out.println("Getting objects...");
		List<TriangleMesh> objects = null;
		try
		{
			objects = ObjectParser.parseObjectsFromFile("box.obj");
		}
		catch (SceneObjectException ex)
		{
			System.out.println("Unable to parse file..");
			ex.printStackTrace();
		}

		if (objects.size() == 0)
		{
			System.out.println("No objects found in file!");
		}

		for (int i = 0; i < objects.size(); i++)
		{
			System.out.println(objects.get(i));
		}
		System.out.println("End!...");

	}

	/**
	 * Return positive or negative doubles, separated by spaces. Will break for Strings containing
	 * strange number sequences (ex. "0.2.35.1")
	 * 
	 * @param s
	 *            is the input String to be parsed.
	 * @return an array of all doubles found in the string.
	 */
	private static float[] getFloats(String s)
	{

		// This line represents a mesh vertex.
		Pattern p = Pattern.compile("-?[\\d.]+");
		Matcher m = p.matcher(s);
		ArrayList<Float> list = new ArrayList<Float>();
		while (m.find() == true)
		{
			list.add(Float.parseFloat(m.group()));
		}
		float[] out = new float[list.size()];

		for (int i = 0; i < list.size(); i++)
		{
			out[i] = list.get(i);
		}

		return out;

	}

	/**
	 * Return positive or negative ints, separated by spaces.
	 * 
	 * @param s
	 *            is the input String to be parsed.
	 * @return an array of all doubles found in the string.
	 */
	private static int[] getIntegers(String s)
	{

		// This line represents a mesh vertex.
		Pattern p = Pattern.compile("[\\d]+");
		Matcher m = p.matcher(s);
		ArrayList<Integer> list = new ArrayList<Integer>();
		while (m.find() == true)
		{
			list.add(Integer.parseInt(m.group()));
		}
		int[] out = new int[list.size()];

		for (int i = 0; i < list.size(); i++)
		{
			out[i] = list.get(i);
		}

		return out;
	}

	/**
	 * Return integers in the String which are immediately followed by a forward slash
	 * 
	 * @param s
	 *            is the target string
	 * @return an array of ints found in the String.
	 */
	private static int[] getLeftInts(String s)
	{
		Pattern p = Pattern.compile("([\\d]+)[/]");
		Matcher m = p.matcher(s);
		ArrayList<Integer> list = new ArrayList<Integer>();
		while (m.find() == true)
		{
			list.add(Integer.parseInt(m.group(1)));
		}
		int[] out = new int[list.size()];

		for (int i = 0; i < list.size(); i++)
		{
			out[i] = list.get(i);
		}

		return out;
	}

	/**
	 * Return integers in the String which are immediately preceded by a forward slash
	 * 
	 * @param s
	 *            is the target string
	 * @return an array of ints found in the String.
	 */
	private static int[] getRightInts(String s)
	{
		Pattern p = Pattern.compile("[/]([\\d]+)");
		Matcher m = p.matcher(s);
		ArrayList<Integer> list = new ArrayList<Integer>();
		while (m.find() == true)
		{
			list.add(Integer.parseInt(m.group(1)));
		}
		int[] out = new int[list.size()];

		for (int i = 0; i < list.size(); i++)
		{
			out[i] = list.get(i);
		}

		return out;
	}

	private static TriangleMesh createMesh(String name, List<Integer> faceMesh, List<Integer> faceTex, List<Pt> meshVertices, List<Float> texVertices)
	{
		Transformation trans = new Transformation();
		Pt[] verts = new Pt[meshVertices.size()];
		for (int i = 0; i < verts.length; i++)
		{
			verts[i] = meshVertices.get(i);
		}
		int[] fMeshInd = new int[faceMesh.size()];
		for (int i = 0; i < faceMesh.size(); i++)
		{
			fMeshInd[i] = faceMesh.get(i);
		}
		float[] uvs = null;
		if (texVertices.size() > 0)
		{
			uvs = new float[faceTex.size()];
			for (int i = 0; i < faceTex.size(); i++)
			{
				uvs[i] = texVertices.get(faceTex.get(i));
			}
		}
		// Currently not using UVs, since they're broken.
		TriangleMesh t = new TriangleMesh(trans, fMeshInd.length / 3, meshVertices.size(),
				fMeshInd,
				verts, null, null, null);
		t.setName(name);
		return t;
	}

	public static HashMap<String, Material> parseMaterialFromFile(String fileName)
	{
		HashMap<String, Material> materials = new HashMap<String, Material>();

		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(RESOURCE_PATH + fileName));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		boolean readingMaterial = false;

		// A map of UUIDs to Strings, pairing objects with their material names
		HashMap<UUID, String> materialMap = new HashMap<UUID, String>();

		boolean parsing = false;

		// Name of the current object
		String mtlName = "";

		// Ambient intensity
		double amb = 0.0;

		// Diffuse color
		Vector3d diffuse = new Vector3d(0, 0, 0);

		// Specular color
		Vector3d specular = new Vector3d(0, 0, 0);

		// Alpha (1.0 == full opacity, 0.0 == full transparency)
		double alpha = 1.0;

		// The line currently being parsed
		String currentLine;
		try
		{
			currentLine = in.readLine();
			while (currentLine != null)
			{
				if (currentLine.startsWith("newmtl"))
				{
					if (parsing)
					{
						Material newMtl = new Material();
						newMtl.diffuseColor = diffuse;
						newMtl.specularColor = specular;
						newMtl.ambientIntensity = amb;
						newMtl.alpha = alpha;
						materials.put(mtlName, newMtl);
					}
					parsing = true;
					mtlName = currentLine.split(" ")[1];
					amb = 0.0;
					diffuse = new Vector3d(0, 0, 0);
					specular = new Vector3d(0, 0, 0);
					alpha = 1.0;
				}

				else if (currentLine.startsWith("Kd"))
				{
					float[] diffs = getFloats(currentLine);
					diffuse = new Vector3d(diffs[0], diffs[1], diffs[2]);
				}

				else if (currentLine.startsWith("Ks"))
				{
					float[] specs = getFloats(currentLine);
					specular = new Vector3d(specs[0], specs[1], specs[2]);
				}

				else if (currentLine.startsWith("d"))
				{
					alpha = Double.parseDouble(currentLine.split(" ")[1]);
				}

				else if (currentLine.startsWith("Ka"))
				{
					float[] ambs = getFloats(currentLine);
					double avg = 0.0;

					for (int i = 0; i < ambs.length; i++)
					{
						avg += ambs[i];
					}

					avg /= ambs.length;
					amb = avg;
				}

				currentLine = in.readLine();
			}
			if (parsing)
			{
				Material newMtl = new Material();
				newMtl.diffuseColor = diffuse;
				newMtl.specularColor = specular;
				newMtl.ambientIntensity = amb;
				newMtl.alpha = alpha;
				materials.put(mtlName, newMtl);
			}
			in.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		return materials;

	}

	public static List<TriangleMesh> parseObjectsFromFile(String fileName)
			throws SceneObjectException
	{
		ArrayList<TriangleMesh> objects = new ArrayList<TriangleMesh>();

		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(RESOURCE_PATH + fileName));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		// The filename of material library .mtl file which contains material
		// information for all objects in this .obj file
		String matLibrary = "";

		// The number of vertices contained in objects parsed so far.
		int vertexOffset = 1;

		// A map of UUIDs to Strings, pairing objects with their material names
		HashMap<Integer, String> materialMap = new HashMap<Integer, String>();

		boolean parsing = false;

		// Name of the current object
		String objName = "";

		// Name of the material for the current object, used when parsing
		// material library to get texture info
		String matName = "";

		// Filename of the texture file associated with the current object, if
		// one exists.
		String textureFile = "";

		// List of the index pairs that make up the mesh vertices of each face
		ArrayList<Integer> faceMeshIndices = new ArrayList<Integer>();

		// List of the index pairs that make up the texture vertices of each face
		ArrayList<Integer> faceTextureIndices = new ArrayList<Integer>();

		// List of all mesh vertices, in order
		ArrayList<Pt> meshVertices = new ArrayList<Pt>();

		// List of all texture vertices, in order
		ArrayList<Float> textureVertices = new ArrayList<Float>();

		// The line currently being parsed
		String currentLine;
		try
		{
			currentLine = in.readLine();
			while (currentLine != null)
			{
				if (currentLine.startsWith("o"))
				{
					if (parsing)
					{
						TriangleMesh newMesh = createMesh(objName, faceMeshIndices, faceTextureIndices, meshVertices, textureVertices);
						materialMap.put(newMesh.getID(), matName);
						objects.add(newMesh);
						vertexOffset = newMesh.Points.length;
					}
					parsing = true;
					objName = currentLine.split(" ")[1];
					matName = "";
					faceMeshIndices = new ArrayList<Integer>();
					faceTextureIndices = new ArrayList<Integer>();
					meshVertices = new ArrayList<Pt>();
					textureVertices = new ArrayList<Float>();
				}

				else if (currentLine.startsWith("v "))
				{
					float[] meshVerts = getFloats(currentLine);
					meshVertices.add(new Pt(meshVerts[0], meshVerts[1], meshVerts[2]));

				}

				else if (currentLine.startsWith("vt"))
				{
					float[] texVerts = getFloats(currentLine);
					textureVertices.add(texVerts[0]);
					textureVertices.add(texVerts[1]);
				}
				else if (currentLine.startsWith("f"))
				{
					int[] mesh = null;
					int[] tex = null;
					Pattern p = Pattern.compile("[\\d]+[/][\\d]+");
					Matcher m = p.matcher(currentLine);
					if (m.find())
					{
						mesh = getLeftInts(currentLine);
						tex = getRightInts(currentLine);
					}
					else
					{
						mesh = getIntegers(currentLine);
					}

					/*
					 * Accounts for un-triangulated Blender exports, which use quads instead of
					 * triangles!
					 */
					int[] newMesh = mesh;
					int[] newTex = tex;

					if (mesh != null)
					{
						if (mesh.length == 4)
						{

							newMesh = new int[mesh.length + 2];
							newMesh[0] = mesh[0];
							newMesh[1] = mesh[1];
							newMesh[2] = mesh[2];
							newMesh[3] = mesh[0];
							newMesh[4] =
									mesh[2];
							newMesh[5] = mesh[3];

							for (int i = 0; i < newMesh.length; i++)
							{

								faceMeshIndices.add(newMesh[i] - vertexOffset);
							}
						}
					}

					if (tex != null)
					{
						if (tex.length == 4)

						{

							newTex = new int[tex.length + 2];
							newTex[0] = mesh[0];
							newTex[1] = mesh[1];
							newTex[2] = mesh[2];
							newTex[3] = mesh[0];
							newTex[4] = mesh[2];
							newTex[5] = mesh[3];

						}
						for (int i = 0; i < newMesh.length; i++)
						{

							faceTextureIndices.add(newTex[i] - vertexOffset);
						}
					}
				}

				else if (currentLine.startsWith("usemtl"))
				{
					// This line represents the name of the material associated with this object.
					matName = currentLine.split(" ")[1];
				}

				else if (currentLine.startsWith("mtllib"))
				{
					// This line represents the name of the material library (.mtl) file.
					matLibrary = currentLine.split(" ")[1];

				}

				else if (currentLine.startsWith("#"))
				{
					// This line is a comment, do nothing with it.
				}
				else if (currentLine.startsWith("s"))
				{
					// This line determines whether smooth shading is enabled for this object.
					// Current not used, do nothing.
				}
				currentLine = in.readLine();
			}
			if (parsing)
			{
				TriangleMesh newMesh = createMesh(objName, faceMeshIndices, faceTextureIndices, meshVertices, textureVertices);
				materialMap.put(newMesh.getID(), matName);
				objects.add(newMesh);
			}
			in.close();

		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		HashMap<String, Material> materials = parseMaterialFromFile(matLibrary);
		for (int i = 0; i < objects.size(); i++)
		{
			TriangleMesh current = objects.get(i);
			current.material = materials.get(materialMap.get(current.getID()));
		}

		return objects;

	}
}
