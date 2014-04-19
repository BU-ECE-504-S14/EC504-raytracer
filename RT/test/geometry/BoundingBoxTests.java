package geometry;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.AbstractSceneObject.NotIntersectableException;
import objects.AbstractSceneObject.RefinementException;
import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;

import org.junit.Test;

public class BoundingBoxTests {
	
	@Test
	public void testBBPointConstructor() {
		BBox testPointBB = new BBox(new Pt(1,1,1));
		assertEquals(testPointBB.getpMax(),testPointBB.getpMin());
	}
	
	@Test
	public void testBBTwoPointConstructor() {
		BBox testTwoPointsBB = new BBox(new Pt(1,-1,1), new Pt(-1,1,-1));
		assertEquals(true , testTwoPointsBB.getpMin().x == -1f);
		assertEquals(true , testTwoPointsBB.getpMin().y == -1f);
		assertEquals(true , testTwoPointsBB.getpMin().z == -1f);
		assertEquals(true , testTwoPointsBB.getpMax().x == 1f);
		assertEquals(true , testTwoPointsBB.getpMax().y == 1f);
		assertEquals(true , testTwoPointsBB.getpMax().z == 1f);
	}
	
	@Test
	public void testBBBBConstructor() {
		BBox testTwoPointsBB = new BBox(new Pt(-1,1,-1), new Pt(1,-1,1));
		BBox testBBBB = new BBox(testTwoPointsBB);
		
		assertEquals(true , testBBBB.getpMin().x == -1f);
		assertEquals(true , testBBBB.getpMin().y == -1f);
		assertEquals(true , testBBBB.getpMin().z == -1f);
		assertEquals(true , testBBBB.getpMax().x == 1f);
		assertEquals(true , testBBBB.getpMax().y == 1f);
		assertEquals(true , testBBBB.getpMax().z == 1f);
	}
	
	@Test
	public void UnionBBPt() {
		BBox testTwoPointsBB1 = new BBox(new Pt(-1,-1,-1), new Pt(0,0,0));
		Pt testPt = new Pt(1,0,1);
		BBox UnionBBPt = BBox.union(testTwoPointsBB1, testPt);
		
		assertEquals(true , UnionBBPt.getpMin().x == -1f);
		assertEquals(true , UnionBBPt.getpMin().y == -1f);
		assertEquals(true , UnionBBPt.getpMin().z == -1f);
		assertEquals(true , UnionBBPt.getpMax().x == 1f);
		assertEquals(true , UnionBBPt.getpMax().y == 0f);
		assertEquals(true , UnionBBPt.getpMax().z == 1f);
	}
	
	@Test
	public void UnionBBBB() {
		BBox testTwoPointsBB1 = new BBox(new Pt(-1,-1,-1), new Pt(0,0,0));
		BBox testTwoPointsBB2 = new BBox(new Pt(0,0,0), new Pt(1,1,1));
		BBox UnionBBBB = BBox.union(testTwoPointsBB1, testTwoPointsBB2);
		
		assertEquals(true , UnionBBBB.getpMin().x == -1f);
		assertEquals(true , UnionBBBB.getpMin().y == -1f);
		assertEquals(true , UnionBBBB.getpMin().z == -1f);
		assertEquals(true , UnionBBBB.getpMax().x == 1f);
		assertEquals(true , UnionBBBB.getpMax().y == 1f);
		assertEquals(true , UnionBBBB.getpMax().z == 1f);
	}
	
	@Test
	public void overlapsTest() {
		BBox oTest1 = new BBox(new Pt(0,0,0), new Pt(1,1,1));
		BBox overlaps = new BBox(new Pt(.5,.5,.5), new Pt(2,2,2));
		BBox DNoverlap = new BBox(new Pt(.5,2,.5), new Pt(2,2,2));
		
		assertEquals(true, oTest1.overlaps(overlaps));
		assertEquals(false, oTest1.overlaps(DNoverlap));
	}
	
