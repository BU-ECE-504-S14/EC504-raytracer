/**
 *
 */

package GUI;

import geometry.Pt;
import geometry.Vec;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import objects.SceneObject;
import objects.Sphere;
import raytracer.Camera;
import raytracer.Renderer;
import raytracer.Util;
import scene.Light;
import scene.MaterialScene;
import scene.PointLight;
import scene.Scene;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class CameraPanel extends JPanel
{
	public Camera myCamera;
	JPanel namePanel;
	JLabel nameLabel;
	JPanel nameLabelPanel;

	JButton update;
	JButton target;

	JPanel axisPanel;
	JPanel axisInfo;
	JLabel axisInfoLabel;
	JLabel axisParamLabel;
	JPanel axisParamLabelPanel;
	JPanel axisInfoLabelPanel;

	PositionPanel posPanel;
	PositionPanel tarPanel;
	PositionPanel upPanel;

	ParameterPanel fovPanel;

	JPanel buttonPanel;

	JButton updateButton;
	JButton targetButton;

	public CameraPanel()
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		myCamera = null;
		this.setBorder(new LineBorder(Color.BLACK));
	}

	public CameraPanel(Camera targetCamera)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		myCamera = targetCamera;

		setupPanels();
	}

	public void setCamera(Camera c)
	{
		myCamera = c;
		setupPanels();
	}

	public void setupPanels()
	{
		this.removeAll();
		ActionListener up = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateCamera();
			}
		};

		namePanel = new JPanel();
		nameLabel = new JLabel("Camera (Look-At):");
		nameLabelPanel = new JPanel();
		nameLabelPanel.add(nameLabel);
		namePanel.add(nameLabelPanel);
		this.add(namePanel);

		axisInfo = new JPanel();

		axisPanel = new JPanel();
		axisInfoLabel = new JLabel("AxisAngle4d: ");
		axisParamLabel = new JLabel(axisToString(myCamera.orientation));
		axisInfoLabelPanel = new JPanel();
		axisParamLabelPanel = new JPanel();
		axisInfoLabelPanel.add(axisInfoLabel);
		axisParamLabelPanel.add(axisParamLabel);
		axisPanel.add(axisInfoLabelPanel, BorderLayout.NORTH);
		axisPanel.add(axisParamLabelPanel, BorderLayout.SOUTH);

		this.add(axisPanel);

		posPanel = new PositionPanel("Camera Position: ", myCamera.getPosition());
		this.add(posPanel);

		tarPanel = new PositionPanel("Camera Target: ", new Vector3d(0, 0, 0));
		this.add(tarPanel);
		upPanel = new PositionPanel("Camera Up Vector: ", new Vector3d(0, 1, 0));

		this.add(upPanel);
		fovPanel = new ParameterPanel("Camera FOV: ", "" + Math.toDegrees(myCamera.fieldOfView), 4);
		this.add(fovPanel);

		buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		updateButton = new JButton("Update Camera");
		updateButton.addActionListener(up);
		buttonPanel.add(updateButton);

		targetButton = new JButton("Target Selected Object");
		buttonPanel.add(targetButton);

		this.add(buttonPanel);
	}

	public static String axisToString(AxisAngle4d axis)
	{
		double axisX = Util.chopDouble(axis.x);
		double axisY = Util.chopDouble(axis.y);

		double axisZ = Util.chopDouble(axis.z);

		double axisRot = Util.chopDouble(axis.angle);

		return axisX + ", " + axisY + ", " + axisZ + ", " + axisRot;

	}

	public void setTargetListener(ActionListener a)
	{
		targetButton.addActionListener(a);
	}

	public void setTarget(SceneObject o)
	{
		Vector3d p = o.getTransform().getTranslation();
		tarPanel.xField.setText("" + p.x);
		tarPanel.yField.setText("" + p.y);
		tarPanel.zField.setText("" + p.z);

		revalidate();
	}

	public void updateCameraInfo() throws SceneObjectException
	{
		double fov = Double.parseDouble(fovPanel.getValue());
		if (fov < 1)
		{
			fov = 1;
		}
		else if (fov > 360)
		{
			fov = 360;
		}
		Vector3d pos = posPanel.getPosition();

		Vector3d target = tarPanel.getPosition();

		Vector3d up = upPanel.getPosition();

		myCamera.fieldOfView = Math.toRadians(fov);
		myCamera.setPosition(new Pt(pos));
		myCamera.lookAt(new Pt(target), new Vec(up));
		axisParamLabel.setText(axisToString(myCamera.orientation));
	}

	public void updateCamera()
	{
		try
		{
			updateCameraInfo();
		}
		catch (SceneObjectException e)
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
		System.out.println(myCamera.toString());
	}
}
