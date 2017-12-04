import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class ServerToClientThread extends Thread{

	/** Parent of this object */
	private Server server;

	/** Control socket for communicating with the client */
       	private Socket controlSocket;

	/** Data socket to handle image transfer */
	private Socket dataSocket;

	/** Control output stream for sending responses to the client */
        private DataOutputStream outToClient;

	/** Data output stream to send the image */
	private ByteArrayOutputStream dataToClient;

	/** Control input stream for receiving commands from the client */
        private BufferedReader inFromClient;

	/** This Timer continually sends the image to the client */
	private Timer sendTimer;

	/**
	* Creates a new ServerToClientThread object with the given control socket and ServerUpdateThread
	*
	* @param controlSocket The socket that control commands will be send over
	* @param server The parent server of this object
	*/
	public ServerToClientThread(Socket controlSocket, Server server) {
		this.controlSocket = controlSocket;
		this.server = server;

		try {
			// Set up control sockets
			outToClient = new DataOutputStream(controlSocket.getOutputStream());
			inFromClient = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));

			// Notify client of connection
			outToClient.writeBytes("200 command ok\n");
		} catch (Exception e) {
			// Close the connection if communications cannot be established
			quit();
		}

		// Create and start send timer
		sendTimer = new Timer("SendTimer");
		sendTimer.schedule(new SendImage(), 0, 10);
	}

	/**
	* Closes the connection to the client
	*/
	private void quit() {
		// Clean up resources
		try {
			if (controlSocket != null) controlSocket.close();
		} catch (Exception e) {
			controlSocket = null;
		}

		try {
			if (dataSocket != null) dataSocket.close();
		} catch (Exception e) {
			dataSocket = null;
		}
	}

	/**
	* A helper method to determine if the server is connected to a client
	*
	* @return Whether the server is connected to a client
	*/
	private boolean isConnected() {
		return controlSocket != null && controlSocket.isConnected() && !controlSocket.isClosed();
	}

	/**
	* Operation of this thread
	*/
	public void run() {
		String address = controlSocket.getInetAddress().toString();
		System.out.println(address + " connected");

		while (isConnected()) {
			try {
				// Wait for a command from the client
				while (!inFromClient.ready());

				// Command
				String command = inFromClient.readLine();

				if (command.startsWith("quit")) {
					quit();
				} else {
					
				}
			} catch (Exception e) {
				// Fail quietly
			}
		}

		System.out.println(address + "disconnected\n");

	}

	/**
	* This inner class provides the task funcionality needed by the Timer.
	*/
	private class SendImage extends TimerTask {

		/**
		* Creates a SendImage extension of TimerTask to pass to the Timer
		*
		* @param outToClient The control connection this should send byte count to client over
		*/
		public SendImage() {
			try {
				// Establish data connection with client
				dataSocket = new Socket(controlSocket.getInetAddress(), 5340);
			
				// Set up data stream
				dataToClient = (ByteArrayOutputStream)dataSocket.getOutputStream();
			} catch (Exception e) {
				// Close the connection if data connection cannot be established
				quit();
			}
		}

		public void run() {
			// Canvas that the image is loaded from
			JComponent comp = server.getComponent();

			// Buffer the image to transform into bytes
			BufferedImage buff = new BufferedImage(comp.getSize().width, comp.getSize().height, BufferedImage.TYPE_INT_RGB);

			// Draw the current image to the buffered image
			Graphics2D draw = buff.createGraphics();
			draw.drawImage(comp.createImage(comp.getSize().width, comp.getSize().height), 0, 0, null);
			draw.dispose();

			// Write the image to the data connection
			try {
				// Load image into buffer
				ImageIO.write(buff, "jpg", dataToClient);

				// Notify client on number of bytes to read
				outToClient.writeBytes(dataToClient.size() + "\n");

				// Push all bytes to client
				dataToClient.flush();
			} catch (Exception e) {
				// Fail quietly
			}
		}
	}

}
