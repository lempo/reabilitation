package reabilitation;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Popup;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import jdatepicker.DatePicker;
import jdatepicker.JDatePicker;
import listeners.GroupsMouseListener;
import listeners.MenuMouseListener;
import listeners.SmallMenuMouseListener;
import listeners.TasksMouseListener;
import listeners.TasksPopupMenuHandler;
import listeners.WindowButtonsMouseListener;
import listeners.WindowDraggingListener;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import customcomponent.CustomButton;
import customcomponent.CustomDialog;
import customcomponent.TooltipLabel;
import customcomponent.CustomPanel;
import customcomponent.CustomPasswordField;
import customcomponent.CustomTextField;
import customcomponent.CustomLabel;
import customcomponent.MenuPanel;
import customcomponent.ResultsPanel;
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

////////////////////////////////////////
// !!!!!!!!!!!!!!!!! Note that this class has A LOT OF public fields
////////////////////////////////////////
public class Reabilitation extends JFrame {

	private static final long serialVersionUID = 2193812198591285704L;

	////////////////////////////////////////
	// APP INSTANSE
	////////////////////////////////////////
	public static Reabilitation reabilitation;

	////////////////////////////////////////
	// ABOUT THE WINDOW
	////////////////////////////////////////
	private static int WINDOW_WIDTH = 1020;
	public static int WINDOW_HEIGHT = 790;
	private static boolean resized = false;
	public static Point lastDragPosition;
	private static int logoSpace = 289;
	private static int iconsSpace = 10;

	////////////////////////////////////////
	// UI EVENTS
	////////////////////////////////////////
	private static MouseListener draggingMouseListener;
	private static WindowDraggingListener draggingMouseMotionListener;

	////////////////////////////////////////
	// UI ELEMENTS
	////////////////////////////////////////
	/** Main panel */
	private static BackgroundPanel panel;
	/** Panel with program menu */
	private static CustomPanel menuPanel;
	/** Panel with current content */
	public static JPanel actualPanel;
	/** Panel with header and small menu */
	private static JPanel headerPanel;
	/** Panel with window buttons */
	private static JPanel windowPanel;
	/** Menu icons */
	private static CustomLabel exitIcon;
	private static CustomLabel aboutIcon;
//	private static CustomLabel helpIcon;
	public static CustomLabel tasksIcon;
	private static CustomLabel usersIcon;
	private static CustomLabel smallMenuAboutIcon;
	private static CustomLabel smallMenuBeginingIcon;
	private static CustomLabel smallMenuEndIcon;
	private static CustomLabel smallMenuResultsIcon;
	private static CustomLabel closeIcon;
	private static CustomLabel restoreIcon;
	private static CustomLabel hideIcon;
	/** Tasks popup menu */
	public static Popup popup = null;
	public static MenuPanel popupMenuPanel;

	////////////////////////////////////////
	// GROUPS AND TASKS
	////////////////////////////////////////
	public static TaskGroup[] taskGroups;
	public static Task[] tasks;
	public static int currentTaskGroup = 0;
	public static int currentTask = 0;
	public static AbstractTask showedTask = null;

	////////////////////////////////////////
	// SCREEN NAVIGATION
	////////////////////////////////////////
	/** Current method name */
	public static String currentMethod;
	/** Parameter types of current method */
	private static Class[] paramTypes;
	/** Arguments of current method */
	private static Object[] args;

	////////////////////////////////////////
	// USER DATA
	////////////////////////////////////////
	public static String userName = "";
	public static String userPass = "";
	private static boolean specialistLogged;

	////////////////////////////////////////
	// USER STATISTICS
	////////////////////////////////////////
	private static String[][] resultsRows;

