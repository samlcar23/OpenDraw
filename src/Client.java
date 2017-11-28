import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Client for server menu used to communicate with server
 * @author Sam Carson
 *
 */
public class Client {
	
	private ObjectOutputStream out;
	
	private ObjectInputStream in;
	
	private Socket socket;
	
	private ServerListGUI gui;
	
	private String server;
	
	private int port;
	
	/*
	 * constructor
	 */
	public Client(String server, int port, ServerListGUI gui) {
		this.server = server;
		this.port = port;
		this.gui = gui;
	}
	

	/*
	 * start
	 */
	public boolean start() {
		
		//try to connect to server
		try {
			socket = new Socket(server, port);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		//print to console that it is connected
		System.out.println("Connection accepted " 
				+ socket.getInetAddress() + ":" + socket.getPort());
		
		//try to create the object streams
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException eIO) {
			eIO.printStackTrace();
			return false;
		}
		
		//create the Thread to listen from server
		new ListenFromServer().start();
		
		//client start was successful
		return true;
		
	}
	
	
	/*
	 * send serverInfo to server
	 */
	public void sendServerInfo(ServerInfo server) {
		try {
			out.writeObject(server);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * disconnect
	 * close input/output streams
	 */
	private void disconnect() {
		try { 
			if(in!= null) in.close();
		}
		catch(Exception e) {}
		try {
			if(out != null) out.close();
		}
		catch(Exception e) {}
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {}
		
		// inform the GUI
		if(gui != null)
			gui.connectionFailed();
	}
	
	/*
	 * run client
	 */
	public static void main(String[] args) {
		int portNum = 5335;
		String serverAdd = "localhost";
		ServerListGUI gui = new ServerListGUI(serverAdd, portNum);
		
		//create client
		Client client = new Client(serverAdd, portNum, gui);
		
		gui.start(client);
		
		if(!client.start()) {
			return;
		}
	}
	
	/*
	 * a class that waits for the serverInfo object from the server and then adds
	 * it to the table in the gui.
	 */
	class ListenFromServer extends Thread {

		public void run() {
			
			//waits for serverInfo objects
			while(true) {
				try {
					ServerInfo server = (ServerInfo) in.readObject();
					//sends serverInfo to the gui to add to the table
					gui.append(server);
				}
				catch(IOException e) {
					System.out.println("Server has closed the connection: " + e);
					if(gui != null) 
						gui.connectionFailed();
					break;
				}
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
	
	
	
	
	
	
}
