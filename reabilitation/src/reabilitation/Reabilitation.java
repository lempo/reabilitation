package reabilitation;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import jdatepicker.DatePicker;
import jdatepicker.JDatePicker;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import customcomponent.CustomDialog;
import customcomponent.CustomLabel;
import customcomponent.CustomPanel;
import customcomponent.CustomPasswordField;
import customcomponent.CustomTextField;
import customcomponent.MenuPanel;
import customcomponent.ResultsPanel;
import customuiandrender.ButtonCustomUI;
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
import tasks.AbstractTask;

public class Reabilitation extends JFrame {

	private static final long serialVersionUID = 2193812198591285704L;

	private String userName = "";
	private String userCardNumber = "";

	private Reabilitation reabilitation;
	public AbstractTask showedTask = null;

	private int width = 1020;
	private int height = 790;

	// main panel
	BgPanel panel;

	// panel with program menu
	CustomPanel menuPanel;

	// panel with current content
	JPanel actualPanel;

	// panel with header and small menu
	JPanel headerPanel;

	// panel with window buttons
	JPanel windowPanel;

	// menu icons
	JLabel exitIcon;
	JLabel aboutIcon;
	JLabel helpIcon;
	JLabel tasksIcon;
	JLabel smallMenuAboutIcon;
	JLabel smallMenuBeginingIcon;
	JLabel smallMenuEndIcon;
	JLabel smallMenuResultsIcon;
	JLabel closeIcon;
	JLabel restoreIcon;
	JLabel hideIcon;

	// ������ �������� (� ������� ����) �� �����������
	private int logoSpace = 289;
	// ������ ����� �������� ����
	private int iconsSpace = 10;

	// ��� �������������� ����
	private Point lastDragPosition;

	private TaskGroup[] taskGroups;
	private Task[] tasks;

	private int currentTaskGroup = 0;
	private int currentTask = 0;

	Popup popup = null;
	MenuPanel popupMenuPanel;

	private String[][] resultsRows;

	/** Current method name */
	private String currentMethod;
	/** Parameter types of current method */
	Class[] paramTypes;
	/** Arguments of current method */
	Object[] args;

	private boolean resized = false;

	private MouseListener draggingMouseListener;
	private MouseMotionListener draggingMouseMotionListener;

	public Reabilitation() {
		super("Reabilitation");
		reabilitation = this;
		
		if (Utills.getCheckUpdatesAuto()) {
			String location = null;
			try {
				location = HTTPClient.getVersion(Utills.getVersionDate());
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
				CustomDialog d1 = new CustomDialog(reabilitation,
						InterfaceTextDefaults.getInstance().getDefault("do_update"),
						InterfaceTextDefaults.getInstance().getDefault("yes"),
						InterfaceTextDefaults.getInstance().getDefault("no"), false);
				if (d1.getAnswer() == 1) {
					try {
						Process proc = Runtime.getRuntime().exec("java -jar updater.jar " + location);
						System.exit(0);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else
					return;
			}
		}
		
		setBounds(50, 50, width, height);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// ������ ����������� ������ ���������
		this.setUndecorated(true);

		InternalEventHandler internalEventHandler = new InternalEventHandler();
		long eventMask = MouseEvent.MOUSE_PRESSED;
		Toolkit.getDefaultToolkit().addAWTEventListener(internalEventHandler, eventMask);

		draggingMouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				lastDragPosition = e.getLocationOnScreen();
			}
		};

		draggingMouseMotionListener = new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {

				Point currentDragPosition = e.getLocationOnScreen();
				int deltaX = currentDragPosition.x - lastDragPosition.x;
				int deltaY = currentDragPosition.y - lastDragPosition.y;
				if (deltaX != 0 || deltaY != 0) {
					int x = getLocation().x + deltaX;
					int y = getLocation().y + deltaY;
					setLocation(x, y);
					lastDragPosition = currentDragPosition;
				}
			}
		};

