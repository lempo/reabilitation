package customcomponent;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import reabilitation.Utils;

public class CustomLabel extends JLabel {
	private static final long serialVersionUID = 3450780289569006011L;

	public CustomLabel(String name, ImageLinkDefaults.Key imageKey, MouseListener mouseListener) {
		super();
		ImageIcon icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(imageKey));
		setIcon(icon);
		setName(name);
		addMouseListener(mouseListener);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	public CustomLabel(String text, int textSize, Color textColor, boolean bold, 
			ImageLinkDefaults.Key imageKey, int iconTextGap, MouseListener mouseListener) {
		super();
		ImageIcon icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(imageKey));
		setIcon(icon);
		setIconTextGap(iconTextGap);
		addMouseListener(mouseListener);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		String t;
		if (bold)
			t = "<html><div style='font: bold " + textSize + "pt Arial Narrow; color: rgb("
					+ textColor.getRed()
					+ ", "
					+ textColor.getGreen()
					+ ", "
					+ textColor.getBlue()
					+ ");'>"
				+ text + "</div></html>";
		else
			t = "<html><div style='font: " + textSize + "pt Arial Narrow; color: rgb("
					+ textColor.getRed()
					+ ", "
					+ textColor.getGreen()
					+ ", "
					+ textColor.getBlue()
					+ ");'>"
				+ text + "</div></html>";
		setText(t);
	}
	
	public CustomLabel(ImageLinkDefaults.Key imageKey) {
		super();
		ImageIcon icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(imageKey));
		setIcon(icon);
	}
	
	public CustomLabel(String imagePath) {
		super();
		ImageIcon icon = Utils.createImageIcon(imagePath);
		setIcon(icon);
	}
	
	public CustomLabel(String textDefaultsKey, int textSize, Color textColor, boolean bold) {
		super();
		String t;
		if (bold)
			t = "<html><div style='font: bold " + textSize + "pt Arial Narrow; color: rgb("
					+ textColor.getRed()
					+ ", "
					+ textColor.getGreen()
					+ ", "
					+ textColor.getBlue()
					+ ");'>"
				+ InterfaceTextDefaults.getInstance().getDefault(textDefaultsKey) + "</div></html>";
		else
			t = "<html><div style='font: " + textSize + "pt Arial Narrow; color: rgb("
					+ textColor.getRed()
					+ ", "
					+ textColor.getGreen()
					+ ", "
					+ textColor.getBlue()
					+ ");'>"
				+ InterfaceTextDefaults.getInstance().getDefault(textDefaultsKey) + "</div></html>";
		setText(t);
	}
}
