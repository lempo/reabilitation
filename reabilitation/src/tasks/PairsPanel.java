package tasks;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import defaults.ImageLinkDefaults;
import reabilitation.utils.Utils;

public class PairsPanel extends JPanel {

	private static final long serialVersionUID = -3246995457594248989L;

	private Pairs w;

	private int[][] flags;
	private JLabel[] cards;
	private int[] cardsF;

	private Random rand;

	private int diff;

	private JLabel opened;
	private JLabel opened1;
	private boolean pause = false;

	private int correct = 0;
	private int attempts = 0;

	private boolean pause1 = false;
	private boolean canPause = true;

	private long pauseTime = 0;
	private long startPauseTime = 0;
	
	private Timer timer;

	public PairsPanel(int diff, Pairs w) {
		super();
		this.diff = diff;
		this.w = w;
		this.setDoubleBuffered(true);
		this.setOpaque(false);
		generate();
	}

	public void fromBegining() {
		pauseTime = 0;
		startPauseTime = 0;
		correct = 0;
		attempts = 0;
		ImageIcon icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.PAIRS)
				+ Integer.toString(diff) + "/" + "empty.png");
		for (int i = 0; i < diff * diff; i++) {
			cardsF[i] = 0;
			cards[i].setIcon(icon);
			cards[i].updateUI();
			cards[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}		
	}

	public void generate() {
		rand = new Random(System.nanoTime());
		flags = new int[(diff * diff) / 2][2];
		for (int i = 0; i < (diff * diff) / 2; i++) {
			flags[i][0] = 0;
			flags[i][1] = 0;
		}
		cards = new JLabel[diff * diff];
		cardsF = new int[diff * diff];
		ImageIcon icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.PAIRS)
				+ Integer.toString(diff) + "/" + "empty.png");
		CardsMouseListener l = new CardsMouseListener();
		int i = 0;
		GridLayout experimentLayout = new GridLayout(diff, diff);
		this.setLayout(experimentLayout);
		while (i < diff * diff) {
			int j = rand.nextInt((diff * diff) / 2);
			if (flags[j][0] == 0)
				flags[j][0] = 1;
			else if (flags[j][1] == 0)
				flags[j][1] = 1;
			else
				continue;
			cards[i] = new JLabel();
			cards[i].setIcon(icon);
			cards[i].setName(Integer.toString(i) + " " + Integer.toString(j));
			cards[i].addMouseListener(l);
			cards[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			this.add(cards[i]);
			cardsF[i] = 0;
			i++;
		}
	}

	public int getCorrectPercent() {
		return (int) Math.round(((float) correct / attempts) * 100.0);
	}

	class CardsMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (pause || pause1)
				return;
			JLabel l = (JLabel) e.getSource();
			String[] parts = l.getName().split(" ");
			int i = Integer.parseInt(parts[0]);

			ImageIcon icon;

			if (cardsF[i] == 0) {

				icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.PAIRS)
						+ Integer.toString(diff) + "/" + parts[1] + ".png");

				int f = -1;
				for (int j = 0; j < cardsF.length; j++)
					if ((cardsF[j] == 1) && (j != i))
						f = j;

				if (f != -1) {
					if (cards[f].getName().split(" ")[1].equals(parts[1])) {
						correct++;
						attempts++;
						cardsF[i] = 2;
						cardsF[f] = 2;
						l.setIcon(icon);
						l.updateUI();
						l.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						int k = 0;
						for (int j = 0; j < cardsF.length; j++)
							if (cardsF[j] != 2)
								k = 1;
						if (k == 0)
							w.showResults();
					} else {
						attempts++;
						opened1 = l;
						l.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						l.setIcon(icon);
						l.updateUI();
						pause = true;
						timer = new Timer();
						timer.schedule(new MyTimerTask(), 1000);
					}
				} else {
					cardsF[i] = 1;
					icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.PAIRS)
							+ Integer.toString(diff) + "/" + parts[1] + ".png");
					l.setIcon(icon);
					l.updateUI();
					opened = l;
					l.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	public long getPauseTime() {
		return pauseTime;
	}

	public boolean pause() {
		if (canPause) {
			pause1 = true;
			startPauseTime = new Date().getTime();
			return true;
		} else
			return false;
	}

	public void start() {
		if (pause1) {
			pauseTime += new Date().getTime() - startPauseTime;
			pause1 = false;
		}
	}

	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			ImageIcon icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.PAIRS)
					+ Integer.toString(diff) + "/" + "empty.png");

			String[] parts = opened1.getName().split(" ");
			int i = Integer.parseInt(parts[0]);
			cardsF[i] = 0;
			parts = opened.getName().split(" ");
			i = Integer.parseInt(parts[0]);
			cardsF[i] = 0;

			opened1.setIcon(icon);
			opened1.updateUI();
			opened.setIcon(icon);
			opened.updateUI();

			opened1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			opened.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			pause = false;
		}
	}
}
