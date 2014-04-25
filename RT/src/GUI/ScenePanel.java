package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import parser.ObjectParser;
import raytracer.Camera;
import raytracer.RenderViewer;
import raytracer.Renderer;
import scene.BuddhaScene;
import scene.BunnyScene;
import scene.Light;
import scene.MaterialScene;
import scene.PointLight;
import scene.Scene;
import scene.SpheresInRoom;
import scene.SpheresInSpace;
import util.SceneObjectException;

public class ScenePanel extends JPanel
{

	JPanel previewPanel;
	TransformPanel transformPanel;
	LightTable lights;
	ObjectTable objects;
	Scene myScene;
	RenderSettingsPanel rendering;

	CameraPanel cameraPanel;

	JPanel listPanel;

	JPanel rightPanel;
	JPanel buttonPanel;

	JButton dupeObject;
	JButton addMesh;
	JButton importScene;
	JButton addSphere;
	JButton removeObject;
	JButton save;
	JButton load;
	JButton settings;
	JButton renderButton;
	JButton addLight;
	JButton remLight;

	File lastFile = null;

	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Sphere s = new Sphere();
		ScenePanel sp = new ScenePanel(new MaterialScene(s));
		f.add(sp);
		f.pack();
		f.setVisible(true);
	}

	public ScenePanel(Scene s)
	{
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		myScene = s;
		setupPanels();
		this.setMinimumSize(new Dimension(600, 700));
	}

	public void setupPanels()
	{
		lights = new LightTable(myScene.getLightArray());
		listPanel = new JPanel();
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(5, 2));
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		rendering = new RenderSettingsPanel(myScene);
		objects = new ObjectTable(myScene.getObjectArray());
		previewPanel = new JPanel();
		rightPanel = new JPanel(new BorderLayout());


		listPanel.setMinimumSize(new Dimension(350, 700));
		listPanel.setPreferredSize(new Dimension(350, 700));
		rightPanel.setPreferredSize(new Dimension(350, 700));
		rightPanel.setMinimumSize(new Dimension(350, 700));


		addMesh = new JButton("Import meshes");
		addSphere = new JButton("Add sphere");
		dupeObject = new JButton("Duplicate");
		removeObject = new JButton("Remove");
		importScene = new JButton("Import scene");
		save = new JButton("Save Scene");
		load = new JButton("Load Scene");
		settings = new JButton("Render Settings");
		renderButton = new JButton("Render Scene");
		addLight = new JButton("Add Light");
		remLight = new JButton("Remove Light");

		renderButton.setPreferredSize(new Dimension(150, 75));

		buttonPanel.add(addSphere);
		buttonPanel.add(addLight);
		buttonPanel.add(addMesh);
		buttonPanel.add(importScene);
		buttonPanel.add(dupeObject);
		buttonPanel.add(removeObject);
		buttonPanel.add(save);
		buttonPanel.add(load);
		buttonPanel.add(settings);
		buttonPanel.add(renderButton);

		transformPanel = new TransformPanel(new Sphere());
		updateTransformPanel();
		cameraPanel = new CameraPanel();
		rightPanel.add(transformPanel, BorderLayout.NORTH);

		updateCameraPanel();
		rightPanel.add(cameraPanel, BorderLayout.SOUTH);

		objects.onMouseClick(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt)
			{
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 1)
				{
					updateTransformPanel();
				}
			}
		});

		addSphere.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				addNewSphere();
			}

		});

		addLight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				addLight();
			}
		});

		remLight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				removeLight((PointLight) lights.getSelectedObject());
			}
		});

		addMesh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				addMeshesFromObj();
			}

		});

		dupeObject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				dupeObject(objects.getSelectedObject());
				dupeLight(lights.getSelectedObject());
			}

		});

		removeObject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				removeObject(objects.getSelectedObject());
				removeLight((PointLight) lights.getSelectedObject());
			}

		});

		importScene.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				addMeshesFromScn();
			}
		});

		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveScene();
			}
		});

		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadScene();
			}
		});

		settings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				showSettings();
			}
		});

		renderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				renderScene();
			}
		});

		buttonPanel.setPreferredSize(new Dimension(300, 150));
		buttonPanel.setMinimumSize(new Dimension(300, 150));
		listPanel.add(objects);
		listPanel.add(lights);
		listPanel.add(buttonPanel);
		this.add(listPanel);
		this.add(rightPanel);
	}

	public void updateCameraPanel()
	{
		if (myScene.getCamera() == null)
		{
			// do nothing!
		}
		else
		{
			cameraPanel.setCamera(myScene.getCamera());

			cameraPanel.setTargetListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e)
				{

					if (objects.getSelectedObject() != null)
					{
						cameraPanel.setTarget(objects.getSelectedObject());
					}
				}

			});
			cameraPanel.revalidate();
		}
	}

	public void updateTransformPanel()
	{
		if (myScene.getObjects().size() == 0)
		{
			Sphere dummySphere = new Sphere();
			dummySphere.setName("Dummy sphere -- add more scene objects!");
			transformPanel.newObject(dummySphere);
		}
		else if (objects.getSelectedObject() == null)
		{
			transformPanel.newObject(myScene.getObjects().get(0));
		}
		else
		{
			transformPanel.newObject(objects.getSelectedObject());
		}

		transformPanel.addFieldListeners(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				transformPanel.updateTransformInfo();
				updateLists();
			}
		});

		System.out.println(transformPanel.myObject.getTransform().toString());
		transformPanel.revalidate();
	}

	public void showSettings()
	{
		new RenderSettingsFrame(myScene, "Rendering Settings");
	}

	public void updateLists()
	{
		objects.updateObjects(myScene.getObjectArray());
		lights.updateLights(myScene.getLightArray());
	}

	public void renderScene()
	{
		try
		{
			new RenderViewer(Renderer.renderScene(myScene));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addLight()
	{
		myScene.addLight(new PointLight());
		updateLists();
	}

	public void removeLight(PointLight l)
	{
		if (l != null)
		{
			if (myScene.getLights().size() > 1)
			{
				myScene.removeLight(l);
				updateLists();
			}
			else {
				System.out.println("Must have at least one light in the scene, ignoring delete!");
			}
		}
	}

	public void addNewSphere()
	{
		myScene.addSceneObject(new Sphere());
		updateLists();
	}

	public void loadScene()
	{
		File f = getSceneFile();
		if (f != null)
		{
			lastFile = f;
			myScene = Scene.readSceneFromFile(f);
			updateLists();
			updateCameraPanel();
		}
	}

	public File getOutputLocation()
	{
		JFileChooser choose = null;
		if (lastFile != null)
		{
			choose = new JFileChooser(lastFile);
		}
		else
		{
			choose = new JFileChooser("./scn");
		}

		choose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("BRUTE FORCE scenes (.scn)",
				"scn");
		choose.setFileFilter(filter);

		int result = choose.showSaveDialog(this);

		if (result == choose.APPROVE_OPTION)
		{
			if (!choose.getSelectedFile().getAbsolutePath().endsWith(".scn"))
			{
				return new File(choose.getSelectedFile() + ".scn");
			}
			else
			{
				return choose.getSelectedFile();
			}
		}
		else
		{
			return null;
		}
	}

	public File getObjFile()
	{
		File target = null;
		JFileChooser choose = new JFileChooser("./res");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Wavefront Objects (.obj)",
				"obj");
		choose.setFileFilter(filter);
		int returnVal = choose.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			target = choose.getSelectedFile();
			if (myScene.settings.isVERBOSE())
			{
				System.out.println("Importing triangle mesh from: " + target.getName());
			}
		}
		return target;
	}

	public File getSceneFile()
	{
		File target = null;
		JFileChooser choose = new JFileChooser("./scn");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("BRUTE FORCE scenes (.scn)",
				"scn");
		choose.setFileFilter(filter);
		int returnVal = choose.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			target = choose.getSelectedFile();
			if (myScene.settings.isVERBOSE())
			{
				System.out.println("Importing scene from: " + target.getName());
			}
		}
		return target;
	}

	public void addMeshesFromScn()
	{
		File scnFile = getSceneFile();
		if (scnFile != null)
		{
			List<SceneObject> objects = null;

			objects = ObjectParser.parseObjectsFromSceneFile(scnFile);
			for (SceneObject o : objects)
			{
				myScene.addSceneObject(o);
			}
			updateLists();
		}
	}

	public void saveScene()
	{
		File outFile = getOutputLocation();
		if (outFile != null)
		{
			Scene.writeSceneToFile(myScene, outFile);
		}
	}

	public void addMeshesFromObj()
	{
		File meshFile = getObjFile();
		if (meshFile != null)
		{

			List<TriangleMesh> meshes = null;
			try
			{
				meshes = ObjectParser.parseObjectsFromFile(meshFile);
			}
			catch (SceneObjectException e)
			{
				e.printStackTrace();
			}
			for (TriangleMesh t : meshes)
			{
				myScene.addSceneObject(t);
			}
			updateLists();
		}
	}

	public void removeObject(SceneObject o)
	{
		if (o != null)
		{
			if (myScene.getObjects().size() > 1)
			{
				myScene.removeSceneObject(o);
				updateLists();
			}
			else {
				System.out.println("Must have at least one object in the scene, ignoring delete!");
			}
		}
	}

	public void dupeLight(PointLight l)
	{
		if (l != null)
		{
			PointLight newLight = new PointLight(l);

			myScene.addLight(newLight);
			updateLists();
		}
	}

	public void dupeObject(SceneObject o)
	{
		if (o != null)
		{
			SceneObject newObject = null;
			if (o instanceof TriangleMesh)
			{
				newObject = new TriangleMesh((TriangleMesh) o);
			}
			if (o instanceof Sphere)
			{
				newObject = new Sphere((Sphere) o);
			}

			myScene.addSceneObject(newObject);
			updateLists();
		}
	}
}
