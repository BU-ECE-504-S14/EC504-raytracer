/**
 *
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.Sphere;
import raytracer.Renderer;
import scene.Light;
import scene.MaterialScene;
import scene.PointLight;
import scene.Scene;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class LightPanel extends JPanel
{
	public PointLight myLight;
	JPanel namePanel;
	JLabel nameLabel;
	JPanel nameLabelPanel;

	PositionPanel posPanel;
	PositionPanel color;
	ParameterPanel radiosity;
	ParameterPanel softShadowOff;

	JButton updateButton;

	public static void main(String[] args)
	{
		PointLight demoLight = new PointLight();
		float radius = .5f;
		Vector3d position = new Vector3d(0, 0, 2);
		AxisAngle4d rotation = new AxisAngle4d(0, 0, 0, 0);

		JFrame testFrame = new JFrame("Scene Object Information: Sphere");
		// testFrame.setMinimumSize(new Dimension(650, 600));
		// testFrame.setPreferredSize(new Dimension(650, 600));
		testFrame.setResizable(false);
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		LightPanel testPanel = new LightPanel(demoLight);
		testFrame.add(testPanel);
		testFrame.pack();
		testFrame.setVisible(true);
	}

	public LightPanel(PointLight targetLight)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		myLight = targetLight;

		setupPanels();
	}

	public void setupPanels()
	{
		this.removeAll();
		ActionListener up = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateLight();
			}
		};

		namePanel = new JPanel();
		nameLabel = new JLabel("Point Light:");
		nameLabelPanel = new JPanel();
		nameLabelPanel.add(nameLabel);

		this.add(namePanel);

		posPanel = new PositionPanel("Position: ", myLight.getPosition());
		this.add(posPanel);
		color = new PositionPanel("Color: ", myLight.color);
		this.add(color);
		radiosity = new ParameterPanel("Radiosity: ", "" + myLight.getRadio(), 4);
		this.add(radiosity);
		softShadowOff = new ParameterPanel("Radiosity: ", "" + myLight.getSoftShadowOffset(), 4);
		this.add(softShadowOff);
		updateButton = new JButton("Update");
		updateButton.addActionListener(up);
		this.add(updateButton);
	}

	public void updateLightInfo() throws SceneObjectException
	{
		double rad = Double.parseDouble(radiosity.getValue());
		if (rad < 0)
		{
			rad = 0;
		}

		double soft = Double.parseDouble(softShadowOff.getValue());
		if (soft < 0)
		{
			soft = 0;
		}
		Vector3d pos = posPanel.getPosition();

		Vector3d col = color.getPosition();
		if (col.x < 0 || col.x > 1 || col.y < 0 || col.y > 1 || col.z < 0 || col.z > 1)
		{
			throw new SceneObjectException("Light color must be between 0 and 1!");
		}

		myLight.setRadio(rad);
		myLight.setColor(col);
		myLight.setSoftShadowOffset(Double.parseDouble(softShadowOff.getValue()));
		myLight.setPosition(posPanel.getPosition());
	}

	public void updateLight()
	{
		try
		{
			updateLightInfo();
		}
		catch (SceneObjectException e)
		{
			JFrame errorFrame = new JFrame("Object update error!");
			JPanel errorPanel = new JPanel(new GridLayout(2, 0));
			JLabel errorLabel = new JLabel(
					"Unable to update object parameters -- check for weird data!");
			JLabel errorLabel2 = new JLabel(e.toString());
			errorPanel.add(errorLabel);
			errorPanel.add(errorLabel2);
			errorFrame.add(errorPanel);
			errorFrame.pack();
			errorFrame.setVisible(true);

		}
		System.out.println(myLight.toString());
	}
}
