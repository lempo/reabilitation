package tasks;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import customuiandrender.ButtonCustomUI;
import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import defaults.TextLinkDefaults;
import reabilitation.utils.Utils;

public class FiguresPanel extends AbstractPanel {
	private static final long serialVersionUID = 4766982486145454609L;
	Figures w;
	Random rand;

	private int diff = Figures.EASY;

	private int correct = 0;

	private boolean pause = false;
	private boolean canPause = true;

	private long pauseTime = 0;
	private long startPauseTime = 0;

	private int num = 5;

	private ArrayList<String> topviews;
	private ArrayList<HashMap<String, Point>> points;

	private int counter;

	private int[] whereAmI;
	private int[] whatISee;

	private Point selectedPoint = null;
	private int viewedPoint = 0;

	public FiguresPanel(int width, int height, int diff, Figures w) {
		super(width, height);
		this.diff = diff;
		this.w = w;

		generate();
		switch (diff) {
		case Figures.EASY:
			showWhereAmI();
			break;
		case Figures.MIDDLE:
			showWhatISee();
			break;
		case Figures.HARD:
			showWhereAmI();
			break;
		}
	}
	
	public void fromBegining() {
		pauseTime = 0;
		startPauseTime = 0;
		correct = 0;
		counter = 0;
		selectedPoint = null;
		viewedPoint = 0;
		switch (diff) {
		case Figures.EASY:
			showWhereAmI();
			break;
		case Figures.MIDDLE:
			showWhatISee();
			break;
		case Figures.HARD:
			showWhereAmI();
			break;
		}
	}

	public void generate() {
		rand = new Random(System.nanoTime());
		Document doc = Utils.openXML(
				TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.FIGURES) + Integer.toString(diff) + ".xml");
		topviews = new ArrayList<String>();
		points = new ArrayList<HashMap<String, Point>>();
		selectedPoint = null;

		counter = 0;

		NodeList n = doc.getElementsByTagName("topview");

		int[] f = new int[n.getLength()];
		for (int i = 0; i < f.length; i++)
			f[i] = 0;
		int realNum = Math.min(num, n.getLength());
		num = realNum;

		switch (diff) {
		case Figures.EASY:
			whereAmI = new int[realNum];
			break;
		case Figures.MIDDLE:
			whatISee = new int[realNum];
			break;
		case Figures.HARD:
			whereAmI = new int[realNum];
			whatISee = new int[realNum];
			break;
		}

