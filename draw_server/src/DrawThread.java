import java.awt.Graphics2D;
import java.util.LinkedList;

/**
 * Thread responsible for updating its parent DrawServer's drawing canvas
 *
 * @author Troy Madsen
 */
public class DrawThread extends Thread {

	/**
	* Creates a new DrawThread with the provided drawing canvas and update list
	*
	* @param canvas The drawing canvas that updates are drawn on
	* @param updates The list of new updates to be drawn on the drawing canvas
	*/
	public DrawThread(Graphics2D canvas, LinkedList updates) {
		
	}

	/**
	* Operation of DrawThread
	*/
	public void run() {

	}

}
