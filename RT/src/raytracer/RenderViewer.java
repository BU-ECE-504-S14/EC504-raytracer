package raytracer;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import GUI.PreviewPanel;

/**
 * A simple frame renderer for BufferedImages as output from the raytracer.
 * 
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class RenderViewer extends JFrame
{

	private static final long serialVersionUID = 1L;

	static boolean looping = true;

	public RenderViewer()
	{
		super("Basic Graphical Rendering");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public RenderViewer(BufferedImage bi)
	{
		this();
		add(new PreviewPanel(bi));
		setVisible(true);
		pack();
	}

}
