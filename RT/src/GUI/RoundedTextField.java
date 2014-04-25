package GUI;

import javax.swing.JTextField;

public class RoundedTextField extends JTextField{
	
	public RoundedTextField(String s, int length){
		super(s, length);
		double num = Double.parseDouble(getText());
		int round = (int)(num * 1000);
		double fin = round / 1000.0;
		setText(fin + "");
	}
}
