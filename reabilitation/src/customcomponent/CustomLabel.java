package customcomponent;

import javax.swing.JLabel;
import javax.swing.JToolTip;

public class CustomLabel extends JLabel {
	private static final long serialVersionUID = 4215871955786310032L;

	public JToolTip createToolTip() {
		JToolTip tip = new CustomToolTip();
		return tip;
	}
}
