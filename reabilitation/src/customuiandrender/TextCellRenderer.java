package customuiandrender;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TextCellRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 7501412975433454653L;
	private Color borderColor = new Color(215, 204, 200);
	private Color textColor;
	private int alignment;

	public TextCellRenderer(Color color, int alignment) {
		this.textColor = color;
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
		setText("<html><div style='font: 16pt Arial Narrow; color: rgb(" + textColor.getRed() + ", "
				+ textColor.getGreen() + ", " + textColor.getBlue() + "); padding: 5px;'>" + s + "</div></html>");
		setHorizontalAlignment(alignment);
		return this;
	}
}
