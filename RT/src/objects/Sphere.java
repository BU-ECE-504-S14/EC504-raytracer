package objects;

import javax.vecmath.Vector3d;

import raytracer.Util;
import scene.DifferentialGeometry;
import scene.Transformation;

public class Sphere extends AbstractSceneObject {

	private float radius = 1;
	private Vector3d position = new Vector3d(0, 0, 0);
	private float zmin,zmax;
	private float thetaMin,thetaMax,phiMax;
	public Transformation t;
	
	public Sphere(float radius, float z0, float z1, float pm, Transformation t){
		this.radius = radius;
		zmin = Util.clamp(Math.min(z0, z1), -radius, radius);
		zmax = Util.clamp(Math.max(z0, z1), -radius, radius);
		thetaMin = (float) Math.acos(Util.clamp(zmin/radius, -1f, 1f));
		thetaMax = (float) Math.acos(Util.clamp(zmax/radius, -1f, 1f));
		phiMax = (float) Math.toRadians(Util.clamp(pm, 0.0f, 360.0f));
		this.t = new Transformation(t);
	}

	public Vector3d getNormalAt(Vector3d pointOfIntersection) {
		Vector3d ret = new Vector3d(pointOfIntersection);
		ret.sub(position);	//ret =  point of intersection - sphere center position
		ret.scale(-1);
		ret.normalize();	//make into unit normal vector
		return ret;
	}

	@Override
	public boolean Intersect(Ray ray, DifferentialGeometry dg){
		float phi;
		Pt phit;
		
		//transform ray to obj space
		Ray o_ray = this.t.world2Object(ray);
		
		//calculate quadratic sphere coeffs
		float A = (float) o_ray.direction.dot(o_ray.direction); //dx^2 + dy^2 + dz^2
		float B = (float) (2f*o_ray.direction.dot(position));
		float C = (float) (o_ray.position.dot(o_ray.position))-radius*radius;
		
		float[] t = {0,0};
		if(!Quadratic(A,B,C,t)) return false;
		
		//compute intersection distance along ray
		if(t[0] > o_ray.maxt || t[1] < o_ray.mint) return false;
		float thit = t[0];
		if(t[0] < o_ray.mint){
			thit = t[1];
			if(thit > o_ray.maxt) return false;
		}
		
		//compute sphere hit position and phi
		phit = o_ray.getPointAt(thit);
		phi = computePhi(phit);
		
		//test sphere intersection against clipping parameters
		if((zmin > -radius && phit.z < zmin) || (zmax < radius && phit.z > zmax) || (phi > phiMax) ) {
			
			if(thit == t[1]) return false;
			if(t[1] > o_ray.maxt) return false;
			
			thit = t[1];
			phit = o_ray.getPointAt(thit);
			phi = computePhi(phit);
			if( (zmin > -radius && phit.z < zmin) || 
			    (zmax < radius && phit.z > zmax) || (phi > phiMax) ) return false;
		}
		
		//find parametric representation of sphere hit
		float u = phi / phiMax;
		float theta = (float) Math.acos(Util.clamp((float) (phit.z/radius), -1f, 1f));
		float v = (theta - thetaMin) / (thetaMax - thetaMin);
		
		//compute dpdu dpdv
		float zradius = (float) Math.sqrt(phit.x *phit.x + phit.y * phit.y);
		float invzradius = 1f / zradius;
		float cosphi = (float) (phit.x * invzradius);
		float sinphi = (float) (phit.y * invzradius);
		Vec dpdu = new Vec(-phiMax * phit.y, phiMax * phit.x, 0);
		Vec dpdv = new Vec(phit.z * cosphi, phit.z * sinphi, -radius * Math.sin(theta));
		dpdv.scale((thetaMax - thetaMin));
		
		//compute dndu dndv based on Weingarten equations
		Vec d2Pduu = new Vec(phit.x, phit.y, 0f);
		d2Pduu.scale(-phiMax * phiMax);
		Vec d2Pduv = new Vec(-sinphi, cosphi,0f);
		d2Pduv.scale((thetaMax - thetaMin) * phit.z * phiMax);
		Vec d2Pdvv = new Vec(phit.x, phit.y, phit.z);
		d2Pdvv.scale(-(thetaMax - thetaMin) * (thetaMax - thetaMin));
	    
		//compute coeffs for fundamental forms
		float E = (float) dpdu.dot(dpdu);
		float F = (float) dpdu.dot(dpdv);
		float G = (float) dpdv.dot(dpdv);
		Vec N = new Vec();
		N.cross(dpdu, dpdv);
		N.normalize();
		float e = (float) N.dot(d2Pduu);
		float f = (float) N.dot(d2Pduv);
		float g = (float) N.dot(d2Pdvv);
		
		float invEGF2 = 1f / (E*G - F*F);
		Vec sdpdu = new Vec(dpdu);
		Vec sdpdv = new Vec(dpdv);
		sdpdu.scale((f * F - e * G) * invEGF2);
		sdpdv.scale((e * F - f * E) * invEGF2);
		sdpdu.add(sdpdv);
		Normal dndu = new Normal(sdpdu);
		
		sdpdu = new Vec(dpdu);
		sdpdv = new Vec(dpdv);
		sdpdu.scale((g * F - f * G) * invEGF2);
		sdpdv.scale((f * F - g * E) * invEGF2);
		sdpdu.add(sdpdv);
		Normal dndv = new Normal(sdpdu);
		
		//Differential geometry initialization
		dg.update(this.t.object2World(phit),
				  this.t.object2World(dpdu), 
				  this.t.object2World(dpdv), 
				  this.t.object2World(dndu),
				  this.t.object2World(dndv), u, v, this);
		
		//update hit parameter
		ray.tHit = thit;
		
		return true;
	}
	
	private float computePhi(Pt phit){
		if(phit.x == 0 && phit.y ==0) phit.x = 1E-5f * radius; //make phit a small number to avoid 0/0 division
		float phi = (float) Math.atan2(phit.y, phit.x);
		if(phi < 0f) phi += 2f*Math.PI;
		return phi;
	}
	
	private boolean Quadratic(float A, float B, float C, float[] t){
		
		//find quadratic discriminant
		float discrim = B*B - 4f*A*C;
		if(discrim <= 0) return false;
		float rootdiscrim = (float) Math.sqrt(discrim);
		
		//compute t0,t1 i.e. the roots page no 119
		float q;
		if (B < 0) q = -0.5f * (B-rootdiscrim);
		else	   q = -0.5f * (B+rootdiscrim);
		t[0] = new Float(q / A);
		t[1] = new Float(C / q);
		
		//swap if t1<t0
		if (t[0] >= t[1]){
			float tmp = t[0];
			t[0] = t[1];
			t[1] = tmp;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "Sphere(radius=" + radius + ", position=" + position + ")" +
		material.toString();
	}
}
