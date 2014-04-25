/**
 *
 */

package GUI;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class ParameterPanel extends JPanel
{
	JLabel paramLabel;
	JPanel paramLabelPanel;
	JTextField paramField;

	public ParameterPanel(String paramName, String initialValue, int initialSize)
	{
		super(new FlowLayout(FlowLayout.LEFT));
		setup(paramName, initialValue, initialSize);
	}

	public void addFieldListener(ActionListener go)
	{
		paramField.addActionListener(go);
	}
	
	public void setText(String s){
		paramField.setText(s);
	}

	public String getValue()
	{
		return paramField.getText();
	}

	public void setup(String name, String initial, int size)
	{
		paramField = new JTextField(initial, size);
		paramLabel = new JLabel(name);
		paramLabelPanel = new JPanel();
		paramLabelPanel.add(paramLabel);

		add(paramLabelPanel);
		add(paramField);
	}
}
