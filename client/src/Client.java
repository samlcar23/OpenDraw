import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

/**
 * Handles the server communication and creates a drawing space
 *
 * @author Troy Madsen
 */
public class Client extends Thread implements Observer {

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
			System.out.println("WAITING TO BE READY");

			while (!inFromServer.ready());

			System.out.println("DYNOMTITESEFJIOS");

			// Exit if the server has no file to be transferred
			if (!inFromServer.readLine().equals("200 command ok")) {
				running = false;
				return false;
			}

			System.out.println("WAITIN ON DAT SOCKET DOE");

			// Create data connection
			ServerSocket welcomeSocket = new ServerSocket(port + 2);
			outToServer.writeBytes("ready\n");
			outToServer.flush();
			dataSocket = welcomeSocket.accept();

			System.out.println("AIJFOJOI");

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
		try {
			if (arg instanceof String) {
				if (((String) arg).equals("clear")) {
					outToServer.writeBytes("clear\n");
				}
			}else if (arg instanceof DrawSpace) {
				DrawSpace drawSpace = (DrawSpace) arg;
				String shape = drawSpace.getShape();
				int oX = drawSpace.getClicked()[0];
				int oY = drawSpace.getClicked()[1];
				int cX = drawSpace.getDrag()[0];
				int cY = drawSpace.getDrag()[1];;
				int scale = drawSpace.getScale();
				boolean filled = drawSpace.getFilled();
				int red = drawSpace.rgbValue()[0];
				int green = drawSpace.rgbValue()[1];
				int blue = drawSpace.rgbValue()[2];
				if (shape.equals("pen")) {
					outToServer.writeBytes("pen " + oX + " " + oY + " " + cX + " " + cY + " " + red + " " + green + " " + blue + "\n");
				}else if (shape.equals("brush")) {
					outToServer.writeBytes("brush / " + oX + " " + oY + " " + scale + " " + red + " " + green + " " + blue + "\n");
				}else if (shape.equals("circle") || shape.equals("square") || shape.equals("triangle")) {
					outToServer.writeBytes(shape + " " + oX + " " + oY + " " + scale + " " + filled + " " + red + " " + green + " " + blue + "\n");
				}else if (shape.equals("eraser")) {
					outToServer.writeBytes(shape + " " + oX + " " + oY + " " + scale + "\n");
				}else {
					outToServer.writeBytes("stamp " + shape + " " + oX + " " + oY + " " + scale + " " + red + " " + green + " " + blue + "\n");
				}
			}
			outToServer.flush();
		}catch (IOException e) {

		}
	}

	@Override
	public void run() {

		running = true;

		// Continually update the image
		while (running) {
			System.out.println("FUCK");
			try {
				while (!inFromServer.ready());

				System.out.println("ready or not");

				int sizeMatters = Integer.parseInt(inFromServer.readLine());

				System.out.println("size matters\t" + sizeMatters);

				byte[] bytes = new byte[sizeMatters];
				dataFromServer.read(bytes, 0, sizeMatters);

				System.out.println("image");

				ImageIcon imageIcon = new ImageIcon(bytes);
				Image image = imageIcon.getImage();

				System.out.println(image);
				gui.setImage(image);

			}catch (IOException e) {

			}
		}
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
		System.out.println("BOO TANG");
		System.out.println(isConnected());

		// Create and subscribe to gui
		gui = new Whiteboard();
		gui.addObserver(this);
	}

}
