/**
 *
 */

package GUI;

import geometry.Transformation;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.Sphere;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class SphereInfoPanel extends JPanel
{
	Sphere mySphere;
	public SpherePanel mySpherePanel;

	ParameterPanel namePanel;
	ParameterPanel zMinPanel;
	ParameterPanel zMaxPanel;
	ParameterPanel thetaMinPanel;
	ParameterPanel thetaMaxPanel;
	ParameterPanel phiMaxPanel;
	JSlider rotationSlider; 

	public SphereInfoPanel(Sphere targetSphere)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		mySphere = targetSphere;
		setupPanels();
		setVisible(true);
	}

	public void addFieldListeners(ActionListener go)
	{
		namePanel.addFieldListener(go);
		zMinPanel.addFieldListener(go);
		zMaxPanel.addFieldListener(go);
		thetaMinPanel.addFieldListener(go);
		thetaMaxPanel.addFieldListener(go);
		phiMaxPanel.addFieldListener(go);
	}

	public void setupPanels()
	{

		removeAll();
		namePanel = new ParameterPanel("Name: ", mySphere.getName(), 20);
		
		zMinPanel = new ParameterPanel("zMin: ", ""+ mySphere.getzMin(), 5);
		zMaxPanel = new ParameterPanel("zMax: ", ""+ mySphere.getzMax(), 5);
		thetaMinPanel = new ParameterPanel("thetaMin: ", ""+ mySphere.getThetaMin(), 5);
		thetaMaxPanel = new ParameterPanel("thetaMax: ", ""+ mySphere.getThetaMax(), 5);
		phiMaxPanel = new ParameterPanel("phiMax: " , ""+Math.toDegrees(mySphere.phiMax), 5);
		
		add(namePanel);
		add(zMinPanel);
		add(zMaxPanel);
		add(thetaMinPanel);
		add(thetaMaxPanel);
		add(phiMaxPanel);

		
	}

	public void updateSphereInfo()
	{
		try
		{
			String newName = namePanel.getValue();
			
			mySphere.setName(newName);
			mySphere.setzMinMax( Float.parseFloat(zMinPanel.getValue()), 
								 Float.parseFloat(zMaxPanel.getValue())  );
			mySphere.phiMax = (float)Math.toRadians(Float.parseFloat(phiMaxPanel.getValue()));
			
		}
		catch (Exception e)
		{
			JFrame errorFrame = new JFrame("Object update error!");
			JPanel errorPanel = new JPanel(new GridLayout(2, 0));
			JLabel errorLabel = new JLabel(
					"Unable to update object parameters -- check for weird data!");
			JLabel errorLabel2 = new JLabel(e.toString());
			errorPanel.add(errorLabel);
			errorPanel.add(errorLabel2);
			errorFrame.add(errorPanel);
			errorFrame.pack();
			errorFrame.setVisible(true);
		}
	}
}
