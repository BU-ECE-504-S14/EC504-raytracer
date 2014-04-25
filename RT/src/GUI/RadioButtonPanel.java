/**
 *
 */

package GUI;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class RadioButtonPanel extends JPanel
{
	JLabel paramLabel;
	JPanel paramLabelPanel;
	JRadioButton paramButton;

	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		JPanel p = new JPanel();
		p.add(new RadioButtonPanel("Stuff", true));
		p.add(new RadioButtonPanel("Things", false));
		f.add(p);
		f.pack();
		f.setVisible(true);

	}

	public JRadioButton getButton()
	{
		return paramButton;
	}

	public RadioButtonPanel(String paramName, boolean initialValue)
	{
		super(new FlowLayout(FlowLayout.LEFT));
		setup(paramName, initialValue);
	}
	
	public void setButtonLabel(String s){
		paramLabel.setText(s);
	}

	public void addFieldListener(ActionListener go)
	{
		paramButton.addActionListener(go);
	}

	public boolean getValue()
	{
		return paramButton.isSelected();
	}

	public void setSelected(boolean b)
	{
		paramButton.setSelected(b);
	}

	public void setup(String name, boolean initial)
	{
		paramButton = new JRadioButton();
		paramButton.setSelected(initial);
		paramLabel = new JLabel(name);
		paramLabelPanel = new JPanel();
		paramLabelPanel.add(paramLabel);

		add(paramLabelPanel);
		add(paramButton);
	}
}
