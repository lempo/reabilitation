package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import reabilitation.Reabilitation;
import reabilitation.Utils;

public class TasksMouseListener implements MouseListener  {
	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		int i = Integer.parseInt(l.getName());
		Reabilitation.currentTask = i;
		Reabilitation.reabilitation.showTaskInfo(i);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		int i = Integer.parseInt(l.getName());

		ImageIcon icon = Utils.createImageIcon(Reabilitation.tasks[i].getRolloverImage());
		l.setIcon(icon);
		Reabilitation.actualPanel.revalidate();
		Reabilitation.actualPanel.repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		JLabel l = (JLabel) e.getSource();
		int i = Integer.parseInt(l.getName());

		ImageIcon icon = Utils.createImageIcon(Reabilitation.tasks[i].getImage());
		l.setIcon(icon);
		Reabilitation.actualPanel.revalidate();
		Reabilitation.actualPanel.repaint();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
