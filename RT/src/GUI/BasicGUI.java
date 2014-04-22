package GUI;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import raytracer.Renderer;
import scene.Scene;

/**
 * Example class for wrapping the basic_renderer demo with a GUI interface. Currently just
 * has a button that renders a basic example scene.
 * 
 * 
@author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class BasicGUI extends JFrame
{
	int startWidth = 200;
	int startHeight = 200;
	String[] labels = new String[] { "Width", "Height", "Antialiasing", "Shadows" };
	String[] descriptions = new String[] { "Width of the rendered image, in pixels.",
			"Height of the rendred image, in pixels.",
			"Antialiasing quality, in number of samples.", "Shadow quality, in number of samples." };
	String[] initials = new String[] { "400", "300", "1", "1" };
	int[] widths = new int[] { 4, 4, 2, 2 };

	// The panel where the labels will be written
	JPanel labelPanel;
	// The panel where text fields will be placed
	JPanel fieldPanel;
	// The panel where the rendered image will show up
	JPanel imagePanel;
	// The panel where the render button will show up
	JPanel buttonPanel;

	// The fields where text will be entered
	JTextField[] paramFields;

	JPanel GUIPanel;

	ImageIcon renderIcon = null;
	BufferedImage renderOutput = null;
	Renderer render = new Renderer();
	Scene targetScene = render.constructSampleScene();
	JPanel p = new JPanel();
	JButton go = new JButton("Make it go!");
	JLabel renderHolder = new JLabel();

	public BasicGUI()
	{
		super("Basic Raytracer Demo");

		// Set up Paramater Entry Panel
		JPanel parameterPanel = new JPanel(new BorderLayout());
		labelPanel = new JPanel(new GridLayout(labels.length, 1));
		fieldPanel = new JPanel(new GridLayout(labels.length, 1));
		imagePanel = new JPanel(new GridLayout(1, 1));
		buttonPanel = new JPanel(new GridLayout(1, 1));
		paramFields = new JTextField[labels.length];

		parameterPanel.add(labelPanel, BorderLayout.WEST);
		parameterPanel.add(fieldPanel, BorderLayout.EAST);
		parameterPanel.add(buttonPanel, BorderLayout.SOUTH);

		for (int ii = 0; ii < labels.length; ii++)
		{
			paramFields[ii] = new JTextField(initials[ii]);
			// Set the tooltip
			paramFields[ii].setToolTipText(descriptions[ii]);
			// Set the size of the
			paramFields[ii].setColumns(widths[ii]);

			JLabel lab = new JLabel(labels[ii], SwingConstants.RIGHT);
			lab.setLabelFor(paramFields[ii]);
			JPanel l = new JPanel(new FlowLayout(FlowLayout.LEFT));
			l.add(lab);
			labelPanel.add(l);
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			p.add(paramFields[ii]);
			fieldPanel.add(p);
		}
		paramFields[2].setEnabled(false);
		paramFields[3].setEnabled(false);
		

		JButton renderButton = new JButton("Render!");
		renderButton.addActionListener(act);
		buttonPanel.add(renderButton);

		setSize(startWidth, startHeight);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(parameterPanel, BorderLayout.WEST);
		getContentPane().add(imagePanel, BorderLayout.EAST);

		setSize(400, 200);
		setVisible(true);
	}

	public void updateRenderingParams()
	{
		Renderer.setOptionWidth(Integer.parseInt(paramFields[0].getText()));
		Renderer.setOptionHeight(Integer.parseInt(paramFields[1].getText()));
		Renderer.setOptionAntialiasing(Integer.parseInt(paramFields[2].getText()));
		Renderer.setOptionShadow(Integer.parseInt(paramFields[3].getText()));
	}

	public void renderScene()
	{
		try
		{
			renderOutput = Renderer.renderScene(targetScene);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		renderIcon = new ImageIcon(renderOutput);
		if (renderHolder.getIcon() == null)
		{
			renderHolder = new JLabel(renderIcon, SwingConstants.CENTER);
			imagePanel.add(renderHolder);
		}
		else
		{
			renderHolder.setIcon(renderIcon);
		}
		pack();
	}

	ActionListener act = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e)
		{
			updateRenderingParams();
			renderScene();
		}
	};

	public static void main(String[] args)
	{
		new BasicGUI();
	}

}
