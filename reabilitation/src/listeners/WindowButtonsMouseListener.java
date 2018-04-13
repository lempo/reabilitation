package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import customcomponent.CustomDialog;
import defaults.ImageLinkDefaults;
import reabilitation.Reabilitation;
import reabilitation.utils.Utils;

public class WindowButtonsMouseListener implements MouseListener  {
	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		switch (l.getName()) {
		case "close":
			if (Reabilitation.showedTask != null)
				Reabilitation.showedTask.pause();
			if (!Reabilitation.currentMethod.equals("showFirstScreen")) {
				CustomDialog d = new CustomDialog(Reabilitation.reabilitation, "sure_exit", "exit", "cancel", true);
				if (d.getAnswer() == 1)
					System.exit(0);
				else if (Reabilitation.showedTask != null)
					Reabilitation.showedTask.start();
			} else
				System.exit(0);
			break;
		case "restore":
			Reabilitation.reabilitation.resize();
			break;
		case "hide":
			Reabilitation.reabilitation.setState(JFrame.ICONIFIED);
			break;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		ImageIcon icon;
		switch (l.getName()) {
		case "close":
			icon = Utils
					.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.CLOSE_ROLLOVER));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "restore":
			icon = Utils.createImageIcon(
					ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.RESTORE_ROLLOVER));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "hide":
			icon = Utils
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
			icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.CLOSE));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "restore":
			icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.RESTORE));
			l.setIcon(icon);
			l.updateUI();
			break;
		case "hide":
			icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.HIDE));
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
