package listeners;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import reabilitation.Reabilitation;

public class WindowDraggingListener implements MouseMotionListener {
	@Override
	public void mouseDragged(MouseEvent e) {

		Point currentDragPosition = e.getLocationOnScreen();
		int deltaX = currentDragPosition.x - Reabilitation.lastDragPosition.x;
		int deltaY = currentDragPosition.y - Reabilitation.lastDragPosition.y;
		if (deltaX != 0 || deltaY != 0) {
			int x = Reabilitation.reabilitation.getLocation().x + deltaX;
			int y = Reabilitation.reabilitation.getLocation().y + deltaY;
			Reabilitation.reabilitation.setLocation(x, y);
			Reabilitation.lastDragPosition = currentDragPosition;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
}
