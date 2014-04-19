/**
 * 
 */
package scene;

import javax.vecmath.Vector3d;

import objects.SceneObject;

/**
 * @author Tolga
 *
 */
public class Intersection {
	
	public Vector3d p;
	public Vector3d nn; //normalized normal
	public float u,v;
	public SceneObject shape;
    public Vector3d dpdu, dpdv;
    public Vector3d dndu, dndv;
	
	/**
	 * 
	 */
	public Intersection(){
		u=v=0;
		shape = null;
	}
	
	public Intersection(Intersection i){
		this.p = i.p;
		this.dpdu = i.dpdu;
		this.dpdv = i.dpdv;
		this.dndu = i.dndu;
		this.dndv = i.dndv;
		this.u=i.u;
		this.v=i.v;
		this.shape=i.shape;
		this.nn = i.nn;
		/*if(false){//shape && shape.ReverseOrientation shape.TransformSwapsHandedness)
			nn.negate(); 
		}*/
	}
	
	public Intersection(Vector3d p, Vector3d dpdu, Vector3d dpdv,Vector3d dndu,Vector3d dndv,float u,float v,SceneObject shape) {
		this.p = new Vector3d(p);
		this.dpdu = new Vector3d(dpdu);
		this.dpdv = new Vector3d(dpdv);
		this.dndu = new Vector3d(dndu);
		this.dndv = new Vector3d(dndv);
		this.u=u;
		this.v=v;
		this.shape=shape;
		nn = new Vector3d();
		nn.cross(dpdu, dpdv);
		nn.normalize();
		/*if(false){//shape && shape.ReverseOrientation shape.TransformSwapsHandedness)
			nn.negate(); 
		}*/

	}
	
	public void update(Vector3d p, Vector3d dpdu, Vector3d dpdv,Vector3d dndu,Vector3d dndv,float u,float v,SceneObject shape){
		this.p = new Vector3d(p);
		this.dpdu = new Vector3d(dpdu);
		this.dpdv = new Vector3d(dpdv);
		this.dndu = new Vector3d(dndu);
		this.dndv = new Vector3d(dndv);
		this.u=u;
		this.v=v;
		this.shape = shape;
		nn = new Vector3d();
		nn.cross(dpdu, dpdv);
		nn.normalize();
	}
}
