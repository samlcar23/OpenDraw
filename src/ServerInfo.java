

import java.io.Serializable;

public class ServerInfo implements Serializable {
	
	
	/**
	 * Serializable id
	 */
	private static final long serialVersionUID = -7951921108943135754L;
	/**
	 * server name
	 */
	private String name;
	/**
	 * returns the name of the server
	 */
	public String getName() { return name;}
	/**
	 * server password
	 */
	private String password;
	/**
	 * returns the password of the server
	 */
	public String getPassword() { return password;}
	/**
	 * IP of the server
	 */
	private String IP;
	/**
	 * returns the IP of the server
	 */
	public String getIP() { return IP;}
	
	/**
	 * constructor 
	 * @param serverName
	 * @param serverPass
	 * @param IPAddress
	 */
	public ServerInfo(String serverName, String serverPass, String IPAddress) {
		name = serverName;
		password = serverPass;
		IP = IPAddress;
	}
	
	/**
	 * returns the ServerInfo object
	 */
	public ServerInfo getServerInfo() {
		return this;
	}
}
