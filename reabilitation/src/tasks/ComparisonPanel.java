package tasks;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import defaults.ImageLinkDefaults;
import reabilitation.Utils;

public class ComparisonPanel extends JPanel {

	private static final long serialVersionUID = -1614080792424547001L;

	private Random rand;

	private int diff;

	private JLabel[] variants;
	private JLabel one;
	private int picNum;

	// the order of set of pictures to show
	private int[] order;
	// the set of correct answers
	private int[] ones;
	private int counter = 0;

	/** ������� ��� �������� �������� */
	private int num;
	private int correct;

	private Comparison back;

	VariantsMouseListener l;

	private boolean pause = false;
	private boolean canPause = true;

	private long pauseTime = 0;
	private long startPauseTime = 0;
	
	// pictures in folder
	private static final int NUM = 10;

	public ComparisonPanel(int diff, Comparison back, int num) {
		super();
		this.diff = diff;
		this.setDoubleBuffered(true);
		this.setOpaque(false);
		correct = 0;
		this.back = back;
		this.num = num;
		generate();
		showPictures();
	}

	public void fromBegining() {
		pauseTime = 0;
		startPauseTime = 0;
		counter = 0;
		correct = 0;
		showPictures();
	}

	public void generate() {
		order = new int[num];
		ones = new int[num];
		rand = new Random(System.nanoTime());
		/*File list = null;
		java.net.URL URL = Utills.class
				.getResource(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.COMPARISON)
						+ Integer.toString(diff) + "/");
		try {
			list = new File(URL.toURI());
		} catch (URISyntaxException e) {
			list = new File(URL.getPath());
		}
		File dir[] = list.listFiles();*/
		int len = NUM;
		int[] usage = new int[len];
		for (int i = 0; i < len; i++)
			usage[i] = 0;
		int i = 0;
		while (i < num) {
			int dirNum = rand.nextInt(len);
			if (usage[dirNum] == 0) {
				order[i] = dirNum;
				ones[i] = rand.nextInt(9);
				usage[dirNum] = 1;
				i++;
			}
		}
	}

	public void showPictures() {
		this.removeAll();
		l = new VariantsMouseListener();
		int dirNum = order[counter];
		variants = new JLabel[9];
		picNum = ones[counter];
		one = new JLabel();
		ImageIcon icon = Utils.createImageIcon(
				ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.COMPARISON) + Integer.toString(diff) + "/"
						+ Integer.toString(dirNum) + "/" + Integer.toString(picNum) + ".png");
		one.setIcon(icon);
		JPanel p = new JPanel();
		GridLayout experimentLayout = new GridLayout(3, 3, 1, 1);
		p.setLayout(experimentLayout);
		for (int i = 0; i < 9; i++) {
			icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.COMPARISON)
					+ Integer.toString(diff) + "/" + Integer.toString(dirNum) + "/" + Integer.toString(i) + ".png");
			variants[i] = new JLabel();
			variants[i].setIcon(icon);
			variants[i].setName(Integer.toString(i));
			variants[i].addMouseListener(l);
			variants[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			p.add(variants[i]);
		}
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(one);
		this.add(Box.createHorizontalStrut(50));
		this.add(p);
	}

	public long getPauseTime() {
		return pauseTime;
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

	public int getCorrectPercent() {
		return (int) Math.round(((float) correct / num) * 100.0);
	}

	class VariantsMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (pause)
				return;
			JLabel l = (JLabel) e.getSource();
			int i = Integer.parseInt(l.getName());
			if (i == picNum)
				correct++;
			counter++;
			if (counter == num) {
				back.showResults();
			} else
				showPictures();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (pause)
				return;
			JLabel l = (JLabel) e.getSource();
			Dimension d = l.getPreferredSize();
			l.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
			l.setPreferredSize(d);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (pause)
				return;
			JLabel l = (JLabel) e.getSource();
			l.setBorder(null);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (pause)
				return;
			JLabel l = (JLabel) e.getSource();
			Dimension d = l.getPreferredSize();
			l.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.blue));
			l.setPreferredSize(d);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (pause)
				return;
			JLabel l = (JLabel) e.getSource();
			l.setBorder(null);
		}
	}

}
