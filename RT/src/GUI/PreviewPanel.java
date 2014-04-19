/**
JPanel redrawing implementation adapted from: http://stackoverflow.com/questions/15377842/how-to-refresh-reload-image-inside-jpanel
 *
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class PreviewPanel extends JPanel
{

	BufferedImage myImage = null;
	
	JLabel textLabel;
	JPanel text = new JPanel();
	JPanel prevImage = new JPanel();
	JLabel prevImageLabel = new JLabel();

	public PreviewPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		prevImage.add(prevImageLabel);
		textLabel = new JLabel("Preview: " );
		text.add(textLabel);
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		add(text);
		add(prevImage);
	}

	public void updatePreview(BufferedImage img)
	{
		myImage = img;
		Dimension textSize = new Dimension(img.getWidth(), 20);
		Dimension labelSize = new Dimension(img.getWidth(), img.getHeight());
		ImageIcon i = new ImageIcon(myImage);

		text.setPreferredSize(textSize);
		text.setMaximumSize(textSize);
		
		prevImage.setPreferredSize(labelSize);
		prevImageLabel.setIcon(i);
		prevImageLabel.repaint();
	}
}
