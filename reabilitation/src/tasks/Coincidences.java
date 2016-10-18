package tasks;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import customcomponent.CustomDialog;
import customuiandrender.ButtonCustomUI;
import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import reabilitation.HTTPClient;
import reabilitation.Reabilitation;
import reabilitation.Utills;

public class Coincidences extends AbstractTask {
	private static final long serialVersionUID = -2008862322889925832L;
	public static final int EASY = 0;
	public static final int MIDDLE = 1;
	public static final int HARD = 2;
	private int diff = EASY;
	private CoincidencesPanel leftPanel;
	private CoincidencesPanel rightPanel;
	private JLabel q;
	private JButton yes;
	private JButton no;

	private final String easy = InterfaceTextDefaults.getInstance().getDefault("coincidences_easy_question");
	private final String middle = InterfaceTextDefaults.getInstance().getDefault("coincidences_middle_question");
	private final String hard = InterfaceTextDefaults.getInstance().getDefault("coincidences_hard_question");
	private String question = easy;

	/** ������� ��� �������� */
	private final int num = 10;
	/** ������� ��� �������� ��� */
	private int n;
	/** ������� ���������� ������� */
	private int f;

	private long pauseTime = 0;
	private long startPauseTime = 0;

	private JLabel onPause;

	int[][] colorFigureNum_left;
	int[][] colorFigureNum_right;

	public Coincidences(int width, int height, String text, Reabilitation reabilitation, String username,
			String userCardNumber, String taskName, String taskGroupName) {
		super(width, height, text, reabilitation, username, userCardNumber, taskName, taskGroupName);

		onPause = new JLabel("<html><div style='font: bold 24pt Arial Narrow; color: rgb(68, 83, 91); padding-left: 200px;'>"
				+ InterfaceTextDefaults.getInstance().getDefault("pause") + "</div></html>");
		onPause.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		showStandartSettings();
	}

	public void showInfo() {
		showStandartInfo();
	}

	public void showResults() {
		totalTime = new Date().getTime() - startTime - pauseTime;
		correct = (int) Math.round(((float) f / num) * 100.0);
		HTTPClient.saveResult(username, userCardNumber, taskName, correct, taskGroupName);
		showStandartResults();
	}

	private void generate() {
		leftPanel = new CoincidencesPanel(200, 400);
		rightPanel = new CoincidencesPanel(200, 400);

		int[] cons = new int[num];
		for (int i = 0; i < num; i++)
			cons[i] = 0;
		Random rand = new Random(System.nanoTime());
		int i = 0;
		while (i < num / 2) {
			int j = rand.nextInt(num);
			if (cons[j] != 0)
				continue;
			cons[j] = 1;
			i++;
		}

		colorFigureNum_left = new int[num][3];
		colorFigureNum_right = new int[num][3];
		for (int j = 0; j < num; j++) {
			leftPanel.generate();
			if (cons[j] == 1)
				switch (diff) {
				case EASY:
					rightPanel.setColor(leftPanel.getColor());
					break;
				case MIDDLE:
					rightPanel.setColor(leftPanel.getColor());
					rightPanel.setFigure(leftPanel.getFigure());
					break;
				case HARD:
					rightPanel.setColor(leftPanel.getColor());
					rightPanel.setFigure(leftPanel.getFigure());
					rightPanel.setNum(leftPanel.getNum());
					break;
				}
			rightPanel.generate();
			colorFigureNum_left[j][0] = leftPanel.getColor();
			colorFigureNum_left[j][1] = leftPanel.getFigure();
			colorFigureNum_left[j][2] = leftPanel.getNum();
			colorFigureNum_right[j][0] = rightPanel.getColor();
			colorFigureNum_right[j][1] = rightPanel.getFigure();
			colorFigureNum_right[j][2] = rightPanel.getNum();
		}
	}

