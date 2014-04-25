/**
 *
 */

package GUI;

import javax.swing.JFrame;

import objects.Sphere;
import scene.MaterialScene;
import scene.Scene;

/**
 * @author BU CS673 - Clone Productions
 */
public class MainGUI extends JFrame
{

	public static void main(String[] args){
		MainGUI GUI = new MainGUI();
	}
	public MainGUI()
	{
		super();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Sphere s = new Sphere();
		Scene scn = new MaterialScene(s);
		ScenePanel sp = new ScenePanel(scn);

		add(sp);
		pack();
		setVisible(true);
	}
}
