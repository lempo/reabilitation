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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import customcomponent.CustomDialog;
import customuiandrender.ButtonCustomUI;
import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import reabilitation.Reabilitation;
import reabilitation.Utills;

public abstract class AbstractTask extends JPanel {

	private static final long serialVersionUID = -8574143595853111582L;

	protected Reabilitation reabilitation;
	protected String taskName = "";
	protected String taskGroupName = "";

	protected long startTime;
	protected long totalTime;
	protected String username = "";
	protected String userCardNumber = "";
	protected int correct;
	private JLabel repeatIcon;
	private JLabel allResultsIcon;
	private String text;

	protected boolean pause = false;

	protected IconsMouseListener iconsListener = new IconsMouseListener();

	protected JPanel bottomPanel;

	protected JLabel startIcon;
	protected JLabel pauseIcon;
	protected JLabel beginingIcon;

	protected JButton back;
	protected JButton forward;

	protected boolean dontShowBreakingDialog = false;
	
	AbstractTask(int width, int height, String text, Reabilitation reabilitation, String userName,
			String userCardNumber, String taskName, String taskGroupName) {
		super();
		this.setPreferredSize(new Dimension(width, height));
		this.setOpaque(false);
		this.text = text;
		this.reabilitation = reabilitation;
		this.username = userName;
		this.userCardNumber = userCardNumber;
		this.taskName = taskName;
		this.taskGroupName = taskGroupName;
	}

