import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

/**
 * Handles the server communication and creates a drawing space
 *
 * @author Troy Madsen
 */
public class Client implements Observer {

	/** The gui of the Client */
	private Whiteboard gui;

	/** Control socket for communicating with the server */
	private Socket controlSocket;

	/** Control output stream for sending commands to the server  */
	private DataOutputStream outToServer;

	/** Control input stream for receiving responses from the server */
	private BufferedReader inFromServer;

	/** The socket for data */
	private Socket dataSocket;

	/** Data in */
	private InputStream dataFromServer;

	/** Flag to keep the client running */
	private boolean running;

	/**
	* Notifies the server of the disconnect and terminates the server connection
	*
	* @return Whether the disconnect was successful
	*/
	private boolean quit() {
		// No server to disconnect from
		if (!isConnected()) return false;

		try {
			outToServer.writeBytes("quit\n");
		} catch (Exception e) {
			// Fail quietly
		} finally {
			// Free resources

			try {
				if (controlSocket != null) controlSocket.close();
			} catch (Exception ex) {
				// Fail quietly
			}

			controlSocket = null;
		}

		return true;
	}

	/**
	* Connects to the server at the specified ip and port
	*
	* @param host String representation of the server address to connect to
	* @param port The port number to connect to on the server
	* @return Whether connect was successful
	*/
	private boolean connect(String host, int port) {
		// Disconnect from current server before connecting to another
		quit();

		try {
			// Set up control socket
			controlSocket = new Socket(host, port);
			outToServer = new DataOutputStream(controlSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));

			// Set up data socket
			// Wait for the 
			while (!inFromServer.ready());

			// Exit if the server has no file to be transferred
			if (!inFromServer.readLine().equals("200 command ok")) running = false;

			// Create data connection
			dataSocket = new Socket(host, port + 2);

			// Set up data stream
			dataFromServer = dataSocket.getInputStream();
		} catch (Exception e) {
			// Ensure no connection remains
			quit();
		}

		return isConnected();
	}

	/**
 	* A helper method to determine if the client is connected to a server
 	*
 	* @return Whether the client is connected to a server
 	*/
	private boolean isConnected() {
		return controlSocket != null && controlSocket.isConnected() && !controlSocket.isClosed();
	}

	/**
	* The implementation of the Observer class
	*
	* @param o Observable object
	* @param arg Changes made
	*/
	public void update(Observable o, Object arg) {
		
	}

	/**
	* Creates a new Client object connected to the server at
	* the specified ip and port
	*
	* @param ip The ip address of the host server
	* @param port The port number of the host server
	*/
	public Client(String ip, int port) {
		// Connect to the provided server
		connect(ip, port);

		// Create and subscribe to gui
		gui = new Whiteboard();
		gui.addObserver(this);

		// Continually update the image
		while (running) {
			
		}
	}

}
