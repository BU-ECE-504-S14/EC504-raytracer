/**
 *
 */

package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.vecmath.Vector3d;

import objects.Material;
import objects.SceneObject;
import objects.Sphere;
import util.ColorFormatException;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class MaterialPanel extends JPanel
{
	Material myMaterial;

	ColorPanel diffusePanel;
	ColorPanel specularPanel;
	ParameterPanel diffuseIndexPanel;
	ParameterPanel specularIndexPanel;
	ParameterPanel ambientPanel;
	ParameterPanel transparencyPanel;
	ParameterPanel refractionPanel;
	ParameterPanel reflectionPanel;
	ParameterPanel shininessPanel;

	public static void main(String[] args)
	{
		Sphere demoSphere = new Sphere();
		demoSphere.radius = 20;
		demoSphere.position = new Vector3d(15, 10, 5);
		JFrame testFrame = new JFrame();
		MaterialPanel testPanel = new MaterialPanel(demoSphere.material);
		testFrame.add(testPanel);
		testFrame.setContentPane(testPanel);
		testFrame.pack();
		testFrame.setVisible(true);
	}

	public MaterialPanel(Material targetMaterial)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		myMaterial = targetMaterial;
		setupPanels();
		setVisible(true);
	}

	public void addFieldListeners(ActionListener go)
	{
		diffusePanel.addFieldListeners(go);
		specularPanel.addFieldListeners(go);
		diffuseIndexPanel.addFieldListener(go);
		specularIndexPanel.addFieldListener(go);
		ambientPanel.addFieldListener(go);
		transparencyPanel.addFieldListener(go);
		reflectionPanel.addFieldListener(go);
		refractionPanel.addFieldListener(go);
		shininessPanel.addFieldListener(go);
	}

	public void setupPanels()
	{
		diffusePanel = new ColorPanel("Diffuse Color: ", myMaterial.diffuseColor);
		specularPanel = new ColorPanel("Specular Color: ", myMaterial.specularColor);
		diffuseIndexPanel = new ParameterPanel("Diffuse index: ", "" + myMaterial.diffuseIndex, 4);
		specularIndexPanel = new ParameterPanel("Specular index: ", "" + myMaterial.specularIndex,
				4);
		ambientPanel = new ParameterPanel("Ambient intensity: ", "" + myMaterial.ambientIntensity,
				4);
		transparencyPanel = new ParameterPanel("Transparency: ", "" + myMaterial.transparency, 4);
		reflectionPanel = new ParameterPanel("Reflection: ", "" + myMaterial.reflectionIndex, 4);
		refractionPanel = new ParameterPanel("Refraction: ", "" + myMaterial.refractionIndex, 4);
		shininessPanel = new ParameterPanel("Shininess: ", "" + myMaterial.shininess, 4);

		add(diffusePanel);
		add(specularPanel);
		add(diffuseIndexPanel);
		add(specularIndexPanel);
		add(ambientPanel);
		add(transparencyPanel);
		add(reflectionPanel);
		add(refractionPanel);
		add(shininessPanel);
	}

	public boolean isValidColor(Vector3d col)
	{
		double x = col.getX();
		double y = col.getY();
		double z = col.getZ();

		if (x > 1 || x < 0 || y > 1 || y < 0 || z > 1 || z < 0)
		{
			return false;
		}
		else
			return true;
	}

	public void updateMaterialInfo()
	{
		try
		{
			Vector3d diffuseColor = diffusePanel.getColor();
			Vector3d specularColor = specularPanel.getColor();

			if (!isValidColor(diffuseColor) || !isValidColor(specularColor)){
				throw new ColorFormatException("Entered invalid color value!");
			}

			double diffuseIndex = Double.parseDouble(diffuseIndexPanel.getValue());
			double specularIndex = Double.parseDouble(specularIndexPanel.getValue());
			double ambientIntensity = Double.parseDouble(ambientPanel.getValue());
			double transparency = Double.parseDouble(transparencyPanel.getValue());
			double reflection = Double.parseDouble(reflectionPanel.getValue());
			double refraction = Double.parseDouble(refractionPanel.getValue());
			double shininess = Double.parseDouble(shininessPanel.getValue());

			myMaterial.diffuseColor = diffuseColor;
			myMaterial.specularColor = specularColor;
			myMaterial.diffuseIndex = diffuseIndex;
			myMaterial.specularIndex = specularIndex;
			myMaterial.ambientIntensity = ambientIntensity;
			myMaterial.transparency = transparency;
			myMaterial.reflectionIndex = reflection;
			myMaterial.refractionIndex = refraction;
			myMaterial.shininess = shininess;
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
