import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Server responsible for receiving client connections and maintaining the shared drawing canvas
 *
 * @author Troy Madsen
 */
public class DrawServer {

	/** The drawing canvas of the server */
	private Graphics2D canvas;

	/** Thread responsible for updating canvas */
	private DrawThread drawer;

	//TODO What type of queue should this be?
	/** List of updates for to be drawn on the canvas */
	private LinkedList<String> updates;

	/**
	* Creates a DrawServer object with a drawing canvas of width = 500
	* and height = 500
	*/
	public DrawServer() {
		this(1000, 500);
	}

	/**
	* Creates a DrawServer object with a drawing canvas of the given dimensions and
	* awaits client connections
	*
	* @param width The width of the drawing canvas
	* @param height The height of the drawing canvas
	*/
	public DrawServer(int width, int height) {
		// Create a drawing canvas for the clients to draw on
		canvas = (new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)).createGraphics();

		// Create a LinkedList of updates to write to the drawing canvas
		updates = new LinkedList<>();

		// Create DrawThread to maintain drawing canvas
		drawer = new DrawThread(canvas, updates);
	
		// Socket to receive new connections to the server
		ServerSocket welcomeSocket = null;

		// New socket being opened
		Socket controlSocket = null;

		System.out.println("Awaiting connections\n");
		try {
			welcomeSocket = new ServerSocket(5338);

			// Continually wait for new connections
			while (true) {
				// Accept new conncetion
				controlSocket = welcomeSocket.accept();

				// Create a new thread to handle each client
				ClientThread thread = new ClientThread(controlSocket, drawer);

				// Start the thread
				thread.start();
			}
		} catch (Exception e) {
			System.out.println("Error occurred");
		} finally {
			// Clean up resources
			try {
				if (controlSocket != null) controlSocket.close();
			} catch (Exception e) {
				// Fail quietly
			}

			try {
				if (welcomeSocket != null) welcomeSocket.close();
			} catch (Exception e) {
				// Fail quietly
			}
		}
	}

	/**
	* Entry point of the DrawServer class. Creates a new DrawServer with given parameters
	* or of default size.
	*
	* @param args A list of parameters to set the size of the drawing canvas to in the
	* format DrawServer [WIDTH] [HEIGHT]
	*/
	public static void main(String[] args) {

		// Parse and launch the appropriate DrawServer configuration
		if (args.length == 0) {
			new DrawServer();
		} else {
			try {
				new DrawServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			} catch (Exception e) {
				System.out.println("Incorrect parameters");
			}
		}

		System.out.println("Server shutting down");

	}

}
