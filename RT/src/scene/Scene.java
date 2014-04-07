package scene;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.Ray;
import objects.SceneObject;
import raytracer.Camera;
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

	/**
	 * looks for the first object that a ray intersects, and the point at which the
	 * ray intersects that object. If no intersection return null.
	 * 
	 * @param ray, ray that you want to analyze.
	 * @param intersection, output parameter.
	 * @return intersected object.
	 */
	public boolean getFirstIntersectedObject(Ray ray,
			DifferentialGeometry dg) {
		return getFirstIntersectedObject(ray, dg, objects);
	}

	/*ask aaron about this coding practice. Is this overloading.*/
	public boolean getFirstIntersectedObject(Ray ray,
			DifferentialGeometry dg, Collection<SceneObject> objs) {
		SceneObject nearest = null;

		for (SceneObject o : objs) {
			if ( o.IntersectP(ray)) nearest = o;
		}
		if (nearest == null)
			return false;
		return nearest.Intersect(ray, dg);
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
