package customcomponent;

import javax.swing.JDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import customuiandrender.ButtonCustomUI;
import customuiandrender.PanelCustomUI;
import defaults.InterfaceTextDefaults;

public class CustomDialog extends JDialog {
	private static final long serialVersionUID = 6949884880454578705L;
	private int answer = 0;
	CustomTextField pass;
	CustomTextField login;

	public CustomDialog(JFrame parent, String title, String y, String n) {
		super(parent, title);

		setUndecorated(true);
		setPreferredSize(new Dimension(350, 200));
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		getContentPane().setBackground(Color.WHITE);

		// set the position of the window
		Point p = new Point(400, 400);
		setLocation(p.x, p.y);

		// Create a message
		JPanel messagePane = new JPanel();
		messagePane.setUI(new PanelCustomUI(false));
		messagePane.setOpaque(false);
		messagePane.setLayout(new GridBagLayout());

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
		c.weighty = 1.0;

		messagePane.add(new JLabel("<html><div style='font: bold 16pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ title.toUpperCase().replaceAll(" ", "<br/>") + "</div></html>"), c);

		pass = new CustomTextField(20, InterfaceTextDefaults.getInstance().getDefault("password"));
		login = new CustomTextField(20, InterfaceTextDefaults.getInstance().getDefault("name_surname_patronymic"));
		
		c.gridy = 1;
		messagePane.add(login, c);
		c.gridy = 2;
		messagePane.add(pass, c);

		JButton yes = new JButton(y);
		yes.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
		yes.setBorder(null);
		yes.setOpaque(false);
		yes.setPreferredSize(new Dimension(85, 35));
		yes.setMinimumSize(new Dimension(85, 35));
		yes.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				answer = 1;
				setVisible(false);
				dispose();
			}
		});
		JButton no = new JButton(n);
		no.setUI(new ButtonCustomUI(new Color(239, 83, 80)));
		no.setBorder(null);
		no.setOpaque(false);
		no.setPreferredSize(new Dimension(85, 35));
		no.setMinimumSize(new Dimension(85, 35));
		no.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		no.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				answer = 0;
				setVisible(false);
				dispose();
			}
		});

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 4);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		messagePane.add(no, c);

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(0, 4, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;

		messagePane.add(yes, c);

		getContentPane().add(messagePane);

		pack();

		setVisible(true);
	}

	public CustomDialog(JFrame parent, String title, String y, String n, boolean shad) {
		super(parent, title);

		setUndecorated(true);
		setPreferredSize(new Dimension(310, 160));
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		getContentPane().setBackground(Color.WHITE);

		// set the position of the window
		Point p = new Point(400, 400);
		setLocation(p.x, p.y);

		// Create a message
		JPanel messagePane = new JPanel();
		messagePane.setUI(new PanelCustomUI(false));
		messagePane.setOpaque(false);
		messagePane.setLayout(new GridBagLayout());

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
		c.weighty = 1.0;

		messagePane.add(new JLabel("<html><div style='font: bold 16pt Arial Narrow; color: rgb(70, 110, 122);'>"
				+ title.toUpperCase().replaceAll(" ", "<br/>") + "</div></html>"), c);

		// Create a button

		if ((y == null) || (n == null)) {

			String v = "";
			if (y != null)
				v = y;
			else {
				if (n != null)
					v = n;
			}

			JButton yes = new JButton(v);
			yes.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
			yes.setBorder(null);
			yes.setOpaque(false);
			yes.setPreferredSize(new Dimension(85, 35));
			yes.setMinimumSize(new Dimension(85, 35));
			yes.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			yes.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					answer = 1;
					setVisible(false);
					dispose();
				}
			});

			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridx = 0;
			c.gridy = 1;
			c.insets = new Insets(0, 0, 0, 0);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;

			messagePane.add(yes, c);
		} else {
			JButton yes = new JButton(y);
			yes.setUI(new ButtonCustomUI(new Color(38, 166, 154)));
			yes.setBorder(null);
			yes.setOpaque(false);
			yes.setPreferredSize(new Dimension(85, 35));
			yes.setMinimumSize(new Dimension(85, 35));
			yes.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			yes.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					answer = 1;
					setVisible(false);
					dispose();
				}
			});
			JButton no = new JButton(n);
			no.setUI(new ButtonCustomUI(new Color(239, 83, 80)));
			no.setBorder(null);
			no.setOpaque(false);
			no.setPreferredSize(new Dimension(85, 35));
			no.setMinimumSize(new Dimension(85, 35));
			no.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			no.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					answer = 0;
					setVisible(false);
					dispose();
				}
			});

			c.anchor = GridBagConstraints.EAST;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			c.insets = new Insets(0, 0, 0, 4);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;

			messagePane.add(no, c);

			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.NONE;
			c.gridheight = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridx = 1;
			c.gridy = 1;
			c.insets = new Insets(0, 4, 0, 0);
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;

			messagePane.add(yes, c);
		}

		if (shad) {
			DropShadowBorder shadow = new DropShadowBorder();
			shadow.setShadowColor(Color.GRAY);
			shadow.setShadowSize(10);
			shadow.setShadowOpacity((float) 0.2);
			shadow.setShowRightShadow(true);
			shadow.setShowBottomShadow(true);
			messagePane.setBorder(shadow);
		}
		// get content pane, which is usually the
		// Container of all the dialog's components.
		getContentPane().add(messagePane);

		pack();

		setVisible(true);
	}

	// override the createRootPane inherited by the JDialog, to create the
	// rootPane.
	// create functionality to close the window when "Escape" button is pressed

	public JRootPane createRootPane() {

		JRootPane rootPane = new JRootPane();

		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");

		Action action = new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				System.out.println("escaping..");
				setVisible(false);
				dispose();

			}

		};

		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", action);

		return rootPane;
	}

	public int getAnswer() {
		return answer;
	}

	public String getLogin() {
		if (login != null)
			return login.getText();
		else
			return null;
	}

	public String getPass() {
		if (pass != null)
			return pass.getText();
		else
			return null;
	}
}
