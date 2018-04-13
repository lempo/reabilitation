package customuiandrender;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.TableCellRenderer;

import defaults.ImageLinkDefaults;
import reabilitation.utils.Utils;

public class TableHeaderRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = -5990074622330262146L;
	private Color borderColor = new Color(215, 204, 200);

	public TableHeaderRenderer() {
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
		if (column < table.getColumnCount() - 1)
			setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, borderColor));
		setOpaque(true);
		setBackground(new Color(161, 136, 127));
		setText("<html><div style='font: 16pt Arial Narrow; color: white; padding: 5px;'>" + s + "</div></html>");
		setIcon(Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.HEADER_ARROW)));

		ImageIcon sortIcon = null;
		java.util.List<? extends RowSorter.SortKey> sortKeys = table.getRowSorter().getSortKeys();
		if (sortKeys.size() > 0 && sortKeys.get(0).getColumn() == table.convertColumnIndexToModel(column)) {
			switch (sortKeys.get(0).getSortOrder()) {
			case ASCENDING:
				sortIcon = Utils.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.HEADER_ARROW_SORT));
				break;
			case DESCENDING:
				sortIcon = Utils.createImageIcon(
						ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.HEADER_ARROW_SORT));
				break;
			case UNSORTED:
				sortIcon = Utils
						.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.HEADER_ARROW));
				break;
			}
			setIcon(sortIcon);
		}
		setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		setHorizontalAlignment(JLabel.CENTER);
		return this;
	}
}
