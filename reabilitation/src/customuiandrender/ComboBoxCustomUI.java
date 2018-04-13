package customuiandrender;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import defaults.ImageLinkDefaults;
import reabilitation.utils.Utils;

public class ComboBoxCustomUI extends BasicComboBoxUI {

	String[] strings;

	public ComboBoxCustomUI(String[] tasksStrings) {
		strings = tasksStrings;
	}

	@Override
	protected JButton createArrowButton() {
		CustomButton btnL = new CustomButton("");
		btnL.setBorderPainted(false);
		btnL.setBorder(null);
		btnL.setContentAreaFilled(false);
		btnL.setIcon(
				Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW_BUTTON)));
		return btnL;
	}

	protected ComboPopup createPopup() {
		BasicComboPopup bcp = (BasicComboPopup) super.createPopup();

		// set the border around the popup
		bcp.setBorder(BorderFactory.createEmptyBorder());

		return bcp;
	}

	private class CustomButton extends JButton {
		private static final long serialVersionUID = 2249712165952452305L;
		Color unselected = new Color(236, 239, 241);
		Color border = new Color(204, 204, 204);

		public CustomButton(String string) {
			super(string);
		}

		public void paintComponent(Graphics g) {
			Rectangle area = new Rectangle();
			SwingUtilities.calculateInnerArea(this, area);
			g.setColor(unselected);
			g.fillRoundRect(area.x - 10, area.y, area.width + 10, area.height, 8, 8);
			g.setColor(border);
			g.drawRoundRect(area.x - 10, area.y, area.width + 9, area.height - 1, 8, 8);
			super.paintComponent(g);
		}

	}

}
