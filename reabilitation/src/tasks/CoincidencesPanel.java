package tasks;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import javax.swing.JPanel;

public class CoincidencesPanel extends JPanel {

	private static final long serialVersionUID = 372760270300425907L;
	private int width;
	private int height;

	private static int maxNum = 5;

	private static final int CIRCLE = 0;
	private static final int SQUARE = 1;
	private static final int TRIANGLE = 2;
	private static final int TRAPEZE = 3;
	private static final int RECTANGLE = 4;
	private static final int LOZENGE = 5;
	private static final int OVAL = 6;

	private static int[] FIGURES = { CIRCLE, SQUARE, TRIANGLE, TRAPEZE, RECTANGLE, LOZENGE, OVAL };
	private static Color[] COLORS = { new Color(239, 83, 80), new Color(255, 183, 77), new Color(255, 237, 89),
			new Color(30, 144, 85), new Color(144, 202, 249), new Color(21, 101, 192), new Color(81, 45, 168) };

	private Color borderColor = new Color(120, 144, 156);
	private Color backgroundColor = Color.WHITE;

	private Point[] coords;
	private int color;
	private int figure;
	private int num;

	private boolean figureFlag = false;
	private boolean colorFlag = false;
	private boolean numFlag = false;

	Random rand;

	public CoincidencesPanel(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		this.setPreferredSize(new Dimension(width, height));
		this.setDoubleBuffered(true);
	}

	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(backgroundColor);
		g.fillRoundRect(0, 0, width - 1, height - 1, 8, 8);
		g.setColor(borderColor);
		g.drawRoundRect(0, 0, width - 1, height - 1, 8, 8);
		g.setColor(COLORS[color]);
		int r = width / 6;
		for (int i = 0; i < coords.length; i++) {
			switch (figure) {
			case CIRCLE:
				g.fillOval((int) coords[i].getX() - r, (int) coords[i].getY() - r, r * 2, r * 2);
				break;
			case SQUARE:
				g.fillRect((int) coords[i].getX() - r, (int) coords[i].getY() - r, r * 2, r * 2);
				break;
			case TRIANGLE:
				int h = (int) (Math.sqrt(5.0) * r);
				int[] xPoints = new int[] { (int) (coords[i].getX() - r), (int) coords[i].getX(),
						(int) (coords[i].getX() + r) };
				int[] yPoints = new int[] { (int) (coords[i].getY() - h / 2), (int) (coords[i].getY() + h / 2),
						(int) (coords[i].getY() - h / 2) };
				g.fillPolygon(xPoints, yPoints, xPoints.length);
				break;
			case TRAPEZE:
				int[] xPoints1 = new int[] { (int) (coords[i].getX() - r), (int) (coords[i].getX() - r / 2),
						(int) (coords[i].getX() + r / 2), (int) (coords[i].getX() + r) };
				int[] yPoints1 = new int[] { (int) (coords[i].getY() + r), (int) (coords[i].getY() - r),
						(int) (coords[i].getY() - r), (int) (coords[i].getY() + r) };
				g.fillPolygon(xPoints1, yPoints1, xPoints1.length);
				break;
			case RECTANGLE:
				g.fillRect((int) coords[i].getX() - r, (int) (coords[i].getY() - r * 1.5), r * 2, r * 3);
				break;
			case LOZENGE:
				int[] xPoints11 = new int[] { (int) (coords[i].getX() - r), (int) coords[i].getX(),
						(int) (coords[i].getX() + r), (int) coords[i].getX() };
				int[] yPoints11 = new int[] { (int) coords[i].getY(), (int) (coords[i].getY() + r),
						(int) coords[i].getY(), (int) (coords[i].getY() - r) };
				g.fillPolygon(xPoints11, yPoints11, xPoints11.length);
				break;
			case OVAL:
				g.fillOval((int) coords[i].getX() - r, (int) (coords[i].getY() - r * 1.5), r * 2, r * 3);
				break;
			}
		}
	}

	public void generate() {
		rand = new Random(System.nanoTime());
		if (!numFlag)
			num = 1 + rand.nextInt(maxNum);
		if (!colorFlag)
			color = rand.nextInt(COLORS.length);
		if (!figureFlag)
			figure = rand.nextInt(FIGURES.length);
		numFlag = false;
		colorFlag = false;
		figureFlag = false;
		coords = new Point[num];
		if (num == 1) {
			coords[0] = new Point(width / 2, height / 2);
			return;
		}
		if (num % 2 == 0) {
			int d = num / 2;
			int w = width / 2;
			int h = height / d;
			coords[0] = new Point(w / 2, h / 2);
			coords[1] = new Point(w / 2 + w, h / 2);
			int t = h / 2;
			int i = 2;
			while (i < coords.length) {
				t += h;
				coords[i] = new Point(w / 2, t);
				i++;
				coords[i] = new Point(w / 2 + w, t);
				i++;
			}
		} else {
			int d = (num / 3) * 2 + Math.min(num % 3, 1);
			int w = width / 2;
			int h = height / d;
			coords[0] = new Point(w / 2, h / 2);
			coords[1] = new Point(w / 2 + w, h / 2);
			int t = h / 2;
			int i = 2;
			while (i < coords.length) {
				t += h;
				coords[i] = new Point(width / 2, t);
				t += h;
				i++;
				if (i == coords.length)
					break;
				coords[i] = new Point(w / 2, t);
				i++;
				coords[i] = new Point(w / 2 + w, t);
				i++;
			}
		}
	}

	public int getColor() {
		return color;
	}

	public int getFigure() {
		return figure;
	}

	public int getNum() {
		return num;
	}

	public void setColor(int color) {
		this.color = color;
		colorFlag = true;
	}

	public void setFigure(int figure) {
		this.figure = figure;
		figureFlag = true;
	}

	public void setNum(int num) {
		this.num = num;
		numFlag = true;
	}
}
