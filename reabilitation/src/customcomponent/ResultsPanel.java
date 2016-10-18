package customcomponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Date;

import javax.swing.JPanel;

import defaults.InterfaceTextDefaults;
import reabilitation.Results;

public class ResultsPanel extends JPanel {
	private static final long serialVersionUID = -6189097882809540226L;
	private int width;
	private int height;
	private int graficsHeight;
	private Results[] results;

	private static Color[] colors = { new Color(239, 83, 80), new Color(255, 183, 77), new Color(255, 237, 89),
			new Color(30, 144, 85), new Color(144, 202, 249), new Color(21, 101, 192), new Color(81, 45, 168),
			new Color(38, 166, 154), new Color(161, 136, 127), new Color(69, 90, 100), new Color(255, 255, 0),
			new Color(255, 0, 255), new Color(0, 255, 255) };

	private int deltaValue;

	private Color coordinateLinesColor = new Color(196, 206, 211);
	private Color helpLinesColor = new Color(220, 226, 230);
	private Color textColor = new Color(69, 90, 100);

	private int coordinateLinesWigth = 2;

	private int minValue = Integer.MAX_VALUE;
	private int maxValue = -1;

	private Date minDate = null;
	private Date maxDate = null;

	private int helpLinesNum = 12;

	public ResultsPanel(Results[] results, int width, int height) {
		super();
		this.results = results;
		this.width = width;
		this.height = height;
		this.graficsHeight = (int) Math.round(height * 0.8);
		this.setPreferredSize(new Dimension(width, height));
		this.setOpaque(false);
		this.setBorder(null);

		for (int i = 0; i < results.length; i++) {

			if (results[i].getMaxValue() > maxValue)
				maxValue = results[i].getMaxValue();
			if (results[i].getMinValue() < minValue)
				minValue = results[i].getMinValue();
			if ((minDate == null) || (((Date) results[i].getMap().keySet().toArray()[0]).getTime() < minDate.getTime()))
				minDate = (Date) results[i].getMap().keySet().toArray()[0];
			if ((maxDate == null) || (((Date) results[i].getMap().keySet().toArray()[results[i].getMap().size() - 1]))
					.getTime() > maxDate.getTime())
				maxDate = (Date) results[i].getMap().keySet().toArray()[results[i].getMap().size() - 1];
		}
		deltaValue = (int) Math.round(((maxValue - minValue) / (float) (helpLinesNum - 2)));
	}

	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		// draw coordinate lines
		g.setColor(coordinateLinesColor);
		g.setStroke(new BasicStroke(coordinateLinesWigth));
		g.drawLine(30, 30, width, 30);
		g.drawLine(30, 30, 30, graficsHeight);

		// draw help lines
		float vGap = (graficsHeight - 30) / (float) helpLinesNum;
		float hGap = (width - 30) / (float) helpLinesNum;
		Font f = new Font("Arial Narrow", Font.BOLD, 14);
		g.setFont(f);
		for (int i = 0; i < helpLinesNum; i++) {
			// long line
			g.setColor(helpLinesColor);
			g.drawLine(50, (int) Math.round(graficsHeight - i * vGap), width - 20,
					(int) Math.round(graficsHeight - i * vGap));

			// short lines
			g.setColor(coordinateLinesColor);
			g.drawLine(30, (int) Math.round(graficsHeight - i * vGap), 40, (int) Math.round(graficsHeight - i * vGap));
			g.drawLine((int) Math.round(width - i * hGap), 30, (int) Math.round(width - i * hGap), 40);
			g.setColor(textColor);
			if (i != 0)
				g.drawString(Integer.toString(maxValue - (i - 1) * deltaValue), 0,
						(int) Math.round(graficsHeight - i * vGap));
		}

		// draw grafics
		int valueLength = maxValue - minValue;
		long dateLength = maxDate.getTime() - minDate.getTime();
		float graficsWidth = width - 30 - hGap * 2;
		float graficHeight = graficsHeight - 30 - vGap * 2;
		g.setStroke(new BasicStroke(coordinateLinesWigth * 3));
		for (int j = 0; j < results.length; j++) {
			g.setColor(colors[j]);
			Date previousDate = (Date) results[j].getMap().keySet().toArray()[0];
			int previousValue = results[j].getMap().get(previousDate);
			g.drawRect(
					(int) Math.round(
							graficsWidth * (previousDate.getTime() - minDate.getTime()) / dateLength + hGap + 30),
					(int) Math.round(graficHeight * (previousValue - minValue) / valueLength + vGap + 30), 1, 1);
			for (int i = 1; i < results[j].getMap().size(); i++) {
				Date currentDate = (Date) results[j].getMap().keySet().toArray()[i];
				int currentValue = results[j].getMap().get(currentDate);
				g.drawLine(
						(int) Math.round(
								graficsWidth * (previousDate.getTime() - minDate.getTime()) / dateLength + hGap + 30),
						(int) Math.round(graficHeight * (previousValue - minValue) / valueLength + vGap + 30),
						(int) Math.round(
								graficsWidth * (currentDate.getTime() - minDate.getTime()) / dateLength + hGap + 30),
						(int) Math.round(graficHeight * (currentValue - minValue) / valueLength + vGap + 30));
				previousDate = currentDate;
				previousValue = currentValue;
			}
		}
		/*
		 * for (int j = 0; j < results.length; j++) { g.setColor(colors[j]);
		 * Date previousDate = (Date) results[j].getMap().keySet().toArray()[0];
		 * int previousValue = results[j].getMap().get(previousDate);
		 * g.drawRect( (int) Math.round( (((width - 30 - hGap * 2) *
		 * (previousDate.getTime() - minDate.getTime())) / dateLength) + 30 +
		 * hGap), (int) Math.round( ((float) (previousValue - minValue) /
		 * (valueLength / (float) (graficsHeight - vGap * 2))) + vGap) + 30, 1,
		 * 1); for (int i = 1; i < results[j].getMap().size(); i++) { Date
		 * currentDate = (Date) results[j].getMap().keySet().toArray()[i]; int
		 * currentValue = results[j].getMap().get(currentDate); g.drawLine(
		 * (int) Math.round( (((width - 30 - hGap * 2) * (previousDate.getTime()
		 * - minDate.getTime())) / dateLength) + 30 + hGap), (int)
		 * Math.round(((float) (previousValue - minValue) / (valueLength /
		 * (float) (graficsHeight - vGap * 2))) + vGap) + 30, (int) Math.round(
		 * (((width - 30 - hGap * 2) * (currentDate.getTime() -
		 * minDate.getTime())) / dateLength) + 30 + hGap), (int) Math.round(
		 * ((float) (currentValue - minValue) / (valueLength / (float)
		 * (graficsHeight - vGap * 2))) + vGap) + 30); previousDate =
		 * currentDate; previousValue = currentValue; } }
		 */

		// draw legend
		g.setColor(textColor);
		g.drawString(InterfaceTextDefaults.getInstance().getDefault("date_legend"), width
				- g.getFontMetrics().stringWidth(InterfaceTextDefaults.getInstance().getDefault("date_legend")) - 10,
				g.getFontMetrics().getAscent());
		g.drawString(InterfaceTextDefaults.getInstance().getDefault("time_legend"), 30,
				height - g.getFontMetrics().getAscent() - 40);

	}

	public static Color[] getColors() {
		return colors;
	}
}
