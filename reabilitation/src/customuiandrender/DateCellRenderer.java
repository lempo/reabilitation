package customuiandrender;

import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class DateCellRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = -6178848692866468257L;

	/**
	 * date format
	 */
	private final DateFormat format;

	private Color borderColor = new Color(215, 204, 200);

	/**
	 * Constructs renderer
	 *
	 * @param format
	 *            date format to use. See
	 *            <code>java.text.SimpleDateFormat</code> for description
	 */
	public DateCellRenderer(String format) {
		this.format = new SimpleDateFormat(format);
		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
		setOpaque(false);
	}

	/**
	 * Returns renderer component
	 *
	 * @param table
	 *            renderable table
	 * @param value
	 *            value to render
	 * @param isSelected
	 *            flag that indicates wether the cell is selected
	 * @param hasFocus
	 *            flag that indicates wether the cell has focus
	 * @param row
	 *            cell's row
	 * @param column
	 *            cell's column
	 * 
	 * @return cell renderer component
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (!(value instanceof Date)) {
			setForeground(Color.red);
			setBackground(Color.white);
			setText("Table element is not a java.util.Date!");
			return this;
		}
		Date date = (Date) value;
		setText("<html><div style='font: 16pt Arial Narrow; color: rgb(161, 136, 127); padding: 5px;'>"
				+ format.format(date) + "</div></html>");

		return this;
	}
}
