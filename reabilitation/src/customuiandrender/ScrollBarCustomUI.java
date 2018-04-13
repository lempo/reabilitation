package customuiandrender;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

import defaults.ImageLinkDefaults;
import reabilitation.utils.Utils;

public class ScrollBarCustomUI extends BasicScrollBarUI {

	private int trackColorR = 239;
	private int trackColorG = 235;
	private int trackColorB = 233;

	private int thumbColorR = 215;
	private int thumbColorG = 204;
	private int thumbColorB = 200;

	@Override
	protected JButton createDecreaseButton(int orientation) {
		JButton btnL = new JButton("");
		btnL.setBorderPainted(false);
		btnL.setBorder(null);
		btnL.setContentAreaFilled(false);
		if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
			btnL.setIcon(
					Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.INCREASE)));
		else
			btnL.setIcon(Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.DECREASE_HORIZONTAL)));
		return btnL;
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		JButton btnL = new JButton("");
		btnL.setBorderPainted(false);
		btnL.setBorder(null);
		btnL.setContentAreaFilled(false);
		if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
			btnL.setIcon(
					Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.DECREASE)));
		else
			btnL.setIcon(Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.INCREASE_HORIZONTAL)));
		return btnL;
	}

	@Override
	protected void paintDecreaseHighlight(Graphics g) {
		Insets insets = scrollbar.getInsets();
		Rectangle thumbR = getThumbBounds();
		g.setColor(new Color(137, 144, 144));

		if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
			int x = insets.left + decrButton.getWidth() / 2 - 2;
			int y = decrButton.getY() + decrButton.getHeight();
			int w = 4;
			int h = thumbR.y - y;
			g.fillRect(x, y, w, h);
		} else {
			int x, w;
			if (scrollbar.getComponentOrientation().isLeftToRight()) {
				x = decrButton.getX() + decrButton.getWidth();
				w = thumbR.x - x;
			} else {
				x = thumbR.x + thumbR.width;
				w = decrButton.getX() - x;
			}
			int y = insets.top;
			int h = scrollbar.getHeight() - (insets.top + insets.bottom);
			g.fillRect(x, y, w, h);
		}
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		Graphics2D g2g = (Graphics2D) g;
		g2g.setColor(new Color(trackColorR, trackColorG, trackColorB));
		g2g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		Graphics2D g2g = (Graphics2D) g;
		g2g.setColor(new Color(thumbColorR, thumbColorG, thumbColorB));
		g2g.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 2, 2);
	}
}
