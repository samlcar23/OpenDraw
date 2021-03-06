import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Client for server menu used to communicate with server
 * @author Sam Carson
 *
 */
public class Launcher {
	
	private ObjectOutputStream out;
	
	private ObjectInputStream in;
	
	private Socket socket;
	
	private LauncherGui gui;
	
	private String server;
	
	private int port;
	
	private String myIP;
	
	/*
	 * constructor
	 */
	public Launcher(String server, int port, LauncherGui gui) {
		this.server = server;
		this.port = port;
		this.gui = gui;
		
		//get ip of client
		String ip = "";
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));

			ip = in.readLine(); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//set ip of client
		myIP = ip;
		
		
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
	
	/**
	 * returns the ip of the client
	 */
	public String getMyIP() {
		return myIP;
	}
	
	
	/*
	 * disconnect
	 * close input/output streams
	 */
	public void disconnect() {
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
		if(gui != null) {
			gui.connectionFailed();
		}
		
		System.exit(0);
	}

	public void createServer(String ip){

		int port = 5445;

		Server server = new Server();
		server.start();

		Client c = new Client(ip, port);

		//sends serverInfo object to server with type 1 to indicate that it should be removed
		ServerInfo serverToDelete = new ServerInfo(null, null, ip, 1);
		sendServerInfo(serverToDelete);
	}

	public void joinServer(String ip){
		int port = 5445;

		Client c = new Client(ip, port);
	}

	
	/*
	 * run client
	 */
	public static void main(String[] args) {
		int portNum = 5335;
		//aws ip = 13.58.209.10
		String serverAdd = "13.58.209.10";
		LauncherGui gui = new LauncherGui(serverAdd, portNum);
		
		//create client
		Launcher client = new Launcher(serverAdd, portNum, gui);
		
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
					
					System.out.println("Type: " + server.getType());
					
					//checks whether to add or delete a server
					if (server.getType() == 0) {
						//sends serverInfo to the gui to add to the table
						gui.append(server);
					} else {
						//remove serverInfo from gui
						gui.delete(server);
					}
					
					
					
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
