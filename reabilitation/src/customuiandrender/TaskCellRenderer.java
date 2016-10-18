package customuiandrender;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TaskCellRenderer extends JPanel implements TableCellRenderer {

	private static final long serialVersionUID = -9179897389004860895L;
	private Color borderColor = new Color(215, 204, 200);

	public TaskCellRenderer() {
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
		String[] parts = s.split("_");
		JLabel l = new JLabel();
		l.setText("<html><div style='font: 16pt Arial Narrow; color: rgb(68, 83, 91); padding: 5px;'>" + parts[0]
				+ "</div></html>");
		JLabel c = new JLabel();
		c.setPreferredSize(new Dimension(10, 10));
		c.setOpaque(true);
		parts = parts[1].split(",");
		c.setBackground(new Color(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
		this.removeAll();
		this.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();

		c1.anchor = GridBagConstraints.CENTER;
		c1.fill = GridBagConstraints.NONE;
		c1.gridheight = 1;
		c1.gridwidth = 1;
		c1.gridx = 0;
		c1.gridy = 0;
		c1.insets = new Insets(0, 0, 0, 0);
		c1.ipadx = 0;
		c1.ipady = 0;
		c1.weightx = 1.0;
		c1.weighty = 0.0;

		this.add(l, c1);

		c1.anchor = GridBagConstraints.CENTER;
		c1.fill = GridBagConstraints.NONE;
		c1.gridheight = 1;
		c1.gridwidth = GridBagConstraints.REMAINDER;
		c1.gridx = 1;
		c1.gridy = 0;
		c1.insets = new Insets(0, 0, 0, 0);
		c1.ipadx = 0;
		c1.ipady = 0;
		c1.weightx = 1.0;
		c1.weighty = 0.0;

		this.add(c, c1);
		return this;
	}
}
