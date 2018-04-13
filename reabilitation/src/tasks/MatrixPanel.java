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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.Timer;

import customuiandrender.ButtonCustomUI;
import customuiandrender.ProgressBarCustomUI;
import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import reabilitation.utils.Utils;

public class MatrixPanel extends AbstractPanel {
	private static final long serialVersionUID = 2810359745297138538L;
	Matrix w;
	Random rand;

	// pictures in folder
	private static final int NUM = 25;

	private int picturesNum;
	private int additionalPicturesNum;
	private int rows;
	private int cols;
	private int rememberTime;
	private int remindTime;

	private int diff = Matrix.EASY;

	private Color borderColor = new Color(68, 83, 91);

	private String[] pictures;
	private String[] additionalPictures;
	private String[] allPictures;
	private int[] usage;

	private int counter;
	private int showPicturesCounter;

	private JLabel[] labels;
	private int[] pos;
	private int selected[];
	private int remembered[];

	CellsMouseListener listener;

	private Point lastDragPosition;
	private int listNum;

	private int pauseTime = 0;
	private long startPauseTime = 0;

	private Timer timer;
	private JProgressBar bar;
	private int barType;
	private JLabel nextPictureForTimer;
	private JLabel left;

	JPanel table;

	private int correct = 0;

	private boolean pause = false;
	private boolean canPause = true;

	private int limit = 6;

	private int[] rememberedControl;
	private int[] selectedControl;

	public MatrixPanel(int width, int height, int diff, Matrix w) {
		super(width, height);
		this.diff = diff;
		this.w = w;

		switch (diff) {
		case Words.EASY:
			picturesNum = 5;
			rows = 3;
			cols = 3;
			rememberTime = Integer.MAX_VALUE;
			remindTime = Integer.MAX_VALUE;
			additionalPicturesNum = 2;
			break;
		case Words.MIDDLE:
			picturesNum = 12;
			rows = 6;
			cols = 6;
			rememberTime = 60;
			remindTime = 60;
			additionalPicturesNum = 5;
			break;
		case Words.HARD:
			picturesNum = 20;
			rows = 8;
			cols = 8;
			rememberTime = 30;
			remindTime = 30;
			additionalPicturesNum = 5;
			break;
		}

		generate();
		showInfo_1();
	}

	public void fromBegining() {
		if (timer != null && timer.isRunning())
			timer.stop();
		counter = 0;
		showPicturesCounter = 0;
		pauseTime = 0;
		startPauseTime = 0;
		correct = 0;
		showInfo_1();
	}

	public void generate() {
		rand = new Random(System.nanoTime());

		pictures = new String[picturesNum];
		additionalPictures = new String[additionalPicturesNum];

		ArrayList<String> variants = new ArrayList<String>();

		for (int i = 1; i <= NUM; i++) {
			variants.add(Integer.toString(i) + ".png");
		}

		usage = new int[variants.size()];
		for (int i = 0; i < usage.length; i++)
			usage[i] = 0;

		int i = 0;
		while (i < picturesNum) {
			int j = rand.nextInt(usage.length);
			if (usage[j] != 0)
				continue;
			pictures[i] = variants.get(j);
			usage[j] = 1;
			i++;
		}
		i = 0;
		while (i < additionalPicturesNum) {
			int j = rand.nextInt(usage.length);
			if (usage[j] != 0)
				continue;
			additionalPictures[i] = variants.get(j);
			usage[j] = 1;
			i++;
		}
	}

