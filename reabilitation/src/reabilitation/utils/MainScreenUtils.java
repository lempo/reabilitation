package reabilitation.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import customcomponent.CustomButton;
import customcomponent.CustomDialog;
import customcomponent.CustomLabel;
import customcomponent.CustomPasswordField;
import customcomponent.CustomTextField;
import customcomponent.ResultsPanel;
import customcomponent.TooltipLabel;
import customuiandrender.ComboBoxCustomUI;
import customuiandrender.ComboBoxRenderer;
import customuiandrender.DateCellRenderer;
import customuiandrender.IconCellRenderer;
import customuiandrender.PanelCustomUI;
import customuiandrender.ScrollBarCustomUI;
import customuiandrender.TableHeaderRenderer;
import customuiandrender.TaskCellRenderer;
import customuiandrender.TextCellRenderer;
import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import defaults.TextLinkDefaults;
import dialogs.Dialogs;
import exception.DiskPermissionsException;
import exception.HddSerialScriptException;
import exception.ProgramFilesBrokenException;
import exception.ServerConnectionException;
import jdatepicker.DatePicker;
import jdatepicker.JDatePicker;
import listeners.GroupsMouseListener;
import listeners.TasksMouseListener;
import reabilitation.HTTPClient;
import reabilitation.Reabilitation;
import reabilitation.Results;
import reabilitation.TableModel;
import reabilitation.Task;
import reabilitation.TaskGroup;
import reabilitation.UserTableModel;
import tasks.AbstractTask;

public class MainScreenUtils {

