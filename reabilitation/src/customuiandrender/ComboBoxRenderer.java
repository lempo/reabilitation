package customuiandrender;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import customcomponent.CustomComboBoxLabel;

public class ComboBoxRenderer extends CustomComboBoxLabel implements ListCellRenderer {

	private static final long serialVersionUID = -3344161098705324609L;

	public ComboBoxRenderer() {
		setOpaque(false);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		
		setText("<html><div style='font: 14pt Arial Narrow; color: rgb(68, 83, 91); padding: 3px;'>"
				+ value.toString() + "</div></html>");
		// check if this cell represents the current DnD drop location
		JList.DropLocation dropLocation = list.getDropLocation();
		if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) {
			setSelected(true);
			// check if this cell is selected
		} else if (isSelected) {
			setSelected(true);
			setText("<html><div style='font: 14pt Arial Narrow; color: white; padding: 3px;'>"
					+ value.toString() + "</div></html>");
			// unselected, and not the DnD drop location
		} else {
			setSelected(false);
		}
		if (((getLocation().x == 0) && (getLocation().y == 0)) || (index == -1)) {
			setText("<html><div style='font: 14pt Arial Narrow; color: rgb(68, 83, 91); padding: 3px;'>"
					+ value.toString() + "</div></html>");
			setSelected(false);
			setSel(true);
		} else
			setSel(false);

		return this;
	}
}