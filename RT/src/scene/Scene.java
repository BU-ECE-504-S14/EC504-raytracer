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
 * A representation of a scene, which contains various objects which can be intersected and illuminated by rays.
  * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
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
	 * looks for the first object that a ray intersects, and the point at which the
	 * ray intersects that object. If no intersection return null.
	 * 
	 * @param ray, ray that you want to analyze.
	 * @param intersection, output parameter.
	 * @return intersected object.
	 */
	public SceneObject getFirstIntersectedObject(Ray ray,
			Intersection intersection) {
		return getFirstIntersectedObject(ray, intersection, objects);
	}

	/*ask aaron about this coding practice. Is this overloading.*/
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
		intersection.ray = nearestIntersection.ray;
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
