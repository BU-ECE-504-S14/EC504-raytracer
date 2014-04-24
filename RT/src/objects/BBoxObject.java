package objects;

import raytracer.Util;
import scene.Intersection;
import geometry.BBox;
import geometry.Normal;
import geometry.Pt;
import geometry.Ray;
import geometry.Vec;
/**
 * bounding box objects are simple objects based on the bounding boxes of shapes. They are meant for preview scenes
 * only are not currently set up to work with textures.
 * @author DavidsMac
 *
 */
public class BBoxObject extends AbstractSceneObject {

	private BBox bb = null;
	
	public BBoxObject(BBox b) {
		this.bb = new BBox(b);
	}
	
	@Override
	public boolean IntersectP(Ray ray) {
		float[] bounds = {1f,1f};
		boolean intersected = bb.IntersectP(ray, bounds);
		float hitT = bounds[0] < bounds[1] ? bounds[0] : bounds[1];
		
		if(intersected) {
			ray.maxt = hitT;
		}
		
		return intersected;
	}
	
	@Override
	public boolean Intersect(Ray ray, Intersection inter) {
		float[] bounds = {1f,1f};
		boolean intersected = bb.IntersectP(ray, bounds);
		float hitT = bounds[0] < bounds[1] ? bounds[0] : bounds[1];
		
		if(intersected) {
			ray.maxt = hitT;
		} else {
			return false;
		}
		
		Pt[] corners = bb.getCorners();
		
		Vec[] dpdus = new Vec[6];
		Vec[] dpdvs = new Vec[6];
		Vec[] bases = new Vec[6];
		Normal[] ns = new Normal[6];
		ns[0] = setDpduDpdvAndNormal(dpdvs, dpdus, bases, corners[4], corners[2], corners[0], 0); //front face
		ns[1] = setDpduDpdvAndNormal(dpdvs, dpdus, bases, corners[3], corners[5], corners[1], 1); //back face
		ns[2] = setDpduDpdvAndNormal(dpdvs, dpdus, bases, corners[0], corners[3], corners[1], 2); //left face
		ns[3] = setDpduDpdvAndNormal(dpdvs, dpdus, bases, corners[7], corners[4], corners[5], 3); //right face
		ns[4] = setDpduDpdvAndNormal(dpdvs, dpdus, bases, corners[2], corners[7], corners[3], 4); //top face
		ns[5] = setDpduDpdvAndNormal(dpdvs, dpdus, bases, corners[5], corners[0], corners[1], 5); //bottom face
		
		// Initialize point in world space at which ray makes contact with bounding box

		Pt Hit = ray.getPointAt(hitT);
		
		//find bbox that was hit by ray
		for(int ii = 0; ii < 6; ii++) {
			Vec hitPtVec = new Vec(Hit);
			hitPtVec.sub(bases[ii]);
			double intrinsic = ns[ii].dot(hitPtVec); 
			if( (intrinsic < .001d) && (intrinsic > -.001d) ) {
				inter.update(Hit, dpdus[ii], dpdvs[ii],  
								new Normal(0, 0, 0), new Normal(0, 0, 0), 1f, 1f, this);
				return true;
			}
		}
		
		return false;
	}
	
	
	@Override
	public BBox getWorldBound() {
		return new BBox(bb);
	}
	
	private Normal setDpduDpdvAndNormal(Vec[] v,Vec[] u, Vec[] b, Pt vEnd ,Pt uEnd, Pt base, int ii) {
		b[ii] = new Vec(base);
		v[ii] = new Vec(vEnd);
		u[ii] = new Vec(uEnd);
		v[ii].sub(base);
		u[ii].sub(base); 
		Normal normal = new Normal();
		normal.cross(v[ii], u[ii]);
		normal.normalize();
		normal.negate();
		return normal;
	}

}