	public Reabilitation() {
		super("Reabilitation");
		reabilitation = this;

		if (Utils.getCheckUpdatesAuto()) {
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
				CustomDialog d1 = new CustomDialog(reabilitation, "do_update", "yes", "no", false);
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

		setBounds(50, 50, WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		Toolkit.getDefaultToolkit().addAWTEventListener(new TasksPopupMenuHandler(), MouseEvent.MOUSE_PRESSED);

		draggingMouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				lastDragPosition = e.getLocationOnScreen();
			}
		};
		draggingMouseMotionListener = new WindowDraggingListener();

		showLoginScreen();
	}

	private void start(boolean specLogged) {
		specialistLogged = specLogged;
		panel = new BackgroundPanel(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.BACKGROUND),
				WINDOW_WIDTH, WINDOW_HEIGHT);

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
		headerPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, 60));

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

		showGroups();
	}

	public void createWindowButtons() {
		windowPanel = new JPanel();
		windowPanel.setOpaque(false);

		WindowButtonsMouseListener windowButtonsMouseListener = new WindowButtonsMouseListener();
		closeIcon = new CustomLabel("close", ImageLinkDefaults.Key.CLOSE, windowButtonsMouseListener);
		closeIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		restoreIcon = new CustomLabel("restore", ImageLinkDefaults.Key.RESTORE, windowButtonsMouseListener);
		restoreIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		hideIcon = new CustomLabel("hide", ImageLinkDefaults.Key.HIDE, windowButtonsMouseListener);
		hideIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		windowPanel.removeAll();
		windowPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, (int) Math.round(WINDOW_HEIGHT * 0.038)));
		windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.X_AXIS));
		windowPanel.add(Box.createHorizontalGlue());

		float space = (float) (WINDOW_WIDTH * 0.032);
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

	public void showUsers() {
		menuPanel.setX1(0);
		menuPanel.setX2(0);
		menuPanel.repaint();

		currentMethod = "showUsers";
		paramTypes = new Class[] {};
		args = new Object[] {};

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		CustomLabel heading = new CustomLabel("users", 24, new Color(70, 110, 122), true);
		CustomButton newUser = new CustomButton("create_new_user", 
												new Color(38, 166, 154), 
												300, 
												35);
		newUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editUserOrNewUser(null, null);
			}
		});

		headerPanel.removeAll();
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(heading);
		heading.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(newUser);
		newUser.setAlignmentY(BOTTOM_ALIGNMENT);
		headerPanel.add(Box.createHorizontalStrut(20));
		headerPanel.add(Box.createHorizontalGlue());
		headerPanel.revalidate();
		headerPanel.repaint();

		String rows[][] = null;
		try {
			rows = HTTPClient.listPatients();
			if (rows.length == 0) {
				actualPanel.revalidate();
				actualPanel.repaint();
				return;
			}
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
					showUsers();
				}

				if (col == 3) {
					editUserOrNewUser((String) table.getModel().getValueAt(row, 2),
							(String) table.getModel().getValueAt(row, 1));
				}
			}
		});

		int maxWidth = (int) (Math.min(tableWidth, Math.round(WINDOW_WIDTH * 0.9)) + 10);
		int maxHeight = (int) Math.min(tableHeight, Math.round(WINDOW_HEIGHT * 0.6));

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
		c.insets = new Insets((int) Math.round(WINDOW_HEIGHT * 0.1), 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 1.0;

		actualPanel.add(scroll, c);

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	/**
	 * Shows sign in/up screen
	 */
	public void showLoginScreen() {
		currentMethod = "showLoginScreen";
		paramTypes = new Class[] {};
		args = new Object[] {};

		panel = new BackgroundPanel(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.FIRST_SCREEN),
				WINDOW_WIDTH, WINDOW_HEIGHT);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		createWindowButtons();

		if (!resized) {
			windowPanel.addMouseListener(draggingMouseListener);
			windowPanel.addMouseMotionListener(draggingMouseMotionListener);
		}

		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
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
						userName = login.getText().trim();
						userPass = pass.getPass();
						start(true);
					} else if (HTTPClient.loginPatient(login.getText().trim(), pass.getPass())) {
						userName = login.getText().trim();
						userPass = pass.getPass();
						start(false);
					}
					else
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
		p.add(logo, c);

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

		c.insets = new Insets((int) Math.round(WINDOW_HEIGHT / 3.5), 0, 0, 0);
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		p.add(container, c);

		CustomLabel copyright = new CustomLabel(ImageLinkDefaults.Key.COPYRIGHT);

		c.insets = new Insets((int) Math.round(WINDOW_HEIGHT / 15), 0, 0, 0);
		c.gridx = 0;
		c.gridy = 2;
		p.add(copyright, c);

		p.revalidate();
		p.repaint();

		panel.add(windowPanel);
		panel.add(p);
		setContentPane(panel);
		panel.revalidate();
		panel.repaint();
	}

	private void createMainMenu() {
		MenuMouseListener menuMouseListener = new MenuMouseListener();

		CustomButton loginSpec = new CustomButton("rule_users", 
													new Color(96, 125, 139), 
													new Color(144, 164, 174), 
													235,
													35);
		loginSpec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (showedTask != null && !showedTask.isDontShowBreakingDialog()) {
					showedTask.pause();
					CustomDialog d1 = new CustomDialog(reabilitation, "sure_break_task", "break", "cancel", true);
					if (d1.getAnswer() == 1)
						showUsers();
					else if (showedTask != null)
						showedTask.start();
				} else
					showUsers();
			}
		});

		exitIcon = new CustomLabel("exit", ImageLinkDefaults.Key.MAIN_MENU_EXIT, menuMouseListener);
		aboutIcon = new CustomLabel("about", ImageLinkDefaults.Key.MAIN_MENU_ABOUT, menuMouseListener);