	public static void showLoginScreen(JPanel targetPanel, Reabilitation reabilitation) {
		targetPanel.setOpaque(false);
		targetPanel.setLayout(new GridBagLayout());
		CustomLabel heading = new CustomLabel("authorization", 24, new Color(176, 190, 197), true);

		CustomPasswordField pass = new CustomPasswordField(28,
				InterfaceTextDefaults.getInstance().getDefault("password"));
		CustomTextField login = new CustomTextField(28,
				InterfaceTextDefaults.getInstance().getDefault("name_surname_patronymic"));

		CustomButton start = new CustomButton("enter2", new Color(38, 166, 154), 73, 40);
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (HTTPClient.loginSpec(login.getText().trim(), pass.getPass())) {
						Reabilitation.userName = login.getText().trim();
						Reabilitation.userPass = pass.getPass();
						reabilitation.start(true);
					} else if (HTTPClient.loginPatient(login.getText().trim(), pass.getPass())) {
						Reabilitation.userName = login.getText().trim();
						Reabilitation.userPass = pass.getPass();
						reabilitation.start(false);
					} else
						new CustomDialog(reabilitation, "failed_login", "ok", null, true);
				} catch (ServerConnectionException e1) {
					e1.printStackTrace();
					Dialogs.showServerConnectionErrorDialog(e1);
				} catch (ProgramFilesBrokenException e1) {
					e1.printStackTrace();
					Dialogs.showFilesBrokenErrorDialog(e1);
				} catch (HddSerialScriptException e1) {
					e1.printStackTrace();
					Dialogs.showHddSerialErrorDialog(e1);
				}
			}
		});

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
		CustomLabel logo = new CustomLabel(ImageLinkDefaults.Key.KOMPLIMED);
		targetPanel.add(logo, c);

		JPanel container = new JPanel();
		container.setOpaque(false);
		container.setLayout(new GridBagLayout());

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		container.add(heading, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 0, 0);
		c.gridwidth = 2;
		c.gridy = 1;
		c.gridx = 0;
		container.add(login, c);

		c.gridy = 2;
		container.add(pass, c);

		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(10, 0, 0, 0);
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 3;
		container.add(start, c);

		c.insets = new Insets((int) Math.round(Reabilitation.WINDOW_HEIGHT / 3.5), 0, 0, 0);
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		targetPanel.add(container, c);

		CustomLabel copyright = new CustomLabel(ImageLinkDefaults.Key.COPYRIGHT);

		c.insets = new Insets((int) Math.round(Reabilitation.WINDOW_HEIGHT / 15), 0, 0, 0);
		c.gridx = 0;
		c.gridy = 2;
		targetPanel.add(copyright, c);

		targetPanel.revalidate();
		targetPanel.repaint();
	}

	public static void showUsersScreen(JPanel targetPanel, Reabilitation reabilitation) {
		targetPanel.removeAll();
		targetPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		String rows[][] = null;
		try {
			rows = HTTPClient.listPatients();
			if (rows.length == 0)
				return;
		} catch (ServerConnectionException e) {
			e.printStackTrace();
			Dialogs.showServerConnectionErrorDialog(e);
			return;
		} catch (ProgramFilesBrokenException e1) {
			e1.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e1);
			return;
		}

		UserTableModel model = new UserTableModel(rows);
		JTable table = new JTable(model);
		RowSorter<UserTableModel> sorter = new TableRowSorter<UserTableModel>(model);
		table.setRowSorter(sorter);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setBorder(null);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(-1, -1));
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumn(InterfaceTextDefaults.getInstance().getDefault("number"))
				.setCellRenderer(new TextCellRenderer(new Color(161, 136, 127), SwingConstants.CENTER));
		table.getColumn(InterfaceTextDefaults.getInstance().getDefault("password"))
				.setCellRenderer(new TextCellRenderer(new Color(161, 136, 127), SwingConstants.CENTER));
		table.getColumn(InterfaceTextDefaults.getInstance().getDefault("user"))
				.setCellRenderer(new TextCellRenderer(new Color(69, 90, 100), SwingConstants.LEFT));
		table.getColumn(InterfaceTextDefaults.getInstance().getDefault("editing"))
				.setCellRenderer(new IconCellRenderer(SwingConstants.CENTER));
		table.getColumn(InterfaceTextDefaults.getInstance().getDefault("deleting"))
				.setCellRenderer(new IconCellRenderer(SwingConstants.CENTER));
		table.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
		// ajust rows and columns
		int tableHeight = 0;
		int tableWidth = 0;
		for (int row = 0; row < table.getRowCount(); row++) {
			int rowHeight = table.getRowHeight();
			for (int column = 0; column < table.getColumnCount(); column++) {
				Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
				rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
			}
			table.setRowHeight(row, rowHeight);
			tableHeight += rowHeight;
		}
		TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			Component comp = table.prepareRenderer(table.getCellRenderer(0, column), 0, column);
			TableColumn col = columnModel.getColumn(column);
			TableCellRenderer headerRenderer = col.getHeaderRenderer();
			if (headerRenderer == null) {
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			}
			Object headerValue = col.getHeaderValue();
			Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0,
					column);
			int maxWidth;
			if (column == 2)
				maxWidth = 500;
			else
				maxWidth = Math.max(comp.getPreferredSize().width, headerComp.getPreferredSize().width + 20);
			table.getColumnModel().getColumn(column).setPreferredWidth(maxWidth);
			tableWidth += maxWidth;
		}

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = table.rowAtPoint(evt.getPoint());
				int col = table.columnAtPoint(evt.getPoint());
				if (col == 4) {
					try {
						HTTPClient.deletePatient((String) table.getModel().getValueAt(row, 2),
								(String) table.getModel().getValueAt(row, 1));
					} catch (ServerConnectionException e) {
						e.printStackTrace();
						Dialogs.showServerConnectionErrorDialog(e);
					} catch (ProgramFilesBrokenException e) {
						e.printStackTrace();
						Dialogs.showFilesBrokenErrorDialog(e);
					}
					reabilitation.showUsers();
				}

				if (col == 3) {
					reabilitation.editUserOrNewUser((String) table.getModel().getValueAt(row, 2),
							(String) table.getModel().getValueAt(row, 1));
				}
			}
		});

		int maxWidth = (int) (Math.min(tableWidth, Math.round(Reabilitation.WINDOW_WIDTH * 0.9)) + 10);
		int maxHeight = (int) Math.min(tableHeight, Math.round(Reabilitation.WINDOW_HEIGHT * 0.6));

		table.setPreferredScrollableViewportSize(new Dimension(maxWidth, maxHeight));
		JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
		scroll.getHorizontalScrollBar().setUI(new ScrollBarCustomUI());
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setOpaque(true);
		scroll.setMaximumSize(new Dimension(maxWidth, maxHeight));

		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets((int) Math.round(Reabilitation.WINDOW_HEIGHT * 0.1), 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 1.0;
		targetPanel.add(scroll, c);

		targetPanel.revalidate();
		targetPanel.repaint();
	}

	public static JLabel getGroupLabel(TaskGroup taskGroup, int order, GroupsMouseListener groupsMouseListener) {
		JLabel group = new TooltipLabel();
		group.setIcon(Utils.createImageIcon(taskGroup.getImage()));
		group.setHorizontalTextPosition(JLabel.CENTER);
		group.setVerticalTextPosition(JLabel.BOTTOM);
		group.setName(Integer.toString(order));
		group.addMouseListener(groupsMouseListener);
		group.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		group.createToolTip();
		group.setToolTipText(
				"<html><div style='font: bold 14pt Arial Narrow; color: #455a64; padding: 10px; padding-top: 13px; padding-bottom: 5px;'>"
						+ taskGroup.getName().toUpperCase() + "</div>"
						+ "<div style='font: 13pt Arial Narrow; color: #455a64; padding: 10px;  padding-top: 0px; padding-bottom: 13px;'>"
						+ taskGroup.getToolTipText() + "</div></html>");
		return group;
	}

	public static void showGroupsScren(JPanel targetPanel, TaskGroup[] taskGroups,
			GroupsMouseListener groupsMouseListener, Reabilitation reabilitation) {
		JPanel p = new JPanel();
		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");

		try {
			text.setPage(reabilitation.getClass()
					.getResource(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.WELCOME)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		text.setOpaque(false);
		p.add(text);
		final Dimension textDimension = new Dimension((int) (Reabilitation.WINDOW_WIDTH * 0.9), 175);
		text.setPreferredSize(textDimension);
		text.setMaximumSize(textDimension);
		text.setMinimumSize(textDimension);
		p.setUI(new PanelCustomUI(true));

		targetPanel.removeAll();
		targetPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(38, 20, 0, 20);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		JLabel[] groups = new JLabel[taskGroups.length];
		for (int i = 0; i < taskGroups.length; i++)
			groups[i] = getGroupLabel(taskGroups[i], i, groupsMouseListener);
		int i;
		for (i = 0; i < Math.ceil((double) groups.length / 5); i++) {
			for (int j = 0; j < 5; j++) {
				if ((i * 5 + j) == groups.length)
					break;
				c.gridx = j;
				c.gridy = i;

				targetPanel.add(groups[i * 5 + j], c);
			}
		}

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = i;
		c.insets = new Insets(10, 20, 10, 20);

		targetPanel.add(p, c);

		targetPanel.revalidate();
		targetPanel.repaint();
	}

	public static void showTasksScren(JPanel targetPanel, TaskGroup taskGroup, Task[] tasks,
			TasksMouseListener tasksMouseListener) {
		CustomLabel image = new CustomLabel(taskGroup.getBigImage());
		
		final int imageWidth = (int) image.getPreferredSize().getWidth();
		final int textWidth = (int) (Reabilitation.WINDOW_WIDTH * 0.85 - imageWidth);
		final int textHeight = 300;
		final Dimension textDimension = new Dimension(textWidth, textHeight);

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");
		text.setText("<html><div style='font: 15pt Arial Narrow; color: rgb(68, 83, 91);'>" + taskGroup.getText()
				+ "</div></html>");
		text.setOpaque(false);
		text.setPreferredSize(textDimension);

		JScrollPane scroll = new JScrollPane(text);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setPreferredSize(textDimension);
		scroll.setMinimumSize(textDimension);
		scroll.setMaximumSize(textDimension);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());

		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JLabel[] tasksLabels = new JLabel[tasks.length];

		ImageIcon icon;
		for (int j = 0; j < Math.ceil((double) tasks.length / 2); j++) {
			for (int k = 0; k < 2; k++) {
				if ((j * 2 + k) == tasks.length)
					break;
				icon = Utils.createImageIcon(tasks[j * 2 + k].getImage());
				tasksLabels[j * 2 + k] = new JLabel(
						"<html><div style='font: 19pt Arial Narrow; color: rgb(115, 84, 73); text-align: center; margin-bottom: 5px; margin-top: 5px;'>"
								+ InterfaceTextDefaults.getInstance().getDefault("task_number")
								+ Integer.toString(j * 2 + k + 1) + ". " + tasks[j * 2 + k].getName().toUpperCase()
								+ "</div><div style='font: 12pt Arial Narrow; color: rgb(115, 84, 73); text-align: left;'>"
								+ tasks[j * 2 + k].getShortText() + "</div></html>");
				tasksLabels[j * 2 + k].setIcon(icon);
				tasksLabels[j * 2 + k].setHorizontalTextPosition(JLabel.CENTER);
				tasksLabels[j * 2 + k].setVerticalTextPosition(JLabel.BOTTOM);
				tasksLabels[j * 2 + k].setVerticalAlignment(SwingConstants.TOP);
				tasksLabels[j * 2 + k]
						.setPreferredSize(new Dimension(icon.getIconWidth() + 120, (int) (icon.getIconHeight() + 100)));
				tasksLabels[j * 2 + k].setName(Integer.toString(j * 2 + k));
				tasksLabels[j * 2 + k].addMouseListener(tasksMouseListener);
				tasksLabels[j * 2 + k].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				c.anchor = GridBagConstraints.NORTH;
				c.fill = GridBagConstraints.NONE;
				c.gridheight = 1;
				c.gridwidth = 1;
				c.gridx = k;
				c.gridy = j;
				c.insets = new Insets(0, 0, 0, 0);
				c.ipadx = 0;
				c.ipady = 0;
				c.weightx = 1.0;
				c.weighty = 0.0;

				p.add(tasksLabels[j * 2 + k], c);
			}
		}
		
		final int tasksScrollHeight = (int) (Reabilitation.WINDOW_HEIGHT * 0.7 - text.getPreferredSize().getHeight());
		final Dimension tasksScrollDimension = new Dimension(textWidth, tasksScrollHeight);

		JScrollPane scrollTasks = new JScrollPane(p);
		scrollTasks.setPreferredSize(tasksScrollDimension);
		scrollTasks.setMaximumSize(tasksScrollDimension);
		scrollTasks.setMinimumSize(tasksScrollDimension);
		scrollTasks.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollTasks.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollTasks.getViewport().setOpaque(false);
		scrollTasks.setOpaque(false);
		scrollTasks.setBorder(null);
		scrollTasks.getVerticalScrollBar().setUI(new ScrollBarCustomUI());

		targetPanel.removeAll();
		targetPanel.setLayout(new GridBagLayout());

		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(40, 40, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		targetPanel.add(image, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.insets = new Insets(40, 40, 0, 40);
		c.weightx = 1.0;

		targetPanel.add(scroll, c);

		c.gridy = 1;
		c.weightx = 0.0;

		targetPanel.add(scrollTasks, c);

		targetPanel.revalidate();
		targetPanel.repaint();
	}

	public static void showTaskInfoScren(JPanel targetPanel, Task task, ActionListener showTaskListener) {
		CustomLabel image = new CustomLabel(task.getBigImage());

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");
		text.setText("<html><div style='font: bold 22pt Arial Narrow; color: rgb(115, 84, 73); padding-bottom: 20 px'>"
				+ task.getName().toUpperCase()
				+ "</div><div  style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>" + task.getLongText()
				+ "</div></html>");
		text.setOpaque(false);
		
		final int imaheWidth = (int) image.getPreferredSize().getWidth();
		final int textWidth = (int) (Reabilitation.WINDOW_WIDTH * 0.85 - imaheWidth);
		final int textHeight = 100 + Utils.calculateTextHeight(text.getText(), textWidth, text);
		final Dimension textDimension = new Dimension(textWidth, textHeight);
		
		text.setPreferredSize(textDimension);
		text.setMaximumSize(textDimension);
		text.setMinimumSize(textDimension);

		CustomButton start = new CustomButton("begin_task", new Color(38, 166, 154), 200, 35);
		start.addActionListener(showTaskListener);

		GridBagConstraints c = new GridBagConstraints();

		targetPanel.removeAll();
		targetPanel.setLayout(new GridBagLayout());

		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(-180, 40, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		targetPanel.add(image, c);

		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.insets = new Insets(-200, 40, 0, 40);
		c.weightx = 1.0;

		targetPanel.add(text, c);

		c.gridy = 1;
		c.insets = new Insets(40, 40, 0, 40);
		targetPanel.add(start, c);

		targetPanel.revalidate();
		targetPanel.repaint();
	}

	public static void showAboutScreen(JPanel targetPanel, Reabilitation reabilitation) {
		// read version
		String v = null;
		try {
			v = Utils.getVersion();
		} catch (ProgramFilesBrokenException e4) {
			e4.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e4);
		}
		String dd = null;
		try {
			dd = Utils.getVersionDate();
		} catch (ProgramFilesBrokenException e4) {
			e4.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e4);
		}

		// read config
		String name = null;
		String keyNumber = null;
		try {
			name = Utils.getLicenceUserName();
			keyNumber = Utils.getLicenceKey();
		} catch (ProgramFilesBrokenException e3) {
			e3.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e3);
		}

		// days left
		int days = 0;
		try {
			days = HTTPClient.daysLeft(keyNumber, name);
		} catch (ServerConnectionException e2) {
			e2.printStackTrace();
			Dialogs.showServerConnectionErrorDialog(e2);
		} catch (DiskPermissionsException e1) {
			e1.printStackTrace();
			Dialogs.showDiskPermissionsErrorDialog(e1);
		} catch (ProgramFilesBrokenException e1) {
			e1.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e1);
		} catch (HddSerialScriptException e1) {
			e1.printStackTrace();
			Dialogs.showHddSerialErrorDialog(e1);
		}

		String from = null;
		try {
			from = HTTPClient.getFrom(keyNumber, name);
		} catch (ServerConnectionException e2) {
			e2.printStackTrace();
			Dialogs.showServerConnectionErrorDialog(e2);
		} catch (DiskPermissionsException e1) {
			e1.printStackTrace();
			Dialogs.showDiskPermissionsErrorDialog(e1);
		} catch (ProgramFilesBrokenException e1) {
			e1.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e1);
		} catch (HddSerialScriptException e1) {
			e1.printStackTrace();
			Dialogs.showHddSerialErrorDialog(e1);
		}

		JLabel key = new JLabel();
		String t = "<html><div style='font: bold 14pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("key")
				+ ": <span style='font: 15pt Arial Narrow; color: rgb(115, 84, 73);'>" + keyNumber
				+ "</span></div></html>";
		key.setText(t);

		JLabel licenze = new JLabel();
		t = "<html><div style='font: bold 14pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("license")
				+ ": <br><span style='font: 15pt Arial Narrow; color: rgb(115, 84, 73);'>" + name + " " + from
				+ "</span></div></html>";
		licenze.setText(t);

		String day = InterfaceTextDefaults.getInstance().getDefault("day");
		if (days % 10 == 1)
			if (days / 10 != 1)
				day = InterfaceTextDefaults.getInstance().getDefault("day_1");
		if ((days % 10 == 2) || (days % 10 == 3) || (days % 10 == 4))
			if (days / 10 != 1)
				day = InterfaceTextDefaults.getInstance().getDefault("day_234");
		JLabel left = new JLabel();
		t = "<html><div style='font: bold 14pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("days_left")
				+ ": <span style='font: 15pt Arial Narrow; color: rgb(115, 84, 73);'>" + days + " " + day
				+ "</span></div></html>";
		left.setText(t);

		JLabel version = new JLabel();
		t = "<html><div style='font: bold 14pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("version")
				+ ": <span style='font: 15pt Arial Narrow; color: rgb(115, 84, 73);'>" + v + " "
				+ InterfaceTextDefaults.getInstance().getDefault("from") + " " + dd + "</span></div></html>";
		version.setText(t);

		CustomButton checkUpdates = new CustomButton("check_updates", new Color(38, 166, 154), 252, 40);
		ImageIcon icon = Utils
				.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.CIRCLE_ARROW));
		checkUpdates.setIcon(icon);
		checkUpdates.setIconTextGap(20);
		checkUpdates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ask DB for updates
				String location = null;
				try {
					location = HTTPClient.getVersion(Utils.getVersionDate());
				} catch (ServerConnectionException e2) {
					e2.printStackTrace();
					Dialogs.showServerConnectionErrorDialog(e2);
				} catch (ProgramFilesBrokenException e1) {
					e1.printStackTrace();
					Dialogs.showFilesBrokenErrorDialog(e1);
				}
				if (location != null) {
					// update
					// show dialog
					CustomDialog d1 = new CustomDialog(reabilitation, "do_update", "yes", "no", true);
					if (d1.getAnswer() == 1) {
						try {
							System.out.println(location);
							Process proc = Runtime.getRuntime().exec("java -jar updater.jar " + location);
							System.exit(0);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else
						return;
				} else {
					// don't update
					// show dialog
					new CustomDialog(reabilitation, "no_updates", "ok", null, true);
				}
			}
		});

		JCheckBox checkAuto = new JCheckBox("<html><div style='font: 15pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("check_updates_auto") + "</div></html>");
		checkAuto.setOpaque(false);
		checkAuto.setSelected(Utils.getCheckUpdatesAuto());
		checkAuto.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String s = Utils.readFile(Utils.getFilePath() + "/config");
				if (checkAuto.isSelected())
					s = s.replaceAll("<checkUpdatesAuto>.*</checkUpdatesAuto>",
							"<checkUpdatesAuto>true</checkUpdatesAuto>");
				else
					s = s.replaceAll("<checkUpdatesAuto>.*</checkUpdatesAuto>",
							"<checkUpdatesAuto>false</checkUpdatesAuto>");
				Utils.writeFile(s, Utils.getFilePath() + "/config");
			}
		});

		CustomLabel about = new CustomLabel("about", 22, new Color(115, 84, 73), true);

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");

		try {
			text.setPage(reabilitation.getClass()
					.getResource(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.ABOUT)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JScrollPane scroll = new JScrollPane(text);
		scroll.setPreferredSize(
				new Dimension((int) (Reabilitation.WINDOW_WIDTH * 0.95), (int) (Reabilitation.WINDOW_HEIGHT * 0.45)));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());

		targetPanel.removeAll();
		targetPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(40, 40, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		targetPanel.add(key, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.insets = new Insets(40, 0, 0, 0);

		targetPanel.add(version, c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(20, 40, 0, 0);
		c.weightx = 0.0;

		targetPanel.add(licenze, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.insets = new Insets(20, 0, 0, 0);

		targetPanel.add(checkUpdates, c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(20, 40, 0, 0);

		targetPanel.add(left, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(20, 0, 0, 0);

		targetPanel.add(checkAuto, c);

		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setBackground(new Color(176, 190, 197));
		sep.setForeground(new Color(176, 190, 197));
		sep.setPreferredSize(new Dimension((int) (scroll.getPreferredSize().getWidth() * 0.98), 1));

		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(20, 40, 0, 20);

		targetPanel.add(sep, c);

		c.gridy = 4;
		c.insets = new Insets(20, 40, 0, 0);

		targetPanel.add(about, c);

		c.gridy = 5;
		c.insets = new Insets(0, 20, 0, 0);

		targetPanel.add(scroll, c);

		targetPanel.revalidate();
		targetPanel.repaint();
	}

	public static void showHelpScreen(JPanel targetPanel, Reabilitation reabilitation) {
		CustomLabel faq = new CustomLabel("faq", 22, new Color(115, 84, 73), false);

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");

		try {
			text.setPage(reabilitation.getClass()
					.getResource(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.FAQ)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		targetPanel.removeAll();
		targetPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
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

		targetPanel.add(faq, c);

		JScrollPane scroll = new JScrollPane(text);
		scroll.setPreferredSize(
				new Dimension((int) (Reabilitation.WINDOW_WIDTH * 0.95), (int) (Reabilitation.WINDOW_HEIGHT * 0.7)));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());

		c.gridy = 1;

		targetPanel.add(scroll, c);

		targetPanel.revalidate();
		targetPanel.repaint();
	}

	public static AbstractTask showTaskScreen(JPanel targetPanel, TaskGroup taskGroup, Task task,
			Reabilitation reabilitation) {
		String s = task.getClassName();
		Class c;
		Class[] intArgsClass = new Class[] { int.class, int.class, String.class, Reabilitation.class, String.class,
				String.class, String.class, String.class };
		// TODO �� �� �����
		Integer h = new Integer((int) (Reabilitation.WINDOW_HEIGHT * 0.75));
		Integer w = new Integer(970);
		String t1 = task.getLongLongText();
		Object[] intArgs = new Object[] { w, h, t1, reabilitation, Reabilitation.userName, Reabilitation.userPass,
				task.getName(), taskGroup.getName() };
		Constructor intArgsConstructor = null;
		AbstractTask p = null;
		try {
			c = Class.forName(s);
			intArgsConstructor = c.getConstructor(intArgsClass);
			p = (AbstractTask) Utils.createObject(intArgsConstructor, intArgs);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}

		targetPanel.removeAll();
		targetPanel.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();

		c1.anchor = GridBagConstraints.WEST;
		c1.fill = GridBagConstraints.NONE;
		c1.gridheight = 1;
		c1.gridwidth = GridBagConstraints.REMAINDER;
		c1.gridx = 0;
		c1.gridy = 0;
		c1.insets = new Insets(30, 0, 0, 0);
		c1.ipadx = 0;
		c1.ipady = 0;
		c1.weightx = 0.0;
		c1.weighty = 0.0;

		targetPanel.add(p, c1);

		targetPanel.revalidate();
		targetPanel.repaint();

		return p;
	}

	private static JScrollPane createResultsTable(String[][] rows, int width, int height) {
		TableModel model = new TableModel(rows);
		JTable table = new JTable(model);
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
		scroll.getHorizontalScrollBar().setUI(new ScrollBarCustomUI());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setBorder(null);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(-1, -1));
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumn(InterfaceTextDefaults.getInstance().getDefault("date"))
				.setCellRenderer(new DateCellRenderer("dd.MM.yyyy"));
		table.getColumn(InterfaceTextDefaults.getInstance().getDefault("exersize"))
				.setCellRenderer(new TextCellRenderer(new Color(69, 90, 100), SwingConstants.CENTER));
		table.getColumn(InterfaceTextDefaults.getInstance().getDefault("result"))
				.setCellRenderer(new TextCellRenderer(new Color(69, 90, 100), SwingConstants.CENTER));
		table.getColumn(InterfaceTextDefaults.getInstance().getDefault("task")).setCellRenderer(new TaskCellRenderer());
		table.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
		// ajust rows and columns
		int tableHeight = 0;
		int tableWidth = 0;
		for (int row = 0; row < table.getRowCount(); row++) {
			int rowHeight = table.getRowHeight();
			for (int column = 0; column < table.getColumnCount(); column++) {
				Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
				rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
			}
			table.setRowHeight(row, rowHeight);
			tableHeight += rowHeight;
		}
		TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			Component comp = table.prepareRenderer(table.getCellRenderer(0, column), 0, column);
			TableColumn col = columnModel.getColumn(column);
			TableCellRenderer headerRenderer = col.getHeaderRenderer();
			if (headerRenderer == null) {
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			}
			Object headerValue = col.getHeaderValue();
			Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, false, false, 0,
					column);
			int maxWidth = Math.max(comp.getPreferredSize().width, headerComp.getPreferredSize().width + 20);
			table.getColumnModel().getColumn(column).setPreferredWidth(maxWidth);
			tableWidth += maxWidth;
		}
		table.setPreferredScrollableViewportSize(
				new Dimension(Math.min(width, tableWidth), Math.min(height, tableHeight)));
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setOpaque(false);
		return scroll;
	}

	private static ResultsPanel createResultsPanel(String[][] rows, int width, int height) {
		ArrayList<String> a = new ArrayList<String>();
		for (int i = 0; i < rows.length; i++)
			if (!a.contains(rows[i][2]))
				a.add(rows[i][2]);
		Results[] r = new Results[a.size()];
		for (int i = 0; i < a.size(); i++) {
			TreeMap<Date, Integer> map = new TreeMap<Date, Integer>();
			for (int j = 0; j < rows.length; j++)
				if (rows[j][2].equals(a.get(i))) {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date result = null;
					try {
						result = df.parse(rows[j][0]);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					map.put(result, Integer.parseInt(rows[j][3]));
					rows[j][2] += "_" + ResultsPanel.getColors()[i].getRed();
					rows[j][2] += "," + ResultsPanel.getColors()[i].getGreen();
					rows[j][2] += "," + ResultsPanel.getColors()[i].getBlue();
				}
			r[i] = new Results(map, a.get(i));
		}
		ResultsPanel resultsPanel = new ResultsPanel(r, width, height);
		return resultsPanel;
	}

	public static void showResults(JPanel targetPanel, String[][] rows, String[][] filteredRows, String[] taskNames, int selectedTask,
			Date filterDate1, Date filterDate2) {
		if (filteredRows.length == 0)
			return;

		String[][] rowsCopy = new String[filteredRows.length][filteredRows[0].length];
		for (int i = 0; i < filteredRows.length; i++)
			for (int j = 0; j < filteredRows[0].length; j++)
				rowsCopy[i][j] = new String(filteredRows[i][j]);

		targetPanel.removeAll();
		targetPanel.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();

		CustomLabel subHeading = new CustomLabel("results_dynamic", 22, new Color(115, 84, 73), false);

		c1.anchor = GridBagConstraints.WEST;
		c1.fill = GridBagConstraints.NONE;
		c1.gridheight = 1;
		c1.gridwidth = GridBagConstraints.REMAINDER;
		c1.gridx = 0;
		c1.gridy = 0;
		c1.insets = new Insets(20, 40, 0, 0);
		c1.ipadx = 0;
		c1.ipady = 0;
		c1.weightx = 0.0;
		c1.weighty = 0.0;

		targetPanel.add(subHeading, c1);

		CustomLabel filter = new CustomLabel("filter", 16, new Color(70, 110, 122), true);

		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		p.setPreferredSize(new Dimension(Reabilitation.WINDOW_WIDTH, 40));

		c1.gridwidth = 1;
		c1.insets = new Insets(10, 40, 0, 0);
		c1.weightx = 1.0;
		p.add(filter, c1);

		JComboBox tasks = new JComboBox(taskNames);
		tasks.setRenderer(new ComboBoxRenderer());
		tasks.setUI(new ComboBoxCustomUI(taskNames));
		tasks.setOpaque(false);
		tasks.setSelectedItem(taskNames[selectedTask]);
		tasks.setMaximumRowCount(tasks.getModel().getSize());

		c1.insets = new Insets(10, 0, 0, 0);
		c1.gridx = 1;
		p.add(tasks, c1);

		CustomLabel interval = new CustomLabel("interval", 16, new Color(68, 83, 91), false);

		c1.gridx = 2;
		p.add(interval, c1);

		DatePicker date1 = new JDatePicker();
		if (filterDate1 != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(filterDate1);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			date1.getModel().setDate(year, month, day);
			date1.getModel().setSelected(true);
		}

		c1.gridx = 3;
		p.add((Component) date1, c1);

		JLabel def = new JLabel();
		def.setOpaque(true);
		def.setBackground(new Color(176, 190, 197));
		def.setPreferredSize(new Dimension(20, 5));

		c1.gridx = 4;
		p.add(def, c1);

		DatePicker date2 = new JDatePicker();
		if (filterDate2 != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(filterDate2);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			date2.getModel().setDate(year, month, day);
			date2.getModel().setSelected(true);
		}

		c1.gridx = 5;
		p.add((Component) date2, c1);

		CustomButton show = new CustomButton("show", new Color(38, 166, 154), 100, 30);
		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Date selectedDate1 = null, selectedDate2 = null;
				Calendar selectedValue1 = (Calendar) date1.getModel().getValue();
				if (selectedValue1 != null)
					selectedDate1 = selectedValue1.getTime();
				Calendar selectedValue2 = (Calendar) date2.getModel().getValue();
				if (selectedValue2 != null)
					selectedDate2 = selectedValue2.getTime();
				String[][] r = filterResults(rows, tasks.getSelectedItem().toString(), selectedDate1, selectedDate2);
				showResults(targetPanel, rows, r, taskNames, tasks.getSelectedIndex(), selectedDate1, selectedDate2);
			}
		});

		c1.insets = new Insets(10, 0, 0, 20);
		c1.gridx = 6;
		p.add(show, c1);

		c1.fill = GridBagConstraints.HORIZONTAL;
		c1.gridwidth = GridBagConstraints.REMAINDER;
		c1.gridx = 0;
		c1.gridy = 1;
		c1.insets = new Insets(0, 0, 0, 0);
		c1.weightx = 0.0;

		targetPanel.add(p, c1);

		ResultsPanel resultsPanel = createResultsPanel(rowsCopy, 400, 500);

		c1.anchor = GridBagConstraints.NORTHWEST;
		c1.fill = GridBagConstraints.NONE;
		c1.gridwidth = 1;
		c1.gridy = 2;
		c1.insets = new Insets(30, 40, 0, 0);
		c1.weightx = 1.0;
		targetPanel.add(resultsPanel, c1);

		JScrollPane scroll = createResultsTable(rowsCopy, 400, 400);

		c1.gridwidth = GridBagConstraints.REMAINDER;
		c1.insets = new Insets(60, 0, 0, 0);
		c1.gridx = 1;
		c1.gridy = 2;
		targetPanel.add(scroll, c1);

		targetPanel.revalidate();
		targetPanel.repaint();
	}
	
	private static String[][] filterResults(String[][] rows, String taskName, Date date1, Date date2) {
		// InterfaceTextDefaults.getInstance().getDefault("all_tasks")
		ArrayList<String[]> r = new ArrayList<String[]>();
		if ((date1 != null) && (date2 != null) && (taskName != null)) {
			for (int j = 0; j < rows.length; j++) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date result = null;
				try {
					result = df.parse(rows[j][0]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if ((rows[j][2].equals(taskName)
						|| taskName.equals(InterfaceTextDefaults.getInstance().getDefault("all_tasks")))
						&& result.after(date1) && result.before(date2))
					r.add(rows[j]);
			}
		} else if (taskName != null) {
			for (int j = 0; j < rows.length; j++)
				if (rows[j][2].equals(taskName)
						|| taskName.equals(InterfaceTextDefaults.getInstance().getDefault("all_tasks")))
					r.add(rows[j]);
		} else if ((date1 != null) && (date2 != null)) {
			for (int j = 0; j < rows.length; j++) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date result = null;
				try {
					result = df.parse(rows[j][0]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (result.after(date1) && result.before(date2))
					r.add(rows[j]);
			}
		}

		String r1[][];
		r1 = new String[r.size()][];

		for (int i = 0; i < r.size(); i++)
			r1[i] = r.get(i);

		return r1;
	}
	
	public static void showEditUserOrNewUser(JPanel targetPanel, 
			String userOldName, 
			String userOldPass,
			Reabilitation reabilitation) {
		boolean newUser = false;
		if (userOldName == null && userOldPass == null)
			newUser = true;
		
		targetPanel.removeAll();
		targetPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		
		CustomLabel passwordLabel = new CustomLabel("password", 19, new Color(70, 110, 122), true);
		CustomLabel nameLabel = new CustomLabel("name_surname_patronymic", 19, new Color(70, 110, 122), true);

		JTextField passwordField;
		if (newUser)
			passwordField = new CustomPasswordField(40, "");
		else {
			passwordField = new CustomTextField(40, "");
			passwordField.setText(userOldPass);
		}

		CustomTextField nameField = new CustomTextField(40, "");
		nameField.setText(userOldName);

		CustomButton cancel = new CustomButton("cancel", new Color(239, 83, 80), 180, 35);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reabilitation.showUsers();
			}
		});

		CustomButton save = new CustomButton("save", new Color(38, 166, 154), 180, 35);
		boolean creatingNewUser = newUser;
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!nameField.getText().trim().equals("") && !passwordField.getText().trim().equals("")) {
					try {
						if (creatingNewUser) {
							if (!HTTPClient.newPatient(nameField.getText().trim(),
									((CustomPasswordField) passwordField).getPass().trim()))
								new CustomDialog(reabilitation, "user_exists", "ok", null, true);
						} else {
							if (!HTTPClient.editPatient(userOldName, userOldPass, nameField.getText().trim(),
									passwordField.getText().trim()))
								new CustomDialog(reabilitation, "user_exists", "ok", null, true);
						}
					} catch (ServerConnectionException e1) {
						e1.printStackTrace();
						Dialogs.showServerConnectionErrorDialog(e1);
					} catch (ProgramFilesBrokenException e1) {
						e1.printStackTrace();
						Dialogs.showFilesBrokenErrorDialog(e1);
					}
					reabilitation.showUsers();
				} else
					new CustomDialog(reabilitation, "fill_all_fields", "ok", null, true);
			}
		});

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 10, 10, 10);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		targetPanel.add(nameLabel, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;

		targetPanel.add(nameField, c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;

		targetPanel.add(passwordLabel, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;

		targetPanel.add(passwordField, c);

		c.gridwidth = 1;
		c.gridy = 2;

		targetPanel.add(cancel, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 2;

		targetPanel.add(save, c);

		targetPanel.revalidate();
		targetPanel.repaint();
	}
}