	@Test
	public void insideTest() {
		BBox iTest1 = new BBox(new Pt(0,0,0), new Pt(1,1,1));
		Pt insidePt = new Pt(.5,.5,.5);
		Pt edgePt = new Pt(1,1,1);
		Pt outsidePt = new Pt(2,2,2);
		
		assertEquals(true, iTest1.inside(insidePt));
		assertEquals(true, iTest1.inside(edgePt));
		assertEquals(false, iTest1.inside(outsidePt));
	}
	
	@Test 
	public void expandTest() {
		BBox eTest = new BBox(new Pt(0,0,0));
		eTest.expand(1f);
		
		assertEquals(true , eTest.getpMin().x == -1f);
		assertEquals(true , eTest.getpMin().y == -1f);
		assertEquals(true , eTest.getpMin().z == -1f);
		assertEquals(true , eTest.getpMax().x == 1f);
		assertEquals(true , eTest.getpMax().y == 1f);
		assertEquals(true , eTest.getpMax().z == 1f);
	}
	
	@Test 
	public void surfaceAreaAndVolumeTest() {
		BBox SAVTest = new BBox(new Pt(-1,-1,-1), new Pt(1,1,1));
		int surfaceArea = 6*(2*2);
		int volume  = (int) 2*2*2;
		
		assertEquals(surfaceArea, (int) SAVTest.surfaceArea());
		assertEquals(volume, (int) SAVTest.volume());
	}
	
	@Test
	public void lerpTest() {
		BBox lTest = new BBox(new Pt(0,0,0), new Pt(1,1,1));
		Pt center = lTest.lerp(.5f, .5f, .5f);
		Pt lowerQuarterCenter = lTest.lerp(.25f, .25f, .25f);
		Pt lowerRightQuarterCenter = lTest.lerp(.25f, .75f, .25f);
		
		assertEquals(true , center.x == .5f);
		assertEquals(true , center.y == .5f);
		assertEquals(true , center.z == .5f);
		assertEquals(true , lowerQuarterCenter.x == .25f);
		assertEquals(true , lowerQuarterCenter.y == .25f);
		assertEquals(true , lowerQuarterCenter.z == .25f);
		assertEquals(true , lowerRightQuarterCenter.x == .25f);
		assertEquals(true , lowerRightQuarterCenter.y == .75f);
		assertEquals(true , lowerRightQuarterCenter.z == .25f);
	}
	
	@Test
	public void IntersectPTest() {
		BBox interTestF = new BBox(new Pt(-1,-1,2), new Pt(2,2,4));
		Ray frontHit = new Ray(new Pt(0,0,0), new Vec(0,0,2));
		Ray frontHitE = new Ray(new Pt(0,0,0), new Vec(-1,-1,2));
		Ray frontMiss = new Ray(new Pt(0,0,0), new Vec(-2,-2,2));
		
		BBox interTestT = new BBox(new Pt(-1,-4,-1), new Pt(1,-2,1));
		Ray topHit = new Ray(new Pt(0,0,0), new Vec(0,-2,0));
		Ray topHitE = new Ray(new Pt(0,0,0), new Vec(1,-2,1));
		Ray topMiss = new Ray(new Pt(0,0,0), new Vec(2,-2,2));
		
		assertEquals(true, interTestF.IntersectP(frontHit, new float[2]));
		assertEquals(true, interTestF.IntersectP(frontHitE, new float[2]));
		assertEquals(false, interTestF.IntersectP(frontMiss, new float[2]));
		
		assertEquals(true, interTestT.IntersectP(topHit, new float[2]));
		assertEquals(true, interTestT.IntersectP(topHitE, new float[2]));
		assertEquals(false, interTestT.IntersectP(topMiss, new float[2]));
	}
	
	@Test 
	public void cornersTest() {
		BBox cTest = new BBox(new Pt(-1,-1,-1), new Pt(1,1,1));
		Pt[] corners = cTest.getCorners();
		
		assertEquals(true, corners[0].equals(new Pt(-1f,-1f,-1f))); //000
		assertEquals(true, corners[1].equals(new Pt(-1f,-1f,1f))); //001
		assertEquals(true, corners[2].equals(new Pt(-1f,1f,-1f))); //010
		assertEquals(true, corners[3].equals(new Pt(-1f,1,1))); //011
		assertEquals(true, corners[4].equals(new Pt(1f,-1f,-1f))); //100
		assertEquals(true, corners[5].equals(new Pt(1f,-1f,1f))); //101
		assertEquals(true, corners[6].equals(new Pt(1f,1f,-1f))); //110
		assertEquals(true, corners[7].equals(new Pt(1f,1f,1f))); //111
	}
	
