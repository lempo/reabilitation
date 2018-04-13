package tasks;

import java.awt.Dimension;

import javax.swing.JPanel;

public abstract class AbstractPanel extends JPanel {

	private static final long serialVersionUID = 2605491575677568114L;

	public AbstractPanel(int width, int height) {
		super();
		final Dimension dimension = new Dimension(width, height);
		this.setPreferredSize(dimension);
		this.setMinimumSize(dimension);
		this.setMaximumSize(dimension);
		this.setDoubleBuffered(true);
		this.setOpaque(false);
	}
}
