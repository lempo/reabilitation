package reabilitation;

import javax.swing.table.AbstractTableModel;

import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;

public class UserTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7673278101257259937L;
	String[][] values;
	String[] cols = { InterfaceTextDefaults.getInstance().getDefault("number"),
			InterfaceTextDefaults.getInstance().getDefault("password"),
			InterfaceTextDefaults.getInstance().getDefault("user"),
			InterfaceTextDefaults.getInstance().getDefault("editing"),
			InterfaceTextDefaults.getInstance().getDefault("deleting") };

	public UserTableModel(String[][] values) {
		super();
		this.values = values;
	}

	@Override
	public int getRowCount() {
		return values.length;
	}

	@Override
	public int getColumnCount() {
		return cols.length;
	}

	@Override
	public Object getValueAt(int r, int c) {
		if (cols[c].equals(InterfaceTextDefaults.getInstance().getDefault("number")))
			return Integer.toString(r);
		if (cols[c].equals(InterfaceTextDefaults.getInstance().getDefault("editing")))
			return ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.EDIT);
		if (cols[c].equals(InterfaceTextDefaults.getInstance().getDefault("deleting")))
			return ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.DELETE);
		return values[r][c - 1];
	}

	@Override
	public String getColumnName(int c) {
		return cols[c];
	}

	@Override
	public Class<?> getColumnClass(int col) {

		Class retVal = Object.class;

		if (getRowCount() > 0)
			retVal = getValueAt(0, col).getClass();

		return retVal;
	}
}