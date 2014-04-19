/**
 *
 */

package GUI;

import geometry.Transformation;

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
		Vector3d scale = new Vector3d(20,20,20);
		Vector3d position = new Vector3d(15,10,5);
		AxisAngle4d rotation = new AxisAngle4d(0,0,0,0);
		Transformation trans = new Transformation(scale, position, rotation);
		
		Sphere demoSphere = new Sphere(1f,-1f,360, trans);
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

		radiusPanel = new ParameterPanel("Radius: ", "" + mySphere.getRadius(), 5);

		posPanel = new PositionPanel("Position: ", mySphere.getPosition());
		
		zMinPanel = new ParameterPanel("zMin: ", ""+ mySphere.getzMin(), 5);
		zMaxPanel = new ParameterPanel("zMax: ", ""+ mySphere.getzMax(), 5);
		thetaMinPanel = new ParameterPanel("thetaMin: ", ""+ mySphere.getThetaMin(), 5);
		thetaMaxPanel = new ParameterPanel("thetaMax: ", ""+ mySphere.getThetaMax(), 5);
		phiMaxPanel = new ParameterPanel("phiMax: " , ""+Math.toDegrees(mySphere.phiMax), 5);
		
		transformPosition = new PositionPanel("Transform position: ", mySphere.getTransform().getTranslation());
		
		Vector3d transformationAxis = new Vector3d(mySphere.getRotation().x, mySphere.getRotation().y, mySphere.getRotation().z);
		Vector3d transformationScale = new Vector3d(mySphere.getScale());
		
		transformScale = new PositionPanel("Transform scale: " , transformationScale);
		transformRotationAxis = new PositionPanel("Transform rotation axis: ", transformationAxis);
		transformRotationAngle = new ParameterPanel("Transform rotation angle: ", ""+Math.toDegrees(mySphere.getRotation().angle), 5);

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
			
			mySphere.setName(newName);
			mySphere.setPosition(newPos);
			mySphere.setScaleRad(transformScale.getPosition());
			mySphere.setzMinMax( Float.parseFloat(zMinPanel.getValue()), 
								 Float.parseFloat(zMaxPanel.getValue())  );
			mySphere.phiMax = (float)Math.toRadians(Float.parseFloat(phiMaxPanel.getValue()));
			mySphere.setRotation(new AxisAngle4d(transformRotationAxis.getPosition(), Math.toRadians(Double.parseDouble(transformRotationAngle.getValue()))));
			
			
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
