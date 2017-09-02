package tasks;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import customuiandrender.ButtonCustomUI;
import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import defaults.TextLinkDefaults;
import reabilitation.Utils;

public class TravelPanel extends JPanel {
	private static final long serialVersionUID = 4766982486145454609L;
	Travel w;
	Random rand;

	private int diff = Travel.EASY;

	private JLabel[] images;
	private JLabel[] names;
	private Point[] points;
	private String map;
	private String mapName;

	private int correct = 0;

	private boolean pause = false;
	private boolean canPause = true;

	private long pauseTime = 0;
	private long startPauseTime = 0;

	BgPanel p;

	/**
	 * ������ ����������� ����, ��� ����� ��������� � ������, ���������
	 * ������������ ��� �������������� � �����������
	 */
	private int[] inList;
	/**
	 * ������ ��������� ������ ��� ���������� ����� � ������, ���������
	 * ������������ ��� �������������� � �����������
	 */
	private int listLastNumber;
	/**
	 * �����-����������, �� ������� ������������ ������������� ����� �
	 * ����������/�������������
	 */
	private JLabel[] dest;

	private Point lastDragPosition;
	NewMouseMotionListener draggingListener;
	NewMouseListener clickListener;

	public TravelPanel(int width, int height, int diff, Travel w) {
		super();
		this.diff = diff;
		this.w = w;

		this.setPreferredSize(new Dimension(width, height));
		this.setDoubleBuffered(true);
		this.setOpaque(false);
		generate();
		showInfo_1();
	}
	
	public void fromBegining() {
		pauseTime = 0;
		startPauseTime = 0;
		correct = 0;
		showInfo_1();
	}

