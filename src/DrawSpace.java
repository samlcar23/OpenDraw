import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JComponent;

public class DrawSpace extends JComponent {

	private Graphics2D graphics2D;

	private Image image;

	private int clickX, clickY, dragX, dragY;
	
	private int scale;

	public Graphics2D getGraphics() {
		return graphics2D;
	}

	public void setGraphics(Graphics2D graphicInput) {
		graphics2D = graphicInput;
	}

	public int[] getClicked() {
		return new int[] { clickX, clickY };
	}

	public int[] getDrag() {
		return new int[] { dragX, dragY };
	}
	
	public int getScale() {
		return scale;
	}
	
	public void setScale(int newScale) {
		scale = newScale;
	}
	
	public void draw(int oX, int oY, int cX, int cY, String shape, int scale, boolean filled) {
		if (shape.equals("line") == true) {
			graphics2D.drawLine(oX, oY, cX, cY);
		} else if (shape.equals("circle") == true) {
			if (filled == true) {
				graphics2D.fillOval(cX - scale/2, cY - scale/2, scale, scale);
			} else {
				graphics2D.drawOval(cX - scale/2, cY - scale/2, scale, scale);
			}
		} else if (shape.equals("square") == true) {
			if (filled == true) {
				graphics2D.fillRect(cX - scale/2, cY - scale/2, scale, scale);
			} else {
				graphics2D.drawRect(cX - scale/2, cY - scale/2, scale, scale);
			}
		} else if (shape.equals(null) == false) {
			graphics2D.setFont(new Font("TimesRoman", Font.PLAIN, scale));
			graphics2D.drawString(shape, cX - shape.length() * 2, cY);
		}
	}

	public void paintComponent(Graphics g) {
		if (image == null) {
			image = createImage(this.getSize().width, this.getSize().height);
			graphics2D = (Graphics2D) image.getGraphics();

			graphics2D.setPaint(Color.white);
			graphics2D.fillRect(0, 0, getSize().width, this.getSize().height);

			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			graphics2D.setPaint(Color.black);

			update();
		}

		g.drawImage(image, 0, 0, null);
	}

	public void update() {
		repaint();
	}
}
