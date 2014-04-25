/**
 *
 */

package GUI;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Rana Alrabeh, Tolga Bolukbasi, Aaron Heuckroth, David Klaus, and
 *         Bryant Moquist
 */
public class ParameterPanel extends JPanel {
	JLabel paramLabel;
	JPanel paramLabelPanel;
	JTextField paramField;

	public ParameterPanel(String paramName, String initialValue, int initialSize) {
		super(new FlowLayout(FlowLayout.LEFT));
		setup(paramName, initialValue, initialSize);
	}

	public void addFieldListener(ActionListener go) {
		paramField.addActionListener(go);
	}

	public void setText(String s) {
		paramField.setText(s);
	}

	public String getValue() {
		return paramField.getText();
	}

	public void setup(String name, String input, int size) {

		String initial = "";
		if (input != null) {
			initial = input;
		}
		
		boolean isDouble = false;
		boolean isInt = false;
		double initD = 0;
		int initI = 0;
		try {
			initI = Integer.parseInt(initial);
		} catch (NumberFormatException e) {
			isInt = false;
		}

		try {
			initD = Double.parseDouble(initial);
		} catch (NumberFormatException e) {
			isDouble = false;
		}

		if (isDouble) {
			paramField = new RoundedTextField(initial, size);
		} else {
			paramField = new JTextField(initial, size);
		}

		paramLabel = new JLabel(name);
		paramLabelPanel = new JPanel();
		paramLabelPanel.add(paramLabel);

		add(paramLabelPanel);
		add(paramField);
	}
}
