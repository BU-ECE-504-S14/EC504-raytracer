/**
 *
 */

package GUI;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;

import util.RenderSettingException;

/**
 * @author BU CS673 - Clone Productions
 */
public class ShadowSettingPanel extends JPanel
{
	private String name = "Shadow Settings";
	private RadioButtonPanel simple;
	private RadioButtonPanel soft;
	private RadioButtonPanel recursive;
	private ButtonGroup group;

	public static void main(String[] args)
	{
		ShadowSettingPanel p = new ShadowSettingPanel(0);
		JFrame f = new JFrame();
		f.add(p);
		f.pack();
		f.setVisible(true);
	}

	public ShadowSettingPanel()
	{
		this(0);
	}

	public ShadowSettingPanel(int shadowSetting)
	{
		super(new FlowLayout(FlowLayout.LEFT));
		setupPanels(shadowSetting);
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

	private void setupPanels(int setting)
	{
		group = new ButtonGroup();
		simple = new RadioButtonPanel(" Simple: ", false);
		group.add(simple.getButton());
		soft = new RadioButtonPanel("Soft: ", false);
		group.add(soft.getButton());
		recursive = new RadioButtonPanel("Recursive (pretty): ", false);
		group.add(recursive.getButton());

		add(simple);
		add(soft);
		add(recursive);

		try
		{
			updatePanels(setting);
		}
		catch (RenderSettingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setSimple()
	{
		simple.setSelected(true);
		soft.setSelected(false);
		recursive.setSelected(false);
	}

	private void setSoft()
	{
		simple.setSelected(false);
		soft.setSelected(true);
		recursive.setSelected(false);
	}

	private void setRecursive()
	{
		simple.setSelected(false);
		soft.setSelected(false);
		recursive.setSelected(true);
	}

	public int getSetting() throws RenderSettingException
	{
		if (simple.getValue())
		{
			return 0;
		}
		else if (soft.getValue())
		{
			return 1;
		}
		else if (recursive.getValue())
		{
			return 2;
		}
		else
		{
			throw new RenderSettingException("Cannot return valid shadow setting!");
		}
	}

	public void updatePanels(int setting) throws RenderSettingException
	{
		if (setting == 0)
		{
			setSimple();
		}
		else if (setting == 1)
		{
			setSoft();
		}
		else if (setting == 3)
		{
			setRecursive();
		}

		else
		{
			throw new RenderSettingException("Shadow parameter outside of expected range: "
					+ setting);
		}
	}

}
