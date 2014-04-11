/**
 *
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.Material;
import objects.Sphere;
import raytracer.Renderer;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class SphereInfoPanel extends JPanel
{
	Sphere mySphere;
	public SpherePanel mySpherePanel;

	ParameterPanel namePanel;
	ParameterPanel radiusPanel;
	ParameterPanel zMinPanel;
	ParameterPanel zMaxPanel;
	ParameterPanel thetaMinPanel;
	ParameterPanel thetaMaxPanel;
	ParameterPanel phiMaxPanel;
	PositionPanel posPanel;
	PositionPanel transformScale;
	PositionPanel transformPosition;
	PositionPanel transformRotationAxis;
	ParameterPanel transformRotationAngle;
	JSlider rotationSlider; 

	public static void main(String[] args)
	{
		Sphere demoSphere = new Sphere();
		demoSphere.radius = 20;
		demoSphere.position = new Vector3d(15, 10, 5);
		JFrame testFrame = new JFrame();
		SphereInfoPanel testPanel = new SphereInfoPanel(demoSphere);
		testFrame.add(testPanel);
		testFrame.setContentPane(testPanel);
		testFrame.pack();
		testFrame.setVisible(true);
	}

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
		radiusPanel.addFieldListener(go);
		posPanel.addFieldListeners(go);
		zMinPanel.addFieldListener(go);
		zMaxPanel.addFieldListener(go);
		thetaMinPanel.addFieldListener(go);
		thetaMaxPanel.addFieldListener(go);
		phiMaxPanel.addFieldListener(go);
		transformPosition.addFieldListeners(go);
		transformRotationAxis.addFieldListeners(go);
		transformRotationAngle.addFieldListener(go);
		transformScale.addFieldListeners(go);
	}

	public void setupPanels()
	{

		removeAll();
		namePanel = new ParameterPanel("Name: ", mySphere.getName(), 20);

		radiusPanel = new ParameterPanel("Radius: ", "" + mySphere.radius, 5);

		posPanel = new PositionPanel("Position: ", mySphere.position);
		
		zMinPanel = new ParameterPanel("zMin: ", ""+mySphere.zmin, 5);
		zMaxPanel = new ParameterPanel("zMax: ", ""+mySphere.zmax, 5);
		thetaMinPanel = new ParameterPanel("thetaMin: ", ""+mySphere.thetaMin, 5);
		thetaMaxPanel = new ParameterPanel("thetaMax: ", ""+mySphere.thetaMax, 5);
		phiMaxPanel = new ParameterPanel("phiMax: " , ""+Math.toDegrees(mySphere.phiMax), 5);
		
		transformPosition = new PositionPanel("Transform position: ", mySphere.trans.getTranslation());
		
		Vector3d transformationAxis = new Vector3d(mySphere.trans.getRotation().x, mySphere.trans.getRotation().y, mySphere.trans.getRotation().z);
		Vector3d transformationScale = new Vector3d(mySphere.trans.getScale());
		
		transformScale = new PositionPanel("Transform scale: " , transformationScale);
		transformRotationAxis = new PositionPanel("Transform rotation axis: ", transformationAxis);
		transformRotationAngle = new ParameterPanel("Transform rotation angle: ", ""+Math.toDegrees(mySphere.trans.getRotation().angle), 5);

		add(namePanel);
		add(posPanel);
		add(radiusPanel);
		add(zMinPanel);
		add(zMaxPanel);
		add(thetaMinPanel);
		add(thetaMaxPanel);
		add(phiMaxPanel);
		add(transformPosition);
		add(transformScale);
		add(transformRotationAxis);
		add(transformRotationAngle);
		
		JSlider rotationSlider = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
		
		ChangeListener c = new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				JSlider source = (JSlider)e.getSource();
				transformRotationAngle.paramField.setText(""+source.getValue());
				mySpherePanel.updateSphere();
			}
		};
		
		rotationSlider.addChangeListener(c);
		add(rotationSlider);
		
	}

	public void updateSphereInfo()
	{
		try
		{
			Vector3d newPos = posPanel.getPosition();
			float newRad = Float.parseFloat(radiusPanel.getValue());

			if (newRad <= 0)
			{
				throw new SceneObjectException("Cannot update sphere (" + mySphere.getName()
						+ "): Radius is less than or equal to zero!");
			}
			String newName = namePanel.getValue();

			mySphere.position = newPos;
			mySphere.radius = newRad;
			mySphere.setName(newName);
			mySphere.zmin = Float.parseFloat(zMinPanel.getValue());
			mySphere.zmax = Float.parseFloat(zMaxPanel.getValue());
			mySphere.thetaMin = Float.parseFloat(thetaMinPanel.getValue());
			mySphere.thetaMax = Float.parseFloat(thetaMaxPanel.getValue());
			mySphere.phiMax = (float)Math.toRadians(Float.parseFloat(phiMaxPanel.getValue()));
			mySphere.trans.setRotation(new AxisAngle4d(transformRotationAxis.getPosition(), Math.toRadians(Double.parseDouble(transformRotationAngle.getValue()))));
			mySphere.trans.setTranslation(transformPosition.getPosition());
			mySphere.trans.setScale(transformScale.getPosition());
			
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
