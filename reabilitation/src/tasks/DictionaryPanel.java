package tasks;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.Timer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import customuiandrender.ButtonCustomUI;
import customuiandrender.ProgressBarCustomUI;
import defaults.InterfaceTextDefaults;
import defaults.TextLinkDefaults;
import reabilitation.Utils;

public class DictionaryPanel extends JPanel {

	private static final long serialVersionUID = 5149813749764929359L;
	Dictionary w;
	Random rand;

	private int wordsNum;
	private int syllableNum;
	private int rows;
	private int cols;
	private int taskTime;

	private int diff = Dictionary.EASY;

	private Color borderColor = new Color(68, 83, 91);

	private String[][] words;

	private int pauseTime = 0;
	private long startPauseTime = 0;

	private int correct = 0;
	private int attempts = 0;

	private String category = "";

	private JLabel[] firstSyllables;
	private JLabel[] secondSyllables;
	private JLabel[] thirdSyllables;

	private JLabel firstSyllable = null;
	private JLabel secondSyllable = null;
	private JLabel thirdSyllable = null;

	FirstMouseListener firstListener = new FirstMouseListener();
	SecondMouseListener secondListener = new SecondMouseListener();
	ThirdMouseListener thirdListener = new ThirdMouseListener();

	private Timer timer;
	private JLabel left = new JLabel();
	private JProgressBar bar;

	private boolean pause = false;
	private boolean canPause = true;

