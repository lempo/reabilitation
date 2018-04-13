package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import reabilitation.Reabilitation;
import reabilitation.utils.Utils;

public class GroupsMouseListener implements MouseListener  {
	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		int i = Integer.parseInt(l.getName());
		Reabilitation.currentTaskGroup = i;
		Reabilitation.reabilitation.showTasks(i);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		int i = Integer.parseInt(l.getName());
		ImageIcon icon = Utils.createImageIcon(Reabilitation.taskGroups[i].getRolloverImage());
		l.setIcon(icon);
		l.updateUI();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		int i = Integer.parseInt(l.getName());
		ImageIcon icon = Utils.createImageIcon(Reabilitation.taskGroups[i].getImage());
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