	public void composeBottomPanel() {
		bottomPanel = new JPanel();
		bottomPanel.setOpaque(false);
		bottomPanel.setLayout(new GridBagLayout());

		back = new JButton(InterfaceTextDefaults.getInstance().getDefault("back"));
		back.setUI(new ButtonCustomUI(new Color(239, 83, 80)));
		back.setBorder(null);
		back.setOpaque(false);
		back.setPreferredSize(new Dimension(200, 30));
		back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pause();
				CustomDialog d1 = new CustomDialog(reabilitation,
						InterfaceTextDefaults.getInstance().getDefault("sure_break_task"),
						InterfaceTextDefaults.getInstance().getDefault("break"),
						InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
				if (d1.getAnswer() == 1)
					showInfo();
				else
					start();
			}
		});
		// hide buuton (not needed)
		back.setVisible(false);

		forward = new JButton(InterfaceTextDefaults.getInstance().getDefault("forward"));
		forward.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		forward.setBorder(null);
		forward.setOpaque(false);
		forward.setPreferredSize(new Dimension(200, 30));
		forward.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pause();
				CustomDialog d1 = new CustomDialog(reabilitation,
						InterfaceTextDefaults.getInstance().getDefault("sure_break_task"),
						InterfaceTextDefaults.getInstance().getDefault("break"),
						InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
				if (d1.getAnswer() == 1)
					showResults();
				else
					start();
			}

		});
		// hide buuton (not needed)
		forward.setVisible(false);

		startIcon = new JLabel();
		pauseIcon = new JLabel();
		beginingIcon = new JLabel();

		ImageIcon icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.START));
		startIcon.setIcon(icon);
		startIcon.setName("start");
		startIcon.addMouseListener(iconsListener);
		startIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.PAUSE));
		pauseIcon.setIcon(icon);
		pauseIcon.setName("pause");
		pauseIcon.addMouseListener(iconsListener);
		pauseIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.BEGINING));
		beginingIcon.setIcon(icon);
		beginingIcon.setName("begining");
		beginingIcon.addMouseListener(iconsListener);
		beginingIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		bottomPanel.setPreferredSize(
				new Dimension((int) this.getPreferredSize().getWidth(), startIcon.getIcon().getIconHeight() + 30));

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(25, 40, 0, 40);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		bottomPanel.add(back, c);

		c.gridx = 1;

		bottomPanel.add(forward, c);

		c.anchor = GridBagConstraints.SOUTHEAST;
		c.gridx = 2;
		c.insets = new Insets(25, 40, 0, 0);
		c.weightx = 1.0;

		// bottomPanel.add(startIcon, c);

		// c.gridx = 3;
		// c.weightx = 0.0;

		bottomPanel.add(pauseIcon, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 3;
		c.weightx = 0.0;
		c.insets = new Insets(25, 40, 0, 20);

		bottomPanel.add(beginingIcon, c);
	}

	public void showStandartResults() {
		
		dontShowBreakingDialog = true;

		JLabel name = new JLabel();
		String t = "<html><div style='font: 18pt Arial Narrow; color: rgb(115, 84, 73);'>" + username + "</div></html>";
		name.setText(t);

		JLabel time = new JLabel();
		GregorianCalendar c1 = new GregorianCalendar();
		c1.setTime(new Date(startTime));
		t = "<html><div style='font: 14pt Arial Narrow; color: rgb(68, 83, 91);'>" + c1.get(Calendar.DAY_OF_MONTH) + "."
				+ c1.get(Calendar.MONTH) + "." + c1.get(Calendar.YEAR) + " "
				+ InterfaceTextDefaults.getInstance().getDefault("in") + " " + c1.get(Calendar.HOUR_OF_DAY) + ":"
				+ c1.get(Calendar.MINUTE) + "</div></html>";
		time.setText(t);

		String a = Integer.toString(correct) + " %";
		JLabel accuracy = new JLabel();
		t = "<html><div style='font: bold 16pt Arial Narrow; color: rgb(68, 83, 91);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("accuracy")
				+ ": <span style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>" + a + "</span></div></html>";
		accuracy.setText(t);

		int s = (int) (totalTime * 0.001);
		int m = s / 60;
		s = s % 60;
		String min = InterfaceTextDefaults.getInstance().getDefault("min");
		String sec = InterfaceTextDefaults.getInstance().getDefault("sec");

		if (m % 10 == 1)
			if (m / 10 != 1)
				min = InterfaceTextDefaults.getInstance().getDefault("min_1");

		if ((m % 10 == 2) || (m % 10 == 3) || (m % 10 == 4))
			if (m / 10 != 1)
				min = InterfaceTextDefaults.getInstance().getDefault("min_234");

		if (s % 10 == 1)
			if (m / 10 != 1)
				sec = InterfaceTextDefaults.getInstance().getDefault("sec_1");

		if ((s % 10 == 2) || (s % 10 == 3) || (s % 10 == 4))
			if (m / 10 != 1)
				sec = InterfaceTextDefaults.getInstance().getDefault("sec_234");

		min = " " + min + " ";
		sec = " " + sec;

		JLabel reactionTime = new JLabel();
		t = "<html><div style='font: bold 16pt Arial Narrow; color: rgb(68, 83, 91);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("reaction_time")
				+ ": <span style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>" + Integer.toString(m) + min
				+ Integer.toString(s) + sec + "</span></div></html>";
		reactionTime.setText(t);

		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setBackground(new Color(176, 190, 197));
		sep.setForeground(new Color(176, 190, 197));
		sep.setPreferredSize(new Dimension((int) (getPreferredSize().getWidth() * 0.5), 1));

		repeatIcon = new JLabel();
		allResultsIcon = new JLabel();

		ImageIcon icon = Utills
				.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.REPEAT_TASK));
		repeatIcon.setIcon(icon);
		repeatIcon.setName("repeattask");
		repeatIcon.addMouseListener(iconsListener);
		repeatIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ALL_RESULTS));
		allResultsIcon.setIcon(icon);
		allResultsIcon.setName("allresults");
		allResultsIcon.addMouseListener(iconsListener);
		allResultsIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		removeAll();

		setLayout(new GridBagLayout());

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

		add(name, c);

		c.gridy = 1;
		c.insets = new Insets(0, 0, 25, 0);

		add(time, c);

		c.gridy = 2;
		c.insets = new Insets(0, 0, 5, 0);

		add(accuracy, c);

		c.gridy = 3;
		c.insets = new Insets(0, 0, 50, 0);

		add(reactionTime, c);

		c.gridy = 4;

		add(sep, c);

		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 1;
		c.gridy = 5;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 1.0;

		add(repeatIcon, c);

		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.insets = new Insets(0, 60, 0, 0);

		add(allResultsIcon, c);

		revalidate();
		repaint();
	}

	public void showStandartInfo() {
		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 22pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("task").toUpperCase() + "</div></html>";
		heading.setText(t);

		JTextPane t1 = new JTextPane();
		t1.setEditable(false);
		t1.setContentType("text/html;charset=utf-8");
		t1.setText(
				"<html><div style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>" + getText() + "</div></html>");
		t1.setOpaque(false);
		t1.setPreferredSize(new Dimension((int) (getPreferredSize().getWidth() * 0.85),
				40 + Utills.calculateTextHeight(t1.getText(), (int) (getPreferredSize().getWidth() * 0.85), t1)));

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("begin_task"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(200, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showTask(false);
			}
		});

		removeAll();
		setLayout(new GridBagLayout());

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

		add(heading, c);

		c.gridy = 1;
		c.insets = new Insets(40, 20, 0, 0);
		c.weightx = 0.0;

		add(t1, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridy = 2;
		c.insets = new Insets(40, 0, 0, 0);
		c.weighty = 1.0;

		add(start, c);

		revalidate();
		repaint();
	}

	public void showStandartSettings() {

		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("settings") + "</div></html>";
		heading.setText(t);

		JLabel level = new JLabel();
		t = "<html><div style='font: bold 20pt Arial Narrow; color: rgb(68, 83, 91);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("diff") + "</div></html>";
		level.setText(t);

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

		removeAll();
		setLayout(new GridBagLayout());

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

		add(heading, c);

		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(40, 200, 0, 0);
		c.weightx = 0.0;

		add(level, c);

		c.gridy = 2;

		add(easyButton, c);

		c.gridy = 3;
		c.insets = new Insets(10, 200, 0, 0);

		add(middleButton, c);

		c.gridy = 4;

		add(hardButton, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridy = 5;
		c.insets = new Insets(40, 0, 0, 0);
		c.weighty = 1.0;

		add(start, c);

		revalidate();
		repaint();
	}

	class RadioListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "easy":
				setDiff(getEasy());
				break;
			case "middle":
				setDiff(getMiddle());
				break;
			case "hard":
				setDiff(getHard());
				break;
			}
		}
	}

	class IconsMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			switch (l.getName()) {
			case "start":
				start();
				break;
			case "begining":
				pause();
				CustomDialog d1 = new CustomDialog(reabilitation,
						InterfaceTextDefaults.getInstance().getDefault("sure_break_task"),
						InterfaceTextDefaults.getInstance().getDefault("break"),
						InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
				if (d1.getAnswer() == 1) {
					// TODO
					start();
					showTask(true);
				}
				else
					start();
				break;
			case "pause":
				pause();
				break;
			case "repeattask":
				dontShowBreakingDialog = false;
				showTask(false);
				break;
			case "allresults":
				reabilitation.showResults();
				break;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			ImageIcon icon;
			switch (l.getName()) {
			case "start":
				icon = Utills
						.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.START_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "begining":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.BEGINING_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "pause":
				if (!pause) {
					icon = Utills.createImageIcon(
							ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.PAUSE_ROLLOVER));
					l.setIcon(icon);
					l.updateUI();
				}
				break;
			case "repeattask":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.REPEAT_TASK_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "allresults":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ALL_RESULTS_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			ImageIcon icon;
			switch (l.getName()) {
			case "start":
				icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.START));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "begining":
				icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.BEGINING));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "pause":
				if (!pause) {
					icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.PAUSE));
					l.setIcon(icon);
					l.updateUI();
				}
				break;
			case "repeattask":
				icon = Utills
						.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.REPEAT_TASK));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "allresults":
				icon = Utills
						.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ALL_RESULTS));
				l.setIcon(icon);
				l.updateUI();
				break;
			}
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	public String getText() {
		return text;
	}

	public void pause() {
		if (pauseIcon != null && startIcon != null) {
			bottomPanel.remove(pauseIcon);
			GridBagConstraints c = new GridBagConstraints();

			c.anchor = GridBagConstraints.SOUTHEAST;
			c.gridx = 2;
			c.insets = new Insets(25, 40, 0, 0);
			c.weightx = 1.0;

			// bottomPanel.add(startIcon, c);

			// c.gridx = 3;
			// c.weightx = 0.0;

			bottomPanel.add(startIcon, c);
			bottomPanel.revalidate();
			bottomPanel.repaint();
		}
	}

	public void start() {
		if (pauseIcon != null && startIcon != null) {
			bottomPanel.remove(startIcon);
			GridBagConstraints c = new GridBagConstraints();

			c.anchor = GridBagConstraints.SOUTHEAST;
			c.gridx = 2;
			c.insets = new Insets(25, 40, 0, 0);
			c.weightx = 1.0;

			// bottomPanel.add(startIcon, c);

			// c.gridx = 3;
			// c.weightx = 0.0;

			bottomPanel.add(pauseIcon, c);
			bottomPanel.revalidate();
			bottomPanel.repaint();
		}
	}

	public void setDontShowBreakingDialog(boolean dontShowBreakingDialog) {
		this.dontShowBreakingDialog = dontShowBreakingDialog;
	}

	public boolean isDontShowBreakingDialog() {
		return dontShowBreakingDialog;
	}

	public abstract void showInfo();

	public abstract void setDiff(int diff);

	public abstract int getEasy();

	public abstract int getMiddle();

	public abstract int getHard();

	public abstract void showTask(boolean repeat);

	public abstract void showResults();
}
