package GUI;

import javax.swing.JFrame;

import scene.Scene;

public class RenderSettingsFrame extends JFrame {

	RenderSettingsPanel rsp;

	public RenderSettingsFrame(Scene s, String title){
		super(title);
		rsp = new RenderSettingsPanel(s);
		rsp.parentFrame = this;
		this.add(rsp);
		this.pack();
		this.setVisible(true);
	}
}
