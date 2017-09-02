package customcomponent;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JButton;

import customuiandrender.ButtonCustomUI;
import defaults.InterfaceTextDefaults;

public class CustomButton extends JButton {
	private static final long serialVersionUID = 6080693326637982978L;

	public CustomButton(String textDefaultsKey, Color color, int width, int height) {
		super(InterfaceTextDefaults.getInstance().getDefault(textDefaultsKey));
		setUI(new ButtonCustomUI(color));
		init(width, height);
	}
	
	public CustomButton(String textDefaultsKey, Color color, Color borderColor, int width, int height) {
		super(InterfaceTextDefaults.getInstance().getDefault(textDefaultsKey));
		setUI(new ButtonCustomUI(color, borderColor));
		init(width, height);
	}
	
	private void init(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		setBorder(null);
		setOpaque(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
}
