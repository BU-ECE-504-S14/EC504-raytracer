/**
 *
 */

package GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.vecmath.Vector3d;

/**
@author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class PositionPanel extends JPanel
{
	
	JPanel nameLabelPanel;
	JLabel nameLabel;
	JPanel namePanel;
	
	JPanel positionSubPanel;
	
	JPanel xLabelPanel;
	JPanel yLabelPanel;
	JPanel zLabelPanel;
	JLabel xLabel;
	JLabel yLabel;
	JLabel zLabel;
	public JTextField xField;
	public JTextField yField;
	public JTextField zField;

	public PositionPanel(String name, Vector3d position)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setup(name, position);
	}

	public void addFieldListeners(ActionListener go)
	{
		xField.addActionListener(go);
		yField.addActionListener(go);
		zField.addActionListener(go);
	}

	public Vector3d getPosition()
	{
		double x = Double.parseDouble(xField.getText());
		double y = Double.parseDouble(xField.getText());
		double z = Double.parseDouble(xField.getText());
		return new Vector3d(x, y, z);
	}

	public void setup(String name, Vector3d position)
	{
		nameLabel = new JLabel(name);
		nameLabelPanel = new JPanel();
		nameLabelPanel.add(nameLabel);
		namePanel = new JPanel();
		namePanel.add(nameLabelPanel);
		
		positionSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		xField = new JTextField("" + position.getX(), 4);
		yField = new JTextField("" + position.getY(), 4);
		zField = new JTextField("" + position.getZ(), 4);

		xLabelPanel = new JPanel();
		yLabelPanel = new JPanel();
		zLabelPanel = new JPanel();

		xLabel = new JLabel("  X: ", 2);
		xLabel.setLabelFor(xField);
		yLabel = new JLabel("Y: ", 2);
		yLabel.setLabelFor(yField);
		zLabel = new JLabel("Z: ", 2);
		zLabel.setLabelFor(xField);

		xLabelPanel.add(xLabel);
		yLabelPanel.add(yLabel);
		zLabelPanel.add(zLabel);

		positionSubPanel.add(xLabel);
		positionSubPanel.add(xField);
		positionSubPanel.add(new JPanel()); // spacer
		positionSubPanel.add(yLabel);
		positionSubPanel.add(yField);
		positionSubPanel.add(new JPanel()); // spacer
		positionSubPanel.add(zLabel);
		positionSubPanel.add(zField);
		
		namePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
		namePanel.setMinimumSize(new Dimension(Short.MAX_VALUE, 25));
		
		add(namePanel);
		add(positionSubPanel);
	}
}
