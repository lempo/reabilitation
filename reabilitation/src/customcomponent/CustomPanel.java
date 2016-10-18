package customcomponent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class CustomPanel extends JPanel {
	
	Color color;
	
	int x1 = 0;
	int x2 = 0;
	
	public CustomPanel(Color color) {
		this.color = color;
	}
	
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(color);
		g.fillRoundRect(x1, 5, x2 - x1, 100, 10, 10);
		
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}
}
