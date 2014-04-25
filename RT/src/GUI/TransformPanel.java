/**
 *
 */

package GUI;

import geometry.Transformation;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.SceneObject;
import objects.Sphere;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class TransformPanel extends JPanel
{
	SceneObject myObject;
	public SpherePanel myObjectPanel;

	ParameterPanel namePanel;
	PositionPanel transformScale;
	PositionPanel transformPosition;
	PositionPanel transformRotationAxis;
	ParameterPanel transformRotationAngle;
	JSlider rotationSlider;

	JButton transformButton;

	public TransformPanel(SceneObject targetObject)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		myObject = targetObject;
		setupPanels();
		setVisible(true);
	}

	public void addFieldListeners(ActionListener go)
	{
		namePanel.addFieldListener(go);
		transformPosition.addFieldListeners(go);
		transformRotationAxis.addFieldListeners(go);
		transformRotationAngle.addFieldListener(go);
		transformScale.addFieldListeners(go);
	}

	public void setupPanels()
	{

		removeAll();
		namePanel = new ParameterPanel("Name: ", myObject.getName(), 20);

		transformPosition = new PositionPanel("Transform position: ", myObject.getTransform()
				.getTranslation());

		Vector3d transformationAxis = new Vector3d(myObject.getTransform().getRotation().x,
				myObject.getTransform().getRotation().y, myObject.getTransform().getRotation().z);
		Vector3d transformationScale = new Vector3d(myObject.getTransform().getScale());

		transformScale = new PositionPanel("Transform scale: ", transformationScale);
		transformRotationAxis = new PositionPanel("Transform rotation axis: ", transformationAxis);
		transformRotationAngle = new ParameterPanel("Transform rotation angle: ", ""
				+ Math.toDegrees(myObject.getTransform().getRotation().angle), 5);

		transformButton = new JButton("Transform");
		transformButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				updateTransformInfo();
				System.out.println(myObject.getTransform());
			}

		});

		add(namePanel);
		add(transformPosition);
		add(transformScale);
		add(transformRotationAxis);
		add(transformRotationAngle);

		rotationSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 360, 0);

		ChangeListener c = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();
				transformRotationAngle.paramField.setText("" + source.getValue());
			}
		};

		rotationSlider.addChangeListener(c);
		add(rotationSlider);
		add(transformButton);

	}

	public void newObject(SceneObject o)
	{
		myObject = o;
		setupPanels();
	}

	public void updateTransformInfo()
	{
		try
		{
			String newName = namePanel.getValue();

			Transformation t = myObject.getTransform();
			t.setScale(transformScale.getPosition());
			t.setRotation(new AxisAngle4d(transformRotationAxis.getPosition(), Math
					.toRadians(Double.parseDouble(transformRotationAngle.getValue()))));
			t.setTranslation(transformPosition.getPosition());

			myObject.setTransform(t);

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