package GUI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import parser.ObjectParser;
import raytracer.RenderViewer;
import raytracer.Renderer;
import scene.BuddhaScene;
import scene.BunnyScene;
import scene.MaterialScene;
import scene.Scene;
import scene.SpheresInRoom;
import util.SceneObjectException;

public class ScenePanel extends JPanel
{

	JPanel previewPanel;
	ObjectTable objects;
	Scene myScene;
	RenderSettingsPanel rendering;

	JPanel listPanel;

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

	File lastFile = null;

	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Sphere s = new Sphere();
		Scene scn = new BunnyScene();
		ScenePanel sp = new ScenePanel(scn);

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
	}

	public void setupPanels()
	{
		listPanel = new JPanel();
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(4, 2));
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		rendering = new RenderSettingsPanel(myScene);
		objects = new ObjectTable(myScene.getObjectArray());
		previewPanel = new JPanel();

		listPanel.setPreferredSize(new Dimension(300, 600));

		addMesh = new JButton("Add meshes");
		addSphere = new JButton("Add sphere");
		dupeObject = new JButton("Duplicate");
		removeObject = new JButton("Remove");
		importScene = new JButton("Import scene objects");
		save = new JButton("Save Scene");
		load = new JButton("Load Scene");
		settings = new JButton("Render Settings");
		renderButton = new JButton("Render Scene");

		JPanel renderButtonPanel = new JPanel();
		renderButtonPanel.add(renderButton);
		renderButton.setPreferredSize(new Dimension(150, 75));

		buttonPanel.add(addSphere);
		buttonPanel.add(addMesh);
		buttonPanel.add(importScene);
		buttonPanel.add(dupeObject);
		buttonPanel.add(removeObject);
		buttonPanel.add(save);
		buttonPanel.add(load);
		buttonPanel.add(settings);
		buttonPanel.setPreferredSize(new Dimension(300, 200));

		addSphere.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				addNewSphere();
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
			}

		});

		removeObject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				removeObject(objects.getSelectedObject());
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

		listPanel.add(objects);
		listPanel.add(renderButtonPanel);
		listPanel.add(buttonPanel);
		this.add(listPanel);
	}

	public void showSettings()
	{
		new RenderSettingsFrame(myScene, "Rendering Settings");
	}

	public void updateList()
	{
		objects.updateObjects(myScene.getObjectArray(), false);
	}
	
	public void updateList(boolean b){
		objects.updateObjects(myScene.getObjectArray(), b);
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

	public void addNewSphere()
	{
		myScene.addSceneObject(new Sphere());
		updateList();
	}

	public void loadScene()
	{
		File f = getSceneFile();
		if (f != null)
		{
			lastFile = f;
			myScene = Scene.readSceneFromFile(f);
			updateList();
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
			updateList();
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
			updateList();
		}
	}

	public void removeObject(SceneObject o)
	{
		myScene.removeSceneObject(o);
		updateList(true);
	}

	public void dupeObject(SceneObject o)
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
		updateList();
	}
}
