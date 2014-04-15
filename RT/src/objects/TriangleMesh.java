package objects;

import java.util.ArrayList;
import scene.Transformation;

public class TriangleMesh extends AbstractSceneObject
{
	private int ntris, nverts;
	public int[] vertexIndex;
	public Pt[] Points;
	public Normal[] normals;
	public Vec[] tangents;
	public float uvs[];
	public Transformation trans;

	// Points are expected to be in object space
	public TriangleMesh(Transformation t, int nt, int nv, int[] vi, Pt[] P, Normal[] N, Vec[] S,
			float[] uv)
	{
		this.trans = new Transformation(t);
		ntris = nt;
		nverts = nv;
		vertexIndex = new int[ntris * 3];
		System.arraycopy(vi, 0, vertexIndex, 0, vi.length);

		if (uv != null)
		{
			uvs = new float[nverts * 2];
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
			Points[i] = t.object2World(P[i]); // for triangle mesh object points are
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

}
