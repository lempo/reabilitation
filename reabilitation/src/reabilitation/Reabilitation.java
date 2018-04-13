package reabilitation;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;

import customcomponent.CustomButton;
import customcomponent.CustomDialog;
import customcomponent.CustomLabel;
import customcomponent.CustomPanel;
import customcomponent.MenuPanel;
import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import dialogs.Dialogs;
import exception.ProgramFilesBrokenException;
import exception.ServerConnectionException;
import listeners.GroupsMouseListener;
import listeners.MenuMouseListener;
import listeners.SmallMenuMouseListener;
import listeners.TasksMouseListener;
import listeners.TasksPopupMenuHandler;
import listeners.WindowButtonsMouseListener;
import listeners.WindowDraggingListener;
import reabilitation.utils.MainScreenUtils;
import reabilitation.utils.Utils;
import reabilitation.utils.XmlUtils;
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
	public static int WINDOW_WIDTH = 1020;
	public static int WINDOW_HEIGHT = 790;
	private static boolean fullScreen = false;
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

	public void start(boolean specLogged) {
		specialistLogged = specLogged;
		panel = new BackgroundPanel(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.BACKGROUND),
				WINDOW_WIDTH, WINDOW_HEIGHT);

		menuPanel = new CustomPanel(new Color(55, 71, 79));
		menuPanel.setOpaque(false);
		createMainMenu();

		actualPanel = new JPanel();
		actualPanel.setOpaque(false);
		createWindowButtons();
		if (!fullScreen) {
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
		if (fullScreen)
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

		MainScreenUtils.showUsersScreen(actualPanel, this);
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

		if (!fullScreen) {
			windowPanel.addMouseListener(draggingMouseListener);
			windowPanel.addMouseMotionListener(draggingMouseMotionListener);
		}

		JPanel p = new JPanel();
		MainScreenUtils.showLoginScreen(p, this);

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

		taskGroups = XmlUtils.getTaskGroups();		
		MainScreenUtils.showGroupsScren(actualPanel, taskGroups, new GroupsMouseListener(), this);
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
		
		tasks = XmlUtils.getTasks(i);
		MainScreenUtils.showTasksScren(actualPanel, taskGroups[i], tasks, new TasksMouseListener());
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
		
		MainScreenUtils.showTaskInfoScren(actualPanel, tasks[i], new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showTask(i);
			}
		});
	}

	public void showAbout() {
		menuPanel.setX1(WINDOW_WIDTH - iconsSpace * 3 - exitIcon.getWidth() - aboutIcon.getWidth() - 3);
		menuPanel.setX2(WINDOW_WIDTH - iconsSpace * 3 - exitIcon.getWidth() + 3);
		menuPanel.repaint();

		currentMethod = "showAbout";
		paramTypes = new Class[] {};
		args = new Object[] {};

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

		MainScreenUtils.showAboutScreen(actualPanel, this);
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

		MainScreenUtils.showHelpScreen(actualPanel, this);
	}

	public void showTask(int i) {
		currentMethod = "showTask";
		paramTypes = new Class[] { int.class };
		args = new Object[] { i };

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
		
		showedTask = MainScreenUtils.showTaskScreen(actualPanel, taskGroups[currentTaskGroup], tasks[i], this);
	}

	public void showResults(String[][] rows, String[] taskNames, int selectedTask, Date filterDate1, Date filterDate2) {
		currentMethod = "showResults";
		paramTypes = new Class[] { String[][].class, String[].class, int.class, Date.class, Date.class };
		args = new Object[] { rows, taskNames, selectedTask, filterDate1, filterDate2 };

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

		MainScreenUtils.showResults(actualPanel, resultsRows, rows, taskNames, selectedTask, filterDate1, filterDate2);
	}

	public void editUserOrNewUser(String userOldName, String userOldPass) {
		currentMethod = "editUserOrNewUser";
		paramTypes = new Class[] { String.class, String.class };
		args = new Object[] { userOldName, userOldPass };

		boolean newUser = false;
		if (userOldName == null && userOldPass == null)
			newUser = true;

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

		MainScreenUtils.showEditUserOrNewUser(actualPanel, userOldName, userOldPass, this);
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
		if (!fullScreen) {
			sSize = Toolkit.getDefaultToolkit().getScreenSize();
			fullScreen = true;
			windowPanel.removeMouseListener(draggingMouseListener);
			windowPanel.removeMouseMotionListener(draggingMouseMotionListener);
		} else {
			sSize = new Dimension(1020, 790);
			fullScreen = false;
			windowPanel.addMouseListener(draggingMouseListener);
			windowPanel.addMouseMotionListener(draggingMouseMotionListener);
		}
		WINDOW_HEIGHT = sSize.height;
		WINDOW_WIDTH = sSize.width;
		if (fullScreen)
			setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		else
			setBounds(50, 50, WINDOW_WIDTH, WINDOW_HEIGHT);

		if (!currentMethod.equals("showLoginScreen")) {
			panel = new BackgroundPanel(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.BACKGROUND),
					WINDOW_WIDTH, WINDOW_HEIGHT);

			createMainMenu();
			createWindowButtons();
			if (!fullScreen) {
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
