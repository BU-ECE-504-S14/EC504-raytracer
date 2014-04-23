package objects;

import geometry.BBox;
import geometry.Normal;
import geometry.Pt;
import geometry.Transformation;
import geometry.Vec;

import java.util.ArrayList;
import java.util.UUID;

public class TriangleMesh extends AbstractSceneObject
{
	private int ntris, nverts;
	public int[] vertexIndex;
	public Pt[] Points;
	public Normal[] normals;
	public Vec[] tangents;
	public float uvs[];
	public Transformation trans;

	public TriangleMesh(TriangleMesh t)
	{
		this.material = new Material(t.material);
		ntris = t.ntris;
		nverts = t.nverts;
		Points = new Pt[t.Points.length];
		vertexIndex = new int[t.vertexIndex.length];
		for (int i = 0; i < vertexIndex.length; i++)
		{
			vertexIndex[i] = t.vertexIndex[i];
		}
		for (int i = 0; i < t.Points.length; i++)
		{
			Points[i] = t.Points[i];
		}

		if (t.normals != null)
		{
			normals = new Normal[t.normals.length];
			for (int i = 0; i < t.normals.length; i++)
			{
				normals[i] = t.normals[i];
			}
		}

		if (t.tangents != null)
		{
			tangents = new Vec[t.tangents.length];
			for (int i = 0; i < t.tangents.length; i++)
			{
				tangents[i] = t.tangents[i];
			}
		}
		if (t.uvs != null)
		{
			uvs = new float[t.uvs.length];
			for (int i = 0; i < t.uvs.length; i++)
			{
				uvs[i] = t.uvs[i];
			}
		}
		trans = new Transformation(t.trans);
	}

	public TriangleMesh getCopy()
	{
		return new TriangleMesh(this);
	}

	// Points are expected to be in object space
	public TriangleMesh(Transformation t, int nt, int nv, int[] vi, Pt[] P,
			Normal[] N, Vec[] S, float[] uv)
	{
		this.trans = new Transformation(t);
		ntris = nt;
		nverts = nv;
		vertexIndex = new int[ntris * 3];
		System.arraycopy(vi, 0, vertexIndex, 0, vi.length);

		if (uv != null)
		{
			uvs = new float[ntris * 3];
			System.arraycopy(uv, 0, uvs, 0, uv.length);
		}
		else
			uvs = null;

		if (N != null)
		{
			normals = new Normal[nverts];
			System.arraycopy(N, 0, normals, 0, N.length);
		}
		else
			normals = null;

		if (S != null)
		{
			tangents = new Vec[nverts];
			System.arraycopy(S, 0, tangents, 0, S.length);
		}
		else
			tangents = null;

		Points = new Pt[nverts];
		for (int i = 0; i < nverts; i++)
			Points[i] = t.object2World(P[i]); // for triangle mesh object points
												// are
												// stored in world space
		setName("New Triangle Mesh");
	}

	public void updateTransform(Transformation t)
	{
		for (int i = 0; i < Points.length; i++)
		{
			Points[i] = trans.world2Object(Points[i]);
		}

		this.trans = t;

		for (int i = 0; i < Points.length; i++)
		{
			Points[i] = trans.object2World(Points[i]);
		}
	}

	public int getPointCount()
	{
		return Points.length;
	}

	@Override
	public boolean isIntersectable()
	{
		return false;
	}

	@Override
	public void refine(ArrayList<SceneObject> SOA)
	{
		SOA.ensureCapacity(ntris); // avoid vector doubling copy cost
		for (int i = 0; i < ntris; i++)
			SOA.add(new Triangle(this, i));
	}

	@Override
	public String toString()
	{
		String out = "";
		out += "Triangle Mesh (ID: " + id + "\n";
		out += "Name: " + name + "\n";
		out += "Vertices: " + "\n";
		for (int i = 0; i < Points.length; i++)
		{
			out += "Vertex " + i + ": " + Points[i] + "\n";
		}
		out += "\n";

		out += "Faces: " + "\n";
		for (int i = 0; i < vertexIndex.length; i += 3)
		{
			out += "Face " + i / 3 + ": " + vertexIndex[i] + ", " + vertexIndex[i + 1] + ", " + vertexIndex[i + 2] + "\n";
		}
		return out;
	}

	@Override
	public BBox getWorldBound()
	{
		BBox wBox = new BBox();

		for (Pt p : Points)
		{
			wBox = BBox.union(wBox, p);
		}

		return wBox;
	}

}
