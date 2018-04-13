package tasks;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.RepaintManager;
import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import customuiandrender.ButtonCustomUI;
import customuiandrender.ProgressBarCustomUI;
import defaults.InterfaceTextDefaults;
import defaults.TextLinkDefaults;
import reabilitation.utils.Utils;

public class CategoriesPanel extends AbstractPanel {

	private static final long serialVersionUID = -9130625944713597822L;
	Categories w;
	Random rand;

	private int categoriesNum;
	private int catchTime;
	private int rows;
	private int cols;
	/** ���������� ������������ ���� */
	private int num = 10;
	/** ��� ������� ���� */
	private int n = -1;

	private Color borderColor = new Color(68, 83, 91);

	private Point lastDragPosition;

	private long pauseTime = 0;
	private long startPauseTime = 0;

	private int correct = 0;

	private String[] categories;
	/** ������������ ����� ������� � �������� ��������� */
	private int[] categoriesWords;
	private String[] words;
	private JLabel[] categoryLabels;

	private NewMouseMotionListener draggingListener;
	private NewMouseListener clickListener;

	private JLabel currentWord;

	private Timer timer;
	private JLabel left = new JLabel();
	private JProgressBar bar;

	private boolean pause = false;
	private boolean canPause = true;

	public CategoriesPanel(int width, int height, int diff, Categories w) {
		super(width, height);
		this.w = w;

		switch (diff) {
		case Categories.EASY:
			categoriesNum = 2;
			catchTime = 60;
			rows = 1;
			cols = 2;
			break;
		case Categories.MIDDLE:
			categoriesNum = 4;
			catchTime = 30;
			rows = 2;
			cols = 2;
			break;
		case Categories.HARD:
			categoriesNum = 6;
			catchTime = 10;
			rows = 2;
			cols = 3;
			break;
		}

		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (bar.getValue() < catchTime) {
					bar.setValue(bar.getValue() + 1);
					ImageIcon icon = Utils.createImageIcon(
							"resources/image/timer/" + Integer.toString(catchTime - bar.getValue()) + ".png");
					left.setIcon(icon);
				} else {
					timer.stop();
					showNextWord();
				}

			}
		});

		draggingListener = new NewMouseMotionListener();
		clickListener = new NewMouseListener();

		generate();
		showInfo_1();
	}
	
	public void fromBegining() {
		if (timer != null && timer.isRunning())
			timer.stop();
		pauseTime = 0;
		startPauseTime = 0;
		correct = 0;
		showInfo_1();
	}

	public void generate() {
		rand = new Random(System.nanoTime());
		words = new String[num];
		categories = new String[categoriesNum];
		categoriesWords = new int[num];
		String[][] variants = new String[categoriesNum][];
		int[][] usage = new int[categoriesNum][];

		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.CATEGORIES));
		NodeList n = doc.getElementsByTagName("category");

		int[] u = new int[n.getLength()];
		for (int i = 0; i < n.getLength(); i++)
			u[i] = 0;

		int i = 0;
		while (i < categoriesNum) {
			int j = rand.nextInt(n.getLength());
			if (u[j] != 0)
				continue;
			u[j] = 1;
			categories[i] = n.item(j).getAttributes().getNamedItem("name").getNodeValue();
			Node cat = n.item(j);
			int catLen = cat.getChildNodes().getLength();
			variants[i] = new String[catLen];
			usage[i] = new int[catLen];
			for (int t = 0; t < catLen; t++) {
				variants[i][t] = new String(cat.getChildNodes().item(t).getTextContent());
				usage[i][t] = 0;
			}
			i++;
		}

		i = 0;
		while (i < num) {
			int j = rand.nextInt(categoriesNum);
			int t = rand.nextInt(variants[j].length);
			if (usage[j][t] != 0)
				continue;
			words[i] = new String(variants[j][t]);
			categoriesWords[i] = j;
			usage[j][t] = 1;
			i++;
		}
	}

	private void showCategories() {
		n = -1;
		JPanel p = new JPanel();
		p.setOpaque(false);
		GridLayout experimentLayout = new GridLayout(rows, cols);
		p.setLayout(experimentLayout);

		categoryLabels = new JLabel[categoriesNum];
		for (int k = 0; k < categoriesNum; k++) {
			categoryLabels[k] = new JLabel();
			categoryLabels[k].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, borderColor));
			if (k % cols == 0)
				categoryLabels[k].setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, borderColor));
			if (k / cols == 0)
				categoryLabels[k].setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, borderColor));
			if ((k % cols == 0) && (k / cols == 0))
				categoryLabels[k].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
			categoryLabels[k].setText(
					"<html><div style='font: bold 22pt Arial Narrow; color: rgb(68, 83, 91); text-align: center;'>"
							+ categories[k] + "</div></html>");
			categoryLabels[k].setHorizontalAlignment(JLabel.CENTER);
			p.add(categoryLabels[k]);
			categoryLabels[k].setName(Integer.toString(k));
		}

		this.removeAll();
		this.setLayout(new GridBagLayout());

		JLabel task = new JLabel();
		String t = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("categories_task") + "</div></html>";
		task.setText(t);

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(task, c);

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 20, 0, 40);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		this.add(p, c);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(false);
		progressBar.setMinimum(0);
		progressBar.setMaximum(catchTime);
		progressBar.setValue(0);
		progressBar.setUI(new ProgressBarCustomUI());
		progressBar.setBorder(null);
		bar = progressBar;

		ImageIcon icon = Utils
				.createImageIcon("resources/image/timer/" + Integer.toString(catchTime - bar.getValue()) + ".png");
		left.setIcon(icon);

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(10, 20, 0, 20);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(left, c);

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(5, 20, 0, 20);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(progressBar, c);

		this.revalidate();
		this.repaint();

		showNextWord();
	}

	private void showInfo_1() {

		canPause = false;

		long t = new Date().getTime();

		JTextPane t1 = new JTextPane();
		t1.setEditable(false);
		t1.setContentType("text/html;charset=utf-8");
		t1.setText("<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("categories_info") + "</div></html>");
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
				showCategories();
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

	public void showNextWord() {
		if (n == num - 1) {
			timer.stop();
			w.showResults();
		} else {
			if (currentWord != null)
				this.remove(currentWord);
			n++;
			bar.setValue(0);
			ImageIcon icon = Utils
					.createImageIcon("resources/image/timer/" + Integer.toString(catchTime - bar.getValue()) + ".png");
			left.setIcon(icon);
			timer.start();
			JLabel l = new JLabel();
			l.setText("<html><div style='font: bold 22pt Arial Narrow; color: rgb(115, 84, 73);'>" + words[n]
					+ "</div></html>");
			l.setBorder(null);
			l.setName(Integer.toString(n));
			l.setPreferredSize(new Dimension(150, l.getPreferredSize().height));
			l.addMouseMotionListener(draggingListener);
			l.addMouseListener(clickListener);
			l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			currentWord = l;

			GridBagConstraints c = new GridBagConstraints();

			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.VERTICAL;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			c.insets = new Insets(0, 20, 0, 40);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 0.0;
			c.weighty = 1.0;

			this.add(l, c);

			this.revalidate();
			this.repaint();
		}
	}

	public boolean pause() {
		if (canPause) {
			pause = true;
			timer.stop();
			startPauseTime = new Date().getTime();
			return true;
		} else
			return false;
	}

	public void start() {
		if (pause) {
			timer.start();
			pauseTime += new Date().getTime() - startPauseTime;
			pause = false;
		}
	}

	public int getCorrectPercent() {
		return (int) Math.round(((float) correct / num) * 100.0);
	}

	public long getPauseTime() {
		return pauseTime;
	}

	public int getCatchTime() {
		return catchTime;
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
			int i = Integer.parseInt(l.getName());
			Point currentDragPosition = e.getLocationOnScreen();
			int x = currentDragPosition.x;
			int y = currentDragPosition.y;
			int f = 0;
			for (int k = 0; k < categoriesNum; k++) {
				if ((x > categoryLabels[k].getLocationOnScreen().x)
						&& (x < categoryLabels[k].getLocationOnScreen().x + categoryLabels[k].getWidth())
						&& (y > categoryLabels[k].getLocationOnScreen().y)
						&& (y < categoryLabels[k].getLocationOnScreen().y + categoryLabels[k].getHeight())) {
					f = 1;
					if (categoriesWords[i] == k)
						correct++;
					Container parent = l.getParent();
					parent.remove(l);
					parent.revalidate();
					parent.repaint();
					timer.stop();
					l.removeMouseListener(clickListener);
					currentWord = null;
					showNextWord();
				}
			}
			if (f == 0) {
				GridBagConstraints c = new GridBagConstraints();
				c.anchor = GridBagConstraints.CENTER;
				c.fill = GridBagConstraints.VERTICAL;
				c.gridheight = 1;
				c.gridwidth = 1;
				c.gridx = 0;
				c.gridy = 1;
				c.insets = new Insets(0, 20, 0, 40);
				c.ipadx = 0;
				c.ipady = 0;
				c.weightx = 0.0;
				c.weighty = 1.0;
				l.getParent().add(l, c);
				l.getParent().revalidate();
				l.getParent().repaint();
			}
		}

	}
}