	public void generate() {
		rand = new Random(System.nanoTime());
		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.TRAVEL));
		images = new JLabel[diff];
		names = new JLabel[diff];
		points = new Point[diff];
		inList = new int[diff];

		NodeList n = doc.getElementsByTagName("region");
		int j = rand.nextInt(n.getLength());
		Node region = n.item(j);
		map = region.getAttributes().getNamedItem("image").getNodeValue();
		mapName = region.getAttributes().getNamedItem("name").getNodeValue();
		int regLen = region.getChildNodes().getLength();
		if (regLen < diff)
			generate();
		int[] usage = new int[regLen];
		int i = 0;
		while (i < diff) {
			int k = rand.nextInt(regLen);
			if (usage[k] == 0) {
				Node point = region.getChildNodes().item(k);
				NamedNodeMap attr = point.getAttributes();
				points[i] = new Point(Integer.parseInt(attr.getNamedItem("x").getNodeValue()),
						Integer.parseInt(attr.getNamedItem("y").getNodeValue()));
				images[i] = new JLabel();
				ImageIcon icon = Utils.createImageIcon(attr.getNamedItem("image").getNodeValue());
				images[i].setIcon(icon);
				names[i] = new JLabel();
				icon = Utils.createImageIcon(attr.getNamedItem("name").getNodeValue());
				names[i].setIcon(icon);
				usage[k] = 1;
				i++;
			} else
				continue;
		}

	}

	public void showFullMap() {
		BgPanel p = new BgPanel(map);
		p.setLayout(null);

		JLabel mName = new JLabel();
		String t = "<html><div style='font: bold 20pt Arial Narrow; color: rgb(115, 84, 73);'>" + mapName
				+ "</div></html>";
		mName.setText(t);

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("continue"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(150, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pause)
					return;
				showDragNames();
			}
		});

		this.removeAll();
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 2.0;
		c.weighty = 0.0;

		this.add(mName, c);

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(p, c);

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(start, c);

		for (int i = 0; i < diff; i++) {
			p.add(images[i]);
			p.add(names[i]);
			images[i].setBounds(new Rectangle(points[i], images[i].getPreferredSize()));
			names[i].setBounds(new Rectangle(
					new Point(points[i].x, (int) (points[i].y + images[i].getPreferredSize().getHeight())),
					names[i].getPreferredSize()));
		}

		this.revalidate();
		this.repaint();
	}

	public void showDragNames() {
		JLabel task = new JLabel();
		String t = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("travel_task_1") + "</div></html>";
		task.setText(t);

		JLabel mName = new JLabel();
		t = "<html><div style='font: bold 20pt Arial Narrow; color: rgb(115, 84, 73);'>" + mapName + "</div></html>";
		mName.setText(t);

		p = new BgPanel(map);
		p.setLayout(null);

		dest = new JLabel[diff];
		for (int i = 0; i < diff; i++) {
			p.add(images[i]);
			JLabel l = new JLabel();
			l.setPreferredSize(names[i].getPreferredSize());
			l.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
			l.setForeground(Color.WHITE);
			l.setOpaque(true);
			dest[i] = l;
			p.add(l);
			images[i].setBounds(new Rectangle(points[i], images[i].getPreferredSize()));
			l.setBounds(new Rectangle(
					new Point(points[i].x, (int) (points[i].y + images[i].getPreferredSize().getHeight())),
					names[i].getPreferredSize()));
		}

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("continue"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(150, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pause)
					return;
				for (int i = 0; i < diff; i++) {
					if (dest[i].getName() == null)
						return;
					String[] parts1 = dest[i].getName().split(" ");
					String[] parts2 = names[i].getName().split(" ");
					if (parts1[1].equals(parts2[1]))
						correct++;
				}
				showDragImages();
			}
		});

		this.removeAll();
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		draggingListener = new NewMouseMotionListener();
		clickListener = new NewMouseListener();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 10, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(task, c);

		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 2.0;
		c.weighty = 0.0;

		this.add(mName, c);

		for (int i = 0; i < diff; i++) {
			names[i].addMouseListener(clickListener);
			names[i].addMouseMotionListener(draggingListener);

			names[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			names[i].setName("names " + Integer.toString(i));

			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = i + 2;
			c.insets = new Insets(0, 30, 0, 0);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;

			this.add(names[i], c);
			inList[i] = 1;
		}
		listLastNumber = diff + 2;

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(p, c);

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 30);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(start, c);

		this.revalidate();
		this.repaint();
	}

	private void addAgain() {
		this.remove(p);

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(p, c);

		this.revalidate();
		this.repaint();
	}

	public void showDragImages() {
		JLabel task = new JLabel();
		String t = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("travel_task_2") + "</div></html>";
		task.setText(t);

		JLabel mName = new JLabel();
		t = "<html><div style='font: bold 20pt Arial Narrow; color: rgb(115, 84, 73);'>" + mapName + "</div></html>";
		mName.setText(t);

		p = new BgPanel(map);
		p.setLayout(null);

		dest = new JLabel[diff];
		for (int i = 0; i < diff; i++) {
			p.add(names[i]);
			JLabel l = new JLabel();
			l.setPreferredSize(images[i].getPreferredSize());
			l.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
			l.setForeground(Color.WHITE);
			l.setOpaque(true);
			dest[i] = l;
			p.add(l);
			l.setBounds(new Rectangle(points[i], images[i].getPreferredSize()));
			names[i].setBounds(new Rectangle(
					new Point(points[i].x, (int) (points[i].y + images[i].getPreferredSize().getHeight())),
					names[i].getPreferredSize()));
		}

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("continue"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(150, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < diff; i++) {
					if (dest[i].getName() == null)
						return;
					String[] parts1 = dest[i].getName().split(" ");
					String[] parts2 = images[i].getName().split(" ");
					if (parts1[1].equals(parts2[1]))
						correct++;
				}
				w.showResults();
			}
		});

		this.removeAll();
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 10, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(task, c);

		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 2.0;
		c.weighty = 0.0;

		this.add(mName, c);

		for (int i = 0; i < diff; i++) {
			names[i].removeMouseListener(clickListener);
			names[i].removeMouseMotionListener(draggingListener);
			names[i].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			images[i].addMouseListener(clickListener);
			images[i].addMouseMotionListener(draggingListener);

			images[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			images[i].setName("images " + Integer.toString(i));

			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = i + 2;
			c.insets = new Insets(0, 30, 0, 0);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;

			this.add(images[i], c);
			inList[i] = 1;
		}
		listLastNumber = diff + 2;

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(p, c);

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 30);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(start, c);

		this.revalidate();
		this.repaint();
	}

	private void showInfo_1() {

		canPause = false;

		long t = new Date().getTime();

		JTextPane t1 = new JTextPane();
		t1.setEditable(false);
		t1.setContentType("text/html;charset=utf-8");
		t1.setText("<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("travel_info") + "</div></html>");
		t1.setOpaque(false);
		t1.setPreferredSize(new Dimension((int) (this.getPreferredSize().getWidth() * 0.85),
				40 + Utils.calculateTextHeight(t1.getText(), (int) (this.getPreferredSize().getWidth() * 0.85), t1)));

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("begin_task"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(200, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pauseTime += new Date().getTime() - t;
				canPause = true;
				showFullMap();
			}
		});

		this.removeAll();
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(40, 20, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(t1, c);

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(40, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 1.0;

		this.add(start, c);

		this.revalidate();
		this.repaint();
	}

	class BgPanel extends JPanel {
		String image;

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
		return (int) Math.round(((float) correct / (diff * 2)) * 100.0);
	}

	private class NewMouseMotionListener implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (pause)
				return;
			JLabel l = (JLabel) e.getSource();

			Point currentDragPosition = e.getLocationOnScreen();
			int deltaX = currentDragPosition.x - lastDragPosition.x;
			int deltaY = currentDragPosition.y - lastDragPosition.y;
			if (deltaX != 0 || deltaY != 0) {
				int x = l.getLocation().x + deltaX;
				int y = l.getLocation().y + deltaY;
				l.setLocation(x, y);
				lastDragPosition = currentDragPosition;
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}
	}

	private class NewMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (pause)
				return;
			JLabel l = (JLabel) e.getSource();
			String[] parts = l.getName().split(" ");
			int i = Integer.parseInt(parts[1]);
			if (inList[i] == 0) {
				GridBagConstraints c = new GridBagConstraints();
				c.anchor = GridBagConstraints.WEST;
				c.fill = GridBagConstraints.NONE;
				c.gridheight = 1;
				c.gridwidth = 1;
				c.gridx = 0;
				c.gridy = listLastNumber;
				c.insets = new Insets(0, 30, 0, 0);
				c.ipadx = 0;
				c.ipady = 0;
				c.weightx = 1.0;
				c.weighty = 1.0;

				if (parts[0].equals("names")) {
					l.getParent().getParent().add(names[i], c);
					names[i].addMouseListener(clickListener);
				} else {
					l.getParent().getParent().add(images[i], c);
					images[i].addMouseListener(clickListener);
				}
				listLastNumber++;
				inList[i] = 1;
				l.removeMouseListener(clickListener);
				l.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				l.setIcon(null);
				l.getParent().getParent().revalidate();
				l.getParent().getParent().repaint();
				addAgain();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (pause)
				return;
			lastDragPosition = e.getLocationOnScreen();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (pause)
				return;
			JLabel l = (JLabel) e.getSource();
			String[] parts = l.getName().split(" ");
			int i = Integer.parseInt(parts[1]);
			if (inList[i] == 1) {
				Point currentDragPosition = e.getLocationOnScreen();
				int x = currentDragPosition.x;
				int y = currentDragPosition.y;
				int f = 0;
				for (int k = 0; k < diff; k++) {
					if ((x > dest[k].getLocationOnScreen().x)
							&& (x < dest[k].getLocationOnScreen().x + dest[k].getWidth())
							&& (y > dest[k].getLocationOnScreen().y)
							&& (y < dest[k].getLocationOnScreen().y + dest[k].getHeight())
							&& (dest[k].getText().equals(""))) {
						f = 1;
						inList[i] = 0;
						dest[k].setIcon(l.getIcon());
						dest[k].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						dest[k].setName(l.getName());
						dest[k].addMouseListener(clickListener);
						l.removeMouseListener(clickListener);
						Container parent = l.getParent();
						parent.remove(l);
						parent.revalidate();
						parent.repaint();
					}
				}
				if (f == 0) {
					GridBagConstraints c = new GridBagConstraints();
					c.anchor = GridBagConstraints.WEST;
					c.fill = GridBagConstraints.NONE;
					c.gridheight = 1;
					c.gridwidth = 1;
					c.gridx = 0;
					c.gridy = listLastNumber;
					c.insets = new Insets(0, 30, 0, 0);
					c.ipadx = 0;
					c.ipady = 0;
					c.weightx = 1.0;
					c.weighty = 1.0;
					if (parts[0].equals("names"))
						l.getParent().add(names[i], c);
					else
						l.getParent().add(images[i], c);
					listLastNumber++;
					l.getParent().revalidate();
					l.getParent().repaint();
					addAgain();
				}
			}
		}

	}

}
