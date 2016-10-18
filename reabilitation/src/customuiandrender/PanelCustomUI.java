package customuiandrender;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicPanelUI;

public class PanelCustomUI extends BasicPanelUI {

	Color back = new Color(236, 239, 241);
	Color border = new Color(120, 144, 156);

	boolean fill = false;

	public PanelCustomUI(boolean fill) {
		this.fill = fill;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Rectangle area = new Rectangle();
		SwingUtilities.calculateInnerArea(c, area);
		if (fill) {
			g.setColor(back);
			g.fillRoundRect(area.x, area.y, area.width, area.height, 8, 8);
		}
		g.setColor(border);
		g.drawRoundRect(area.x, area.y, area.width - 1, area.height - 1, 8, 8);
	}
}
