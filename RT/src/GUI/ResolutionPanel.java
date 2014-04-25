/**
 *
 */

package GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import util.RenderSettingException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class ResolutionPanel extends JPanel
{

	JPanel mainPanel;
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

	public ResolutionPanel(int width, int height)
	{
		super();
		setup("Resolution: ", width, height);
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

	}

	public void addFieldListeners(ActionListener go)
	{
		xField.addActionListener(go);
		yField.addActionListener(go);
	}

	public void setValue(int width, int height)
	{
		xField.setText("" + width);
		yField.setText("" + height);
	}

	public int getWidth()
	{
		String x = xField.getText();
		return Integer.parseInt(x);
	}

	public int getHeight()
	{
		String y = yField.getText();
		return Integer.parseInt(y);
	}

	public void setup(String name, int width, int height)
	{
		nameLabel = new JLabel(name);
		nameLabelPanel = new JPanel();
		nameLabelPanel.add(nameLabel);
		namePanel = new JPanel();
		namePanel.add(nameLabelPanel);

		positionSubPanel = new JPanel(new GridLayout(2,1));

		xField = new JTextField("" + width, 4);
		yField = new JTextField("" + height, 4);

		xLabelPanel = new JPanel();
		yLabelPanel = new JPanel();

		xLabel = new JLabel("  Width: ");
		//xLabel.setLabelFor(xField);
		yLabel = new JLabel("Height: ");
		//yLabel.setLabelFor(yField);

		xLabelPanel.add(xLabel);
		yLabelPanel.add(yLabel);

		positionSubPanel.add(xLabel);
		positionSubPanel.add(xField);
		positionSubPanel.add(new JPanel()); // spacer
		positionSubPanel.add(yLabel);
		positionSubPanel.add(yField);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		mainPanel.add(namePanel);
		mainPanel.add(positionSubPanel);
		
		add(mainPanel);
	}
}
