/**
 *
 */

package GUI;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import util.RenderSettingException;

/**
@author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist */
public class ThreeRadioPanel extends JPanel
{
	private String name = "Three Radio Panel";

	JPanel nameLabelPanel;
	JLabel nameLabel;
	JPanel namePanel;

	JPanel buttonPanel;

	private RadioButtonPanel option1;
	private RadioButtonPanel option2;
	private RadioButtonPanel option3;
	private int one = 1;
	private int two = 2;
	private int three = 3;
	private String n1 = "One: ";
	private String n2 = "Two: ";
	private String n3 = "Three: ";
	private ButtonGroup group;

	public static void main(String[] args)
	{
		ThreeRadioPanel p = new ThreeRadioPanel("Shadows: ", 1);
		JFrame f = new JFrame();
		f.add(p);
		f.pack();
		f.setVisible(true);
	}

	public void setValues(int o, int tw, int tr)
	{
		one = o;
		two = tw;
		three = tr;
	}

	public ThreeRadioPanel(String s)
	{
		this(s, 0);
	}

	public ThreeRadioPanel(String s, int shadowSetting)
	{
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		name = s;
		setupPanels(shadowSetting);
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

	}

	public void setValue(int i)
	{
		try
		{
			updatePanels(i);
		}
		catch (RenderSettingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setNames(String s1, String s2, String s3)
	{
		n1 = s1;
		n2 = s2;
		n3 = s3;
		updateNames();
	}

	private void setupPanels(int setting)
	{
		nameLabelPanel = new JPanel();
		nameLabel = new JLabel(name);
		namePanel = new JPanel(new FlowLayout());
		nameLabelPanel.add(nameLabel);
		namePanel.add(nameLabelPanel);

		buttonPanel = new JPanel(new FlowLayout());

		group = new ButtonGroup();
		option1 = new RadioButtonPanel(n1, false);
		group.add(option1.getButton());
		option2 = new RadioButtonPanel(n2, false);
		group.add(option2.getButton());
		option3 = new RadioButtonPanel(n3, false);
		group.add(option3.getButton());

		buttonPanel.add(option1);
		buttonPanel.add(option2);
		buttonPanel.add(option3);

		add(namePanel);
		add(buttonPanel);

		try
		{
			updatePanels(setting);
		}
		catch (RenderSettingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateNames();
	}

	private void setOptionOne()
	{
		option1.setSelected(true);
		option2.setSelected(false);
		option3.setSelected(false);
	}

	private void setOptionTwo()
	{
		option1.setSelected(false);
		option2.setSelected(true);
		option3.setSelected(false);
	}

	private void setOptionThree()
	{
		option1.setSelected(false);
		option2.setSelected(false);
		option3.setSelected(true);
	}

	public int getValue()
	{
		if (option1.getValue())
		{
			return one;
		}
		else if (option2.getValue())
		{
			return two;
		}
		else
		{
			return three;
		}
	}

	public void updateNames()
	{
		option1.setButtonLabel(n1);
		option2.setButtonLabel(n2);
		option3.setButtonLabel(n3);
	}

	public void updatePanels(int setting) throws RenderSettingException
	{
		if (setting == one)
		{
			setOptionOne();
		}
		else if (setting == two)
		{
			setOptionTwo();
		}
		else if (setting == three)
		{
			setOptionThree();
		}

		else
		{
			throw new RenderSettingException("Radio button parameter outside of expected range: "
					+ setting + ", accepts: " + one + ", " + two + ", " + three);
		}
	}
}
