package customuiandrender;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicToolTipUI;

import customcomponent.DropShadowBorder;

public class CustomToolTipUI extends BasicToolTipUI {
	Color border = new Color(120, 144, 156);

	@Override
	public void paint(Graphics g, JComponent c) {
		//Graphics2D g2 = (Graphics2D) g;

		JToolTip tip = (JToolTip) c;
		tip.setOpaque(false);
		tip.setBorder(null);
		tip.setBackground(Color.WHITE);
		Rectangle area = new Rectangle();
		SwingUtilities.calculateInnerArea(c, area);

		//GradientPaint blacktowhite = new GradientPaint(area.x, area.height - 5, Color.GRAY, area.x, area.height - 1, Color.WHITE);
		//g2.setPaint(blacktowhite);
		//g2.fill(new RoundRectangle2D.Double(area.x, area.height - 5, area.width - 5, area.height - 1, 8, 8));

		g.setColor(Color.WHITE);
		g.fillRoundRect(area.x, area.y, area.width - 5, area.height - 5, 8, 8);
		g.setColor(border);
		g.drawRoundRect(area.x, area.y, area.width - 5, area.height - 5, 8, 8);
		super.paint(g, c);
		DropShadowBorder shadow = new DropShadowBorder();
		shadow.setShadowColor(Color.GRAY);
		shadow.setShadowSize(5);
		shadow.setShadowOpacity((float) 0.5);
		shadow.setShowRightShadow(true);
		shadow.setShowBottomShadow(true);
		tip.setBorder(shadow);
	}
}
