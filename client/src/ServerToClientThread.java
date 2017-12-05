import java.awt.*;
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
	private DataOutputStream dataToClient;

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
			outToClient.flush();
		} catch (Exception e) {
			// Notify client of connection failure
			try {
				outToClient.writeBytes("123 connection failed\n");
				outToClient.flush();
			} catch (Exception ex) {
				// Fail quietly
			}

			// Close the connection if communications cannot be established
			quit();
		}

		// Create and start send timer
		sendTimer = new Timer("SendTimer");
		sendTimer.schedule(new SendImage(), 1000, 1000);
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
		return controlSocket != null && controlSocket.isConnected() && !controlSocket.isClosed() && dataSocket != null && dataSocket.isConnected() && !dataSocket.isClosed();
	}

	/**
	* Operation of this thread
	*/
	public void run() {
		String address = controlSocket.getInetAddress().toString();
		System.out.println(address + " connected");
		System.out.println(isConnected());
		while (isConnected()) {
			try {
				// Wait for a command from the client
				while (!inFromClient.ready());

				// Command
				String command = inFromClient.readLine();

				if (command.startsWith("quit")) {
					quit();
				} else {
					server.addUpdate(command);
				}
			} catch (Exception e) {
				// Fail quietly
			}
		}

		System.out.println(address + " disconnected\n");

	}

	/**
	* This inner class provides the task funcionality needed by the Timer.
	*/
	private class SendImage extends TimerTask {

		/**
		* Creates a SendImage extension of TimerTask to pass to the Timer
		*/
		public SendImage() {
			try {
				while (!inFromClient.ready());
				String ready = inFromClient.readLine();
				System.out.println("RECEIVED\t" + ready);
				// Establish data connection with client
				dataSocket = new Socket(controlSocket.getInetAddress(), 5447);

				System.out.println("SHIT FUCK SHIT FUCK FUCK SHIT FUCK SHIT SHIT FU");
			
				// Set up data stream
				dataToClient = new DataOutputStream(dataSocket.getOutputStream());

				System.out.println("UNCHARTED TERRITORY OF ISIS");
			} catch (Exception e) {
				// Close the connection if data connection cannot be established
				System.out.println("HASHTAG IM OT THIS BIGCH");
				quit();
			}
		}

		public void run() {

			System.out.println("FSJIOOIFJSEFOJSEFOIJFSEOIJSEFIOJSFOIJFSEIOFSEIJFSEIOJSEFIOJFSEJEFSIOJSEFIOJF:");
			// Canvas that the image is loaded from
			BufferedImage comp = server.getComponent();

			// Buffer the image to transform into bytes
			//BufferedImage buff = new BufferedImage(comp.getSize().width, comp.getSize().height, BufferedImage.TYPE_INT_RGB);

			// Draw the current image to the buffered image
			Graphics2D draw = comp.createGraphics();
			draw.drawImage(comp, 0, 0, null);
			draw.dispose();

			// Write the image to the data connection
			try {
				// Load image into buffer
				ImageIO.write(comp, "jpg", dataToClient);

				// Notify client on number of bytes to read
				int size = dataToClient.size();

				// Push all bytes to client
				dataToClient.flush();

				outToClient.writeBytes(size + "\n");

				outToClient.flush();
			} catch (Exception e) {
				// Fail quietly
			}
		}
	}

}
