package objects;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import scene.Intersection;
import scene.Transformation;

public class TriangleMesh extends AbstractSceneObject{
	public int ntris, nverts;
	public int[] vertexIndex;
	public Pt[] P;
	public Normal[] normals;
	public Vec[] tangents;
	public float uvs[];
	public Transformation t;
	
	TriangleMesh(Transformation t, int nt, int nv, int[] vi, 
			Pt[] P, Normal[] N, Vec[] S, float[] uv){
		this.t = new Transformation(t);
		ntris = nt;
		nverts = nv;
		vertexIndex = new int[ntris*3];
		System.arraycopy(vi, 0, vertexIndex, 0, vi.length);
		
		if(uv != null) {
			uvs = new float[nverts*2];
			System.arraycopy(uv, 0, uvs, 0, uv.length);
		} else uvs = null;
		
		if(N != null) {
			normals = new Normal[nverts];
			System.arraycopy(N, 0, normals, 0, N.length);
		} else normals = null;
		
		if(S != null) {
			tangents = new Vec[nverts];
			System.arraycopy(S, 0, tangents, 0, S.length);
		} else tangents = null;
	}
	
	@Override 
	public boolean isIntersectable(){
		return false;
	}
	
	@Override
	public void refine(ArrayList<SceneObject> SOA){
		
	}

	@Override
	public Vector3d getNormalAt(Vector3d point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean IntersectP(Ray ray) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean Intersect(Ray ray, Intersection inter) {
		// TODO Auto-generated method stub
		return false;
	}

}
