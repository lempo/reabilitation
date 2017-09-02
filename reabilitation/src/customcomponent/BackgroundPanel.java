package customcomponent;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Panel which displays background image.
 * 
 * @author Pokrovskaya Oksana
 *
 */
public class BackgroundPanel extends JPanel {
	private static final long serialVersionUID = -2043137411007649138L;
	private final String image;
	private final int width;
	private final int height;

	public BackgroundPanel(String backgroundPath, int width, int height) {
		image = backgroundPath;
		this.width = width;
		this.height = height;
	}

	public void paintComponent(Graphics g) {
		Image im = null;
		try {
			im = ImageIO.read(getClass().getResource(image));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("problem reading file " + image);
		}
		g.drawImage(im, 0, 0, width, height, this);
	}
}