	public void showTask(boolean repeat) {
		n = 0;
		f = 0;
		startTime = new Date().getTime();
		pauseTime = 0;

		if (!repeat)
			generate();

		/*
		 * int[] cons = new int[num]; for (int i = 0; i < num; i++) cons[i] = 0;
		 * Random rand = new Random(System.nanoTime()); int i = 0; while (i <
		 * num / 2) { int j = rand.nextInt(num); if (cons[j] != 0) continue;
		 * cons[j] = 1; i++; }
		 */

		this.removeAll();
		switch (diff) {
		case EASY:
			question = easy;
			break;
		case MIDDLE:
			question = middle;
			break;
		case HARD:
			question = hard;
			break;
		}
		q = new JLabel();
		String t = "<html><div style='font: 18pt Arial Narrow; color: rgb(115, 84, 73);'>" + question + "</div></html>";
		q.setText(t);

		// leftPanel = new CoincidencesPanel(200, 400);
		// rightPanel = new CoincidencesPanel(200, 400);

		/*
		 * leftPanel.generate(); if (cons[0] == 1) switch (diff) { case EASY:
		 * rightPanel.setColor(leftPanel.getColor()); break; case MIDDLE:
		 * rightPanel.setColor(leftPanel.getColor());
		 * rightPanel.setFigure(leftPanel.getFigure()); break; case HARD:
		 * rightPanel.setColor(leftPanel.getColor());
		 * rightPanel.setFigure(leftPanel.getFigure());
		 * rightPanel.setNum(leftPanel.getNum()); break; }
		 * rightPanel.generate();
		 */

		leftPanel.setColor(colorFigureNum_left[n][0]);
		leftPanel.setFigure(colorFigureNum_left[n][1]);
		leftPanel.setNum(colorFigureNum_left[n][2]);
		leftPanel.generate();
		rightPanel.setColor(colorFigureNum_right[n][0]);
		rightPanel.setFigure(colorFigureNum_right[n][1]);
		rightPanel.setNum(colorFigureNum_right[n][2]);
		rightPanel.generate();

		yes = new JButton(InterfaceTextDefaults.getInstance().getDefault("yes"));
		yes.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		yes.setBorder(null);
		yes.setOpaque(false);
		yes.setPreferredSize(new Dimension(100, 35));
		yes.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pause)
					return;
				if (n < num - 1) {
					switch (diff) {
					case EASY:
						if (leftPanel.getColor() == rightPanel.getColor())
							f++;
						break;
					case MIDDLE:
						if ((leftPanel.getColor() == rightPanel.getColor())
								&& (leftPanel.getFigure() == rightPanel.getFigure()))
							f++;
						break;
					case HARD:
						if ((leftPanel.getColor() == rightPanel.getColor())
								&& (leftPanel.getFigure() == rightPanel.getFigure())
								&& (leftPanel.getNum() == rightPanel.getNum()))
							f++;
						break;
					}
					n++;
					leftPanel.setColor(colorFigureNum_left[n][0]);
					leftPanel.setFigure(colorFigureNum_left[n][1]);
					leftPanel.setNum(colorFigureNum_left[n][2]);
					leftPanel.generate();
					rightPanel.setColor(colorFigureNum_right[n][0]);
					rightPanel.setFigure(colorFigureNum_right[n][1]);
					rightPanel.setNum(colorFigureNum_right[n][2]);
					rightPanel.generate();
					leftPanel.repaint();
					rightPanel.repaint();
				} else {
					switch (diff) {
					case EASY:
						if (leftPanel.getColor() == rightPanel.getColor())
							f++;
						break;
					case MIDDLE:
						if ((leftPanel.getColor() == rightPanel.getColor())
								&& (leftPanel.getFigure() == rightPanel.getFigure()))
							f++;
						break;
					case HARD:
						if ((leftPanel.getColor() == rightPanel.getColor())
								&& (leftPanel.getFigure() == rightPanel.getFigure())
								&& (leftPanel.getNum() == rightPanel.getNum()))
							f++;
						break;
					}
					showResults();
				}
			}
		});

		no = new JButton(InterfaceTextDefaults.getInstance().getDefault("no"));
		no.setUI(new ButtonCustomUI(new Color(239, 83, 80)));
		no.setBorder(null);
		no.setOpaque(false);
		no.setPreferredSize(new Dimension(100, 35));
		no.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		no.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pause)
					return;
				if (n < num - 1) {
					f++;
					switch (diff) {
					case EASY:
						if (leftPanel.getColor() == rightPanel.getColor())
							f--;
						break;
					case MIDDLE:
						if ((leftPanel.getColor() == rightPanel.getColor())
								&& (leftPanel.getFigure() == rightPanel.getFigure()))
							f--;
						break;
					case HARD:
						if ((leftPanel.getColor() == rightPanel.getColor())
								&& (leftPanel.getFigure() == rightPanel.getFigure())
								&& (leftPanel.getNum() == rightPanel.getNum()))
							f--;
						break;
					}
					n++;
					leftPanel.setColor(colorFigureNum_left[n][0]);
					leftPanel.setFigure(colorFigureNum_left[n][1]);
					leftPanel.setNum(colorFigureNum_left[n][2]);
					leftPanel.generate();
					rightPanel.setColor(colorFigureNum_right[n][0]);
					rightPanel.setFigure(colorFigureNum_right[n][1]);
					rightPanel.setNum(colorFigureNum_right[n][2]);
					rightPanel.generate();
					leftPanel.repaint();
					rightPanel.repaint();
				} else {
					f++;
					switch (diff) {
					case EASY:
						if (leftPanel.getColor() == rightPanel.getColor())
							f++;
						break;
					case MIDDLE:
						if ((leftPanel.getColor() == rightPanel.getColor())
								&& (leftPanel.getFigure() == rightPanel.getFigure()))
							f++;
						break;
					case HARD:
						if ((leftPanel.getColor() == rightPanel.getColor())
								&& (leftPanel.getFigure() == rightPanel.getFigure())
								&& (leftPanel.getNum() == rightPanel.getNum()))
							f++;
						break;
					}
					showResults();
				}
			}
		});

		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(q, c);

		c.gridheight = 2;
		c.gridwidth = 1;
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 0);
		c.weightx = 1.0;

		this.add(leftPanel, c);

		c.anchor = GridBagConstraints.SOUTH;
		c.gridheight = 1;
		c.gridx = 1;
		c.insets = new Insets(0, 20, 40, 20);
		c.weightx = 0.0;
		c.weighty = 1.0;

		this.add(yes, c);

		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.gridy = 2;
		c.insets = new Insets(40, 20, 0, 20);

		this.add(no, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridheight = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 0);
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(rightPanel, c);

		composeBottomPanel();

		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 20, 0);
		c.weightx = 1.0;

		this.add(bottomPanel, c);

		this.revalidate();
		this.repaint();
	}

	@Override
	public void pause() {
		if (!pause && (pauseIcon != null) && !dontShowBreakingDialog) {

			super.pause();

			q.setVisible(false);
			leftPanel.setVisible(false);
			rightPanel.setVisible(false);
			yes.setVisible(false);
			no.setVisible(false);

			onPause.setPreferredSize(new Dimension((int) onPause.getPreferredSize().getWidth(),
					(int) (leftPanel.getPreferredSize().getHeight() + q.getPreferredSize().getHeight() + 10)));

			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridheight = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(20, 0, 0, 0);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 0.0;
			c.weighty = 0.0;

			this.add(onPause, c);
			revalidate();
			repaint();

			pause = true;
			startPauseTime = new Date().getTime();

			ImageIcon icon = Utills
					.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.START));
			startIcon.setIcon(icon);
			startIcon.updateUI();
		}
	}

	@Override
	public void start() {
		if (pause && (pauseIcon != null)) {
			super.start();

			q.setVisible(true);
			leftPanel.setVisible(true);
			rightPanel.setVisible(true);
			yes.setVisible(true);
			no.setVisible(true);

			this.remove(onPause);
			revalidate();
			repaint();

			ImageIcon icon = Utills
					.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.PAUSE));
			pauseIcon.setIcon(icon);
			pauseIcon.updateUI();

			pauseTime += new Date().getTime() - startPauseTime;
			pause = false;
		}
	}

	@Override
	public int getEasy() {
		return EASY;
	}

	@Override
	public int getMiddle() {
		return MIDDLE;
	}

	@Override
	public int getHard() {
		return HARD;
	}

	@Override
	public void setDiff(int diff) {
		this.diff = diff;
	}
}
