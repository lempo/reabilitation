package tasks;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import customcomponent.CustomDialog;
import customuiandrender.ButtonCustomUI;
import customuiandrender.SliderCustomUI;
import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import dialogs.Dialogs;
import exception.ServerConnectionException;
import reabilitation.HTTPClient;
import reabilitation.Reabilitation;
import reabilitation.Utills;

public class Listen extends AbstractTask {
	private static final long serialVersionUID = 118086317031257325L;
	public static final int EASY = 0;
	public static final int MIDDLE = 1;
	public static final int HARD = 2;
	private int diff = EASY;

	private ListenPanel panel;

	private JLabel onPause;

	private int volume = 5;

	public Listen(int width, int height, String text, Reabilitation reabilitation, String username,
			String userCardNumber, String taskName, String taskGroupName) {
		super(width, height, text, reabilitation, username, userCardNumber, taskName, taskGroupName);

		onPause = new JLabel("<html><div style='font: bold 24pt Arial Narrow; color: rgb(68, 83, 91); padding-left: 200px;'>"
				+ InterfaceTextDefaults.getInstance().getDefault("pause") + "</div></html>");
		onPause.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		// ������������� ��������� - ����� ��������� ���������
		showSettings();
	}

	public void showSettings() {
		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("settings") + "</div></html>";
		heading.setText(t);

		JLabel level = new JLabel();
		t = "<html><div style='font: bold 20pt Arial Narrow; color: rgb(68, 83, 91);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("diff") + "</div></html>";
		level.setText(t);

		JLabel sound = new JLabel();
		t = "<html><div style='font: bold 20pt Arial Narrow; color: rgb(68, 83, 91);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("sound_level") + "</div></html>";
		sound.setText(t);

		JRadioButton easyButton = new JRadioButton(
				"<html><div style='font: 18pt Arial Narrow; color: rgb(115, 84, 73);'>"
						+ InterfaceTextDefaults.getInstance().getDefault("easy") + "</div></html>");
		easyButton.setActionCommand("easy");
		easyButton.setSelected(true);
		easyButton.setOpaque(false);

		JRadioButton middleButton = new JRadioButton(
				"<html><div style='font: 18pt Arial Narrow; color: rgb(115, 84, 73);'>"
						+ InterfaceTextDefaults.getInstance().getDefault("middle") + "</div></html>");
		middleButton.setActionCommand("middle");
		middleButton.setOpaque(false);

		JRadioButton hardButton = new JRadioButton(
				"<html><div style='font: 18pt Arial Narrow; color: rgb(115, 84, 73);'>"
						+ InterfaceTextDefaults.getInstance().getDefault("hard") + "</div></html>");
		hardButton.setActionCommand("hard");
		hardButton.setOpaque(false);

		ButtonGroup group = new ButtonGroup();
		group.add(easyButton);
		group.add(middleButton);
		group.add(hardButton);

		RadioListener r = new RadioListener();
		easyButton.addActionListener(r);
		middleButton.addActionListener(r);
		hardButton.addActionListener(r);

		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 10, volume);
		slider.setUI(new SliderCustomUI(slider));
		slider.setOpaque(false);
		slider.setFocusable(false);
		slider.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				slider.repaint();
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}
		});
		slider.addChangeListener(new SliderListener());

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("begin_task"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(200, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showInfo();
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
		c.insets = new Insets(0, 20, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		this.add(heading, c);

		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(40, 200, 0, 0);
		c.weightx = 0.0;

		this.add(level, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 2;

		this.add(sound, c);

		c.gridheight = 3;
		c.gridy = 2;

		this.add(slider, c);

		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 1;

		this.add(easyButton, c);

		c.gridy = 3;
		c.insets = new Insets(10, 200, 0, 0);

		this.add(middleButton, c);

		c.gridy = 4;

		this.add(hardButton, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridy = 5;
		c.insets = new Insets(40, 0, 0, 0);
		c.weighty = 1.0;

		this.add(start, c);

		this.revalidate();
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

		if (panel != null && repeat)
			panel.fromBegining();
		else
			panel = new ListenPanel(this.getWidth(), 400, diff, this, (float) (volume / 10.0));

		this.removeAll();
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(20, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		this.add(panel, c);

		composeBottomPanel();

		c.anchor = GridBagConstraints.WEST;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 0);

		this.add(bottomPanel, c);

		this.revalidate();
		this.repaint();
	}

	class RadioListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "easy":
				diff = EASY;
				break;
			case "middle":
				diff = MIDDLE;
				break;
			case "hard":
				diff = HARD;
				break;
			}
		}
	}

	@Override
	public void pause() {
		if (!pause && (panel != null) && panel.pause() && !dontShowBreakingDialog) {
			super.pause();

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

	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			volume = source.getValue();
		}
	}

	@Override
	public void setDiff(int diff) {
		this.diff = diff;
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
}
