package raytracer;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A simple frame renderer for BufferedImages as output from the raytracer.
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist

 */
public class RenderViewer extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public RenderViewer() {
		super("Basic Graphical Rendering");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public RenderViewer(BufferedImage bi) {
		this();
		Dimension d = new Dimension(bi.getWidth(), bi.getHeight()); 
		setSize(d);
		setResizable(false);
		ImageIcon image = new ImageIcon(bi);
		add(new JLabel(image));
		setVisible(true);
	}

}
