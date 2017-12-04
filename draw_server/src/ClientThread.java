import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ClientThread extends Thread{

	/** DrawThread responsible for updating server drawing canvas */
	private DrawThread drawer;

	/** Control socket for communicating with the client */
        private Socket controlSocket;

	/** Control output stream for sending responses to the client */
        private DataOutputStream outToClient;

	/** Control input stream for receiving commands from the client */
        private BufferedReader inFromClient;

	

	/**
	* Creates a new ClientThread objcet with the given control socket and DrawThread
	*
	* @param controlSocket The socket that control commands will be send over
	* @param drawer The thread that will handle drawing any client updates
	*/
	public ClientThread(Socket controlSocket, DrawThread drawer) {
		this.controlSocket = controlSocket;
		this.drawer = drawer;

		try {
			// Set up control sockets
			outToClient = new DataOutputStream(controlSocket.getOutputStream());
			inFromClient = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
		} catch (Exception e) {
			// Close the connection if communications cannot be established
			quit();
		}
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

}
