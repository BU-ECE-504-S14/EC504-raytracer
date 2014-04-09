package objects;


import javax.vecmath.Vector3d;

import raytracer.Util;
import scene.Intersection;

public class Triangle extends AbstractSceneObject {
	
	public TriangleMesh mesh;
	public int v[] = new int[3];
	
	public Triangle(TriangleMesh m, int n){
		mesh = m;
		 v[0] = mesh.vertexIndex[3*n];
		 v[1] = mesh.vertexIndex[3*n+1];
		 v[2] = mesh.vertexIndex[3*n+2];
	}

	//page 170 Pharr
	public boolean TriangleMesh(Ray ray, Intersection Inter) {
		
		//get triangle positions
		Pt p1 = new Pt(mesh.P[v[0]]);
		Pt p2 = new Pt(mesh.P[v[1]]);
		Pt p3 = new Pt(mesh.P[v[2]]);
		
		//compute s1
		Vec e1 = new Vec(p2);
		e1.sub(p1);
		Vec e2 = new Vec(p3);
		e2.sub(p1);
		Vec s1 = new Vec();
		s1.cross(ray.direction, e2);
		float divisor = (float) Util.dotProduct(s1, e1);
		
		//check if ray is parallel to triangle or all vertices are colinear 
		if (divisor == 0f) return false; //bad practice should use epsilon (modify if time allows)  
		float invDivisor = 1f / divisor;
		
		//Compute first barycentric coordinate
		Vec d = new Vec(ray.position);
		d.sub(p1);
		float b1 = (float) (Util.dotProduct(d, s1) * invDivisor);
		if (b1 < 0f || b1 > 1f) return false; //the intersection is out of the triangle
		
		//Compute second barycentric coordinate
		Vec s2 = new Vec();
		s2.cross(d, e1);
		float b2 = (float) (Util.dotProduct(ray.direction, s2) * invDivisor);
		if(b2 < 0 || b2+b1>1) return false; //the intersection is out of the triangle
		
		//Compute intersection point
		float t = (float) (Util.dotProduct(e2, s2) * invDivisor);
		if(t < ray.mint || t > ray.maxt) return false; //there is some other object closer than this or mesh is behind the ray
		
		//We are hitting the triangle! 
		//Compute triangle partial derivatives
		Vec dpdu, dpdv;
		float uvs[][] = new float[3][2];
		GetUVs(uvs);
		
		//Compute deltas for triangle partial derivatives
		float du1 = uvs[0][0] - uvs[2][0]; //u1-u3
		float du2 = uvs[1][0] - uvs[2][0]; //u2-u3
		float dv1 = uvs[0][1] - uvs[2][1]; //v1-v3
		float dv2 = uvs[1][1] - uvs[2][1]; //v2-v3
		Vec dp1 = new Vec(p1);
		dp1.sub(p3);
		Vec dp2 = new Vec(p2);
		dp2.sub(p3);
		
		float determinant = du1 * dv2 - dv1 * du2 ;
		if(determinant == 0f) {
			/*complete tomorrow */
		}
		
		return true;
	}
	public void GetUVs(float uvs[][]){
		if(mesh.uvs != null){//if mesh.uvs is not null
			uvs[0][0] = mesh.uvs[2*v[0]];
			uvs[0][1] = mesh.uvs[2*v[0]+1];
			uvs[1][0] = mesh.uvs[2*v[1]];
			uvs[1][1] = mesh.uvs[2*v[1]+1];
			uvs[2][0] = mesh.uvs[2*v[2]];
			uvs[2][1] = mesh.uvs[2*v[2]+1];
		}
		else {
			uvs[0][0] = 0f; uvs[0][1] = 0f;
			uvs[1][0] = 1f; uvs[1][1] = 0f;
			uvs[2][0] = 1f; uvs[2][1] = 1f;
		}
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