//		helpIcon = new CustomLabel("help", ImageLinkDefaults.Key.MAIN_MENU_HELP, menuMouseListener);
		tasksIcon = new CustomLabel("tasks", ImageLinkDefaults.Key.MAIN_MENU_TASKS, menuMouseListener);

		CustomLabel userIcon = new CustomLabel(ImageLinkDefaults.Key.USER);

		JLabel userNameIcon = new JLabel();
		String t = "<html><div style='font: 14pt Arial Narrow; color: rgb(176, 190, 197);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("user").toUpperCase() + ": "
				+ "<span style='color: rgb(255, 183, 77);'>" + "</span></div>"
				+ "<div style='font: 14pt Arial Narrow; color: rgb(255, 183, 77);'>" + userName + "</div></html>";
		userNameIcon.setText(t);

		logoSpace = (int) Math.round(WINDOW_WIDTH * 0.28);
		if (menuPanel != null) {
			menuPanel.removeAll();
			menuPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, (int) Math.round(WINDOW_HEIGHT * 0.076)));
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

			if (specialistLogged) {
				menuPanel.add(loginSpec);
				menuPanel.add(Box.createHorizontalStrut(iconsSpace));
			}
			menuPanel.add(tasksIcon);
//			menuPanel.add(Box.createHorizontalStrut(iconsSpace));
//			menuPanel.add(helpIcon);
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
		SmallMenuMouseListener smallMenuMouseListener = new SmallMenuMouseListener();
		smallMenuAboutIcon = new CustomLabel("about", ImageLinkDefaults.Key.SMALL_MENU_ABOUT, smallMenuMouseListener);
		smallMenuAboutIcon.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		smallMenuBeginingIcon = new CustomLabel("begining", ImageLinkDefaults.Key.SMALL_MENU_BEGINING,
				smallMenuMouseListener);
		smallMenuBeginingIcon.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		smallMenuEndIcon = new CustomLabel("end", ImageLinkDefaults.Key.SMALL_MENU_END, smallMenuMouseListener);
		smallMenuEndIcon.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		smallMenuResultsIcon = new CustomLabel("results", ImageLinkDefaults.Key.SMALL_MENU_RESULTS,
				smallMenuMouseListener);
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

	public void showGroups() {
		menuPanel.setX1(WINDOW_WIDTH 
				- iconsSpace * 4 
				- exitIcon.getWidth() 
//				- helpIcon.getWidth() 
				- tasksIcon.getWidth()
				- aboutIcon.getWidth() - 3);
		menuPanel.setX2(
				WINDOW_WIDTH 
				- iconsSpace * 4 
				- exitIcon.getWidth() 
//				- helpIcon.getWidth() 
				- aboutIcon.getWidth() + 3);
		menuPanel.repaint();

		currentMethod = "showGroups";
		paramTypes = new Class[] {};
		args = new Object[] {};

		showedTask = null;
		CustomLabel heading = new CustomLabel("task_groups", 24, new Color(70, 110, 122), true);

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
		text.setPreferredSize(new Dimension((int) (WINDOW_WIDTH * 0.9), 175));
		p.setUI(new PanelCustomUI(true));

		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.GROUPS));

		NodeList n = doc.getElementsByTagName("group");
		NamedNodeMap k = null;

		JLabel[] groups = new JLabel[n.getLength()];
		taskGroups = new TaskGroup[n.getLength()];

		ImageIcon icon;
		GroupsMouseListener groupsMouseListener = new GroupsMouseListener();
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			taskGroups[i] = new TaskGroup(k.getNamedItem("name").getNodeValue(), k.getNamedItem("text").getNodeValue(),
					k.getNamedItem("image").getNodeValue(), k.getNamedItem("bigImage").getNodeValue(),
					k.getNamedItem("rolloverImage").getNodeValue(), k.getNamedItem("toolTipText").getNodeValue());

			icon = Utils.createImageIcon(taskGroups[i].getImage());
			groups[i] = new TooltipLabel();
			groups[i].setIcon(icon);
			groups[i].setHorizontalTextPosition(JLabel.CENTER);
			groups[i].setVerticalTextPosition(JLabel.BOTTOM);
			groups[i].setName(Integer.toString(i));
			groups[i].addMouseListener(groupsMouseListener);
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

	private void readTasks(int i) {
		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.GROUPS));

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
		ImageIcon icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.ARROW));
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

		CustomLabel image = new CustomLabel(taskGroups[i].getBigImage());

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");
		text.setText("<html><div style='font: 15pt Arial Narrow; color: rgb(68, 83, 91);'>" + taskGroups[i].getText()
				+ "</div></html>");
		text.setOpaque(false);
		text.setPreferredSize(new Dimension((int) (WINDOW_WIDTH * 0.85 - image.getPreferredSize().getWidth()), 300));

		JScrollPane scroll = new JScrollPane(text);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setPreferredSize(
				new Dimension((int) (WINDOW_WIDTH * 0.85 - image.getPreferredSize().getWidth()), (int) (300)));
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
		TasksMouseListener tasksMouseListener = new TasksMouseListener();

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

		JScrollPane scrollTasks = new JScrollPane(p);
		scrollTasks.setPreferredSize(new Dimension((int) (text.getPreferredSize().getWidth()),
				(int) (WINDOW_HEIGHT * 0.7 - text.getPreferredSize().getHeight())));
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

	public void showTaskInfo(int i) {
		currentMethod = "showTaskInfo";
		paramTypes = new Class[] { int.class };
		args = new Object[] { i };

		showedTask = null;
		String headingText = InterfaceTextDefaults.getInstance().getDefault("task_number") + Integer.toString(i + 1)
				+ ": " + tasks[i].getName().toUpperCase();
		CustomLabel heading = new CustomLabel(headingText, 24, new Color(70, 110, 122), true,
				ImageLinkDefaults.Key.ARROW, 20, new MouseAdapter() {
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

		CustomLabel image = new CustomLabel(tasks[i].getBigImage());

		JTextPane text = new JTextPane();
		text.setEditable(false);
		text.setContentType("text/html;charset=utf-8");
		text.setText("<html><div style='font: bold 22pt Arial Narrow; color: rgb(115, 84, 73); padding-bottom: 20 px'>"
				+ tasks[i].getName().toUpperCase()
				+ "</div><div  style='font: 16pt Arial Narrow; color: rgb(115, 84, 73);'>" + tasks[i].getLongText()
				+ "</div></html>");
		text.setOpaque(false);
		text.setPreferredSize(new Dimension((int) (WINDOW_WIDTH * 0.85 - image.getPreferredSize().getWidth()),
				100 + Utils.calculateTextHeight(text.getText(),
						(int) (WINDOW_WIDTH * 0.85 - image.getPreferredSize().getWidth()), text)));

		CustomButton start = new CustomButton("begin_task", new Color(38, 166, 154), 200, 35);
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
		menuPanel.setX1(WINDOW_WIDTH - iconsSpace * 3 - exitIcon.getWidth() - aboutIcon.getWidth() - 3);
		menuPanel.setX2(WINDOW_WIDTH - iconsSpace * 3 - exitIcon.getWidth() + 3);
		menuPanel.repaint();

		currentMethod = "showAbout";
		paramTypes = new Class[] {};
		args = new Object[] {};

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

		showedTask = null;
		CustomLabel heading = new CustomLabel(InterfaceTextDefaults.getInstance().getDefault("about"), 24,
				new Color(70, 110, 122), true, ImageLinkDefaults.Key.ARROW, 20, new MouseAdapter() {
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
			text.setPage(getClass().getResource(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.ABOUT)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JScrollPane scroll = new JScrollPane(text);
		scroll.setPreferredSize(new Dimension((int) (WINDOW_WIDTH * 0.95), (int) (WINDOW_HEIGHT * 0.45)));
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
		menuPanel.setX1(
				WINDOW_WIDTH 
				- iconsSpace * 4 
				- exitIcon.getWidth() 
//				- helpIcon.getWidth() 
				- aboutIcon.getWidth() - 3);
		menuPanel.setX2(WINDOW_WIDTH 
				- iconsSpace * 4 
				- exitIcon.getWidth() 
				- aboutIcon.getWidth() + 3);
		menuPanel.repaint();

		currentMethod = "showHelp";
		paramTypes = new Class[] {};
		args = new Object[] {};

		showedTask = null;
		CustomLabel heading = new CustomLabel(InterfaceTextDefaults.getInstance().getDefault("help"), 24,
				new Color(70, 110, 122), true, ImageLinkDefaults.Key.ARROW, 20, new MouseAdapter() {
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

		CustomLabel faq = new CustomLabel("faq", 22, new Color(115, 84, 73), false);

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
		scroll.setPreferredSize(new Dimension((int) (WINDOW_WIDTH * 0.95), (int) (WINDOW_HEIGHT * 0.7)));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());

		c.gridy = 1;

		actualPanel.add(scroll, c);

		actualPanel.revalidate();
		actualPanel.repaint();
	}

	public void showTask(int i) {
		currentMethod = "showTask";
		paramTypes = new Class[] { int.class };
		args = new Object[] { i };

		String s = tasks[i].getClassName();
		Class c;
		Class[] intArgsClass = new Class[] { int.class, int.class, String.class, Reabilitation.class, String.class,
				String.class, String.class, String.class };
		// TODO �� �� �����
		Integer h = new Integer((int) (WINDOW_HEIGHT * 0.75));
		Integer w = new Integer(970);
		String t1 = tasks[i].getLongLongText();
		Object[] intArgs = new Object[] { w, h, t1, reabilitation, userName, userPass, tasks[i].getName(),
				taskGroups[currentTaskGroup].getName() };
		Constructor intArgsConstructor = null;
		AbstractTask p = null;
		try {
			c = Class.forName(s);
			intArgsConstructor = c.getConstructor(intArgsClass);
			p = (AbstractTask) Utils.createObject(intArgsConstructor, intArgs);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}

		String t = InterfaceTextDefaults.getInstance().getDefault("task_number") + Integer.toString(i + 1) + ": "
				+ tasks[i].getName().toUpperCase();
		CustomLabel heading = new CustomLabel(t, 24, new Color(70, 110, 122), true, ImageLinkDefaults.Key.ARROW, 20,
				new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						if (showedTask != null) {
							showedTask.pause();
							CustomDialog d1 = new CustomDialog(reabilitation, "sure_break_task", "break", "cancel",
									true);
							if (d1.getAnswer() == 1)
								showTaskInfo(i);
							else if (showedTask != null)
								showedTask.start();
						} else
							showTaskInfo(i);
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

		String t = InterfaceTextDefaults.getInstance().getDefault("results") + ": " + userName.toUpperCase();
		CustomLabel heading = new CustomLabel(t, 24, new Color(70, 110, 122), true, ImageLinkDefaults.Key.ARROW, 20,
				new MouseAdapter() {
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

		actualPanel.add(subHeading, c1);

		CustomLabel filter = new CustomLabel("filter", 16, new Color(70, 110, 122), true);

		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new GridBagLayout());
		p.setPreferredSize(new Dimension(WINDOW_WIDTH, 40));

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

	public void editUserOrNewUser(String userOldName, String userOldPass) {
		currentMethod = "editUserOrNewUser";
		paramTypes = new Class[] { String.class, String.class };
		args = new Object[] { userOldName, userOldPass };

		boolean newUser = false;
		if (userOldName == null && userOldPass == null)
			newUser = true;

		actualPanel.removeAll();
		actualPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		String headingText;
		if (newUser)
			headingText = InterfaceTextDefaults.getInstance().getDefault("create_new_user_heading");
		else
			headingText = InterfaceTextDefaults.getInstance().getDefault("edit_user");
		CustomLabel heading = new CustomLabel(headingText, 24, new Color(70, 110, 122), true,
				ImageLinkDefaults.Key.ARROW, 20, new MouseAdapter() {
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
				showUsers();
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
					showUsers();
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

		actualPanel.add(nameLabel, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;

		actualPanel.add(nameField, c);

		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;

		actualPanel.add(passwordLabel, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;

		actualPanel.add(passwordField, c);

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
			resultsRows = HTTPClient.findResults(userName, userPass);
		} catch (ServerConnectionException e) {
			e.printStackTrace();
			Dialogs.showServerConnectionErrorDialog(e);
		} catch (ProgramFilesBrokenException e) {
			e.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e);
		}
		ArrayList<String> a = new ArrayList<String>();
		for (int i = 0; i < resultsRows.length; i++)
			if (!a.contains(resultsRows[i][2]))
				a.add(resultsRows[i][2]);
		a.add(InterfaceTextDefaults.getInstance().getDefault("all_tasks"));
		String[] n = new String[a.size()];
		showResults(resultsRows, a.toArray(n), 0, null, null);
	}

	public void resize() {

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
		WINDOW_HEIGHT = sSize.height;
		WINDOW_WIDTH = sSize.width;
		if (resized)
			setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		else
			setBounds(50, 50, WINDOW_WIDTH, WINDOW_HEIGHT);

		if (!currentMethod.equals("showLoginScreen")) {
			panel = new BackgroundPanel(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.BACKGROUND),
					WINDOW_WIDTH, WINDOW_HEIGHT);

			createMainMenu();
			createWindowButtons();
			if (!resized) {
				windowPanel.addMouseListener(draggingMouseListener);
				windowPanel.addMouseMotionListener(draggingMouseMotionListener);
			}

			panel.setDoubleBuffered(true);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(windowPanel);
			panel.add(menuPanel);
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
	 * Panel which displays background image.
	 * 
	 * @author Pokrovskaya Oksana
	 *
	 */
	public class BackgroundPanel extends JPanel {
		private static final long serialVersionUID = -2043137411007649138L;
		private final String image;
		private final int width;
		private final int height;

		public BackgroundPanel(String backgroundPath, int width, int height) {
			image = backgroundPath;
			this.width = width;
			this.height = height;
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
}
