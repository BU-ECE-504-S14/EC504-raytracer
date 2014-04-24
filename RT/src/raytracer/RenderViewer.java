package raytracer;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
		JPanel j = new JPanel();
		ImageIcon i = new ImageIcon();
		i.setImage(bi);
		JLabel l = new JLabel(i);
		j.add(l);
		add(j);
		setVisible(true);
		pack();
	}

}
