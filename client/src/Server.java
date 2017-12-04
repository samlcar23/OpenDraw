import java.awt.Container;
import java.awt.image.BufferedImage;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import javax.swing.JComponent;

/**
 * Server responsible for receiving client connections and maintaining the shared drawing canvas
 *
 * @author Troy Madsen
 */
public class Server {

	/** The IP address of the registration server */
	private static String REGISTRY_IP = "13.58.209.10";

	/** The port number of the registration server */
	private static int REGISTRY_PORT = 5335;

	/** The drawing canvas of the server */
	private JComponent component;

	/** Thread responsible for updating canvas */
	private ServerUpdateThread drawer;

	/**
	* Creates a Server object with a drawing canvas of width = 500
	* and height = 500
	*/
	public Server() {
		this(1000, 500);
	}

	/**
	* Creates a Server object with a drawing canvas of the given dimensions and
	* awaits client connections
	*
	* @param width The width of the drawing canvas
	* @param height The height of the drawing canvas
	*/
	public Server(int width, int height) {
		// Create a drawing canvas for the clients to draw on
		component = (JComponent)(new Container());
		component.setSize(width, height);

		// Create ServerUpdateThread to maintain drawing canvas
		drawer = new ServerUpdateThread(this);

		// Start drawer to draw updates to canvas
		drawer.start();
	
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
				ServerToClientThread thread = new ServerToClientThread(controlSocket, this);

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
	* Adds new updates to the ServerUpdateThread to be drawn onto the drawing canvas
	*
	* @param updates A list o new updates to draw on canvas
	*/
	public void addUpdates(LinkedList<String> updates) {
		drawer.addUpdates(updates);
	}

	/**
	* Provides the drawing canvas of the Server
	*/
	public JComponent getComponent() {
		return component;
	}

	/**
	* Sets drawing canvas to the provided one
	*
	* @param component The new drawing canvas to draw
	*/
	public void setComponent(JComponent component) {
		this.component = component;
	}

	/**
	* Entry point of the Server class. Creates a new Server with given parameters
	* or of default size.
	*
	* @param args A list of parameters to set the size of the drawing canvas to in the
	* format Server [WIDTH] [HEIGHT]
	*/
	public static void main(String[] args) {
		// Parse and launch the appropriate Server configuration
		if (args.length == 0) {
			new Server();
		} else {
			try {
				new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			} catch (Exception e) {
				System.out.println("Incorrect parameters");
			}
		}

		System.out.println("Server shutting down");
	}

}
