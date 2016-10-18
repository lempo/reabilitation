package customuiandrender;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

public class ButtonCustomUI extends BasicButtonUI {
	private Color color;
	private Color borderColor = null;

	public ButtonCustomUI(Color color) {
		super();
		this.color = color;
	}
	
	public ButtonCustomUI(Color color, Color borderColor) {
		super();
		this.color = color;
		this.borderColor = borderColor;
	}

	@Override
	protected void paintButtonPressed(Graphics g, AbstractButton b) {
		if (b.isContentAreaFilled() && b.isOpaque()) {
			Rectangle area = new Rectangle();
			SwingUtilities.calculateInnerArea(b, area);
			g.setColor(color);
			g.fillRoundRect(area.x - 1, area.y - 1, area.width - 3, area.height - 3, 12, 12);
		}
	}

	@Override
	public void paint(Graphics g, JComponent c) {

		AbstractButton b = (AbstractButton) c;

		Rectangle area = new Rectangle();
		SwingUtilities.calculateInnerArea(b, area);
		g.setColor(color);
		g.fillRoundRect(area.x - 1, area.y - 1, area.width - 3, area.height - 3, 12, 12);
		
		if (borderColor != null) {
			g.setColor(borderColor);
			g.drawRoundRect(area.x, area.y, area.width - 4, area.height - 4, 10, 10);
		}

		super.paint(g, c);
	}

	@Override
	protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
		AbstractButton b = (AbstractButton) c;
		Font f = new Font("Arial Narrow", Font.PLAIN, 17);
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics(f);

		g.setColor(Color.WHITE);
		BasicGraphicsUtils.drawString(g, text, -1, textRect.x - 4, textRect.y + fm.getAscent() - 3);
	}
}
