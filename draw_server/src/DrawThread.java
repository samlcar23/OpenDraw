import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.LinkedList;
import javax.swing.JComponent;

/**
 * Thread responsible for updating its parent DrawServer's drawing canvas
 *
 * @author Troy Madsen
 */
public class DrawThread extends Thread {

	/** The parent DrawServer of this DrawThread */
	private DrawServer server;

	/** The drawing canvas this maintains */
	private JComponent component;

	/** A wrapper for interacting with the image of the drawing canvas */
	private Graphics2D canvas;

	/** The list of updates this is drawing to the canvas */
	private LinkedList<String> updates;

	/**
	* Creates a new DrawThread with the provided drawing canvas and update list
	*
	* @param server The parent server of this DrawThread
	*/
	public DrawThread(DrawServer server) {
		this.server = server;
		this.component = server.getComponent();

		// Generating the image
		paintComponent();

		this.updates = updates;
	}

	/**
	* Paints the image for the first time
	*/
	private void paintComponent() {
		// Create the drawing canvas
		canvas = (Graphics2D)component.createImage(component.getSize().width, component.getSize().height).getGraphics();

		// Create an empty canvas
		canvas.setPaint(Color.WHITE);
		canvas.fillRect(0, 0, component.getSize().width, component.getSize().height);

		// Canvas features
		canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//TODO is this needed?
		//component.repaint();
	}

	/**
	* Appends all new updates to the drawing canvas to the list of updates to draw
	*
	* @param updates LikedList of new updates to draw to canvas
	*/
	public synchronized void addUpdates(LinkedList<String> updates) {
		this.updates.addAll(updates);
	}

	/**
	* Operation of DrawThread
	*/
	public void run() {
		String update;
		while (true) {
			// Wait for a new update to draw
			while (updates.size() == 0);

			// Get the next update to draw to the canvas
			update = updates.pop();

			// Parse the update for the drawing type and parameters
			String[] params = update.split(" ");

			// Determine which drawing type is being added and draw it
			if (params[0].equals("pen") && params.length == 8) {
				// pen [oX] [oY] [cX] [cY] [Red] [Green] [Blue]
				canvas.setColor(new Color(Integer.parseInt(params[5]), Integer.parseInt(params[6]), Integer.parseInt(params[7])));
				canvas.drawLine(Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]), Integer.parseInt(params[4]));

			} else if (params[0].equals("style") && params.length == 8) {
				// style [/] [cX] [cY] [Scale] [Red] [Green] [Blue]
				canvas.setColor(new Color(Integer.parseInt(params[5]), Integer.parseInt(params[6]), Integer.parseInt(params[7])));
				canvas.setFont(new Font("TimesRoman", Font.PLAIN, Integer.parseInt(params[4])));
				canvas.drawString(params[1], Integer.parseInt(params[2]), Integer.parseInt(params[3]));

			} else if (params[0].equals("circle") && params.length == 8) {
				// circle [cX] [cY] [Scale] [Filled] [Red] [Green] [Blue]
				canvas.setColor(new Color(Integer.parseInt(params[5]), Integer.parseInt(params[6]), Integer.parseInt(params[7])));
				int cX = Integer.parseInt(params[1]);
				int cY = Integer.parseInt(params[2]);
				int scale = Integer.parseInt(params[3]);
				if (params[4].equals("true")) {
					canvas.fillOval(cX - scale / 2, cY - scale / 2, scale, scale);
				} else {
					canvas.drawOval(cX - scale / 2, cY - scale / 2, scale, scale);
				}

			} else if (params[0].equals("square") && params.length == 8) {
				// square [cX] [cY] [Scale] [Filled] [Red] [Green] [Blue]
				canvas.setColor(new Color(Integer.parseInt(params[5]), Integer.parseInt(params[6]), Integer.parseInt(params[7])));
				int cX = Integer.parseInt(params[1]);
				int cY = Integer.parseInt(params[2]);
				int scale = Integer.parseInt(params[3]);
				if (params[4].equals("true")) {
					canvas.fillRect(cX - scale / 2, cY - scale / 2, scale, scale);
				} else {
					canvas.drawRect(cX - scale / 2, cY - scale / 2, scale, scale);
				}

			} else if (params[0].equals("triangle") && params.length == 8) {
				// triangle [cX] [cY] [Scale] [Filled] [Red] [Green] [Blue]
				canvas.setColor(new Color(Integer.parseInt(params[5]), Integer.parseInt(params[6]), Integer.parseInt(params[7])));
				int cX = Integer.parseInt(params[1]);
				int cY = Integer.parseInt(params[2]);
				int scale = Integer.parseInt(params[3]);
				int[] xs = {cX - scale / 2, cX + scale / 2, cX};
				int[] ys = {cY + scale / 2, cY + scale / 2, cY - scale / 2};
				if (params[4].equals("true")) {
					canvas.fillPolygon(xs, ys, 3);
				} else {
					canvas.drawPolygon(xs, ys, 3);
				}

			} else if (params[0].equals("eraser") && params.length == 4) {
				// eraser [cX] [cY] [Scale]
				int cX = Integer.parseInt(params[1]);
				int cY = Integer.parseInt(params[2]);
				int scale = Integer.parseInt(params[3]);
				canvas.setColor(Color.WHITE);
				canvas.fillRect(cX - scale / 2, cY - scale / 2, scale, scale);

			} else if (params[0].equals("stamp") && params.length == 8) {
				// stamp [Word] [cX] [cY] [Scale] [Red] [Green] [Blue]
				canvas.setColor(new Color(Integer.parseInt(params[5]), Integer.parseInt(params[6]), Integer.parseInt(params[7])));
				canvas.setFont(new Font("TimesRoman", Font.PLAIN, Integer.parseInt(params[4])));
				canvas.drawString(params[1], Integer.parseInt(params[2]), Integer.parseInt(params[3]));

			} else if (params[0].equals("resize") && params.length == 3) {
				// resize [Width] [Height]
				//TODO This needs to be added
				//Create a new drawing canvas and set its size
				JComponent newComp = (JComponent)(new Container());
				newComp.setSize(Integer.parseInt(params[1]), Integer.parseInt(params[2]));

				// Draw the old canvas on the new canvas
				newComp.createImage(newComp.getSize().width, newComp.getSize().height).getGraphics().drawImage(component.createImage(component.getSize().width, component.getSize().height), 0, 0, Color.WHITE, null);

				// Set the new canvas
				server.setComponent(newComp);

				// Set the drawing canvas this is operating on
				//TODO component = server.getComponent();
				component = newComp;

				// Generating the image
				paintComponent();

			} else if (params[0].equals("clear") && params.length == 1) {
				// clear
				canvas.setPaint(Color.WHITE);
				canvas.fillRect(0, 0, component.getSize().width, component.getSize().height);

			}
		}
	}

}
