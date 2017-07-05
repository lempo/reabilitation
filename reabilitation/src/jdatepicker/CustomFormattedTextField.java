package jdatepicker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;

public class CustomFormattedTextField extends JFormattedTextField {
	private static final long serialVersionUID = -4303240544556069274L;
	Color back = new Color(236, 239, 241);
	Color border = new Color(204, 204, 204);

	public CustomFormattedTextField(DateComponentFormatter dateComponentFormatter) {
		super(dateComponentFormatter);
		this.setBorder(null);
		this.setOpaque(false);
		this.setFont(new Font("ArialNarrow", Font.BOLD, 14));
		this.setForeground(new Color(70, 110, 122));
	}

	@Override
	public void paintComponent(Graphics g) {
		Rectangle area = new Rectangle();
		SwingUtilities.calculateInnerArea(this, area);
		g.setColor(back);
		g.fillRoundRect(area.x, area.y, area.width + 10, area.height - 1, 5, 5);
		g.setColor(border);
		g.drawRoundRect(area.x, area.y, area.width + 10, area.height - 1, 5, 5);
		super.paintComponent(g);
	}
}