	private void showPictures() {
		JPanel p = new JPanel();
		p.setOpaque(false);
		GridLayout experimentLayout = new GridLayout(rows, cols);
		p.setLayout(experimentLayout);

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
				showInfo_2();
			}
		});

		rand = new Random(System.nanoTime());
		pos = new int[picturesNum];
		int i = 0;
		while (i < picturesNum) {
			int j = rand.nextInt(rows * cols);
			int f = 0;
			for (int t = 0; t < i; t++)
				if (pos[t] == j)
					f = 1;
			if (f != 0)
				continue;
			pos[i] = j;
			i++;
		}
		Arrays.sort(pos);
		int[] additionalPos = new int[additionalPicturesNum];
		i = 0;
		while (i < additionalPicturesNum) {
			int j = rand.nextInt(rows * cols);
			int f = 0;
			for (int t = 0; t < i; t++)
				if (additionalPos[t] == j)
					f = 1;
			for (int t = 0; t < pos.length; t++)
				if (pos[t] == j)
					f = 1;
			if (f != 0)
				continue;
			additionalPos[i] = j;
			i++;
		}
		Arrays.sort(additionalPos);

		labels = new JLabel[rows * cols];
		allPictures = new String[picturesNum + additionalPicturesNum];
		selected = new int[rows * cols];
		remembered = new int[picturesNum + additionalPicturesNum];
		selectedControl = new int[rows * cols];
		rememberedControl = new int[picturesNum + additionalPicturesNum];

		i = 0;
		int q = 0;
		int d = 0;
		for (int k = 0; k < rows * cols; k++) {
			labels[k] = new JLabel();
			labels[k].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, borderColor));
			if (k % cols == 0)
				labels[k].setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, borderColor));
			if (k / cols == 0)
				labels[k].setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, borderColor));
			if ((k % cols == 0) && (k / cols == 0))
				labels[k].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
			if (k == pos[i]) {
				labels[k].setIcon(
						Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MATRIX)
								+ Integer.toString(diff) + "/" + pictures[i]));
				selectedControl[k] = 1;
				allPictures[d] = pictures[i];
				rememberedControl[d] = 1;
				d++;
				if (i < pictures.length - 1)
					i++;
			} else {
				if (k == additionalPos[q]) {
					labels[k].setIcon(null);
					allPictures[d] = additionalPictures[q];
					rememberedControl[d] = 0;
					d++;
					if (q < additionalPictures.length - 1)
						q++;

				} else {
					selectedControl[k] = 0;
				}
			}
			labels[k].setHorizontalAlignment(JLabel.CENTER);
			p.add(labels[k]);
			selected[k] = 0;
			labels[k].setName(Integer.toString(k));
		}

		this.removeAll();
		this.setLayout(new GridBagLayout());

		JLabel task = new JLabel();
		String t = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("matrix_task_1") + "</div></html>";
		task.setText(t);

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
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
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 20, 0, 10);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 2.0;
		c.weighty = 1.0;

		this.add(p, c);

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 10);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(start, c);

		if (diff != Matrix.EASY) {

			JProgressBar progressBar = new JProgressBar();
			progressBar.setStringPainted(false);
			progressBar.setMinimum(0);
			progressBar.setMaximum(rememberTime);
			progressBar.setValue(0);
			progressBar.setUI(new ProgressBarCustomUI());
			progressBar.setBorder(null);
			bar = progressBar;

			left = new JLabel();
			ImageIcon icon = Utils.createImageIcon(
					"resources/image/timer/" + Integer.toString(rememberTime - bar.getValue()) + ".png");
			left.setIcon(icon);

			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridx = 0;
			c.gridy = 2;
			c.insets = new Insets(30, 20, 0, 20);
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

			bar = progressBar;
			barType = 0;

			timer = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					if (barType == 0) {
						if (bar.getValue() < rememberTime) {
							bar.setValue(bar.getValue() + 1);
							ImageIcon icon = Utils.createImageIcon("resources/image/timer/"
									+ Integer.toString(rememberTime - bar.getValue()) + ".png");
							left.setIcon(icon);
						} else {
							timer.stop();
							showInfo_2();
						}
					}
					if (barType == 1) {
						if (bar.getValue() < remindTime) {
							bar.setValue(bar.getValue() + 1);
							ImageIcon icon = Utils.createImageIcon(
									"resources/image/timer/" + Integer.toString(remindTime - bar.getValue()) + ".png");
							left.setIcon(icon);
						} else {
							timer.stop();
							showNext(nextPictureForTimer);
						}
					}
				}
			});
			timer.start();
		}

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
				+ InterfaceTextDefaults.getInstance().getDefault("matrix_info_1") + "</div></html>");
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
				showPictures();
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

	private void showInfo_2() {

		canPause = false;

		long t = new Date().getTime();

		JTextPane t1 = new JTextPane();
		t1.setEditable(false);
		t1.setContentType("text/html;charset=utf-8");
		t1.setText("<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("matrix_info_2") + "</div></html>");
		t1.setOpaque(false);
		t1.setPreferredSize(new Dimension((int) (this.getPreferredSize().getWidth() * 0.85),
				40 + Utils.calculateTextHeight(t1.getText(), (int) (this.getPreferredSize().getWidth() * 0.85), t1)));

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("continue"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(200, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPicturesCounter = 0;
				pauseTime += new Date().getTime() - t;
				canPause = true;
				showNextPicture();
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

	private void showNextPicture() {
		if (showPicturesCounter >= picturesNum + additionalPicturesNum)
			showCellsSelection();
		else {
			JLabel picture = labels[showPicturesCounter];

			picture.setIcon(Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MATRIX)
					+ Integer.toString(diff) + "/" + allPictures[showPicturesCounter]));
			picture.setBorder(null);

			JButton old = new JButton(InterfaceTextDefaults.getInstance().getDefault("old_picture"));
			old.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
			old.setBorder(null);
			old.setOpaque(false);
			old.setPreferredSize(new Dimension(230, 35));
			old.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			old.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (pause)
						return;
					if (showPicturesCounter < picturesNum + additionalPicturesNum) {
						remembered[showPicturesCounter] = 1;
					}
					showNext(picture);
				}
			});

			JButton neww = new JButton(InterfaceTextDefaults.getInstance().getDefault("new_picture"));
			neww.setUI(new ButtonCustomUI(new Color(239, 83, 80)));
			neww.setBorder(null);
			neww.setOpaque(false);
			neww.setPreferredSize(new Dimension(230, 35));
			neww.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			neww.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (pause)
						return;
					if (showPicturesCounter < picturesNum + additionalPicturesNum) {
						remembered[showPicturesCounter] = 0;
					}
					showNext(picture);
				}
			});

			this.removeAll();
			this.setLayout(new GridBagLayout());

			JLabel task = new JLabel();
			String t = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
					+ InterfaceTextDefaults.getInstance().getDefault("matrix_task_2") + "</div></html>";
			task.setText(t);

			GridBagConstraints c = new GridBagConstraints();

			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(0, 0, 20, 0);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 0.0;
			c.weighty = 0.0;

			this.add(task, c);

			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridx = 0;
			c.gridy = 1;
			c.insets = new Insets(40, 20, 0, 0);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 0.0;
			c.weighty = 0.0;

			this.add(picture, c);

			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 2;
			c.insets = new Insets(40, 150, 0, 0);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 0.0;
			c.weighty = 0.0;

			this.add(old, c);

			c.anchor = GridBagConstraints.EAST;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridx = 1;
			c.gridy = 2;
			c.insets = new Insets(40, 20, 0, 150);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 0.0;
			c.weighty = 0.0;

			this.add(neww, c);

			if (diff != Matrix.EASY) {
				JProgressBar progressBar = new JProgressBar();
				progressBar.setStringPainted(false);
				progressBar.setMinimum(0);
				progressBar.setMaximum(remindTime);
				progressBar.setValue(0);
				progressBar.setUI(new ProgressBarCustomUI());
				progressBar.setBorder(null);
				bar = progressBar;

				left = new JLabel();
				ImageIcon icon = Utils.createImageIcon(
						"resources/image/timer/" + Integer.toString(remindTime - bar.getValue()) + ".png");
				left.setIcon(icon);

				c.anchor = GridBagConstraints.WEST;
				c.fill = GridBagConstraints.NONE;
				c.gridheight = 1;
				c.gridwidth = GridBagConstraints.REMAINDER;
				c.gridx = 0;
				c.gridy = 3;
				c.insets = new Insets(100, 20, 0, 20);
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
				c.gridy = 4;
				c.insets = new Insets(5, 20, 0, 20);
				c.ipadx = 0;
				c.ipady = 0;
				c.weightx = 1.0;
				c.weighty = 0.0;

				this.add(progressBar, c);

				bar = progressBar;
				barType = 1;
				nextPictureForTimer = picture;

				timer.start();
			}

			this.revalidate();
			this.repaint();
		}
	}

	private void showNext(JLabel picture) {
		if (showPicturesCounter >= picturesNum + additionalPicturesNum - 1)
			showCellsSelection();
		else {
			showPicturesCounter++;
			picture.setIcon(Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MATRIX)
					+ Integer.toString(diff) + "/" + allPictures[showPicturesCounter]));
			picture.setBorder(null);

			picture.revalidate();
			picture.repaint();

			if (diff != Matrix.EASY) {
				bar.setValue(0);
				nextPictureForTimer = picture;
				ImageIcon icon = Utils.createImageIcon(
						"resources/image/timer/" + Integer.toString(remindTime - bar.getValue()) + ".png");
				left.setIcon(icon);
				timer.start();
			}

			this.revalidate();
			this.repaint();
		}
	}

	private void showCellsSelection() {
		if (timer != null && timer.isRunning())
			timer.stop();
		JPanel p = new JPanel();
		p.setOpaque(false);
		GridLayout experimentLayout = new GridLayout(rows, cols);
		p.setLayout(experimentLayout);

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
				if (counter < picturesNum)
					return;
				else
					showDragPictures();
			}
		});

		listener = new CellsMouseListener();
		for (int k = 0; k < rows * cols; k++) {
			labels[k].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, borderColor));
			if (k % cols == 0)
				labels[k].setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, borderColor));
			if (k / cols == 0)
				labels[k].setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, borderColor));
			if ((k % cols == 0) && (k / cols == 0))
				labels[k].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
			labels[k].setIcon(null);
			labels[k].addMouseListener(listener);
			labels[k].setOpaque(true);
			labels[k].setBackground(Color.WHITE);
			labels[k].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			p.add(labels[k]);
		}

		counter = 0;

		this.removeAll();
		this.setLayout(new GridBagLayout());

		JLabel task = new JLabel();
		String t = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("matrix_task_3") + "</div></html>";
		task.setText(t);

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
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
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 20, 0, 40);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		this.add(p, c);

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 20);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(start, c);

		this.revalidate();
		this.repaint();
	}

	private void showDragPictures() {

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("finalize"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(150, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.showResults();
			}
		});

		table = new JPanel();
		table.setOpaque(false);
		GridLayout experimentLayout = new GridLayout(rows, cols);
		table.setLayout(experimentLayout);

		this.removeAll();
		this.setLayout(new GridBagLayout());

		JLabel task = new JLabel();
		String t = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("matrix_task_4") + "</div></html>";
		task.setText(t);

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(20, 0, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(task, c);

		JLabel[] list = new JLabel[picturesNum + additionalPicturesNum];

		if (diff == Matrix.HARD)
			limit = 8;

		listNum = 0;
		for (int i = 0; i < picturesNum + additionalPicturesNum; i++) {
			if (remembered[i] == 1) {
				JLabel l = new JLabel();
				l.setIcon(Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MATRIX)
						+ Integer.toString(diff) + "/" + allPictures[i]));
				l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

				l.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						if (pause)
							return;
						lastDragPosition = e.getLocationOnScreen();
					}
				});

				c.anchor = GridBagConstraints.CENTER;
				c.fill = GridBagConstraints.VERTICAL;
				c.gridheight = 1;
				c.gridwidth = 1;
				c.gridx = listNum / limit;
				c.gridy = listNum % limit + 1;
				listNum++;
				c.insets = new Insets(0, 30, 0, 0);
				c.ipadx = 0;
				c.ipady = 0;
				c.weightx = 0.0;
				c.weighty = 1.0;

				this.add(l, c);

				list[i] = l;
			}
		}
		int k = 0;
		for (int i = 0; i < rows * cols; i++) {
			// TODO
			if (i == pos[k]) {
				labels[i].setBackground(new Color(149, 179, 215));
				if (k < pos.length - 1)
					k++;
			} else
				labels[i].setBackground(Color.WHITE);
			labels[i].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, borderColor));
			if (i % cols == 0)
				labels[i].setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, borderColor));
			if (i / cols == 0)
				labels[i].setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, borderColor));
			if ((i % cols == 0) && (i / cols == 0))
				labels[i].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
			labels[i].removeMouseListener(listener);
			labels[i].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			table.add(labels[i]);
		}

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 1;
		c.gridx = 100;
		c.gridy = 1;
		c.insets = new Insets(0, 20, 0, 40);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 3.0;
		c.weighty = 1.0;

		this.add(table, c);

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 101;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 20;
		c.ipady = 10;
		c.weightx = 0.0;
		c.weighty = 1.0;

		this.add(start, c);

		this.revalidate();
		this.repaint();

		for (int i = 0; i < picturesNum + additionalPicturesNum; i++)

		{
			if (remembered[i] == 1) {

				JLabel l = list[i];

				l.addMouseMotionListener(new MouseMotionAdapter() {
					@Override
					public void mouseDragged(MouseEvent e) {

						if (pause)
							return;

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
				});

				l.addMouseListener(new MouseAdapter() {
					Container parent;

					@Override
					public void mouseReleased(MouseEvent e) {
						if (pause)
							return;
						Point currentDragPosition = e.getLocationOnScreen();
						int x = currentDragPosition.x;
						int y = currentDragPosition.y;

						int f = 0;
						for (int k = 0; k < rows * cols; k++) {
							// TODO
							boolean fl = false;
							for (int i = 0; i < pos.length; i++) {
								if (pos[i] > k)
									break;
								if (pos[i] == k)
									fl = true;
							}
							if (fl) {
								if ((x > labels[k].getLocationOnScreen().x)
										&& (x < labels[k].getLocationOnScreen().x + labels[k].getWidth())
										&& (y > labels[k].getLocationOnScreen().y)
										&& (y < labels[k].getLocationOnScreen().y + labels[k].getHeight())
										&& (labels[k].getIcon() == null)) {
									f = 1;
									JLabel tmp = labels[k];
									tmp.setIcon(l.getIcon());
									tmp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
									tmp.repaint();
									tmp.addMouseListener(new MouseAdapter() {
										@Override
										public void mouseClicked(MouseEvent e) {
											System.out.println("!");
											tmp.setIcon(null);
											tmp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
											c.anchor = GridBagConstraints.CENTER;
											c.fill = GridBagConstraints.VERTICAL;
											c.gridheight = 1;
											c.gridwidth = 1;
											c.gridx = listNum / limit;
											c.gridy = listNum % limit + 1;
											listNum++;
											c.insets = new Insets(0, 30, 0, 0);
											c.ipadx = 0;
											c.ipady = 0;
											c.weightx = 0.0;
											c.weighty = 1.0;
											parent.add(l, c);
											parent.revalidate();
											parent.repaint();

											addAgain();
										}
									});
									parent.remove(l);
									parent.revalidate();
									parent.repaint();
								}
							}
						}
						if (f == 0) {
							c.anchor = GridBagConstraints.CENTER;
							c.fill = GridBagConstraints.VERTICAL;
							c.gridheight = 1;
							c.gridwidth = 1;
							c.gridx = listNum / limit;
							c.gridy = listNum % limit + 1;
							listNum++;
							c.insets = new Insets(0, 30, 0, 0);
							c.ipadx = 0;
							c.ipady = 0;
							c.weightx = 0.0;
							c.weighty = 1.0;
							parent.add(l, c);
							parent.revalidate();
							parent.repaint();
							addAgain();
						}
					}

					@Override
					public void mousePressed(MouseEvent e) {
						if (pause)
							return;
						parent = l.getParent();
					}
				});
			}
		}

	}

	// ��������� ��� �������������� ������ (�����, ����� ����� ������
	// ����������� � ������ ��� ��������������, ��� �����������
	// "�� ������ �����")
	private void addAgain() {

		this.remove(table);

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 1;
		c.gridx = 100;
		c.gridy = 1;
		c.insets = new Insets(0, 20, 0, 40);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 3.0;
		c.weighty = 1.0;

		this.add(table, c);

		this.revalidate();
		this.repaint();
	}

	public boolean pause() {
		if (canPause) {
			pause = true;
			if (diff != Matrix.EASY)
				timer.stop();
			startPauseTime = new Date().getTime();
			return true;
		} else
			return false;
	}

	public void start() {
		if (pause) {
			if (diff != Matrix.EASY)
				timer.start();
			pauseTime += new Date().getTime() - startPauseTime;
			pause = false;
		}
	}

	public int getPauseTime() {
		return pauseTime;
	}

	public int getCorrectPercent() {
		for (int i = 0; i < picturesNum + additionalPicturesNum; i++)
			if ((rememberedControl[i] == remembered[i]) && (rememberedControl[i] == 1))
				correct++;
		for (int i = 0; i < rows * cols; i++)
			if ((selectedControl[i] == selected[i]) && (selectedControl[i] == 1))
				correct++;
		int i = 0;
		for (int k = 0; k < rows * cols; k++) {
			if (k == pos[i]) {
				String s = labels[k].getIcon().toString();
				s = s.substring(s.lastIndexOf('/') + 1, s.length());
				System.out.println(s);
				System.out.println(pictures[i]);
				if (s.equals(pictures[i])) {
					System.out.println("!");
					correct++;
				}
				if (i < pictures.length - 1)
					i++;
			}
		}

		return (int) Math.round(((float) correct / (picturesNum * 3)) * 100.0);
	}

	class CellsMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (pause)
				return;
			JLabel l = (JLabel) e.getSource();
			int i = Integer.parseInt(l.getName());
			if (selected[i] == 0) {
				if (counter < picturesNum) {
					l.setBackground(new Color(149, 179, 215));
					l.repaint();
					selected[i] = 1;
					counter++;
				}
			} else {
				if (counter > 0) {
					l.setBackground(Color.WHITE);
					l.repaint();
					selected[i] = 0;
					counter--;
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
