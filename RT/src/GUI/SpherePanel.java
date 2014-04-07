/**
 *
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.vecmath.Vector3d;

import objects.Material;
import objects.Sphere;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class SpherePanel extends JPanel
{
	Sphere mySphere;

	JPanel radiusPanel;
	JPanel mainPanel;
	JPanel positionPanel;
	// JPanel propertiesLabelPanel;
	// JPanel propertiesFieldPanel;
	JPanel xPanel;
	JPanel yPanel;
	JPanel zPanel;
	JPanel xLabelPanel;
	JPanel yLabelPanel;
	JPanel zLabelPanel;
	JLabel xLabel;
	JLabel yLabel;
	JLabel zLabel;
	JTextField xField;
	JTextField yField;
	JTextField zField;

	JTextField radiusField;
	JLabel radiusLabel;
	JPanel radiusLabelPanel;

	public static void main(String[] args)
	{
		System.out.println("Test!");
		Sphere demoSphere = new Sphere();
		demoSphere.radius = 20;
		demoSphere.position = new Vector3d(15, 10, 5);
		JFrame testFrame = new JFrame();
		SpherePanel testPanel = new SpherePanel(demoSphere);
		testFrame.add(testPanel);
		testFrame.setContentPane(testPanel);
		testFrame.pack();
		testFrame.setVisible(true);
	}

	public SpherePanel(Sphere targetSphere)
	{
		super(new GridLayout(2, 1));
		mySphere = targetSphere;
		setup();
		setVisible(true);
	}

	public void setup()
	{
		positionPanel = new JPanel(new FlowLayout());
		// propertiesLabelPanel = new JPanel(new GridLayout(4, 1));
		// propertiesFieldPanel = new JPanel(new GridLayout(4, 1));

		ActionListener up = new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				updateSphere();
			}
		};

		xField = new JTextField("" + mySphere.position.getX(), 5);
		xField.addActionListener(up);
		yField = new JTextField("" + mySphere.position.getY(), 5);
		yField.addActionListener(up);
		zField = new JTextField("" + mySphere.position.getZ(), 5);
		zField.addActionListener(up);
		radiusField = new JTextField("" + mySphere.radius, 5);
		radiusField.addActionListener(up);

		xLabelPanel = new JPanel();
		yLabelPanel = new JPanel();
		zLabelPanel = new JPanel();
		radiusLabelPanel = new JPanel();

		xLabel = new JLabel("x: ", 2);
		xLabel.setLabelFor(xField);
		yLabel = new JLabel("y: ", 2);
		yLabel.setLabelFor(yField);
		zLabel = new JLabel("z: ", 2);
		zLabel.setLabelFor(xField);
		radiusLabel = new JLabel("radius: ");
		radiusLabel.setLabelFor(radiusField);

		radiusLabelPanel.add(radiusLabel);

		radiusPanel = new JPanel(new FlowLayout());
		radiusPanel.add(radiusLabelPanel);
		radiusPanel.add(radiusField);

		xLabelPanel.add(xLabel);
		yLabelPanel.add(yLabel);
		zLabelPanel.add(zLabel);
		positionPanel.add(xLabel);
		positionPanel.add(xField);
		positionPanel.add(new JPanel());
		positionPanel.add(yLabel);
		positionPanel.add(yField);
		positionPanel.add(new JPanel());
		positionPanel.add(zLabel);
		positionPanel.add(zField);
		JButton t = new JButton("Test");
		t.addActionListener(up);
		add(positionPanel);
		add(radiusPanel);
	}

	public void updateSphere()
	{
		try
		{
			double newX = Double.parseDouble(xField.getText());
			double newY = Double.parseDouble(yField.getText());
			double newZ = Double.parseDouble(zField.getText());
			double newRad = Double.parseDouble(radiusField.getText());
			mySphere.position = new Vector3d(newX, newY, newZ);
			mySphere.radius = newRad;
			//System.out.println(mySphere.toString());
		}
		catch (Exception e)
		{
			(new JFrame("Unable to update sphere, check for weird data!")).setVisible(true);
		}
	}
}
