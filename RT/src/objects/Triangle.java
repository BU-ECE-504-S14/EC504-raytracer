package objects;

import geometry.BBox;
import geometry.Normal;
import geometry.Pt;
import geometry.Ray;
import geometry.Vec;

import javax.vecmath.Vector3d;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;

import raytracer.Util;
import scene.Intersection;

public class Triangle extends AbstractSceneObject
{

	public TriangleMesh mesh;
	public int v[] = new int[3];

	public Triangle(TriangleMesh m, int n)
	{
		mesh = m;
		v[0] = mesh.vertexIndex[3 * n];
		v[1] = mesh.vertexIndex[3 * n + 1];
		v[2] = mesh.vertexIndex[3 * n + 2];
		this.material = m.material;
		setName("New Triangle");
	}

	@Override
	public boolean IntersectP(Ray ray)
	{
		// get triangle positions. Handedness does not matter in IntersectP
		Pt p1 = new Pt(mesh.Points[v[0]]);
		Pt p2 = new Pt(mesh.Points[v[1]]);
		Pt p3 = new Pt(mesh.Points[v[2]]);

		// compute s1
		Vec e1 = new Vec(p2);
		e1.sub(p1);
		Vec e2 = new Vec(p3);
		e2.sub(p1);
		Vec s1 = new Vec();
		s1.cross(ray.direction, e2);
		float divisor = (float) Util.dotProduct(s1, e1);

		// check if ray is parallel to triangle or all vertices are colinear
		if (divisor == 0f)
			return false; // bad practice should use epsilon (modify if time allows)
		float invDivisor = 1f / divisor;

		// Compute first barycentric coordinate
		Vec d = new Vec(ray.position);
		d.sub(p1);
		float b1 = (float) (Util.dotProduct(d, s1) * invDivisor);
		if (b1 < 0f || b1 > 1f)
			return false; // the intersection is out of the triangle

		// Compute second barycentric coordinate
		Vec s2 = new Vec();
		s2.cross(d, e1);
		float b2 = (float) (Util.dotProduct(ray.direction, s2) * invDivisor);
		if (b2 < 0 || b2 + b1 > 1)
			return false; // the intersection is out of the triangle

		// Compute intersection point
		float t = (float) (Util.dotProduct(e2, s2) * invDivisor);
		if (t < ray.mint || t > ray.maxt)
			return false; // there is some other object closer than this or mesh is behind
							// the ray

		// triangle has definitely been intersected
		ray.maxt = t; // update nearest
		return true;
	}

