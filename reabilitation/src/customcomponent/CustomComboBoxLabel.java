package customcomponent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class CustomComboBoxLabel extends JLabel {
	private static final long serialVersionUID = 4133682732656433680L;
	Color unselected = new Color(236, 239, 241);
	Color selected = new Color(144, 164, 174);
	Color border = new Color(204, 204, 204);
	Color current = unselected;
	protected boolean sel = true;

	public void setSelected(boolean f) {
		if (f)
			current = selected;
		else
			current = unselected;
	}

	public void setSel(boolean sel) {
		this.sel = sel;
	}

	public void paintComponent(Graphics g) {
		Rectangle area = new Rectangle();
		SwingUtilities.calculateInnerArea(this, area);
		g.setColor(current);
		int d = -1;
		if (sel)
			d = 10;
		g.fillRoundRect(area.x, area.y, area.width + d, area.height, 8, 8);
		g.setColor(border);
		if (current.equals(selected))
			g.setColor(selected);
		g.drawRoundRect(area.x, area.y, area.width + d, area.height - 1, 8, 8);
		super.paintComponent(g);
	}

}