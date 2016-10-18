package customuiandrender;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import reabilitation.Utills;

public class IconCellRenderer extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = 6892736819545716510L;
	private Color borderColor = new Color(215, 204, 200);
	private int alignment;

	public IconCellRenderer(int alignment) {
		this.alignment = alignment;
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
		String s = (String) value;
		setIcon(Utills.createImageIcon(s));
		setHorizontalAlignment(alignment);
		return this;
	}
}