	public DictionaryPanel(int width, int height, int diff, Dictionary w) {
		super();
		this.diff = diff;
		this.w = w;

		switch (diff) {
		case Dictionary.EASY:
			wordsNum = 4;
			syllableNum = 2;
			rows = 2;
			cols = 2;
			taskTime = Integer.MAX_VALUE;
			break;
		case Dictionary.MIDDLE:
			wordsNum = 12;
			syllableNum = 3;
			rows = 4;
			cols = 3;
			taskTime = 5 * 60;
			break;
		case Dictionary.HARD:
			wordsNum = 20;
			syllableNum = 3;
			rows = 5;
			cols = 4;
			taskTime = 60;
			break;
		}

		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (bar.getValue() < taskTime) {
					bar.setValue(bar.getValue() + 1);
					if (diff == Dictionary.HARD) {
						ImageIcon icon = Utils.createImageIcon(
								"resources/image/timer/" + Integer.toString(taskTime - bar.getValue()) + ".png");
						left.setIcon(icon);
					}
					if (diff == Dictionary.MIDDLE) {
						ImageIcon icon = Utils.createImageIcon("resources/image/timer/"
								+ Integer.toString((taskTime - bar.getValue()) / 60 + 1) + "m.png");
						left.setIcon(icon);
					}
				} else {
					timer.stop();
					w.showResults();
				}

			}
		});

		this.setPreferredSize(new Dimension(width, height));
		this.setDoubleBuffered(true);
		this.setOpaque(false);
		showInfo_2();
	}
	
	public void fromBegining() {
		if (timer != null && timer.isRunning())
			timer.stop();
		w.beginingIcon.setVisible(true);
		w.pauseIcon.setVisible(true);
		pauseTime = 0;
		startPauseTime = 0;
		correct = 0;
		attempts = 0;
		showInfo_1();
	}

	public void generate() {
		rand = new Random(System.nanoTime());
		words = new String[wordsNum][syllableNum];

		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.DICTIONARY)
				+ Integer.toString(syllableNum) + "/words.xml");
		NodeList n = doc.getElementsByTagName("category");
		Node cat = null;
		for (int i = 0; i < n.getLength(); i++)
			if (n.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(category))
				cat = n.item(i);
		int catLen = cat.getChildNodes().getLength();
		int[] usage = new int[catLen];
		for (int i = 0; i < catLen; i++)
			usage[i] = 0;
		int i = 0;
		while (i < wordsNum) {
			int j = rand.nextInt(catLen);
			if (usage[j] != 0)
				continue;
			usage[j] = 1;
			words[i][0] = cat.getChildNodes().item(j).getAttributes().getNamedItem("first").getNodeValue();
			words[i][1] = cat.getChildNodes().item(j).getAttributes().getNamedItem("second").getNodeValue();
			if (syllableNum == 3)
				words[i][2] = cat.getChildNodes().item(j).getAttributes().getNamedItem("third").getNodeValue();
			i++;
		}
	}

	private void showInfo_1() {
		w.beginingIcon.setVisible(true);
		w.pauseIcon.setVisible(true);
		canPause = false;

		long t = new Date().getTime();

		JTextPane t1 = new JTextPane();
		t1.setEditable(false);
		t1.setContentType("text/html;charset=utf-8");

		String a = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("dictionary_task");
		if (syllableNum == 2)
			a += InterfaceTextDefaults.getInstance().getDefault("dictionary_task_2");
		if (syllableNum == 3)
			a += InterfaceTextDefaults.getInstance().getDefault("dictionary_task_3");
		a += "</div></html>";

		t1.setText(a);

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
				showSyllables();
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
		
		//w.beginingIcon.setVisible(false);

		canPause = false;

		long t = new Date().getTime();

		JTextPane t1 = new JTextPane();
		t1.setEditable(false);
		t1.setContentType("text/html;charset=utf-8");

		String a = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("choose_category");
		a += "</div></html>";

		t1.setText(a);

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
				generate();
				showInfo_1();
			}
		});

		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.DICTIONARY)
				+ Integer.toString(syllableNum) + "/words.xml");
		NodeList n = doc.getElementsByTagName("category");
		JRadioButton[] radio = new JRadioButton[n.getLength()];
		RadioListener r = new RadioListener();
		for (int i = 0; i < n.getLength(); i++) {
			String s = n.item(i).getAttributes().getNamedItem("name").getNodeValue();
			radio[i] = new JRadioButton(
					"<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>" + s + "</div></html>");
			radio[i].setActionCommand(s);
			radio[i].setSelected(false);
			radio[i].setOpaque(false);
			radio[i].addActionListener(r);
		}
		radio[0].setSelected(true);
		category = radio[0].getActionCommand();

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

		int i;
		ButtonGroup group = new ButtonGroup();
		for (i = 0; i < n.getLength(); i++) {

			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridx = 1;
			c.gridy = 1 + i;
			c.insets = new Insets(0, 200, 0, 0);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 0.0;
			c.weighty = 0.0;

			group.add(radio[i]);
			this.add(radio[i], c);
		}

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 1 + i;
		c.insets = new Insets(40, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 1.0;

		this.add(start, c);

		this.revalidate();
		this.repaint();
	}

	private void showSyllables() {
		this.removeAll();
		this.setLayout(new GridBagLayout());

		if (diff != Dictionary.EASY) {
			timer.stop();
			left.setIcon(null);
		}

		firstSyllable = null;
		secondSyllable = null;
		thirdSyllable = null;

		correct = 0;
		attempts = 0;

		JLabel task = new JLabel();
		String t1 = "<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("dictionary_task") + "</div></html>";
		task.setText(t1);

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
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(task, c);

		JLabel first = new JLabel();
		first.setText("<html><div style='font: bold 22pt Arial Narrow; color: rgb(68, 83, 91); text-align: center;'>"
				+ InterfaceTextDefaults.getInstance().getDefault("first_syllable") + "</div></html>");
		first.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
		first.setHorizontalAlignment(JLabel.CENTER);

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 30, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(first, c);

		JLabel second = new JLabel();
		second.setText("<html><div style='font: bold 22pt Arial Narrow; color: rgb(68, 83, 91); text-align: center;'>"
				+ InterfaceTextDefaults.getInstance().getDefault("second_syllable") + "</div></html>");
		second.setHorizontalAlignment(JLabel.CENTER);
		second.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		if (syllableNum == 3)
			c.insets = new Insets(0, 2, 0, 0);
		else
			c.insets = new Insets(0, 2, 0, 30);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(second, c);

		JLabel third = null;
		if (syllableNum == 3) {
			third = new JLabel();
			third.setText("<html><div style='font: bold 22pt Arial Narrow; color: rgb(68, 83, 91); text-align: center;'>"
					+ InterfaceTextDefaults.getInstance().getDefault("third_syllable") + "</div></html>");
			third.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
			third.setHorizontalAlignment(JLabel.CENTER);

			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.BOTH;
			c.gridheight = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridx = 2;
			c.gridy = 1;
			c.insets = new Insets(0, 2, 0, 30);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 1.0;
			c.weighty = 0.0;

			this.add(third, c);
		}

		firstSyllables = new JLabel[wordsNum];
		int[] firstSPos = new int[wordsNum];
		secondSyllables = new JLabel[wordsNum];
		int[] secondSPos = new int[wordsNum];
		int[] thirdSPos = null;
		if (syllableNum == 3) {
			thirdSyllables = new JLabel[wordsNum];
			thirdSPos = new int[wordsNum];
		}
		for (int i = 0; i < wordsNum; i++) {
			firstSPos[i] = 0;
			secondSPos[i] = 0;
			if (syllableNum == 3)
				thirdSPos[i] = 0;
		}

		rand = new Random(System.nanoTime());
		int t = 0;
		while (t < wordsNum) {
			int j = rand.nextInt(wordsNum);
			if (firstSPos[j] != 0)
				continue;
			firstSPos[j] = t;
			t++;
		}
		t = 0;
		while (t < wordsNum) {
			int j = rand.nextInt(wordsNum);
			if (secondSPos[j] != 0)
				continue;
			secondSPos[j] = t;
			t++;
		}
		t = 0;
		if (syllableNum == 3)
			while (t < wordsNum) {
				int j = rand.nextInt(wordsNum);
				if (thirdSPos[j] != 0)
					continue;
				thirdSPos[j] = t;
				t++;
			}

		this.revalidate();
		this.repaint();

		JPanel p1 = new JPanel();
		p1.setOpaque(false);
		GridLayout experimentLayout = new GridLayout(rows, cols);
		p1.setLayout(experimentLayout);
		for (int i = 0; i < wordsNum; i++) {
			firstSyllables[i] = new JLabel();
			firstSyllables[i].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, borderColor));
			if (i % cols == 0)
				firstSyllables[i].setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, borderColor));
			if (i / cols == 0)
				firstSyllables[i].setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, borderColor));
			if ((i % cols == 0) && (i / cols == 0))
				firstSyllables[i].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
			firstSyllables[i].setText(
					"<html><div style='font: bold 22pt Arial Narrow; color: rgb(68, 83, 91); text-align: center;'>"
							+ words[firstSPos[i]][0] + "</div></html>");
			firstSyllables[i].setHorizontalAlignment(JLabel.CENTER);
			p1.add(firstSyllables[i]);
			firstSyllables[i].setName(words[firstSPos[i]][0]);
			firstSyllables[i].setBackground(new Color(149, 179, 215));
			firstSyllables[i].setOpaque(false);
			firstSyllables[i].addMouseListener(firstListener);
			firstSyllables[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		p1.setPreferredSize(new Dimension((int) (first.getPreferredSize().getWidth()),
				(int) (first.getPreferredSize().getHeight() * cols)));

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 30, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		this.add(p1, c);

		JPanel p2 = new JPanel();
		p2.setOpaque(false);
		GridLayout experimentLayout2 = new GridLayout(rows, cols);
		p2.setLayout(experimentLayout2);
		for (int i = 0; i < wordsNum; i++) {
			secondSyllables[i] = new JLabel();
			secondSyllables[i].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, borderColor));
			if (i % cols == 0)
				secondSyllables[i].setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, borderColor));
			if (i / cols == 0)
				secondSyllables[i].setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, borderColor));
			if ((i % cols == 0) && (i / cols == 0))
				secondSyllables[i].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
			secondSyllables[i].setText(
					"<html><div style='font: bold 22pt Arial Narrow; color: rgb(68, 83, 91); text-align: center;'>"
							+ words[secondSPos[i]][1] + "</div></html>");
			secondSyllables[i].setHorizontalAlignment(JLabel.CENTER);
			p2.add(secondSyllables[i]);
			secondSyllables[i].setName(words[secondSPos[i]][1]);
			secondSyllables[i].setBackground(new Color(149, 179, 215));
			secondSyllables[i].setOpaque(false);
			secondSyllables[i].addMouseListener(secondListener);
		}

		p2.setPreferredSize(new Dimension((int) (second.getPreferredSize().getWidth()),
				(int) (second.getPreferredSize().getHeight() * cols)));

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 2;
		if (syllableNum == 3)
			c.insets = new Insets(0, 2, 0, 0);
		else
			c.insets = new Insets(0, 2, 0, 30);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		this.add(p2, c);

		if (syllableNum == 3) {
			JPanel p3 = new JPanel();
			p3.setOpaque(false);
			GridLayout experimentLayout3 = new GridLayout(rows, cols);
			p3.setLayout(experimentLayout3);
			for (int i = 0; i < wordsNum; i++) {
				thirdSyllables[i] = new JLabel();
				thirdSyllables[i].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, borderColor));
				if (i % cols == 0)
					thirdSyllables[i].setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, borderColor));
				if (i / cols == 0)
					thirdSyllables[i].setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, borderColor));
				if ((i % cols == 0) && (i / cols == 0))
					thirdSyllables[i].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor));
				thirdSyllables[i].setText(
						"<html><div style='font: bold 22pt Arial Narrow; color: rgb(68, 83, 91); text-align: center;'>"
								+ words[thirdSPos[i]][2] + "</div></html>");
				thirdSyllables[i].setHorizontalAlignment(JLabel.CENTER);
				p3.add(thirdSyllables[i]);
				thirdSyllables[i].setName(words[thirdSPos[i]][2]);
				thirdSyllables[i].setBackground(new Color(149, 179, 215));
				thirdSyllables[i].setOpaque(false);
				thirdSyllables[i].addMouseListener(thirdListener);
			}

			p3.setPreferredSize(new Dimension((int) (third.getPreferredSize().getWidth()),
					(int) (third.getPreferredSize().getHeight() * cols)));

			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.BOTH;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.gridx = 2;
			c.gridy = 2;
			c.insets = new Insets(0, 2, 0, 30);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;

			this.add(p3, c);

		}

		if (diff != Dictionary.EASY) {
			JProgressBar progressBar = new JProgressBar();
			progressBar.setStringPainted(false);
			progressBar.setMinimum(0);
			progressBar.setMaximum(taskTime);
			progressBar.setValue(0);
			progressBar.setUI(new ProgressBarCustomUI());
			progressBar.setBorder(null);
			bar = progressBar;

			if (diff == Dictionary.HARD) {
				ImageIcon icon = Utils.createImageIcon(
						"resources/image/timer/" + Integer.toString(taskTime - bar.getValue()) + ".png");
				left.setIcon(icon);
			}
			if (diff == Dictionary.MIDDLE) {
				ImageIcon icon = Utils.createImageIcon(
						"resources/image/timer/" + Integer.toString((taskTime - bar.getValue()) / 60) + "m.png");
				left.setIcon(icon);
			}

			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridx = 0;
			c.gridy = 3;
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
			c.gridy = 4;
			c.insets = new Insets(5, 20, 0, 20);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 1.0;
			c.weighty = 0.0;

			this.add(progressBar, c);

			timer.start();
		}

		this.revalidate();
		this.repaint();
	}

	public int getCorrectPercent() {
		return (int) Math.round(((float) correct / wordsNum) * 100.0);
	}

	public boolean pause() {
		if (canPause) {
			pause = true;
			if (diff != Dictionary.EASY)
				timer.stop();
			startPauseTime = new Date().getTime();
			return true;
		} else
			return false;
	}

	public void start() {
		if (pause) {
			if (diff != Dictionary.EASY)
				timer.start();
			pauseTime += new Date().getTime() - startPauseTime;
			pause = false;
		}
	}

	public int getPauseTime() {
		return pauseTime;
	}

	private class FirstMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (pause)
				return;
			if ((firstSyllable == null) && (secondSyllable == null)) {
				JLabel l = (JLabel) e.getSource();
				l.setOpaque(true);
				l.repaint();
				firstSyllable = l;

				for (int i = 0; i < wordsNum; i++) {
					if (secondSyllables[i].getText() != "")
						secondSyllables[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					firstSyllables[i].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

	}

	private class SecondMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (pause)
				return;
			if ((secondSyllable == null) && (firstSyllable != null) && (thirdSyllable == null)) {
				JLabel l = (JLabel) e.getSource();
				l.setOpaque(true);
				l.repaint();
				secondSyllable = l;

				if (syllableNum == 2) {
					String firstS = firstSyllable.getName();
					String secondS = secondSyllable.getName();

					attempts++;

					for (int i = 0; i < wordsNum; i++) {
						if (firstS.equals(words[i][0]) && secondS.equals(words[i][1])) {
							correct++;
							break;
						}
					}

					firstSyllable.setOpaque(false);
					firstSyllable.setText("");
					firstSyllable.removeMouseListener(firstListener);
					secondSyllable.setOpaque(false);
					secondSyllable.setText("");
					secondSyllable.removeMouseListener(secondListener);

					firstSyllable = null;
					secondSyllable = null;

					for (int i = 0; i < wordsNum; i++) {
						if (firstSyllables[i].getText() != "")
							firstSyllables[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						secondSyllables[i].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}

					if (attempts == wordsNum) {
						timer.stop();
						w.showResults();
					}
				}

				else
					for (int i = 0; i < wordsNum; i++) {
						if (thirdSyllables[i].getText() != "")
							thirdSyllables[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						secondSyllables[i].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

	}

	private class ThirdMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (pause)
				return;
			if ((firstSyllable != null) && (secondSyllable != null) && (thirdSyllable == null)) {
				JLabel l = (JLabel) e.getSource();
				l.setOpaque(true);
				l.repaint();
				thirdSyllable = l;

				String firstS = firstSyllable.getName();
				String secondS = secondSyllable.getName();
				String thirdS = thirdSyllable.getName();

				attempts++;

				for (int i = 0; i < wordsNum; i++) {
					if (firstS.equals(words[i][0]) && secondS.equals(words[i][1]) && thirdS.equals(words[i][2])) {
						correct++;
						break;
					}
				}

				firstSyllable.setOpaque(false);
				firstSyllable.setText("");
				firstSyllable.removeMouseListener(firstListener);
				secondSyllable.setOpaque(false);
				secondSyllable.setText("");
				secondSyllable.removeMouseListener(secondListener);
				thirdSyllable.setOpaque(false);
				thirdSyllable.setText("");
				thirdSyllable.removeMouseListener(thirdListener);

				firstSyllable = null;
				secondSyllable = null;
				thirdSyllable = null;

				for (int i = 0; i < wordsNum; i++) {
					if (firstSyllables[i].getText() != "")
						firstSyllables[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					thirdSyllables[i].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

				if (attempts == wordsNum) {
					timer.stop();
					w.showResults();
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
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

	}

	class RadioListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			category = e.getActionCommand();
		}
	}
}
