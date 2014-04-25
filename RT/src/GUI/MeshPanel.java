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
import objects.TriangleMesh;
import raytracer.Renderer;
import scene.MaterialScene;
import scene.Scene;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class MeshPanel extends JPanel
{
	TriangleMesh myMesh;
	Renderer materialRenderer = new Renderer();
	JPanel leftPanel;
	JPanel rightPanel;

	MeshInfoPanel infoPanel;
	MaterialPanel matPanel;

	JButton updateButton;

	PreviewPanel previewPanel;

	JPanel lowerLeftPanel;
	MaterialScene matScene;

	BufferedImage preview = null;

	static JFrame myFrame = null;

	public MeshPanel(TriangleMesh targetMesh)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		myMesh = targetMesh;
		matScene = new MaterialScene(myMesh);
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
		*/
	}

	public void updatePreviewImage()
	{
		matScene.updateScene(myMesh);
		try
		{
			try
			{
				preview = materialRenderer.renderScene(matScene);
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

		previewPanel.setImage(preview);
		previewPanel.updatePreview();

	}

	public void updatePreviewImage(boolean resize)
	{
		matScene.updateScene(myMesh);
		try
		{
			try
			{
				preview = materialRenderer.renderScene(matScene);
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

		previewPanel.setImage(preview);
		previewPanel.updatePreview(resize);

	}

	public void setupPanels()
	{
		ActionListener up = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateMesh();
			}
		};

		ActionListener box = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				{

					matScene.setBox();
					updatePreviewImage(false);
				}
			}
		};

		ActionListener sphere = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				{
					matScene.setSphere();
					updatePreviewImage(false);
				}
			}
		};

		infoPanel = new MeshInfoPanel(myMesh);
		infoPanel.myMeshPanel = this;
		infoPanel.addFieldListeners(up);
		// infoPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));
		matPanel = new MaterialPanel(myMesh.material);
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
		spacerPanel.setPreferredSize(new Dimension(rightPanel.WIDTH, rightPanel.HEIGHT / 2));

		rightPanel.add(new JPanel()); // spacer
		previewPanel.setButtonListeners(box, sphere);
		
		updateButton = new JButton("Update");
		updateButton.addActionListener(up);
		rightPanel.add(updateButton);

		add(leftPanel);
		add(rightPanel);
	}

	public void updateMesh()
	{
		infoPanel.updateSphereInfo();
		matPanel.updateMaterialInfo();
		updatePreviewImage(false);
		if (myFrame != null)
		{
			myFrame.pack();
		}

		System.out.println(myMesh.toString());
	}
}
