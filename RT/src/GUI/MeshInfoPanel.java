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
import objects.TriangleMesh;
import util.SceneObjectException;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and Bryant Moquist
 */
public class MeshInfoPanel extends JPanel
{
	TriangleMesh myMesh;
	MeshPanel myMeshPanel;

	ParameterPanel namePanel;
	JPanel vertexPanel;
	JPanel facePanel;

	public MeshInfoPanel(TriangleMesh targetMesh)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		myMesh = targetMesh;
		setupPanels();
		setVisible(true);
	}

	public void addFieldListeners(ActionListener go)
	{
		namePanel.addFieldListener(go);
	}

	public void setupPanels()
	{

		removeAll();
		namePanel = new ParameterPanel("Name: ", myMesh.getName(), 20);

		JLabel vertexLabel = new JLabel("Vertices: " + myMesh.getPointCount());
		JLabel faceLabel = new JLabel("Faces: " + myMesh.getFaceCount());
		vertexPanel = new JPanel();
		vertexPanel.add(vertexLabel);
		facePanel = new JPanel();
		facePanel.add(faceLabel);

		add(namePanel);
		add(vertexPanel);
		add(facePanel);
		
	}

	public void updateSphereInfo()
	{
		try
		{
			String newName = namePanel.getValue();
			
			myMesh.setName(newName);
			
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
