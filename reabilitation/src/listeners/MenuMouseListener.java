package listeners;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.PopupFactory;

import customcomponent.CustomDialog;
import customcomponent.MenuPanel;
import defaults.ImageLinkDefaults;
import reabilitation.Reabilitation;
import reabilitation.utils.Utils;

public class MenuMouseListener implements MouseListener  {
	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		switch (l.getName()) {
		case "exit":
			if (Reabilitation.showedTask != null && !Reabilitation.showedTask.isDontShowBreakingDialog())
				Reabilitation.showedTask.pause();
			CustomDialog d = new CustomDialog(Reabilitation.reabilitation, "sure_logout", "exit", "cancel", true);
			if (d.getAnswer() == 1)
				Reabilitation.reabilitation.showLoginScreen();
			else if (Reabilitation.showedTask != null)
				Reabilitation.showedTask.start();
			break;
		case "help":
			if (Reabilitation.showedTask != null && !Reabilitation.showedTask.isDontShowBreakingDialog()) {
				Reabilitation.showedTask.pause();
				CustomDialog d1 = new CustomDialog(Reabilitation.reabilitation, "sure_break_task", "break", "cancel", true);
				if (d1.getAnswer() == 1)
					Reabilitation.reabilitation.showHelp();
				else if (Reabilitation.showedTask != null)
					Reabilitation.showedTask.start();
			} else
				Reabilitation.reabilitation.showHelp();

			break;
		case "about":
			if (Reabilitation.showedTask != null && !Reabilitation.showedTask.isDontShowBreakingDialog()) {
				Reabilitation.showedTask.pause();
				CustomDialog d1 = new CustomDialog(Reabilitation.reabilitation, "sure_break_task", "break", "cancel", true);
				if (d1.getAnswer() == 1)
					Reabilitation.reabilitation.showAbout();
				else if (Reabilitation.showedTask != null)
					Reabilitation.showedTask.start();
			} else
				Reabilitation.reabilitation.showAbout();
			break;
		case "tasks":
			if (Reabilitation.popup != null) {
				Reabilitation.popup.hide();
			}
			PopupFactory fac = new PopupFactory();
			Point xy = Reabilitation.tasksIcon.getLocationOnScreen();
			MenuPanel p = new MenuPanel(Reabilitation.popup, Reabilitation.reabilitation);
			Reabilitation.popupMenuPanel = p;
			Reabilitation.popup = fac.getPopup(Reabilitation.tasksIcon, p,
					(int) ((int) xy.getX() - Reabilitation.popupMenuPanel.getPreferredSize().getWidth() + Reabilitation.tasksIcon.getWidth()),
					(int) Math.round(xy.getY() + Reabilitation.tasksIcon.getHeight() + Reabilitation.WINDOW_HEIGHT * 0.02));
			Reabilitation.popup.show();
			break;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		ImageIcon icon;
		switch (l.getName()) {
		case "exit":
			icon = Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_EXIT_ROLLOVER));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "help":
			icon = Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_HELP_ROLLOVER));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "about":
			icon = Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_ABOUT_ROLLOVER));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "tasks":
			icon = Utils.createImageIcon(
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
			icon = Utils
					.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_EXIT));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "help":
			icon = Utils
					.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_HELP));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "about":
			icon = Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.MAIN_MENU_ABOUT));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "tasks":
			icon = Utils.createImageIcon(
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
