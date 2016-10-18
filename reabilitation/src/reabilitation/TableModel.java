package reabilitation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import defaults.InterfaceTextDefaults;

public class TableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7673278101257259937L;
	String[][] values;
	String[] cols = { InterfaceTextDefaults.getInstance().getDefault("date"),
			InterfaceTextDefaults.getInstance().getDefault("exersize"),
			InterfaceTextDefaults.getInstance().getDefault("task"),
			InterfaceTextDefaults.getInstance().getDefault("result") };

	public TableModel(String[][] values) {
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
		if (cols[c].equals(InterfaceTextDefaults.getInstance().getDefault("date"))) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date result = null;
			try {
				result = df.parse(values[r][c]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return result;
		}
		return values[r][c];
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