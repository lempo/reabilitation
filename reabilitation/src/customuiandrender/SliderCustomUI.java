package customuiandrender;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

public class SliderCustomUI extends BasicSliderUI {

	public SliderCustomUI(JSlider arg0) {
		super(arg0);
	}

	public void paintTrack(Graphics g) {
		Color saved_color = g.getColor();
		int width;
		int height;

		Point a = new Point(trackRect.x, trackRect.y + 1);
		Point b = new Point(a);
		Point c = new Point(a);
		Point d = new Point(a);

		if (slider.getOrientation() == JSlider.HORIZONTAL) {
			width = trackRect.width;
			height = (thumbRect.height / 4 == 0) ? 1 : thumbRect.height / 4;

			a.translate(0, (trackRect.height / 2) - (height / 2));
			b.translate(0, (trackRect.height / 2) + (height / 2));
			c.translate(trackRect.width, (trackRect.height / 2) + (height / 2));
			d.translate(trackRect.width, (trackRect.height / 2) - (height / 2));
		} else {
			width = (thumbRect.width / 4 == 0) ? 1 : thumbRect.width / 4;
			height = trackRect.height;

			a.translate((trackRect.width / 2) - (width / 2), 0);
			b.translate((trackRect.width / 2) - (width / 2), trackRect.height);
			c.translate((trackRect.width / 2) + (width / 2), trackRect.height);
			d.translate((trackRect.width / 2) + (width / 2), 0);
		}
		g.setColor(new Color(163, 138, 129));
		g.fillRoundRect(a.x, a.y, width, height, 3, 3);

		g.setColor(saved_color);
	}

	public void paintThumb(Graphics g) {
		Color saved_color = g.getColor();
		int r = 10;

		int x = (int) thumbRect.getCenterX();
		int y = (int) thumbRect.getCenterY();
		
		if (x < r)
			x = r;
		
		if (x > trackRect.width)
			x = trackRect.width;
		
		x -= r;
		y -= r;

		g.setColor(new Color(38, 166, 154));
		g.fillOval(x, y, r * 2, r * 2);

		g.setColor(saved_color);
	}

}
