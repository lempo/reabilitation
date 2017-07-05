package tasks;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import dialogs.Dialogs;
import exception.ServerConnectionException;
import reabilitation.HTTPClient;
import reabilitation.Reabilitation;
import reabilitation.Utills;

public class Comparison extends AbstractTask {
	private static final long serialVersionUID = 5702127239458625154L;
	public static final int EASY = 4;
	public static final int MIDDLE = 9;
	public static final int HARD = 16;
	private ComparisonPanel panel;
	private int diff = EASY;

	private JLabel onPause;
	private JLabel q;

	private String question = InterfaceTextDefaults.getInstance().getDefault("comparison_question");

	/** ������� ��� ����������� ������� */
	private final int num = 10;

	public Comparison(int width, int height, String text, Reabilitation reabilitation, String username,
			String userCardNumber, String taskName, String taskGroupName) {
		super(width, height, text, reabilitation, username, userCardNumber, taskName, taskGroupName);

		onPause = new JLabel("<html><div style='font: bold 24pt Arial Narrow; color: rgb(68, 83, 91); padding-left: 200px;'>"
				+ InterfaceTextDefaults.getInstance().getDefault("pause") + "</div></html>");
		onPause.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		// � ���� ������ ��� �������� ���������
		// showStandartSettings();
		showInfo();
	}

	public void showInfo() {
		showStandartInfo();
	}

	public void showResults() {
		totalTime = new Date().getTime() - startTime - panel.getPauseTime();
		correct = panel.getCorrectPercent();
		try {
			HTTPClient.saveResult(username, userCardNumber, taskName, correct, taskGroupName);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
			Dialogs.showServerConnectionErrorDialog(e);
		}
		showStandartResults();
	}

	public void showTask(boolean repeat) {
		startTime = new Date().getTime();

		q = new JLabel();
		String t = "<html><div style='font: 18pt Arial Narrow; color: rgb(115, 84, 73);'>" + question + "</div></html>";
		q.setText(t);

		if (panel != null && repeat)
			panel.fromBegining();
		else
			panel = new ComparisonPanel(diff, this, num);

		this.removeAll();
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

		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 0);
		c.weightx = 1.0;

		this.add(panel, c);

		composeBottomPanel();

		c.anchor = GridBagConstraints.WEST;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 0.0;

		this.add(bottomPanel, c);

		this.revalidate();
		this.repaint();
	}

	@Override
	public void pause() {
		if (!pause && (panel != null) && panel.pause() && !dontShowBreakingDialog) {
			super.pause();

			q.setVisible(false);
			panel.setVisible(false);

			onPause.setPreferredSize(panel.getPreferredSize());

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

			ImageIcon icon = Utills
					.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.START));
			startIcon.setIcon(icon);
			startIcon.updateUI();
		}
	}

	@Override
	public void start() {
		if (pause && (panel != null)) {
			super.start();

			q.setVisible(true);
			panel.setVisible(true);

			this.remove(onPause);
			revalidate();
			repaint();

			pause = false;

			ImageIcon icon = Utills
					.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.PAUSE));
			pauseIcon.setIcon(icon);
			pauseIcon.updateUI();
			panel.start();
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
