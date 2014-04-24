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
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class ResolutionPanel extends JPanel
{

	JPanel nameLabelPanel;
	JLabel nameLabel;
	JPanel namePanel;

	JPanel positionSubPanel;

	JPanel xLabelPanel;
	JPanel yLabelPanel;
	JLabel xLabel;
	JLabel yLabel;
	public JTextField xField;
	public JTextField yField;

	public ResolutionPanel(String name, Vector2d position)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setup(name, position);
	}

	public void addFieldListeners(ActionListener go)
	{
		xField.addActionListener(go);
		yField.addActionListener(go);
	}

	public Vector2d getResolution()
	{
		double x = Double.parseDouble(xField.getText());
		double y = Double.parseDouble(yField.getText());
		return new Vector2d(x, y);
	}

	public void setup(String name, Vector2d position)
	{
		nameLabel = new JLabel(name);
		nameLabelPanel = new JPanel();
		nameLabelPanel.add(nameLabel);
		namePanel = new JPanel();
		namePanel.add(nameLabelPanel);

		positionSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		xField = new JTextField("" + position.getX(), 4);
		yField = new JTextField("" + position.getY(), 4);

		xLabelPanel = new JPanel();
		yLabelPanel = new JPanel();

		xLabel = new JLabel("  Width: ", 6);
		xLabel.setLabelFor(xField);
		yLabel = new JLabel("Height: ", 6);
		yLabel.setLabelFor(yField);

		xLabelPanel.add(xLabel);
		yLabelPanel.add(yLabel);

		positionSubPanel.add(xLabel);
		positionSubPanel.add(xField);
		positionSubPanel.add(new JPanel()); // spacer
		positionSubPanel.add(yLabel);
		positionSubPanel.add(yField);
		positionSubPanel.add(new JPanel()); // spacer

		namePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
		namePanel.setMinimumSize(new Dimension(Short.MAX_VALUE, 25));

		add(namePanel);
		add(positionSubPanel);
	}
}
