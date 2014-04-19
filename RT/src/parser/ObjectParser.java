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
import java.util.List;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.TriangleMesh;
import objects.TriangleMesh;

/**
 * @author Aaron Heuckroth
 */
public class ObjectParser
{

	public static void main(String[] args)
	{
		System.out.println("Getting objects...");
		List<TriangleMesh> objects = null;
		try
		{
			objects = ObjectParser.findObjects("./res/box.obj");
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < objects.size(); i++)
		{
			System.out.println(objects.get(i));
		}
		System.out.println("End!...");

	}

	public static TriangleMesh readObject(String s, int offset) throws IOException
	{
		TriangleMesh obj = null;
		Vector3d scale = new Vector3d(1, 1, 1);
		Vector3d trans = new Vector3d(0, 0, 0);
		AxisAngle4d rot = new AxisAngle4d(0, 0, 0, 0);
		Transformation t = new Transformation(scale, trans, rot);
		String lines[] = s.split("\n");
		ArrayList<String> pointLines = new ArrayList<String>();
		ArrayList<String> faceLines = new ArrayList<String>();
		String[] name = null;

		for (int i = 0; i < lines.length; i++)
		{
			String currentLine = lines[i];
			if (currentLine.startsWith("v"))
			{
				pointLines.add(currentLine);
			}
			else if (currentLine.startsWith("f"))
			{
				faceLines.add(currentLine);
			}
			else if (currentLine.startsWith("o"))
			{
				name = currentLine.split(" ");
			}
		}

		Pt[] points = new Pt[pointLines.size()];
		int[] faceIndices = new int[faceLines.size() * 3];
		for (int i = 0; i < pointLines.size(); i++)
		{
			String[] p = pointLines.get(i).split(" ");
			points[i] = new Pt(Double.parseDouble(p[1]), Double.parseDouble(p[2]),
					Double.parseDouble(p[3]));
		}
		for (int i = 0; i < faceLines.size(); i++)
		{
			String[] p = faceLines.get(i).split(" ");
			faceIndices[i*3 + 0] = Integer.parseInt(p[1]) -1 - offset;
			faceIndices[i*3 + 1] = Integer.parseInt(p[2]) -1 - offset;
			faceIndices[i*3 + 2] = Integer.parseInt(p[3]) -1 - offset;
		}

		for (int i = 0; i < points.length; i++)
		{
			System.out.println("Point " + i + ": " + points[i]);
		}
		for (int i = 0; i < faceLines.size(); i++)
		{
			System.out.println("Face " + i + ": " + faceIndices[i*3+0] + ", " + faceIndices[i*3+1] + ", "
					+ faceIndices[i*3+2]);
		}
		obj = new TriangleMesh(t, faceLines.size(), pointLines.size(), faceIndices, points, null,
				null, null);
		//obj.setName(name[1]);

		return obj;
	}

	public static List<TriangleMesh> findObjects(String fileName) throws IOException
	{
		ArrayList<TriangleMesh> objects = new ArrayList<TriangleMesh>();
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String currentObject = "";
		int offset = 0;;
		String currentLine = in.readLine();
		while (currentLine != null)
		{
			if (currentLine.startsWith("o"))
			{
				if (currentObject != ""){
					TriangleMesh obj;
					if (currentObject.startsWith("o")){
						obj = readObject(currentObject, offset);
						objects.add(obj);
						offset += obj.getPointCount();
					}
					currentObject = "";
				}
				currentObject += currentLine + "\n";
			}
			else {
				currentObject += currentLine + "\n";
			}
			currentLine = in.readLine();
		}
		if (!currentObject.equals("")){
			objects.add(readObject(currentObject, offset));
		}
		System.out.println("Parsed " + objects.size() + " objects!");
		return objects;
	}
}
