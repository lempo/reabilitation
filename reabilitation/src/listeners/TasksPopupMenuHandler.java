package listeners;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.Popup;

import customcomponent.MenuPanel;
import reabilitation.Reabilitation;
import reabilitation.utils.Utils;

public class TasksPopupMenuHandler implements AWTEventListener  {

	@Override
	public void eventDispatched(AWTEvent event) {
		if (Reabilitation.popup == null)
			return;
		if (MouseEvent.MOUSE_CLICKED == event.getID() && event.getSource() != Reabilitation.tasksIcon) {
			Set<Component> components = Utils.getAllComponents(Reabilitation.popupMenuPanel);
			boolean clickInPopup = false;
			for (Component component : components) {
				if (event.getSource() == component) {
					clickInPopup = true;
				}
			}
			if (!clickInPopup) {
				Reabilitation.popup.hide();
			}
		}
	}
}
