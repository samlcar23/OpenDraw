import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/*
 * a component where mouse drawing can happen driven by a Graphics2D structure
 */
public class DrawSpace extends JComponent {

	/*
	 * Graphics2D structure to hold drawn image
	 */
	private Graphics2D graphics2D;

	/*
	 * starting image
	 */
	private Image image;

	/*
	 * values for click and drag movements of the mouse
	 */
	private int clickX, clickY, dragX, dragY;

	/*
	 * scale of our drawing
	 */
	private int scale;
	
	/*
	 * boolean for is the shape filled
	 */
	private boolean isFilled;
	
	/*
	 * current drawing shape
	 */
	private String shape;
	
	/*
	 * return rgb value for color
	 */
	public int[] rgbValue() {
		int[] rgb = {graphics2D.getColor().getRed(), 
				graphics2D.getColor().getGreen(),
				graphics2D.getColor().getBlue()};
		return rgb;
	}
	
	/*
	 * return is filled
	 */
	public boolean getFilled() {
		return isFilled;
	}
	
	/*
	 * return current shape
	 */
	public String getShape() {
		return shape;
	}

	/*
	 * getter for Graphics2D
	 */
	public Graphics2D getGraphics() {
		return graphics2D;
	}

	/*
	 * setter for Graphics2D
	 */
	public void setGraphics(Graphics2D graphicInput) {
		graphics2D = graphicInput;
	}

	/*
	 * getter for mouse clicks
	 */
	public int[] getClicked() {
		return new int[] { clickX, clickY };
	}

	/*
	 * getter for mouse drags
	 */
	public int[] getDrag() {
		return new int[] { dragX, dragY };
	}

	/*
	 * getter for scale of drawing
	 */
	public int getScale() {
		return scale;
	}

	/*
	 * setter for scale of drawing
	 */
	public void setScale(int newScale) {
		scale = newScale;
	}

	public void setImage(Image image) {
		System.out.println("GRAPHICSSSSS\t" + graphics2D);
		System.out.println("IMMMMMAGGGGEEE\t" + image);
		//if (graphics2D != null) {
			graphics2D.drawImage(image, 0, 0, null);
		//}
	}

	/*
	 * draw method inputs: old X coord 'oX', old Y coord 'oY', current X coord 'cX',
	 * current Y coord 'cY', shape, scale of object, and whether or not its filled
	 */
	public void draw(int oX, int oY, int cX, int cY, String shape, int scale, boolean filled) {
		isFilled = filled;
		this.shape = shape;
		this.scale = scale;
		/*
		 * draw a thin pen line
		 */
		if (shape.equals("pen") == true) {
			graphics2D.drawLine(oX, oY, cX, cY);
		} else if (shape.equals("brush") == true) {
			/*
			 * draw a stylized line
			 */
			graphics2D.setFont(new Font("TimesRoman", Font.PLAIN, scale));
			graphics2D.drawString("|", cX, cY);
			graphics2D.drawString("/", cX - 1, cY - 1);
			graphics2D.drawString("\\", cX + 1, cY + 1);
			graphics2D.drawString("/", cX + 2, cY + 2);
			graphics2D.drawString("\\", cX - 2, cY - 2);

		} else if (shape.equals("circle") == true) {
			if (filled == true) {
				/*
				 * draw a filled circle
				 */
				graphics2D.fillOval(cX - scale / 2, cY - scale / 2, scale, scale);
			} else {
				/*
				 * draw a empty circle
				 */
				graphics2D.drawOval(cX - scale / 2, cY - scale / 2, scale, scale);
			}
		} else if (shape.equals("square") == true) {
			if (filled == true) {
				/*
				 * draw a filled square
				 */
				graphics2D.fillRect(cX - scale / 2, cY - scale / 2, scale, scale);
			} else {
				/*
				 * draw a empty square
				 */
				graphics2D.drawRect(cX - scale / 2, cY - scale / 2, scale, scale);
			}
		} else if (shape.equals("triangle") == true) {
			int[] xs = {cX - scale/2, cX + scale/2, cX};
			int[] ys = {cY + scale/2, cY + scale/2, cY - scale/2};
			if (filled == true) {
				/*
				 * draw a filled triangle
				 */
				graphics2D.fillPolygon(xs, ys, 3);
			} else {
				/*
				 * draw a empty triangle
				 */
				graphics2D.drawPolygon(xs, ys, 3);
			}
		} else if (shape.equals("eraser") == true) {
			/*
			 * eraser
			 */
			Color prevColor = graphics2D.getColor();
			graphics2D.setColor(Color.WHITE);
			graphics2D.fillRect(cX - scale / 2, cY - scale / 2, scale, scale);
			graphics2D.setColor(prevColor);
		} else if (shape.equals(null) == false) {
			/*
			 * stamp cursor
			 */
			graphics2D.setFont(new Font("TimesRoman", Font.PLAIN, scale));
			graphics2D.drawString(shape, cX - shape.length() * 2 * (scale/10), cY);
		}
	}

	/*
	 * initial paint components needed by extends JComponent
	 */
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

	/*
	 * update method
	 */
	public void update() {
		repaint();
	}
}