		showFirstScreen();
	}

	/**
	 * ���������� ��������� ����� ��� �������� ��� �����������
	 * 
	 * @param specLogged
	 *            ���� ����, ��� ��������� ����������
	 */
	private void start(boolean specLogged) {
		panel = new BgPanel(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.BACKGROUND));

		menuPanel = new CustomPanel(new Color(55, 71, 79));
		menuPanel.setOpaque(false);
		createMainMenu();

		actualPanel = new JPanel();
		actualPanel.setOpaque(false);
		createWindowButtons();
		if (!resized) {
			windowPanel.addMouseListener(draggingMouseListener);
			windowPanel.addMouseMotionListener(draggingMouseMotionListener);
		}

		headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		headerPanel.setPreferredSize(new Dimension(width, 60));

		panel.setDoubleBuffered(true);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// panel.add(Box.createVerticalStrut(10));
		panel.add(windowPanel);
		// panel.add(Box.createVerticalStrut(15));
		panel.add(menuPanel);
		// panel.add(Box.createVerticalStrut(15));
		panel.add(headerPanel);
		panel.add(actualPanel);
		panel.add(Box.createVerticalStrut(7));
		setContentPane(panel);

		if (specLogged)
			showUsers();
		else
			showGroups();
	}

	public void createWindowButtons() {
		windowPanel = new JPanel();
		windowPanel.setOpaque(false);

		closeIcon = new JLabel();
		restoreIcon = new JLabel();
		hideIcon = new JLabel();
		WindowMouseListener l = new WindowMouseListener();

		ImageIcon icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.CLOSE));
		closeIcon.setIcon(icon);
		closeIcon.setName("close");
		closeIcon.addMouseListener(l);
		closeIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.RESTORE));
		restoreIcon.setIcon(icon);
		restoreIcon.setName("restore");
		restoreIcon.addMouseListener(l);
		restoreIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		restoreIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.HIDE));
		hideIcon.setIcon(icon);
		hideIcon.setName("hide");
		hideIcon.addMouseListener(l);
		hideIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		hideIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		windowPanel.removeAll();
		windowPanel.setPreferredSize(new Dimension(width, (int) Math.round(height * 0.038)));
		windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.X_AXIS));
		windowPanel.add(Box.createHorizontalGlue());

		float space = (float) (width * 0.032);
		if (resized)
			space *= 1.05;

		windowPanel.add(hideIcon);
		hideIcon.setAlignmentY(BOTTOM_ALIGNMENT);
		windowPanel.add(Box.createHorizontalStrut((int) Math.round(space)));
		windowPanel.add(restoreIcon);
		restoreIcon.setAlignmentY(BOTTOM_ALIGNMENT);
		windowPanel.add(Box.createHorizontalStrut((int) Math.round(space)));
		windowPanel.add(closeIcon);
		closeIcon.setAlignmentY(BOTTOM_ALIGNMENT);
		windowPanel.add(Box.createHorizontalStrut((int) Math.round(space * 0.89)));

		windowPanel.revalidate();
		windowPanel.repaint();
	}

	/**
	 * ���������� ������ �������������
	 */
	public void showUsers() {
		currentMethod = "showUsers";
		paramTypes = new Class[] {};
		args = new Object[] {};

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("users") + "</div></html>";
		heading.setText(t);

		headerPanel.removeAll();
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(heading);
		heading.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.revalidate();
		headerPanel.repaint();

		String rows[][] = null;
		try {
			rows = HTTPClient.listPatients();
		} catch (ServerConnectionException e) {
			e.printStackTrace();
			Dialogs.showServerConnectionErrorDialog(e);
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
					}
					showUsers();
				}

				if (col == 3) {
					editUser((String) table.getModel().getValueAt(row, 2),
							(String) table.getModel().getValueAt(row, 1));
				}
			}
		});

		table.setPreferredScrollableViewportSize(
				new Dimension((int) (Math.min(tableWidth, Math.round(width * 0.9)) + 10),
						(int) Math.min(tableHeight, Math.round(height * 0.7))));
		JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
		scroll.getHorizontalScrollBar().setUI(new ScrollBarCustomUI());
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setOpaque(false);
		scroll.setMaximumSize(new Dimension(Math.min(tableWidth, 900) + 10, Math.min(tableHeight, 500)));
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets((int) Math.round(height * 0.1), 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 1.0;

		actualPanel.add(scroll, c);

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	/**
	 * ���������� ��������
	 */
	public void showFirstScreen() {
		currentMethod = "showFirstScreen";
		paramTypes = new Class[] {};
		args = new Object[] {};

		panel = new BgPanel(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.FIRST_SCREEN));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		createWindowButtons();

		if (!resized) {
			windowPanel.addMouseListener(draggingMouseListener);
			windowPanel.addMouseMotionListener(draggingMouseMotionListener);
		}

		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(176, 190, 197);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("authorization") + "</div></html>";
		heading.setText(t);

		JRadioButton neww = new JRadioButton("<html><div style='font: 14pt Arial Narrow; color: rgb(176, 190, 197);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("new_user").toUpperCase() + "</div></html>");
		neww.setActionCommand("neww");
		neww.setSelected(true);
		neww.setOpaque(false);
		neww.setFocusable(false);
		neww.setIcon(Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.RADIO)));
		neww.setSelectedIcon(
				Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.RADIO_SELECTED)));

		JRadioButton enter = new JRadioButton("<html><div style='font: 14pt Arial Narrow; color: rgb(176, 190, 197);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("enter").toUpperCase() + "</div></html>");
		enter.setActionCommand("enter");
		enter.setSelected(false);
		enter.setOpaque(false);
		enter.setFocusable(false);
		enter.setIcon(Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.RADIO)));
		enter.setSelectedIcon(
				Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.RADIO_SELECTED)));

		ButtonGroup group = new ButtonGroup();
		group.add(neww);
		group.add(enter);

		CustomPasswordField pass = new CustomPasswordField(30, InterfaceTextDefaults.getInstance().getDefault("password"));
		CustomTextField login = new CustomTextField(30,
				InterfaceTextDefaults.getInstance().getDefault("name_surname_patronymic"));

		JButton loginSpec = new JButton(InterfaceTextDefaults.getInstance().getDefault("rule_users"));
		loginSpec.setUI(new ButtonCustomUI(new Color(96, 125, 139), new Color(144, 164, 174)));
		loginSpec.setBorder(null);
		loginSpec.setOpaque(false);
		loginSpec.setPreferredSize(new Dimension(235, 40));
		loginSpec.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		loginSpec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// TODO dialog, later
				CustomDialog d1 = new CustomDialog(reabilitation,
						InterfaceTextDefaults.getInstance().getDefault("login_spec_text"),
						InterfaceTextDefaults.getInstance().getDefault("enter2"),
						InterfaceTextDefaults.getInstance().getDefault("cancel"));
				if (d1.getAnswer() == 1) {
					try {
						if (HTTPClient.loginSpec(d1.getLogin().trim(), d1.getPass().trim())) {
							userName = d1.getLogin().trim();
							userCardNumber = d1.getPass().trim();
							start(true);
						}
					} catch (ServerConnectionException e1) {
						e1.printStackTrace();
						Dialogs.showServerConnectionErrorDialog(e1);
					}
				}
			}
		});

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("enter2"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(73, 40));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (neww.isSelected()) {
					try {
						if (HTTPClient.loginPatient(login.getText().trim(), pass.getPass()))
							return;
					} catch (ServerConnectionException e1) {
						e1.printStackTrace();
						Dialogs.showServerConnectionErrorDialog(e1);
					}
					try {
						HTTPClient.newPatient(login.getText().trim(), pass.getPass());
					} catch (ServerConnectionException e1) {
						e1.printStackTrace();
						Dialogs.showServerConnectionErrorDialog(e1);
					}
					userName = login.getText().trim();
					userCardNumber = pass.getPass();
					start(false);
				} else
					try {
						if (HTTPClient.loginPatient(login.getText().trim(), pass.getPass())) {
							userName = login.getText().trim();
							userCardNumber = pass.getPass();
							start(false);
						}
					} catch (ServerConnectionException e1) {
						e1.printStackTrace();
						Dialogs.showServerConnectionErrorDialog(e1);
					}
			}
		});

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets((int) -Math.round(height / 14), 0, 0, 20);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		p.add(loginSpec, c);

		JLabel logo = new JLabel();
		ImageIcon icon = Utills
				.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.KOMPLIMED));
		logo.setIcon(icon);

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets((int) Math.round(height / 14), (int) Math.round(width / 2.39), 0, 0);
		c.weightx = 0.0;

		p.add(logo, c);

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets((int) Math.round(height / 3.5), (int) Math.round(width / 3.09), 0, 0);
		// c.insets = new Insets(0, (int) Math.round(width / 3.09), 0, 0);
		c.weightx = 0.0;

		p.add(heading, c);

		c.insets = new Insets(20, (int) Math.round(width / 3.09), 0, 0);
		c.gridwidth = 1;
		c.gridy = 4;
		p.add(neww, c);

		c.insets = new Insets(20, 20, 0, 0);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		p.add(enter, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, (int) Math.round(width / 3.09), 0, 0);
		c.gridwidth = 2;
		c.gridy = 5;
		c.gridx = 0;
		p.add(login, c);

		c.gridy = 6;
		p.add(pass, c);

		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(10, 0, 0, 0);
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 7;
		p.add(start, c);

		JLabel copyright = new JLabel();
		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.COPYRIGHT));
		copyright.setIcon(icon);

		c.insets = new Insets((int) Math.round(height / 15), (int) Math.round(width / 3.09), 0, 0);
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 8;
		p.add(copyright, c);

		p.revalidate();
		p.repaint();

		// panel.add(Box.createVerticalStrut(10));
		panel.add(windowPanel);
		panel.add(p);
		setContentPane(panel);
		panel.revalidate();
		panel.repaint();
	}

	private void createMainMenu() {
		// TODO
		exitIcon = new JLabel();
		aboutIcon = new JLabel();
		helpIcon = new JLabel();
		tasksIcon = new JLabel();

		MenuMouseListener l = new MenuMouseListener();

		ImageIcon icon = Utills
				.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_EXIT));
		exitIcon.setIcon(icon);
		exitIcon.setName("exit");
		exitIcon.addMouseListener(l);
		exitIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_ABOUT));
		aboutIcon.setIcon(icon);
		aboutIcon.setName("about");
		aboutIcon.addMouseListener(l);
		aboutIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_HELP));
		helpIcon.setIcon(icon);
		helpIcon.setName("help");
		helpIcon.addMouseListener(l);
		helpIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_TASKS));
		tasksIcon.setIcon(icon);
		tasksIcon.setName("tasks");
		tasksIcon.addMouseListener(l);
		tasksIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JLabel userIcon = new JLabel();
		userIcon.setIcon(Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.USER)));

		JLabel userNameIcon = new JLabel();
		String t = "<html><div style='font: 14pt Arial Narrow; color: rgb(176, 190, 197);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("user").toUpperCase() + ": "
				+ "<span style='color: rgb(255, 183, 77);'>"
				+ "</span></div>"
				+ "<div style='font: 14pt Arial Narrow; color: rgb(255, 183, 77);'>" + userName + "</div></html>";
		userNameIcon.setText(t);

		logoSpace = (int) Math.round(width * 0.28);
		if (menuPanel != null) {
			menuPanel.removeAll();
			menuPanel.setPreferredSize(new Dimension(width, (int) Math.round(height * 0.076)));
			menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));

			JLabel logo = new JLabel();
			logo.setPreferredSize(new Dimension(logoSpace, (int) menuPanel.getPreferredSize().getHeight()));

			BufferedImage img = null;
			try {
				img = ImageIO.read(
						getClass().getResource(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.LOGO)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Image dimg = img.getScaledInstance((int) Math.round(logo.getPreferredSize().getWidth() * 0.93),
					(int) Math.round(logo.getPreferredSize().getHeight() * 0.65), Image.SCALE_SMOOTH);
			logo.setIcon(new ImageIcon(dimg));

			menuPanel.add(Box.createHorizontalStrut(25));
			menuPanel.add(logo);
			menuPanel.add(userIcon);
			menuPanel.add(Box.createHorizontalStrut(iconsSpace));
			menuPanel.add(userNameIcon);
			menuPanel.add(Box.createHorizontalGlue());

			menuPanel.add(tasksIcon);
			menuPanel.add(Box.createHorizontalStrut(iconsSpace));
			menuPanel.add(helpIcon);
			menuPanel.add(Box.createHorizontalStrut(iconsSpace));
			menuPanel.add(aboutIcon);
			menuPanel.add(Box.createHorizontalStrut(iconsSpace));
			menuPanel.add(exitIcon);
			menuPanel.add(Box.createHorizontalStrut(iconsSpace * 2));

			menuPanel.revalidate();
			menuPanel.repaint();
		}
	}

	private void createSmallMenu() {
		smallMenuAboutIcon = new JLabel();
		smallMenuBeginingIcon = new JLabel();
		smallMenuEndIcon = new JLabel();
		smallMenuResultsIcon = new JLabel();

		SmallMenuMouseListener l = new SmallMenuMouseListener();

		ImageIcon icon = Utills
				.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_ABOUT));
		smallMenuAboutIcon.setIcon(icon);
		smallMenuAboutIcon.setName("about");
		smallMenuAboutIcon.addMouseListener(l);
		smallMenuAboutIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		smallMenuAboutIcon.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

		icon = Utills
				.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_BEGINING));
		smallMenuBeginingIcon.setIcon(icon);
		smallMenuBeginingIcon.setName("begining");
		smallMenuBeginingIcon.addMouseListener(l);
		smallMenuBeginingIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		smallMenuBeginingIcon.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_END));
		smallMenuEndIcon.setIcon(icon);
		smallMenuEndIcon.setName("end");
		smallMenuEndIcon.addMouseListener(l);
		smallMenuEndIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		smallMenuEndIcon.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

		icon = Utills
				.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_RESULTS));
		smallMenuResultsIcon.setIcon(icon);
		smallMenuResultsIcon.setName("results");
		smallMenuResultsIcon.addMouseListener(l);
		smallMenuResultsIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		smallMenuResultsIcon.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

		headerPanel.add(smallMenuAboutIcon);
		smallMenuAboutIcon.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalStrut(iconsSpace));

		headerPanel.add(smallMenuBeginingIcon);
		smallMenuBeginingIcon.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalStrut(iconsSpace));

		headerPanel.add(smallMenuEndIcon);
		smallMenuEndIcon.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalStrut(iconsSpace));

		headerPanel.add(smallMenuResultsIcon);
		smallMenuResultsIcon.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalStrut(iconsSpace * 2));
		headerPanel.revalidate();
		headerPanel.repaint();
	}

	/**
	 * �������� ������ �������
	 */
	public void showGroups() {

		menuPanel.setX1(width - iconsSpace * 5 - exitIcon.getWidth() - helpIcon.getWidth() - tasksIcon.getWidth()
				- aboutIcon.getWidth() - 3);
		menuPanel.setX2(width - iconsSpace * 5 - exitIcon.getWidth() - helpIcon.getWidth() - aboutIcon.getWidth() + 3);
		menuPanel.repaint();

		currentMethod = "showGroups";
		paramTypes = new Class[] {};
		args = new Object[] {};

		showedTask = null;
		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("task_groups") + "</div></html>";
		heading.setText(t);

		headerPanel.removeAll();
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(heading);
		heading.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.revalidate();
		headerPanel.repaint();

		JPanel p = new JPanel();
		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");

		try {
			text.setPage(getClass().getResource(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.WELCOME)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		text.setOpaque(false);
		p.add(text);
		text.setPreferredSize(new Dimension((int) (width * 0.9), 175));
		p.setUI(new PanelCustomUI(true));

		Document doc = Utills.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.GROUPS));

		NodeList n = doc.getElementsByTagName("group");
		NamedNodeMap k = null;

		JLabel[] groups = new JLabel[n.getLength()];
		taskGroups = new TaskGroup[n.getLength()];

		ImageIcon icon;
		GroupsMouseListener l = new GroupsMouseListener();
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			taskGroups[i] = new TaskGroup(k.getNamedItem("name").getNodeValue(), k.getNamedItem("text").getNodeValue(),
					k.getNamedItem("image").getNodeValue(), k.getNamedItem("bigImage").getNodeValue(),
					k.getNamedItem("rolloverImage").getNodeValue(), k.getNamedItem("toolTipText").getNodeValue());

			icon = Utills.createImageIcon(taskGroups[i].getImage());
			groups[i] = new CustomLabel();
			groups[i].setIcon(icon);
			groups[i].setHorizontalTextPosition(JLabel.CENTER);
			groups[i].setVerticalTextPosition(JLabel.BOTTOM);
			groups[i].setName(Integer.toString(i));
			groups[i].addMouseListener(l);
			groups[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			groups[i].createToolTip();
			groups[i].setToolTipText(
					"<html><div style='font: bold 14pt Arial Narrow; color: #455a64; padding: 10px; padding-top: 13px; padding-bottom: 5px;'>"
							+ taskGroups[i].getName().toUpperCase() + "</div>"
							+ "<div style='font: 13pt Arial Narrow; color: #455a64; padding: 10px;  padding-top: 0px; padding-bottom: 13px;'>"
							+ taskGroups[i].getToolTipText() + "</div></html>");
		}

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

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

		int i;
		for (i = 0; i < Math.ceil((double) groups.length / 5); i++) {
			for (int j = 0; j < 5; j++) {
				if ((i * 5 + j) == groups.length)
					break;
				c.gridx = j;
				c.gridy = i;

				actualPanel.add(groups[i * 5 + j], c);
			}
		}

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = i;
		c.insets = new Insets(10, 20, 10, 20);

		actualPanel.add(p, c);

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	/**
	 * ��������� ������� ��� ������.
	 * 
	 * @param i
	 *            ����� ������
	 */
	private void readTasks(int i) {
		Document doc = Utills.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.GROUPS));

		NodeList n = doc.getElementsByTagName("group");
		NamedNodeMap k = null;

		NodeList n1 = n.item(i).getChildNodes();
		tasks = new Task[n1.getLength()];

		for (int j = 0; j < n1.getLength(); j++) {
			k = n1.item(j).getAttributes();
			tasks[j] = new Task(k.getNamedItem("name").getNodeValue(), k.getNamedItem("image").getNodeValue(),
					k.getNamedItem("shortText").getNodeValue(), k.getNamedItem("longText").getNodeValue(),
					k.getNamedItem("longLongText").getNodeValue(), k.getNamedItem("bigImage").getNodeValue(),
					k.getNamedItem("className").getNodeValue(), k.getNamedItem("rolloverImage").getNodeValue());
		}
	}

	/**
	 * �������� ���������� ������ ������.
	 * 
	 * @param i
	 *            ����� ������
	 */
	public void showTasks(int i) {
		menuPanel.setX1(0);
		menuPanel.setX2(0);
		menuPanel.repaint();

		currentMethod = "showTasks";
		paramTypes = new Class[] { int.class };
		args = new Object[] { i };

		showedTask = null;
		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ taskGroups[i].getName().toUpperCase() + "</div></html>";
		heading.setText(t);
		ImageIcon icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW));
		heading.setIcon(icon);
		heading.setIconTextGap(20);
		heading.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		heading.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showGroups();
			}
		});
		headerPanel.removeAll();
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(heading);
		heading.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.revalidate();
		headerPanel.repaint();

		JLabel image = new JLabel();
		icon = Utills.createImageIcon(taskGroups[i].getBigImage());
		image.setIcon(icon);

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");
		text.setText("<html><div style='font: 15pt Arial Narrow; color: rgb(68, 83, 91);'>" + taskGroups[i].getText()
				+ "</div></html>");
		text.setOpaque(false);
		text.setPreferredSize(new Dimension((int) (width * 0.85 - image.getPreferredSize().getWidth()), 300));

		JScrollPane scroll = new JScrollPane(text);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setPreferredSize(new Dimension((int) (width * 0.85 - image.getPreferredSize().getWidth()), (int) (300)));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());

		readTasks(i);

		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JLabel[] tasksLabels = new JLabel[tasks.length];
		TasksMouseListener l = new TasksMouseListener();

		for (int j = 0; j < Math.ceil((double) tasks.length / 2); j++) {
			for (int k = 0; k < 2; k++) {
				if ((j * 2 + k) == tasks.length)
					break;
				icon = Utills.createImageIcon(tasks[j * 2 + k].getImage());
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
				tasksLabels[j * 2 + k].addMouseListener(l);
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

		JScrollPane scrollTasks = new JScrollPane(p);
		scrollTasks.setPreferredSize(new Dimension((int) (text.getPreferredSize().getWidth()),
				(int) (height * 0.7 - text.getPreferredSize().getHeight())));
		scrollTasks.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollTasks.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollTasks.getViewport().setOpaque(false);
		scrollTasks.setOpaque(false);
		scrollTasks.setBorder(null);
		scrollTasks.getVerticalScrollBar().setUI(new ScrollBarCustomUI());

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

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

		actualPanel.add(image, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.insets = new Insets(40, 40, 0, 40);
		c.weightx = 1.0;

		actualPanel.add(scroll, c);

		c.gridy = 1;
		c.weightx = 0.0;

		actualPanel.add(scrollTasks, c);

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	/**
	 * �������� �������� ������� � ������� i.
	 * 
	 * @param i
	 *            ����� ������� ������ ������
	 */
	public void showTaskInfo(int i) {
		currentMethod = "showTaskInfo";
		paramTypes = new Class[] { int.class };
		args = new Object[] { i };

		showedTask = null;
		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("task_number") + Integer.toString(i + 1) + ": "
				+ tasks[i].getName().toUpperCase() + "</div></html>";
		heading.setText(t);
		ImageIcon icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW));
		heading.setIcon(icon);
		heading.setIconTextGap(20);
		heading.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		heading.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showTasks(currentTaskGroup);
			}
		});

		headerPanel.removeAll();
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(heading);
		heading.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.revalidate();
		headerPanel.repaint();
		createSmallMenu();

		JLabel image = new JLabel();
		icon = Utills.createImageIcon(tasks[i].getBigImage());
		image.setIcon(icon);

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");
		text.setText("<html><div style='font: bold 22pt Arial Narrow; color: rgb(115, 84, 73); padding-bottom: 20 px'>"
				+ tasks[i].getName().toUpperCase()
				+ "</div><div  style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>" + tasks[i].getLongText()
				+ "</div></html>");
		text.setOpaque(false);
		text.setPreferredSize(new Dimension((int) (width * 0.85 - image.getPreferredSize().getWidth()),
				100 + Utills.calculateTextHeight(text.getText(),
						(int) (width * 0.85 - image.getPreferredSize().getWidth()), text)));

		JButton start = new JButton(InterfaceTextDefaults.getInstance().getDefault("begin_task"));
		start.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		start.setBorder(null);
		start.setOpaque(false);
		start.setPreferredSize(new Dimension(200, 35));
		start.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showTask(i);
			}
		});

		GridBagConstraints c = new GridBagConstraints();

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

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

		actualPanel.add(image, c);

		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.insets = new Insets(-200, 40, 0, 40);
		c.weightx = 1.0;

		actualPanel.add(text, c);

		c.gridy = 1;
		c.insets = new Insets(40, 40, 0, 40);
		actualPanel.add(start, c);

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	public void showAbout() {
		menuPanel.setX1(width - iconsSpace * 3 - exitIcon.getWidth() - aboutIcon.getWidth() - 3);
		menuPanel.setX2(width - iconsSpace * 3 - exitIcon.getWidth() + 3);
		menuPanel.repaint();

		currentMethod = "showAbout";
		paramTypes = new Class[] {};
		args = new Object[] {};

		// read version
		String v = null;
		try {
			v = Utills.getVersion();
		} catch (ProgramFilesBrokenException e4) {
			e4.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e4);
		}
		String dd = null;
		try {
			dd = Utills.getVersionDate();
		} catch (ProgramFilesBrokenException e4) {
			e4.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e4);
		}

		// read config
		String name = null;
		String keyNumber = null;
		try {
			name = Utills.getLicenceUserName();
			keyNumber = Utills.getLicenceKey();
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

		showedTask = null;
		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("about") + "</div></html>";
		heading.setText(t);
		ImageIcon icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW));
		heading.setIcon(icon);
		heading.setIconTextGap(20);
		heading.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		heading.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showGroups();
			}
		});
		headerPanel.removeAll();
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(heading);
		heading.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.revalidate();
		headerPanel.repaint();

		JLabel key = new JLabel();
		t = "<html><div style='font: bold 14pt Arial Narrow; color: rgb(70, 110, 122);'>"
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

		JButton checkUpdates = new JButton(InterfaceTextDefaults.getInstance().getDefault("check_updates"));
		icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.CIRCLE_ARROW));
		checkUpdates.setIcon(icon);
		checkUpdates.setIconTextGap(20);
		checkUpdates.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		checkUpdates.setBorder(null);
		checkUpdates.setOpaque(false);
		checkUpdates.setPreferredSize(new Dimension(252, 40));
		checkUpdates.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		checkUpdates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ask DB for updates
				String location = null;
				try {
					location = HTTPClient.getVersion(Utills.getVersionDate());
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
					CustomDialog d1 = new CustomDialog(reabilitation,
							InterfaceTextDefaults.getInstance().getDefault("do_update"),
							InterfaceTextDefaults.getInstance().getDefault("yes"),
							InterfaceTextDefaults.getInstance().getDefault("no"), true);
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
					new CustomDialog(reabilitation, InterfaceTextDefaults.getInstance().getDefault("no_updates"),
							InterfaceTextDefaults.getInstance().getDefault("ok"), null, true);
				}
			}
		});

		JCheckBox checkAuto = new JCheckBox("<html><div style='font: 15pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("check_updates_auto") + "</div></html>");
		checkAuto.setOpaque(false);
		checkAuto.setSelected(Utills.getCheckUpdatesAuto());
		checkAuto.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String s = Utills.readFile(Utills.getFilePath() + "/config");
				if (checkAuto.isSelected())
					s = s.replaceAll("<checkUpdatesAuto>.*</checkUpdatesAuto>",
							"<checkUpdatesAuto>true</checkUpdatesAuto>");
				else
					s = s.replaceAll("<checkUpdatesAuto>.*</checkUpdatesAuto>",
							"<checkUpdatesAuto>false</checkUpdatesAuto>");
				Utills.writeFile(s, Utills.getFilePath() + "/config");
			}
		});

		JLabel about = new JLabel();
		t = "<html><div style='font: bold 22pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("about") + "</div></html>";
		about.setText(t);

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");

		try {
			text.setPage(getClass().getResource(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.ABOUT)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JScrollPane scroll = new JScrollPane(text);
		scroll.setPreferredSize(new Dimension((int) (width * 0.95), (int) (height * 0.45)));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

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

		actualPanel.add(key, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.insets = new Insets(40, 0, 0, 0);

		actualPanel.add(version, c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(20, 40, 0, 0);
		c.weightx = 0.0;

		actualPanel.add(licenze, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.insets = new Insets(20, 0, 0, 0);

		actualPanel.add(checkUpdates, c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(20, 40, 0, 0);

		actualPanel.add(left, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(20, 0, 0, 0);

		actualPanel.add(checkAuto, c);

		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setBackground(new Color(176, 190, 197));
		sep.setForeground(new Color(176, 190, 197));
		sep.setPreferredSize(new Dimension((int) (scroll.getPreferredSize().getWidth() * 0.98), 1));

		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(20, 40, 0, 20);

		actualPanel.add(sep, c);

		c.gridy = 4;
		c.insets = new Insets(20, 40, 0, 0);

		actualPanel.add(about, c);

		c.gridy = 5;
		c.insets = new Insets(0, 20, 0, 0);

		actualPanel.add(scroll, c);

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	public void showHelp() {
		menuPanel.setX1(width - iconsSpace * 4 - exitIcon.getWidth() - helpIcon.getWidth() - aboutIcon.getWidth() - 3);
		menuPanel.setX2(width - iconsSpace * 4 - exitIcon.getWidth() - aboutIcon.getWidth() + 3);
		menuPanel.repaint();

		currentMethod = "showHelp";
		paramTypes = new Class[] {};
		args = new Object[] {};

		showedTask = null;
		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("help") + "</div></html>";
		heading.setText(t);
		ImageIcon icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW));
		heading.setIcon(icon);
		heading.setIconTextGap(20);
		heading.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		heading.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showGroups();
			}
		});
		headerPanel.removeAll();
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(heading);
		heading.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.revalidate();
		headerPanel.repaint();

		JLabel faq = new JLabel();
		String t1 = "<html><div style='font: 22pt Arial Narrow; color: rgb(115, 84, 73); padding-left: 17px;'><span style='font-weight: bold;'>?&nbsp;&nbsp;&nbsp;</span>"
				+ InterfaceTextDefaults.getInstance().getDefault("faq") + "</div></html>";
		faq.setText(t1);

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");

		try {
			text.setPage(getClass().getResource(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.FAQ)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

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

		actualPanel.add(faq, c);

		JScrollPane scroll = new JScrollPane(text);
		scroll.setPreferredSize(new Dimension((int) (width * 0.95), (int) (height * 0.7)));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());

		c.gridy = 1;

		actualPanel.add(scroll, c);

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	/**
	 * �������� ������� � ������� i
	 * 
	 * @param i
	 *            ����� ������� ������ ������
	 */
	public void showTask(int i) {
		currentMethod = "showTask";
		paramTypes = new Class[] { int.class };
		args = new Object[] { i };

		String s = tasks[i].getClassName();
		Class c;
		Class[] intArgsClass = new Class[] { int.class, int.class, String.class, Reabilitation.class, String.class,
				String.class, String.class, String.class };
		// TODO �� �� �����
		Integer h = new Integer((int) (height * 0.75));
		Integer w = new Integer(970);
		String t1 = tasks[i].getLongLongText();
		Object[] intArgs = new Object[] { w, h, t1, reabilitation, userName, userCardNumber, tasks[i].getName(),
				taskGroups[currentTaskGroup].getName() };
		Constructor intArgsConstructor = null;
		AbstractTask p = null;
		try {
			c = Class.forName(s);
			intArgsConstructor = c.getConstructor(intArgsClass);
			p = (AbstractTask) Utills.createObject(intArgsConstructor, intArgs);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}

		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("task_number") + Integer.toString(i + 1) + ": "
				+ tasks[i].getName().toUpperCase() + "</div></html>";
		heading.setText(t);
		ImageIcon icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW));
		heading.setIcon(icon);
		heading.setIconTextGap(20);
		heading.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		heading.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (showedTask != null) {
					showedTask.pause();
					CustomDialog d1 = new CustomDialog(reabilitation,
							InterfaceTextDefaults.getInstance().getDefault("sure_break_task"),
							InterfaceTextDefaults.getInstance().getDefault("break"),
							InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
					if (d1.getAnswer() == 1)
						showTaskInfo(i);
					else if (showedTask != null)
						showedTask.start();
				} else
					showTaskInfo(i);
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

		});
		headerPanel.removeAll();
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(heading);
		heading.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalGlue());
		createSmallMenu();
		headerPanel.revalidate();
		headerPanel.repaint();

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

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

		actualPanel.add(p, c1);
		showedTask = p;

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	/**
	 * ���������� ���������� ����������� ��� ������������� ������������
	 * 
	 * @param userName
	 *            ��� ������������
	 */
	public void showResults(String[][] rows, String[] taskNames, int selectedTask, Date filterDate1, Date filterDate2) {
		currentMethod = "showResults";
		paramTypes = new Class[] { String[][].class, String[].class, int.class, Date.class, Date.class };
		args = new Object[] { rows, taskNames, selectedTask, filterDate1, filterDate2 };

		if (rows.length == 0)
			return;

		String[][] rowsCopy = new String[rows.length][rows[0].length];
		for (int i = 0; i < rows.length; i++)
			for (int j = 0; j < rows[0].length; j++)
				rowsCopy[i][j] = new String(rows[i][j]);

		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("results") + ": " + userName.toUpperCase()
				+ "</div></html>";
		heading.setText(t);
		ImageIcon icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW));
		heading.setIcon(icon);
		heading.setIconTextGap(20);
		heading.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		heading.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showTasks(currentTaskGroup);
			}
		});
		headerPanel.removeAll();
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(heading);
		heading.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalGlue());
		createSmallMenu();
		headerPanel.revalidate();
		headerPanel.repaint();

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();

		JLabel subHeading = new JLabel();
		t = "<html><div style='font: 22pt Arial Narrow; color: rgb(115, 84, 73);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("results_dynamic") + "</div></html>";
		subHeading.setText(t);

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

		actualPanel.add(subHeading, c1);

		JLabel filter = new JLabel();
		t = "<html><div style='font: bold 16pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("filter") + "</div></html>";
		filter.setText(t);

		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		p.setPreferredSize(new Dimension(width, 40));

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

		JLabel interval = new JLabel();
		t = "<html><div style='font: 16pt Arial Narrow; color: rgb(68, 83, 91);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("interval") + "</div></html>";
		interval.setText(t);

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

		JButton show = new JButton(InterfaceTextDefaults.getInstance().getDefault("show"));
		show.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		show.setBorder(null);
		show.setOpaque(false);
		show.setPreferredSize(new Dimension(100, 30));
		show.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Date selectedDate1 = null, selectedDate2 = null;
				Calendar selectedValue1 = (Calendar) date1.getModel().getValue();
				if (selectedValue1 != null)
					selectedDate1 = selectedValue1.getTime();
				Calendar selectedValue2 = (Calendar) date2.getModel().getValue();
				if (selectedValue2 != null)
					selectedDate2 = selectedValue2.getTime();
				String[][] r = filterResults(resultsRows, tasks.getSelectedItem().toString(), selectedDate1,
						selectedDate2);
				showResults(r, taskNames, tasks.getSelectedIndex(), selectedDate1, selectedDate2);
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

		actualPanel.add(p, c1);

		ResultsPanel resultsPanel = createResultsPanel(rowsCopy, 400, 500);

		c1.anchor = GridBagConstraints.NORTHWEST;
		c1.fill = GridBagConstraints.NONE;
		c1.gridwidth = 1;
		c1.gridy = 2;
		c1.insets = new Insets(30, 40, 0, 0);
		c1.weightx = 1.0;
		actualPanel.add(resultsPanel, c1);

		JScrollPane scroll = createResultsTable(rowsCopy, 400, 400);

		c1.gridwidth = GridBagConstraints.REMAINDER;
		c1.insets = new Insets(60, 0, 0, 0);
		c1.gridx = 1;
		c1.gridy = 2;
		actualPanel.add(scroll, c1);

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	private String[][] filterResults(String[][] rows, String taskName, Date date1, Date date2) {
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

	private JScrollPane createResultsTable(String[][] rows, int width, int height) {
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

	private ResultsPanel createResultsPanel(String[][] rows, int width, int height) {
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

	public void editUser(String userOldName, String userOldPass) {
		currentMethod = "editUser";
		paramTypes = new Class[] { String.class, String.class };
		args = new Object[] { userOldName, userOldPass };

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		JLabel heading = new JLabel();
		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("edit_user") + "</div></html>";
		heading.setText(t);
		ImageIcon icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW));
		heading.setIcon(icon);
		heading.setIconTextGap(20);
		heading.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		heading.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showUsers();
			}
		});

		headerPanel.removeAll();
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(heading);
		heading.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.revalidate();
		headerPanel.repaint();

		JLabel cardNumberLabel = new JLabel();
		t = "<html><div style='font: bold 19pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("password").toUpperCase() + "</div></html>";
		cardNumberLabel.setText(t);

		JLabel nameLabel = new JLabel();
		t = "<html><div style='font: bold 19pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("name_surname_patronymic").toUpperCase()
				+ "</div></html>";
		nameLabel.setText(t);

		CustomTextField cardNumberField = new CustomTextField(40, "");
		cardNumberField.setText(userOldPass);

		CustomTextField nameField = new CustomTextField(40, "");
		nameField.setText(userOldName);

		JButton cancel = new JButton(InterfaceTextDefaults.getInstance().getDefault("cancel"));
		cancel.setUI(new ButtonCustomUI(new Color(239, 83, 80)));
		cancel.setBorder(null);
		cancel.setOpaque(false);
		cancel.setPreferredSize(new Dimension(180, 35));
		cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showUsers();
			}
		});

		JButton save = new JButton(InterfaceTextDefaults.getInstance().getDefault("save"));
		save.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		save.setBorder(null);
		save.setOpaque(false);
		save.setPreferredSize(new Dimension(180, 35));
		save.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!nameField.getText().trim().equals("") && !cardNumberField.getText().trim().equals("")) {
					try {
						HTTPClient.editPatient(userOldName, userOldPass, nameField.getText().trim(),
								cardNumberField.getText().trim());
					} catch (ServerConnectionException e1) {
						e1.printStackTrace();
						Dialogs.showServerConnectionErrorDialog(e1);
					}
					showUsers();
				}
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

		actualPanel.add(nameLabel, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;

		actualPanel.add(nameField, c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;

		actualPanel.add(cardNumberLabel, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;

		actualPanel.add(cardNumberField, c);

		c.gridwidth = 1;
		c.gridy = 2;

		actualPanel.add(cancel, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 2;

		actualPanel.add(save, c);

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	public void showResults() {
		try {
			resultsRows = HTTPClient.findResults(userName, userCardNumber);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
			Dialogs.showServerConnectionErrorDialog(e);
		}
		ArrayList<String> a = new ArrayList<String>();
		for (int i = 0; i < resultsRows.length; i++)
			if (!a.contains(resultsRows[i][2]))
				a.add(resultsRows[i][2]);
		a.add(InterfaceTextDefaults.getInstance().getDefault("all_tasks"));
		String[] n = new String[a.size()];
		showResults(resultsRows, a.toArray(n), 0, null, null);
	}

	private void resize() {

		Dimension sSize;
		if (!resized) {
			sSize = Toolkit.getDefaultToolkit().getScreenSize();
			resized = true;
			windowPanel.removeMouseListener(draggingMouseListener);
			windowPanel.removeMouseMotionListener(draggingMouseMotionListener);
		} else {
			sSize = new Dimension(1020, 790);
			resized = false;
			windowPanel.addMouseListener(draggingMouseListener);

			windowPanel.addMouseMotionListener(draggingMouseMotionListener);
		}
		height = sSize.height;
		width = sSize.width;
		if (resized)
			setBounds(0, 0, width, height);
		else
			setBounds(50, 50, width, height);

		if (!currentMethod.equals("showFirstScreen")) {
			panel = new BgPanel(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.BACKGROUND));

			createMainMenu();
			createWindowButtons();
			if (!resized) {
				windowPanel.addMouseListener(draggingMouseListener);
				windowPanel.addMouseMotionListener(draggingMouseMotionListener);
			}

			panel.setDoubleBuffered(true);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			// panel.add(Box.createVerticalStrut(10));
			panel.add(windowPanel);
			// panel.add(Box.createVerticalStrut(15));
			panel.add(menuPanel);
			// panel.add(Box.createVerticalStrut(15));
			panel.add(headerPanel);
			panel.add(actualPanel);
			panel.add(Box.createVerticalStrut(7));
			setContentPane(panel);
		}

		if (showedTask != null) {
			actualPanel.revalidate();
			actualPanel.repaint();
		} else {
			Method classMethod;
			try {
				classMethod = Reabilitation.class.getMethod(currentMethod, paramTypes);
				classMethod.invoke(this, args);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ������, �������������� ������ ���, � �������� ������������� ���������
	 * �������.
	 * 
	 * @author Pokrovskaya Oksana
	 *
	 */
	class BgPanel extends JPanel {
		String image;

		public BgPanel(String backgroundPath) {
			image = backgroundPath;
		}

		public void paintComponent(Graphics g) {
			Image im = null;
			try {
				im = ImageIO.read(getClass().getResource(image));
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("problem reading file " + image);
			}
			g.drawImage(im, 0, 0, width, height, this);
		}
	}

	class MenuMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			switch (l.getName()) {
			case "exit":
				if (showedTask != null && !showedTask.isDontShowBreakingDialog())
					showedTask.pause();
				CustomDialog d = new CustomDialog(reabilitation,
						InterfaceTextDefaults.getInstance().getDefault("sure_logout"),
						InterfaceTextDefaults.getInstance().getDefault("exit"),
						InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
				if (d.getAnswer() == 1)
					showFirstScreen();
				else if (showedTask != null)
					showedTask.start();
				break;
			case "help":
				if (showedTask != null && !showedTask.isDontShowBreakingDialog()) {
					showedTask.pause();
					CustomDialog d1 = new CustomDialog(reabilitation,
							InterfaceTextDefaults.getInstance().getDefault("sure_break_task"),
							InterfaceTextDefaults.getInstance().getDefault("break"),
							InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
					if (d1.getAnswer() == 1)
						showHelp();
					else if (showedTask != null)
						showedTask.start();
				} else
					showHelp();

				break;
			case "about":
				if (showedTask != null && !showedTask.isDontShowBreakingDialog()) {
					showedTask.pause();
					CustomDialog d1 = new CustomDialog(reabilitation,
							InterfaceTextDefaults.getInstance().getDefault("sure_break_task"),
							InterfaceTextDefaults.getInstance().getDefault("break"),
							InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
					if (d1.getAnswer() == 1)
						showAbout();
					else if (showedTask != null)
						showedTask.start();
				} else
					showAbout();
				break;
			case "tasks":
				if (popup != null) {
					popup.hide();
				}
				PopupFactory fac = new PopupFactory();
				Point xy = tasksIcon.getLocationOnScreen();
				MenuPanel p = new MenuPanel(popup, reabilitation);
				popupMenuPanel = p;
				popup = fac
						.getPopup(tasksIcon, p,
								(int) ((int) xy.getX() - popupMenuPanel.getPreferredSize().getWidth()
										+ tasksIcon.getWidth()),
								(int) Math.round(xy.getY() + tasksIcon.getHeight() + height * 0.02));
				popup.show();
				break;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			ImageIcon icon;
			switch (l.getName()) {
			case "exit":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_EXIT_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "help":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_HELP_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "about":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_ABOUT_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "tasks":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_TASKS_ROLLOVER));
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
			case "exit":
				icon = Utills
						.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_EXIT));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "help":
				icon = Utills
						.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_HELP));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "about":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_ABOUT));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "tasks":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_TASKS));
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

	class WindowMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			switch (l.getName()) {
			case "close":
				if (showedTask != null)
					showedTask.pause();
				if (!currentMethod.equals("showFirstScreen")) {
					CustomDialog d = new CustomDialog(reabilitation,
							InterfaceTextDefaults.getInstance().getDefault("sure_exit"),
							InterfaceTextDefaults.getInstance().getDefault("exit"),
							InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
					if (d.getAnswer() == 1)
						System.exit(0);
					else if (showedTask != null)
						showedTask.start();
				} else
					System.exit(0);
				break;
			case "restore":
				resize();
				break;
			case "hide":
				setState(JFrame.ICONIFIED);
				break;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			ImageIcon icon;
			switch (l.getName()) {
			case "close":
				icon = Utills
						.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.CLOSE_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "restore":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.RESTORE_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "hide":
				icon = Utills
						.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.HIDE_ROLLOVER));
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
			case "close":
				icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.CLOSE));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "restore":
				icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.RESTORE));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "hide":
				icon = Utills.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.HIDE));
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

	class SmallMenuMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			switch (l.getName()) {
			case "about":
				if (showedTask != null && !showedTask.isDontShowBreakingDialog()) {
					showedTask.pause();
					CustomDialog d1 = new CustomDialog(reabilitation,
							InterfaceTextDefaults.getInstance().getDefault("sure_break_task"),
							InterfaceTextDefaults.getInstance().getDefault("break"),
							InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
					if (d1.getAnswer() == 1)
						showTaskInfo(currentTask);
					else if (showedTask != null)
						showedTask.start();
				} else
					showTaskInfo(currentTask);
				break;
			case "begining":
				if (showedTask != null && !showedTask.isDontShowBreakingDialog()) {
					showedTask.pause();
					CustomDialog d1 = new CustomDialog(reabilitation,
							InterfaceTextDefaults.getInstance().getDefault("sure_break_task"),
							InterfaceTextDefaults.getInstance().getDefault("break"),
							InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
					if (d1.getAnswer() == 1)
						showTaskInfo(currentTask);
					else if (showedTask != null)
						showedTask.start();
				} else
					showTaskInfo(currentTask);
				break;
			case "end":
				if (showedTask != null && !showedTask.isDontShowBreakingDialog()) {
					showedTask.pause();
					CustomDialog d1 = new CustomDialog(reabilitation,
							InterfaceTextDefaults.getInstance().getDefault("sure_break_task"),
							InterfaceTextDefaults.getInstance().getDefault("break"),
							InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
					if (d1.getAnswer() == 1)
						showTasks(currentTaskGroup);
					else if (showedTask != null)
						showedTask.start();
				} else
					showTasks(currentTaskGroup);
				break;
			case "results":
				if (showedTask != null && !showedTask.isDontShowBreakingDialog()) {
					showedTask.pause();
					CustomDialog d1 = new CustomDialog(reabilitation,
							InterfaceTextDefaults.getInstance().getDefault("sure_break_task"),
							InterfaceTextDefaults.getInstance().getDefault("break"),
							InterfaceTextDefaults.getInstance().getDefault("cancel"), true);
					if (d1.getAnswer() == 1) {
						showResults();
					} else if (showedTask != null)
						showedTask.start();
				} else {
					showResults();
				}
				break;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			ImageIcon icon;
			switch (l.getName()) {
			case "about":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_ABOUT_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "begining":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_BEGINING_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "end":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_END_ROLLOVER));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "results":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_RESULTS_ROLLOVER));
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
			case "about":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_ABOUT));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "begining":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_BEGINING));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "end":
				icon = Utills
						.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_END));
				l.setIcon(icon);
				l.updateUI();
				break;
			case "results":
				icon = Utills.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_RESULTS));
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

	class GroupsMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			int i = Integer.parseInt(l.getName());
			currentTaskGroup = i;
			showTasks(i);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			int i = Integer.parseInt(l.getName());
			ImageIcon icon = Utills.createImageIcon(taskGroups[i].getRolloverImage());
			l.setIcon(icon);
			l.updateUI();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			int i = Integer.parseInt(l.getName());
			ImageIcon icon = Utills.createImageIcon(taskGroups[i].getImage());
			l.setIcon(icon);
			l.updateUI();
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	class TasksMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			int i = Integer.parseInt(l.getName());
			currentTask = i;
			showTaskInfo(i);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			int i = Integer.parseInt(l.getName());

			ImageIcon icon = Utills.createImageIcon(tasks[i].getRolloverImage());
			l.setIcon(icon);
			actualPanel.revalidate();
			actualPanel.repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JLabel l = (JLabel) e.getSource();
			int i = Integer.parseInt(l.getName());

			ImageIcon icon = Utills.createImageIcon(tasks[i].getImage());
			l.setIcon(icon);
			actualPanel.revalidate();
			actualPanel.repaint();
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	private class InternalEventHandler implements AWTEventListener {

		@Override
		public void eventDispatched(AWTEvent event) {
			if (popup == null)
				return;
			if (MouseEvent.MOUSE_CLICKED == event.getID() && event.getSource() != tasksIcon) {
				Set<Component> components = Utills.getAllComponents(popupMenuPanel);
				boolean clickInPopup = false;
				for (Component component : components) {
					if (event.getSource() == component) {
						clickInPopup = true;
					}
				}
				if (!clickInPopup) {
					popup.hide();
				}
			}
		}

	}
}
