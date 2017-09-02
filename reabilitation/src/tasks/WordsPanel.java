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
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import customcomponent.CustomDialog;
import customuiandrender.ButtonCustomUI;
import customuiandrender.LabelCustomUI;
import customuiandrender.ProgressBarCustomUI;
import customuiandrender.ScrollBarCustomUI;
import defaults.InterfaceTextDefaults;
import defaults.TextLinkDefaults;
import reabilitation.Utils;

public class WordsPanel extends JPanel {
	private static final long serialVersionUID = 2810359745297138538L;
	Words w;
	Random rand;

	private int categoriesNum;
	private int wordsNum;
	private int rows;
	private int cols;
	private int rememberTime;
	private int remindTime;

	private int diff = Words.EASY;

	private Color borderColor = new Color(68, 83, 91);

	private String[] words;
	private String[] additionalWords;
	private String[] allWords;
	private String[][] variants;
	private int[][] usage;

	private int counter;
	private int showWordsCounter;

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
	private JLabel nextWordForTimer;
	private JLabel left;

	JPanel table;

	private int correct = 0;

	private boolean pause = false;
	private boolean canPause = true;

	public WordsPanel(int width, int height, int diff, Words w) {
		super();
		this.diff = diff;
		this.w = w;

		switch (diff) {
		case Words.EASY:
			categoriesNum = 1;
			wordsNum = 4;
			rows = 4;
			cols = 2;
			rememberTime = Integer.MAX_VALUE;
			remindTime = Integer.MAX_VALUE;
			break;
		case Words.MIDDLE:
			categoriesNum = 2;
			wordsNum = 10;
			rows = 5;
			cols = 4;
			rememberTime = 6;
			remindTime = 6;
			break;
		case Words.HARD:
			categoriesNum = 3;
			wordsNum = 16;
			rows = 8;
			cols = 4;
			rememberTime = 4;
			remindTime = 4;
			break;
		}

		this.setPreferredSize(new Dimension(width, height));
		this.setDoubleBuffered(true);
		this.setOpaque(false);
		generate();
		showInfo_1();
	}

	public void fromBegining() {
		if (timer != null && timer.isRunning())
			timer.stop();
		counter = 0;
		showWordsCounter = 0;
		pauseTime = 0;
		startPauseTime = 0;
		correct = 0;
		showInfo_1();
	}

