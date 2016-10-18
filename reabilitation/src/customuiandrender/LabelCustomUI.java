package customuiandrender;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicLabelUI;

public class LabelCustomUI extends BasicLabelUI {

	Color color = new Color(120, 144, 156);
	int stroke;
	boolean fill;
	boolean setTextColor;

	public LabelCustomUI(Color color, int stroke, boolean fill, boolean setTextColor) {
		super();
		this.color = color;
		this.stroke = stroke;
		this.fill = fill;
		this.setTextColor = setTextColor;
	}

	public void paint(Graphics gr, JComponent c) {
		Graphics2D g = (Graphics2D) gr;
		if (setTextColor) {
			JLabel b = (JLabel) c;
			String[] parts = b.getText().split("[<>]");
			if (parts.length > 4)
				b.setText("<html><div style='font: bold 16pt ArialNarrow; color: rgb(" + color.getRed() + ", "
						+ color.getGreen() + ", " + color.getBlue() + "); text-align: left; padding: 10px;'>"
						+ parts[4].toUpperCase() + "</div></html>");
		}
		super.paint(gr, c);
		Rectangle area = new Rectangle();
		SwingUtilities.calculateInnerArea(c, area);
		g.setColor(color);
		g.setStroke(new BasicStroke(stroke));
		g.drawRoundRect(area.x, area.y, area.width - 1, area.height - 1, 8, 8);
		if (fill)
			g.fillRoundRect(area.x, area.y, area.width - 1, area.height - 1, 8, 8);
	}
}
