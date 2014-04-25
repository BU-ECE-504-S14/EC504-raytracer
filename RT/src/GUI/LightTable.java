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
import scene.Light;
import scene.MaterialScene;
import scene.PointLight;
import scene.Scene;

public class LightTable extends JPanel
{

	JPanel nameLabelPanel;
	JLabel nameLabel;
	JPanel namePanel;

	private JList list;
	private JPanel mainPanel;
	private Scene s;

	public void openLightPanel(PointLight l)
	{
		JFrame f = new JFrame("Point Light Properties");

		JPanel p = new LightPanel(l);

		f.add(p);
		f.pack();
		f.setVisible(true);
	}
	
	public void updateLights(PointLight[] lights)
	{
		list.setListData(lights);
	}

	public PointLight getSelectedObject()
	{
		return (PointLight) list.getSelectedValue();
	}

	public LightTable(PointLight[] lights)
	{

		namePanel = new JPanel();
		nameLabel = new JLabel("Lights:");
		nameLabelPanel = new JPanel();
		nameLabelPanel.add(nameLabel);
		namePanel.add(nameLabelPanel);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(namePanel);

		list = new JList<PointLight>(lights);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt)
			{
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2)
				{
					PointLight l = (PointLight) list.getModel().getElementAt(
							list.locationToIndex(evt.getPoint()));
					openLightPanel(l);
				}
				else if (evt.getClickCount() == 3)
				{ // Triple-click
					PointLight l = (PointLight) list.getModel().getElementAt(
							list.locationToIndex(evt.getPoint()));
					openLightPanel(l);
				}
			}
		});

		mainPanel.add(list);
		add(mainPanel);

		this.setPreferredSize(new Dimension(300, 300));
	}

}
