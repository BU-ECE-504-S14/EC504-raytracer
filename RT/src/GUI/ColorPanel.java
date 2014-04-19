/**
 *
 */

package GUI;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.vecmath.Vector3d;

/**
@author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class ColorPanel extends JPanel
{
	JPanel nameLabelPanel;
	JLabel nameLabel;
	JPanel namePanel;

	JPanel colorSubPanel;

	JPanel redLabelPanel;
	JPanel greenLabelPanel;
	JPanel blueLabelPanel;
	JLabel redLabel;
	JLabel greenLabel;
	JLabel blueLabel;
	public JTextField redField;
	public JTextField greenField;
	public JTextField blueField;

	public ColorPanel(String name, Vector3d color)
	{
		super(new GridLayout(2, 1));
		setupPanels(name, color);
	}

	public void addFieldListeners(ActionListener go)
	{
		redField.addActionListener(go);
		greenField.addActionListener(go);
		blueField.addActionListener(go);
	}

	public Vector3d getColor()
	{
		double red = Double.parseDouble(redField.getText());
		double green = Double.parseDouble(greenField.getText());
		double blue = Double.parseDouble(blueField.getText());
		return new Vector3d(red, green, blue);
	}

	public void setupPanels(String name, Vector3d color)
	{
		colorSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		nameLabel = new JLabel(name);
		nameLabelPanel = new JPanel();
		nameLabelPanel.add(nameLabel);
		namePanel = new JPanel();
		namePanel.add(nameLabelPanel);

		redField = new JTextField("" + color.getX(), 4);
		greenField = new JTextField("" + color.getY(), 4);
		blueField = new JTextField("" + color.getZ(), 4);

		redLabelPanel = new JPanel();
		greenLabelPanel = new JPanel();
		blueLabelPanel = new JPanel();

		redLabel = new JLabel("  Red: ", 2);
		redLabel.setLabelFor(redField);
		greenLabel = new JLabel("Green: ", 2);
		greenLabel.setLabelFor(greenField);
		blueLabel = new JLabel("Blue: ", 2);
		blueLabel.setLabelFor(redField);

		redLabelPanel.add(redLabel);
		greenLabelPanel.add(greenLabel);
		blueLabelPanel.add(blueLabel);

		colorSubPanel.add(redLabel);
		colorSubPanel.add(redField);
		colorSubPanel.add(new JPanel()); // spacer
		colorSubPanel.add(greenLabel);
		colorSubPanel.add(greenField);
		colorSubPanel.add(new JPanel()); // spacer
		colorSubPanel.add(blueLabel);
		colorSubPanel.add(blueField);
		
		add(namePanel);
		add(colorSubPanel);
		
	}
}
