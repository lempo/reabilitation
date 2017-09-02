package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import customcomponent.CustomDialog;
import defaults.ImageLinkDefaults;
import reabilitation.Reabilitation;
import reabilitation.Utils;

public class SmallMenuMouseListener implements MouseListener  {
	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		switch (l.getName()) {
		case "about":
			if (Reabilitation.showedTask != null && !Reabilitation.showedTask.isDontShowBreakingDialog()) {
				Reabilitation.showedTask.pause();
				CustomDialog d1 = new CustomDialog(Reabilitation.reabilitation, "sure_break_task", "break", "cancel", true);
				if (d1.getAnswer() == 1)
					Reabilitation.reabilitation.showTaskInfo(Reabilitation.currentTask);
				else if (Reabilitation.showedTask != null)
					Reabilitation.showedTask.start();
			} else
				Reabilitation.reabilitation.showTaskInfo(Reabilitation.currentTask);
			break;
		case "begining":
			if (Reabilitation.showedTask != null && !Reabilitation.showedTask.isDontShowBreakingDialog()) {
				Reabilitation.showedTask.pause();
				CustomDialog d1 = new CustomDialog(Reabilitation.reabilitation, "sure_break_task", "break", "cancel", true);
				if (d1.getAnswer() == 1)
					Reabilitation.reabilitation.showTaskInfo(Reabilitation.currentTask);
				else if (Reabilitation.showedTask != null)
					Reabilitation.showedTask.start();
			} else
				Reabilitation.reabilitation.showTaskInfo(Reabilitation.currentTask);
			break;
		case "end":
			if (Reabilitation.showedTask != null && !Reabilitation.showedTask.isDontShowBreakingDialog()) {
				Reabilitation.showedTask.pause();
				CustomDialog d1 = new CustomDialog(Reabilitation.reabilitation, "sure_break_task", "break", "cancel", true);
				if (d1.getAnswer() == 1)
					Reabilitation.reabilitation.showTasks(Reabilitation.currentTaskGroup);
				else if (Reabilitation.showedTask != null)
					Reabilitation.showedTask.start();
			} else
				Reabilitation.reabilitation.showTasks(Reabilitation.currentTaskGroup);
			break;
		case "results":
			if (Reabilitation.showedTask != null && !Reabilitation.showedTask.isDontShowBreakingDialog()) {
				Reabilitation.showedTask.pause();
				CustomDialog d1 = new CustomDialog(Reabilitation.reabilitation, "sure_break_task", "break", "cancel", true);
				if (d1.getAnswer() == 1) {
					Reabilitation.reabilitation.showResults();
				} else if (Reabilitation.showedTask != null)
					Reabilitation.showedTask.start();
			} else {
				Reabilitation.reabilitation.showResults();
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
			icon = Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_ABOUT_ROLLOVER));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "begining":
			icon = Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_BEGINING_ROLLOVER));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "end":
			icon = Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_END_ROLLOVER));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "results":
			icon = Utils.createImageIcon(
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
			icon = Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_ABOUT));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "begining":
			icon = Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_BEGINING));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "end":
			icon = Utils
					.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.SMALL_MENU_END));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "results":
			icon = Utils.createImageIcon(
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