	@Test
	public void testSphereBBIntersect() throws NotIntersectableException {
		Vector3d scale = new Vector3d(1, 1, 1);
		Vector3d pos = new Vector3d(0, 0, 2);
		AxisAngle4d rot = new AxisAngle4d(0, 0, 1, 0);
		Transformation t = new Transformation(scale, pos, rot);
		SceneObject testSphere = new Sphere(1f, 1f, -1f, 360f, t);
		
		Ray testHitBBandS = new Ray(new Pt(0,0,0), new Vec(0,0,1));
		Ray testHitBB = new Ray(new Pt(0,0,0), new Vec(-1,-1,1));
		Ray testMiss = new Ray(new Pt(0,0,0), new Vec(-2,-2,1));
		
		assertEquals(true, testSphere.IntersectP(testHitBBandS));
		assertEquals(true, testSphere.getWorldBound().IntersectP(testHitBBandS, new float[2]));
		
		assertEquals(false, testSphere.IntersectP(testHitBB));
		assertEquals(true, testSphere.getWorldBound().IntersectP(testHitBB, new float[2]));
		
		assertEquals(false, testSphere.IntersectP(testMiss));
		assertEquals(false, testSphere.getWorldBound().IntersectP(testMiss, new float[2]));
	}
	
	public void testTriangleMeshBBIntersect() throws NotIntersectableException, RefinementException {
		Vector3d scale = new Vector3d(1, 1, 1);
		Vector3d pos = new Vector3d(0, 0, 2);
		AxisAngle4d rot = new AxisAngle4d(0, 0, 1, 0);
		
		Pt[] P = new Pt[6];
		P[0] = new Pt(-1, -1, 0);
		P[1] = new Pt(1, 0, 0);
		P[2] = new Pt(-1, 1, 0);
		P[3] = new Pt(-1, -1, 2);
		P[4] = new Pt(1, 0, 2);
		P[5] = new Pt(-1, 1, 2);

		int[] vi = { 0, 1, 2, 3, 4, 5};
		Transformation t = new Transformation(scale, pos, rot);
		SceneObject triMesh = new TriangleMesh(t, 2, 6, vi, P, null, null, null);
		ArrayList<SceneObject> tris = new ArrayList<SceneObject>();
		triMesh.refine(tris);
		
		
		Ray testHitBBandT = new Ray(new Pt(0,0,0), new Vec(0,0,1));
		Ray testHitBB = new Ray(new Pt(0,0,0), new Vec(1,1,1));
		Ray testMiss = new Ray(new Pt(0,0,0), new Vec(2,2,1));
		
		assertEquals(true, triMesh.getWorldBound().IntersectP(testHitBBandT, new float[2]));
		assertEquals(true, tris.get(0).getWorldBound().IntersectP(testHitBBandT, new float[2]));
		assertEquals(true, triMesh.IntersectP(testHitBBandT));
		assertEquals(true, tris.get(0).IntersectP(testHitBBandT));
		
		assertEquals(true, triMesh.getWorldBound().IntersectP(testHitBB, new float[2]));
		assertEquals(true, tris.get(0).getWorldBound().IntersectP(testHitBB, new float[2]));
		assertEquals(false, triMesh.IntersectP(testHitBB));
		assertEquals(false, tris.get(0).IntersectP(testHitBB));
		
		assertEquals(false, triMesh.getWorldBound().IntersectP(testMiss, new float[2]));
		assertEquals(false, tris.get(0).getWorldBound().IntersectP(testMiss, new float[2]));
		assertEquals(false, triMesh.IntersectP(testMiss));
		assertEquals(false, tris.get(0).IntersectP(testMiss));
	}

}
