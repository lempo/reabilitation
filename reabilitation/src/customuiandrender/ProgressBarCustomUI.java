package customuiandrender;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class ProgressBarCustomUI extends BasicProgressBarUI {

	Color backColor = new Color(233, 83, 80);
	Color mainColor = new Color(69, 90, 100);

	@Override
	protected void paintDeterminate(Graphics g, JComponent c) {
		Graphics2D g2d = (Graphics2D) g.create();
		double dProgress = progressBar.getPercentComplete();
		if (dProgress < 0) {
			dProgress = 0;
		} else if (dProgress > 1) {
			dProgress = 1;
		}
		Rectangle area = new Rectangle();
		SwingUtilities.calculateInnerArea(c, area);
		g.setColor(backColor);
		g.fillRoundRect(area.x, area.y, area.width, area.height, 5, 5);
		g.setColor(mainColor);
		g.fillRoundRect(area.x, area.y, (int) (area.width * dProgress), area.height, 5, 5);

		g2d.dispose();
	}
}
