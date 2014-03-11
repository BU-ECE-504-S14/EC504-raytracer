package scene;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.SceneObject;

import raytracer.Camera;
import raytracer.Ray;
import raytracer.Util;

/**
 * Representation of the scene.  Contains all the objects, the lights, and
 * the camara. To use, first load all of the lights and objects, set the camera
 * and finally, invoke the method organizeObjectsAndCamera.
 */
public class Scene {

	private Collection<SceneObject> objects;
	private Collection<PointLight> lights;
	private Camera camera;

	public Scene() {
		camera = new Camera(new Vector3d(0, 0, 10),
				new AxisAngle4d(0, 0, -1, 0), (float) (Math.PI / 4));
		objects = new HashSet<SceneObject>();
		lights = new HashSet<PointLight>();
	}

	protected Scene(Collection<SceneObject> objects,
			Collection<PointLight> lights, Camera camera) {
		this.objects = objects;
		this.lights = lights;
		this.camera = camera;
	}

	public void addSceneObject(SceneObject obj) {
		this.objects.add(obj);
	}

	public void addLight(PointLight light) {
		this.lights.add(light);
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Collection<SceneObject> getObjects() {
		return objects;
	}

	public SceneObject[] getAllLeafObjects() {
		HashSet<SceneObject> set = new HashSet<SceneObject>();
		for (SceneObject o : objects) {
			gatherLeafObjects(o, set);
		}
		return set.toArray(new SceneObject[set.size()]);
	}

	public void gatherLeafObjects(SceneObject obj, Set<SceneObject> set) {
		Collection<? extends SceneObject> children = obj.getChildren();
		if (children == null) {
			set.add(obj);
		} else {
			for (SceneObject o : children) {
				gatherLeafObjects(o, set);
			}
		}
	}

	public Collection<PointLight> getLights() {
		return lights;
	}

	public Camera getCamera() {
		return camera;
	}

	public Intersection getFirstIntersectedObject(Ray ray) {
		Intersection ret = new Intersection();
		getFirstIntersectedObject(ray, ret, objects);
		return ret;
	}

	/**
	 * Given a ray, find the first object intersected and the point
	 * of intersection in the object. If it intersects with nothing, return null. 
	 * 
	 * @param ray Ray to be analyzed
	 * @param intersection Output parameter with the point of intersection
	 * @return The intersected object.
	 */
	public SceneObject getFirstIntersectedObject(Ray ray,
			Intersection intersection) {
		return getFirstIntersectedObject(ray, intersection, objects);
	}

	public SceneObject getFirstIntersectedObject(Ray ray,
			Intersection intersection, Collection<SceneObject> objs) {
		SceneObject nearest = null;
		Intersection currentIntersection, nearestIntersection = null;
		double nearestDistance = Double.MAX_VALUE;

		for (SceneObject o : objs) {
			if ((currentIntersection = o.intersectsRay(ray)) == null)
				continue;

			Vector3d aux = new Vector3d(currentIntersection.point);
			double currentDistance;
			aux.sub(ray.position);
			currentDistance = Util.Norm(aux);

			if (nearestIntersection == null
					|| currentDistance < nearestDistance) {
				nearest = o;
				nearestIntersection = currentIntersection;
				nearestDistance = currentDistance;
			}
		}
		if (nearestIntersection == null)
			return null;
		intersection.point = nearestIntersection.point;
		intersection.normal = nearestIntersection.normal;
		return nearest;
	}

	public void dumpScene() {
		System.out.println("SCENE");
		System.out.println("Objects:");
		for (SceneObject s : objects) {
			System.out.println("  " + s.toString());
		}
		System.out.println("Lights:");
		for (PointLight p : lights) {
			System.out.println("  " + p.toString());
		}
		System.out.println(camera.toString());
	}

}