		int i = 0;
		while (i < realNum) {
			int j = rand.nextInt(n.getLength());
			if (f[j] != 0)
				continue;
			f[i] = 1;
			topviews.add(n.item(j).getAttributes().getNamedItem("image").getNodeValue());
			HashMap<String, Point> hm = new HashMap<String, Point>();
			NodeList n1 = n.item(j).getChildNodes();
			for (int k = 0; k < n1.getLength(); k++)
				if (n1.item(k).getNodeName().equals("point"))
					hm.put(n1.item(k).getAttributes().getNamedItem("image").getNodeValue(),
							new Point(Integer.parseInt(n1.item(k).getAttributes().getNamedItem("x").getNodeValue()),
									Integer.parseInt(n1.item(k).getAttributes().getNamedItem("y").getNodeValue())));
			points.add(hm);

			int t = rand.nextInt(hm.size());
			switch (diff) {
			case Figures.EASY:
				whereAmI[i] = t;
				break;
			case Figures.MIDDLE:
				whatISee[i] = t;
				break;
			case Figures.HARD:
				int t1 = rand.nextInt(hm.size());
				whereAmI[i] = t;
				whatISee[i] = t1;
				break;
			}

			i++;
		}
	}

	private void showWhereAmI() {

		JLabel task = new JLabel();
		String t = "<html><div style='font: 18pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("figures_where_am_i") + "</div></html>";
		task.setText(t);

		BgPanel top = new BgPanel(topviews.get(counter));
		top.addMouseListener(new NewMouseListener());
		BgPanel point = new BgPanel((String) points.get(counter).keySet().toArray()[whereAmI[counter]]);

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("continue"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(100, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pause)
					return;
				if (selectedPoint == null)
					return;
				Point p = points.get(counter).get(points.get(counter).keySet().toArray()[whereAmI[counter]]);
				if ((p.x == selectedPoint.x) && (p.y == selectedPoint.y))
					correct++;
				if (counter < num - 1) {
					counter++;
					showWhereAmI();
				} else {
					if (diff == Figures.HARD) {
						counter = 0;
						showWhatISee();
					} else
						w.showResults();
				}
			}
		});

		this.removeAll();
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 15, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(task, c);

		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 10, 0, 0);
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;

		this.add(point, c);

		c.gridwidth = 1;
		c.gridx = 1;
		c.insets = new Insets(0, 0, 0, 0);

		this.add(top, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 2;

		this.add(start, c);

		this.revalidate();
		this.repaint();
	}

	private void showWhatISee() {
		JLabel task = new JLabel();
		String t = "<html><div style='font: 18pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("figures_what_i_see") + "</div></html>";
		task.setText(t);

		BgPanel top = new BgPanel(topviews.get(counter));
		top.setMarker(points.get(counter).get(points.get(counter).keySet().toArray()[whatISee[counter]]));
		BgPanel point = new BgPanel((String) points.get(counter).keySet().toArray()[viewedPoint]);

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("continue"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(100, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pause)
					return;
				if (whatISee[counter] == viewedPoint)
					correct++;
				if (counter < num - 1) {
					counter++;
					showWhatISee();
				} else
					w.showResults();
			}
		});

		JLabel left = new JLabel();
		ImageIcon icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW));
		left.setIcon(icon);
		left.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (pause)
					return;
				if (viewedPoint > 0)
					viewedPoint--;
				else
					viewedPoint = points.get(counter).size() - 1;
				point.setImage((String) points.get(counter).keySet().toArray()[viewedPoint]);
			}
		});

		JLabel right = new JLabel();
		icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW_RIGHT));
		right.setIcon(icon);
		right.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		right.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (pause)
					return;
				if (viewedPoint < points.get(counter).size() - 1)
					viewedPoint++;
				else
					viewedPoint = 0;
				point.setImage((String) points.get(counter).keySet().toArray()[viewedPoint]);
			}
		});

		this.removeAll();
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 15, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(task, c);

		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 10, 0, 0);
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;

		this.add(top, c);

		c.gridwidth = 1;
		c.gridx = 1;
		c.insets = new Insets(0, 0, 0, 0);

		this.add(left, c);

		c.gridx = 2;

		this.add(point, c);

		c.gridx = 3;

		this.add(right, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 4;

		this.add(start, c);

		this.revalidate();
		this.repaint();
	}

	class BgPanel extends JPanel {
		String image;
		Point marker = null;

		public BgPanel(String image) {
			this.image = image;
			this.setPreferredSize(new Dimension(400, 400));
		}

		public void paintComponent(Graphics g) {
			Image im = null;
			try {
				im = ImageIO.read(Utils.class.getResource(image));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("problem reading file " + image);
			}
			g.drawImage(im, 0, 0, null);

			if (marker != null) {
				g.setColor(Color.RED);
				g.fillOval(marker.x - 6, marker.y - 6, 12, 12);
			}
		}

		public void setMarker(Point p) {
			marker = p;
			repaint();
		}

		public void setImage(String image) {
			this.image = image;
			repaint();
		}
	}

	public boolean pause() {
		if (canPause) {
			pause = true;
			startPauseTime = new Date().getTime();
			return true;
		} else
			return false;
	}

	public void start() {
		if (pause) {
			pauseTime += new Date().getTime() - startPauseTime;
			pause = false;
		}
	}

	public long getPauseTime() {
		return pauseTime;
	}

	public int getCorrectPercent() {
		if (diff == Figures.HARD)
			return (int) Math.round(((float) correct / (num * 2)) * 100.0);
		return (int) Math.round(((float) correct / num) * 100.0);
	}

	private class NewMouseListener implements MouseListener {

		private int delta = 20;

		@Override
		public void mouseClicked(MouseEvent e) {
			if (pause)
				return;
			BgPanel p = (BgPanel) e.getSource();
			int x = e.getX();
			int y = e.getY();
			HashMap<String, Point> hm = points.get(counter);

			Iterator it = hm.entrySet().iterator();
			while (it.hasNext()) {
				HashMap.Entry pair = (HashMap.Entry) it.next();
				Point point = (Point) pair.getValue();
				if (((x > point.x - delta) && (x < point.x + delta))
						&& ((y > point.y - delta) && (y < point.y + delta))) {
					p.setMarker(point);
					p.repaint();
					selectedPoint = point;
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

}
