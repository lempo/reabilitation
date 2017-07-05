package jdatepicker;

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
	private Color borderColor;
	
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
			g.fillRoundRect(area.x - 10, area.y - 1, area.width + 9, area.height, 12, 12);
		}
	}

	@Override
	public void paint(Graphics g, JComponent c) {

		AbstractButton b = (AbstractButton) c;

		Rectangle area = new Rectangle();
		SwingUtilities.calculateInnerArea(b, area);
		g.setColor(color);
		g.fillRoundRect(area.x - 10, area.y, area.width + 9, area.height - 1, 12, 12);
		
		g.setColor(borderColor);
		g.drawRoundRect(area.x - 10, area.y, area.width + 9, area.height - 1, 10, 10);

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
