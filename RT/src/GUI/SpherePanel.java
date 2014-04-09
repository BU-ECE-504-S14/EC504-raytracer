/**
 *
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.vecmath.Vector3d;

import objects.Material;
import objects.Sphere;
import raytracer.Renderer;
import scene.PreviewScene;
import scene.Scene;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class SpherePanel extends JPanel
{
	Sphere mySphere;
	Renderer previewRenderer = new Renderer();
	JPanel leftPanel;
	JPanel rightPanel;

	SphereInfoPanel infoPanel;
	MaterialPanel matPanel;

	JButton updateButton;

	PreviewPanel previewPanel;
	
	
	JPanel lowerLeftPanel;

	BufferedImage preview;

	public static void main(String[] args)
	{
		Sphere demoSphere = new Sphere();
		demoSphere.radius = .5f;
		demoSphere.position = new Vector3d(0, 0, 2);
		JFrame testFrame = new JFrame("Scene Object Information: Sphere");
		testFrame.setMinimumSize(new Dimension(650, 510));
		testFrame.setPreferredSize(new Dimension(650, 510));
		testFrame.setResizable(true);
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		SpherePanel testPanel = new SpherePanel(demoSphere);
		testFrame.add(testPanel);
		testFrame.pack();
		testFrame.setVisible(true);
	}

	public SpherePanel(Sphere targetSphere)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		mySphere = targetSphere;
		setPreviewDefaults();

		setupPanels();
		setVisible(true);
	}

	private void setPreviewDefaults()
	{
		previewRenderer.setOptionAntialiasing(1);
		previewRenderer.setOptionShadow(1);
		previewRenderer.setOptionShadow(1);
		previewRenderer.setOptionWidth(300);
		previewRenderer.setOptionHeight(300);
	}

	public void updatePreviewImage()
	{
		try
		{
			Scene preScene = new PreviewScene(mySphere);
			Scene.writeSceneToFile(preScene, "sceneTest.scn");
			Scene preScene2 = Scene.readSceneFromFile("sceneTest.scn");
			preview = previewRenderer.renderScene(preScene2);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		previewPanel.updatePreview(preview);
		previewPanel.repaint();

	}

	public void setupPanels()
	{
		ActionListener up = new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				updateSphere();
			}
		};

		infoPanel = new SphereInfoPanel(mySphere);
		infoPanel.addFieldListeners(up);
		//infoPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));
		matPanel = new MaterialPanel(mySphere.material);
		matPanel.addFieldListeners(up);

		rightPanel = matPanel;
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(infoPanel);
		
		previewPanel = new PreviewPanel();
		updatePreviewImage();
		lowerLeftPanel = new JPanel();
		lowerLeftPanel.setLayout(new BorderLayout());
		lowerLeftPanel.add(previewPanel);
		leftPanel.add(lowerLeftPanel);

		add(leftPanel);
		add(rightPanel);
	}

	public void updateSphere()
	{
		infoPanel.updateSphereInfo();
		matPanel.updateMaterialInfo();
		updatePreviewImage();

		System.out.println(mySphere.toString());
	}
}
