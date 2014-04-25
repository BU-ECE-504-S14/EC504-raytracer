/**
 *
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.Sphere;
import raytracer.Renderer;
import scene.MaterialScene;
import scene.Scene;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class SpherePanel extends JPanel
{
	public Sphere mySphere;
	Renderer materialRenderer = new Renderer();
	JPanel leftPanel;
	JPanel rightPanel;

	SphereInfoPanel infoPanel;
	MaterialPanel matPanel;

	JButton updateButton;

	PreviewPanel previewPanel;

	JPanel lowerLeftPanel;
	public MaterialScene matScene;

	BufferedImage preview = null;

	static JFrame myFrame = null;

	public static void main(String[] args)
	{
		Sphere demoSphere = new Sphere();
		float radius = .5f;
		Vector3d position = new Vector3d(0, 0, 2);
		AxisAngle4d rotation = new AxisAngle4d(0, 0, 0, 0);
		demoSphere.setTransform(new Vector3d(radius, radius, radius), position, rotation);

		demoSphere.material.diffuseColor = new Vector3d(.8, .15, .15);
		JFrame testFrame = new JFrame("Scene Object Information: Sphere");
		//testFrame.setMinimumSize(new Dimension(650, 600));
	//	testFrame.setPreferredSize(new Dimension(650, 600));
		testFrame.setResizable(false);
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		SpherePanel testPanel = new SpherePanel(demoSphere);
		testFrame.add(testPanel);
		testFrame.pack();
		testFrame.setVisible(true);
		myFrame = testFrame;
	}

	public SpherePanel(Sphere targetSphere)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		mySphere = targetSphere;
		matScene = new MaterialScene(mySphere);
		setMaterialViewDefaults();

		setupPanels();
		updatePreviewImage();
		setVisible(true);
	}

	private void setMaterialViewDefaults()
	{
		/*
		materialRenderer.setOptionAntialiasing(1);
		materialRenderer.setOptionReflection(3);
		materialRenderer.setOptionRefraction(5);
		materialRenderer.setOptionShadow(0);
		materialRenderer.setOptionWidth(300);
		materialRenderer.setOptionHeight(300);
		materialRenderer.setOptionMultithreading(false);
		*/
	}

	public void updatePreviewImage()
	{
		matScene.updateScene(mySphere);
		try
		{
			try
			{
				previewPanel.setImage(materialRenderer.renderScene(matScene));
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		previewPanel.updatePreview();

	}

	public void updatePreviewImage(boolean resize)
	{
		matScene.updateScene(mySphere);
		try
		{
			try
			{
				previewPanel.setImage(materialRenderer.renderScene(matScene));
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		previewPanel.updatePreview(resize);

	}

	public void setupPanels()
	{
		ActionListener up = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateSphere();
				matScene.updateScene(mySphere);
				updatePreviewImage();
			}
		};

		ActionListener box = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				{

					updateSphere();
					matScene.setBox();
					matScene.updateScene(mySphere);
					updatePreviewImage();
				}
			}
		};

		ActionListener sphere = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				{
					updateSphere();
					matScene.setSphere();
					matScene.updateScene(mySphere);
					updatePreviewImage();
				}
			}
		};

		infoPanel = new SphereInfoPanel(mySphere);
		infoPanel.mySpherePanel = this;
		infoPanel.addFieldListeners(up);
		// infoPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));
		matPanel = new MaterialPanel(mySphere.material);
		matPanel.addFieldListeners(up);

		rightPanel = matPanel;
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(infoPanel);

		previewPanel = new PreviewPanel(preview);
		lowerLeftPanel = new JPanel();
		lowerLeftPanel.setLayout(new BorderLayout());
		lowerLeftPanel.add(previewPanel);
		leftPanel.add(lowerLeftPanel);
		JPanel spacerPanel = new JPanel();
		spacerPanel.setPreferredSize(new Dimension(rightPanel.WIDTH, rightPanel.HEIGHT/2));

		rightPanel.add(new JPanel()); // spacer
		previewPanel.setButtonListeners(box, sphere);

		add(leftPanel);
		add(rightPanel);
	}

	public void updateSphere()
	{
		infoPanel.updateSphereInfo();
		matPanel.updateMaterialInfo();
		if (myFrame != null)
		{
			myFrame.pack();
		}

		System.out.println(mySphere.toString());
	}
}