	public void generate() {
		rand = new Random(System.nanoTime());

		words = new String[wordsNum];
		additionalWords = new String[wordsNum];

		variants = new String[categoriesNum][];
		usage = new int[categoriesNum][];

		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.WORDS));

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
		while (i < wordsNum) {
			int j = rand.nextInt(categoriesNum);
			int t = rand.nextInt(variants[j].length);
			if (usage[j][t] != 0)
				continue;
			words[i] = new String(variants[j][t]);
			usage[j][t] = 1;
			i++;
			System.out.println(variants[j][t]);
		}
		i = 0;
		while (i < wordsNum) {
			int j = rand.nextInt(categoriesNum);
			int t = rand.nextInt(variants[j].length);
			if (usage[j][t] != 0)
				continue;
			additionalWords[i] = new String(variants[j][t]);
			usage[j][t] = 1;
			i++;
			// System.out.println(variants[j][t]);
		}
	}

	private void showWords() {
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
				showInfo_2();
			}
		});

		rand = new Random(System.nanoTime());
		pos = new int[wordsNum];
		int i = 0;
		while (i < wordsNum) {
			int j = rand.nextInt(wordsNum * 2);
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

		labels = new JLabel[wordsNum * 2];
		allWords = new String[wordsNum * 2];
		selected = new int[wordsNum * 2];
		remembered = new int[wordsNum * 2];

		i = 0;
		int q = 0;
		for (int k = 0; k < wordsNum * 2; k++) {
			labels[k] = new JLabel();
			labels[k].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, borderColor));
			if (k % cols == 0)
				labels[k].setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, borderColor));
			if (k / cols == 0)
				labels[k].setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, borderColor));
			if ((k % cols == 0) && (k / cols == 0))
				labels[k].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
			if (k == pos[i]) {
				labels[k].setText(
						"<html><div style='font: bold 18pt Arial Narrow; color: rgb(68, 83, 91); text-align: center;'>"
								+ words[i] + "</div></html>");
				allWords[k] = words[i];
				if (i < words.length - 1)
					i++;
			} else {
				labels[k].setText("<html><div style='font: bold 18pt Arial Narrow; color: white; text-align: center;'>"
						+ additionalWords[q] + "</div></html>");
				allWords[k] = additionalWords[q];
				if (q < additionalWords.length - 1)
					q++;
			}
			labels[k].setHorizontalAlignment(JLabel.CENTER);
			p.add(labels[k]);
			selected[k] = 0;
			remembered[k] = 0;
			labels[k].setName(Integer.toString(k));
		}

		this.removeAll();
		this.setLayout(new GridBagLayout());

		JLabel task = new JLabel();
		String t = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("words_task_1") + "</div></html>";
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

		if (diff != Words.EASY) {

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
							showNext(nextWordForTimer);
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
				+ InterfaceTextDefaults.getInstance().getDefault("words_info_1") + "</div></html>");
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
				showWords();
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
				+ InterfaceTextDefaults.getInstance().getDefault("words_info_2") + "</div></html>");
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
				showWordsCounter = 0;
				pauseTime += new Date().getTime() - t;
				canPause = true;
				showNextWord();
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

	private void showNextWord() {
		if (showWordsCounter >= wordsNum * 2)
			showCellsSelection();
		else {
			JLabel word = labels[showWordsCounter];

			word.setText("<html><div style='font: bold 22pt Arial Narrow; color: rgb(115, 84, 73);'>"
					+ allWords[showWordsCounter] + "</div></html>");
			word.setBorder(null);

			JButton old = new JButton(InterfaceTextDefaults.getInstance().getDefault("old_word"));
			old.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
			old.setBorder(null);
			old.setOpaque(false);
			old.setPreferredSize(new Dimension(200, 35));
			old.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			old.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (pause)
						return;
					if (showWordsCounter < wordsNum * 2) {
						remembered[showWordsCounter] = 1;
					}
					showNext(word);
				}
			});

			JButton neww = new JButton(InterfaceTextDefaults.getInstance().getDefault("new_word"));
			neww.setUI(new ButtonCustomUI(new Color(239, 83, 80)));
			neww.setBorder(null);
			neww.setOpaque(false);
			neww.setPreferredSize(new Dimension(200, 35));
			neww.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			neww.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (pause)
						return;
					showNext(word);
				}
			});

			this.removeAll();
			this.setLayout(new GridBagLayout());

			JLabel task = new JLabel();
			String t = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
					+ InterfaceTextDefaults.getInstance().getDefault("words_task_2") + "</div></html>";
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

			this.add(word, c);

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

			if (diff != Words.EASY) {
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
				nextWordForTimer = word;

				timer.start();
			}

			this.revalidate();
			this.repaint();
		}
	}

	private void showNext(JLabel word) {

		if (showWordsCounter >= wordsNum * 2 - 1)
			showCellsSelection();
		else {
			showWordsCounter++;
			word.setText("<html><div style='font: bold 22pt Arial Narrow; color: rgb(115, 84, 73);'>"
					+ allWords[showWordsCounter] + "</div></html>");
			word.setBorder(null);

			word.revalidate();
			word.repaint();

			if (diff != Words.EASY) {
				bar.setValue(0);
				nextWordForTimer = word;
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
				if (counter < wordsNum)
					return;
				else
					showDragWords();
			}
		});

		listener = new CellsMouseListener();
		for (int k = 0; k < wordsNum * 2; k++) {
			labels[k].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, borderColor));
			if (k % cols == 0)
				labels[k].setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, borderColor));
			if (k / cols == 0)
				labels[k].setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, borderColor));
			if ((k % cols == 0) && (k / cols == 0))
				labels[k].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
			labels[k].setText("");
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
				+ InterfaceTextDefaults.getInstance().getDefault("words_task_3") + "</div></html>";
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

	private void showDragWords() {

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("finalize"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(100, 35));
		start.setMinimumSize(new Dimension(100, 35));
		start.setMaximumSize(new Dimension(100, 35));
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
				+ InterfaceTextDefaults.getInstance().getDefault("words_task_4") + "</div></html>";
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

		JLabel[] list = new JLabel[wordsNum * 2];

		listNum = 1;
		int k = 0;
		for (int i = 0; i < wordsNum * 2; i++) {
			if (remembered[i] == 1) {
				JLabel l = new JLabel();
				l.setText(
						"<html><div style='font: bold 18pt Arial Narrow; color: rgb(115, 84, 73); text-align: center;'>"
								+ allWords[i] + "</div></html>");
				l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

				l.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						if (pause)
							return;
						lastDragPosition = e.getLocationOnScreen();
					}
				});

				c.anchor = GridBagConstraints.CENTER;
				c.fill = GridBagConstraints.BOTH;
				c.gridheight = 1;
				c.gridwidth = 1;
				c.gridx = 0;
				c.gridy = listNum;
				listNum++;
				c.insets = new Insets(0, 30, 0, 0);
				c.ipadx = 0;
				c.ipady = 0;
				c.weightx = 1.0;
				c.weighty = 1.0;

				this.add(l, c);
				list[i] = l;
			}

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

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 20, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 5.0;
		c.weighty = 1.0;

		this.add(table, c);

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(start, c);

		this.revalidate();
		this.repaint();

		table.setPreferredSize(new Dimension((int) (this.getPreferredSize().getWidth() * 0.7),
				(int) (table.getPreferredSize().getHeight())));

		for (int i = 0; i < wordsNum * 2; i++)

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
						for (int k = 0; k < wordsNum * 2; k++) {
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
										&& (labels[k].getText().equals(""))) {
									f = 1;
									JLabel tmp = labels[k];
									String[] parts = l.getText().split("[<>]");
									tmp.setText(
											"<html><div style='font: bold 18pt Arial Narrow; color: rgb(115, 84, 73); text-align: center;'>"
													+ parts[4] + "</div></html>");
									tmp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
									tmp.repaint();
									tmp.addMouseListener(new MouseAdapter() {
										@Override
										public void mouseClicked(MouseEvent e) {
											c.anchor = GridBagConstraints.CENTER;
											c.fill = GridBagConstraints.BOTH;
											c.gridheight = 1;
											c.gridwidth = 1;
											c.gridx = 0;
											c.gridy = listNum;
											listNum++;
											c.insets = new Insets(0, 30, 0, 0);
											c.ipadx = 0;
											c.ipady = 0;
											c.weightx = 1.0;
											c.weighty = 1.0;

											tmp.setText("");
											tmp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
							c.fill = GridBagConstraints.BOTH;
							c.gridheight = 1;
							c.gridwidth = 1;
							c.gridx = 0;
							c.gridy = listNum;
							listNum++;
							c.insets = new Insets(0, 30, 0, 0);
							c.ipadx = 0;
							c.ipady = 0;
							c.weightx = 1.0;
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
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 20, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 5.0;
		c.weighty = 1.0;

		this.add(table, c);

		this.revalidate();
		this.repaint();
	}

	// TODO
	public void showResults() {
		w.setDontShowBreakingDialog(true);
		canPause = false;
		this.removeAll();

		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 22pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("results") + "</div></html>";
		heading.setText(t);

		JLabel yourResult = new JLabel();
		t = "<html><div style='font: bold 21pt Arial Narrow; color: rgb(68, 83, 91);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("your_result") + "</div></html>";
		yourResult.setText(t);

		int k = 0;
		for (int i = 0; i < wordsNum * 2; i++) {
			JLabel currentLabel = labels[i];
			for (MouseListener al : currentLabel.getMouseListeners()) {
				currentLabel.removeMouseListener(al);
			}
			currentLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			currentLabel.setBorder(null);
			currentLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			currentLabel.setBackground(Color.WHITE);

			if (i == pos[k]) {
				String[] parts = currentLabel.getText().split("[<>]");
				if (parts.length > 4)
					if (parts[4].equals(allWords[pos[k]]))
						currentLabel.setUI(new LabelCustomUI(new Color(38, 166, 154), 3, false, true));
					else
						currentLabel.setUI(new LabelCustomUI(new Color(239, 83, 80), 3, false, true));
				if (currentLabel.getText().equals(""))
					currentLabel.setUI(new LabelCustomUI(new Color(239, 83, 80), 3, false, true));
				if (k < pos.length - 1)
					k++;
			} else
				currentLabel.setUI(new LabelCustomUI(new Color(176, 190, 197), 1, false, true));
			currentLabel.setHorizontalAlignment(SwingConstants.LEFT);
			// TODO UI
		}
		table.setPreferredSize(new Dimension((int) Math.round(this.getPreferredSize().getWidth() * 0.8), rows * 60));

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 25, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(heading, c);

		JPanel res = new JPanel();
		res.setLayout(new GridBagLayout());
		res.setOpaque(false);

		JScrollPane scroll = new JScrollPane(res);
		scroll.setPreferredSize(new Dimension((int) Math.round(this.getPreferredSize().getWidth() * 0.95),
				(int) Math.round(this.getPreferredSize().getHeight() * 0.8)));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);

		c.insets = new Insets(0, 5, 20, 0);

		res.add(yourResult, c);

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		res.add(table, c);

		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setBackground(new Color(176, 190, 197));
		sep.setForeground(new Color(176, 190, 197));
		sep.setPreferredSize(new Dimension((int) Math.round(scroll.getPreferredSize().getWidth() * 0.9), 2));

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 5, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		res.add(sep, c);

		JLabel answers = new JLabel();
		t = "<html><div style='font: bold 22pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("answers") + "</div></html>";
		answers.setText(t);

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 5, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		res.add(answers, c);

		JLabel wordsL = new JLabel();
		t = "<html><div style='font: bold 21pt Arial Narrow; color: rgb(68, 83, 91);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("words") + "</div></html>";
		wordsL.setText(t);

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(0, 5, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		res.add(wordsL, c);

		JPanel wordsList = new JPanel();
		wordsList.setOpaque(false);
		wordsList.setLayout(new GridLayout(wordsNum, 1));
		for (int i = 0; i < wordsNum; i++) {
			JLabel w = new JLabel();
			w.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			w.setText("<html><div style='font: bold 18pt Arial Narrow; color: rgb(115, 84, 73); padding: 10px'>"
					+ words[i].toUpperCase() + "</div></html>");
			w.setUI(new LabelCustomUI(new Color(176, 190, 197), 1, false, false));
			w.setHorizontalAlignment(SwingConstants.LEFT);
			wordsList.add(w);
		}

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(0, 0, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		res.add(wordsList, c);

		JLabel placement = new JLabel();
		t = "<html><div style='font: bold 21pt Arial Narrow; color: rgb(68, 83, 91);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("placement") + "</div></html>";
		placement.setText(t);

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 6;
		c.insets = new Insets(0, 5, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		res.add(placement, c);

		JPanel placementT = new JPanel();
		placementT.setOpaque(false);
		placementT.setLayout(new GridLayout(rows, cols));
		k = 0;
		for (int i = 0; i < wordsNum * 2; i++) {
			JLabel w = new JLabel();
			w.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			if (i == pos[k]) {
				w.setUI(new LabelCustomUI(new Color(176, 190, 197), 1, true, false));
				if (k < pos.length - 1)
					k++;
			} else
				w.setUI(new LabelCustomUI(new Color(176, 190, 197), 1, false, false));

			placementT.add(w);
		}
		placementT
				.setPreferredSize(new Dimension((int) Math.round(this.getPreferredSize().getWidth() * 0.8), rows * 60));

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 7;
		c.insets = new Insets(0, 0, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		res.add(placementT, c);

		JLabel wordsPlacement = new JLabel();
		t = "<html><div style='font: bold 21pt Arial Narrow; color: rgb(68, 83, 91);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("words_placement") + "</div></html>";
		wordsPlacement.setText(t);

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 8;
		c.insets = new Insets(0, 5, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		res.add(wordsPlacement, c);

		// TODO
		JPanel placementW = new JPanel();
		placementW.setOpaque(false);
		placementW.setLayout(new GridLayout(rows, cols));
		k = 0;
		for (int i = 0; i < wordsNum * 2; i++) {
			JLabel w = new JLabel();
			w.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			if (i == pos[k]) {
				w.setText("<html><div style='font: bold 18pt Arial Narrow; color: rgb(115, 84, 73); padding: 10px'>"
						+ allWords[pos[k]].toUpperCase() + "</div></html>");
				if (k < pos.length - 1)
					k++;
			}
			w.setUI(new LabelCustomUI(new Color(176, 190, 197), 1, false, false));

			placementW.add(w);
		}
		placementW
				.setPreferredSize(new Dimension((int) Math.round(this.getPreferredSize().getWidth() * 0.8), rows * 60));

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 9;
		c.insets = new Insets(0, 0, 20, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		res.add(placementW, c);

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 20, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		this.add(scroll, c);

		this.revalidate();
		this.repaint();
	}

	public boolean pause() {
		if (canPause) {
			pause = true;
			if (diff != Words.EASY)
				timer.stop();
			startPauseTime = new Date().getTime();
			return true;
		} else
			return false;
	}

	public void start() {
		if (pause) {
			if (diff != Words.EASY)
				timer.start();
			pauseTime += new Date().getTime() - startPauseTime;
			pause = false;
		}
	}

	public int getPauseTime() {
		return pauseTime;
	}

	public int getCorrectPercent() {
		for (int i = 0; i < pos.length; i++) {
			// ��������� ��������� �����
			if (remembered[pos[i]] == 1)
				correct++;
			// ��������� �������� ������
			if (selected[pos[i]] == 1)
				correct++;
			// ��������� ���������� �����
			JLabel l = labels[pos[i]];
			String t = l.getText();
			String[] parts = t.split("[<>]");
			if (parts.length > 1)
				if (parts[4].equals(allWords[pos[i]]))
					correct++;
		}

		return (int) Math.round(((float) correct / (wordsNum * 3)) * 100.0);
	}

	class CellsMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (pause)
				return;
			JLabel l = (JLabel) e.getSource();
			int i = Integer.parseInt(l.getName());
			if (selected[i] == 0) {
				if (counter < wordsNum) {
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
