package GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import objects.SceneObject;
import objects.Sphere;
import objects.TriangleMesh;
import raytracer.RenderViewer;
import raytracer.Renderer;
import scene.MaterialScene;
import scene.Scene;

public class ObjectTable extends JPanel
{

	JPanel nameLabelPanel;
	JLabel nameLabel;
	JPanel namePanel;

	private JList list;
	private JPanel mainPanel;
	private Scene s;

	public void openObjectPanel(SceneObject o)
	{
		JFrame f = new JFrame();

		JPanel p = null;
		if (o instanceof Sphere)
		{
			p = new SpherePanel((Sphere) o);
		}
		else if (o instanceof TriangleMesh)
		{
			p = new MeshPanel((TriangleMesh) o);
		}
		f.add(p);
		f.pack();
		f.setVisible(true);
	}

	public void updateObjects(SceneObject[] objects)
	{
		int index = list.getSelectedIndex();
		list.setListData(objects);
	}

	public SceneObject getSelectedObject()
	{
		return (SceneObject) list.getSelectedValue();
	}
	
	public void onMouseClick(MouseAdapter m){
		list.addMouseListener(m);
	}

	public ObjectTable(SceneObject[] objects)
	{

		namePanel = new JPanel();
		nameLabel = new JLabel("Scene Objects:");
		nameLabelPanel = new JPanel();
		nameLabelPanel.add(nameLabel);
		namePanel.add(nameLabelPanel);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(namePanel);

		list = new JList<SceneObject>(objects);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt)
			{
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2)
				{
					SceneObject o = (SceneObject) list.getModel().getElementAt(
							list.locationToIndex(evt.getPoint()));
					openObjectPanel(o);
				}
				else if (evt.getClickCount() == 3)
				{ // Triple-click
					SceneObject o = (SceneObject) list.getModel().getElementAt(
							list.locationToIndex(evt.getPoint()));
					openObjectPanel(o);

				}
			}
		});

		mainPanel.add(list);
		add(mainPanel);

		this.setPreferredSize(new Dimension(100, 300));
	}

}
