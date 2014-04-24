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
import javax.swing.JTextField;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class CheckBoxPanel extends JPanel
{
	JLabel paramLabel;
	JPanel paramLabelPanel;
	JCheckBox paramBox;
	
	public static void main(String[] args){
		JFrame f = new JFrame();
		JPanel p = new JPanel();
		p.add(new CheckBoxPanel("Stuff", true));
		p.add(new CheckBoxPanel("Things", false));
		f.add(p);
		f.pack();
		f.setVisible(true);

	}

	public CheckBoxPanel(String paramName, boolean initialValue)
	{
		super(new FlowLayout(FlowLayout.LEFT));
		setup(paramName, initialValue);
	}

	public void addFieldListener(ActionListener go)
	{
		paramBox.addActionListener(go);
	}

	public boolean getValue()
	{
		return paramBox.isSelected();
	}
	
	public void setSelected(boolean b){
		paramBox.setSelected(b);
	}

	public void setup(String name, boolean initial)
	{
		paramBox = new JCheckBox();
		paramBox.setSelected(initial);
		paramLabel = new JLabel(name);
		paramLabelPanel = new JPanel();
		paramLabelPanel.add(paramLabel);

		add(paramLabelPanel);
		add(paramBox);
	}
}