	// for implementation details see page 140 Pharr
	@Override
	public boolean Intersect(Ray ray, Intersection Inter)
	{

		// get triangle positions
		// Aaron 4/14/14: Switched p3 and p2 to change 'handedness' of faces to match
		// Blender default.
		Pt p1 = new Pt(mesh.Points[v[0]]);
		Pt p3 = new Pt(mesh.Points[v[1]]);
		Pt p2 = new Pt(mesh.Points[v[2]]);

		// compute s1
		Vec e1 = new Vec(p2);
		e1.sub(p1);
		Vec e2 = new Vec(p3);
		e2.sub(p1);
		Vec s1 = new Vec();
		s1.cross(ray.direction, e2);
		float divisor = (float) Util.dotProduct(s1, e1);

		// check if ray is parallel to triangle or all vertices are colinear
		if (divisor == 0f)
			return false; // bad practice should use epsilon (modify if time allows)
		float invDivisor = 1f / divisor;

		// Compute first barycentric coordinate
		Vec d = new Vec(ray.position);
		d.sub(p1);
		float b1 = (float) (Util.dotProduct(d, s1) * invDivisor);
		if (b1 < 0f || b1 > 1f)
			return false; // the intersection is out of the triangle

		// Compute second barycentric coordinate
		Vec s2 = new Vec();
		s2.cross(d, e1);
		float b2 = (float) (Util.dotProduct(ray.direction, s2) * invDivisor);
		if (b2 < 0 || b2 + b1 > 1)
			return false; // the intersection is out of the triangle

		// Compute intersection point
		float t = (float) (Util.dotProduct(e2, s2) * invDivisor);
		if (t < ray.mint || t > ray.maxt)
			return false; // there is some other object closer than this or mesh is behind
							// the ray

		// We are hitting the triangle!
		// Compute triangle partial derivatives
		Vec dpdu = new Vec();
		Vec dpdv = new Vec();
		float uvs[][] = new float[3][2];
		GetUVs(uvs);

		// Compute deltas for triangle partial derivatives
		float du1 = uvs[0][0] - uvs[2][0]; // u1-u3
		float du2 = uvs[1][0] - uvs[2][0]; // u2-u3
		float dv1 = uvs[0][1] - uvs[2][1]; // v1-v3
		float dv2 = uvs[1][1] - uvs[2][1]; // v2-v3
		Vec dp1 = new Vec(p1);
		dp1.sub(p3);
		Vec dp2 = new Vec(p2);
		dp2.sub(p3);

		float determinant = du1 * dv2 - dv1 * du2;

		// if determinant is 0 coordinate system is 1d. Create arbitrary coordinate system
		// to support uv parameterization.
		if (determinant == 0f)
		{
			Normal n = new Normal();
			n.cross(e2, e1);
			n.normalize();
			Util.makeCoordinateSystem(n, dpdu, dpdv);
		}
		else
		{
			/*
			 * use matrix multiplication with axis vectors and inverse uv axis matrix to
			 * determine change in u and v
			 */

			float invdet = 1f / determinant;

			Vec dp1_copy = new Vec(dp1);
			Vec dp2_copy = new Vec(dp2);

			dp1.scale(dv2); // dv2*dp1
			dp2.scale(dv1); // dv1*dp2
			dp1.sub(dp2); // dv2*dp1 - dv1*dp2
			dp1.scale(invdet); // (dv2*dp1 - dv1*dp2)invdet
			dpdu = new Vec(dp1); // dpdu = (dv2*dp1 - dv1*dp2)invdet

			dp1_copy.scale(-du2); // -du2*dp1
			dp2_copy.scale(du1); // du1*dp2
			dp1_copy.add(dp2_copy); // -du2 * dp1 + du1 * dp2
			dp1_copy.scale(invdet); // (-du2 * dp1 + du1 * dp2) * invdet
			dpdv = new Vec(dp1_copy); // dpdv = (-du2 * dp1 + du1 * dp2) * invdet

			// (David's note) I really love it that Java doesn't let me overload
			// operators!
		}

		// compute triangle parameterizations based on intersection point's distance from
		// triangle origin.
		float b0 = 1 - b1 - b2;
		float tu = b0 * uvs[0][0] + b1 * uvs[1][0] + b2 * uvs[2][0];
		float tv = b0 * uvs[0][1] + b1 * uvs[1][1] + b2 * uvs[2][1];

		/* --create alpha mask test here if time permits-- */

		Pt phit = ray.getPointAt(t);
		Inter.update(phit, dpdu, dpdv, new Normal(0, 0, 0), new Normal(0, 0, 0), tu, tv, this);

		ray.maxt = t; // update nearest
		return true;
	}

	public void GetUVs(float uvs[][])
	{
		if (mesh.uvs != null)
		{// if mesh.uvs is not null
			uvs[0][0] = mesh.uvs[2 * v[0]];
			uvs[0][1] = mesh.uvs[2 * v[0] + 1];
			uvs[1][0] = mesh.uvs[2 * v[1]];
			uvs[1][1] = mesh.uvs[2 * v[1] + 1];
			uvs[2][0] = mesh.uvs[2 * v[2]];
			uvs[2][1] = mesh.uvs[2 * v[2] + 1];
		}
		else
		{
			uvs[0][0] = 0f;
			uvs[0][1] = 0f;
			uvs[1][0] = 1f;
			uvs[1][1] = 0f;
			uvs[2][0] = 1f;
			uvs[2][1] = 1f;
		}
	}

	@Override
	public BBox getWorldBound() {
		Pt p1 = new Pt(mesh.Points[v[0]]);
		Pt p2 = new Pt(mesh.Points[v[1]]);
		Pt p3 = new Pt(mesh.Points[v[2]]);
		
		BBox wBox = new BBox(p1);
		wBox = BBox.union(wBox, p2);
		wBox = BBox.union(wBox, p3);
		
		return wBox;
	}

}
