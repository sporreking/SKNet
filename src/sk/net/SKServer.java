package sk.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * This class is used for hosting a server with SKNet.
 * 
 * @author Alfred Sporre
 *
 */
public class SKServer {
	
	private HashMap<Integer, SKConnection> connections;
	
	private ServerSocket serverSocket;
	
	private SKConnectionListener connectionListener;
	private Thread connectionListenerThread;
	
	private ArrayList<SKPacketListener> packetListeners;
	
	private volatile boolean running = false;
	
	private int timeout = 10000;
	private int maxConnections = 1000;
	
	/**
	 * 
	 * Constructs a new server.
	 * 
	 */
	public SKServer() {
		packetListeners = new ArrayList<>();
		connections = new HashMap<>();
	}
	
	/**
	 * 
	 * Starts all the underlying server threads.
	 * 
	 * @return this server instance
	 * @throws IOException if the server socket could not be opened
	 * @throws IllegalStateException if there are no packet listeners associated with this server
	 */
	public SKServer start() throws IOException {
		
		if(packetListeners.size() < 1)
			throw new IllegalStateException("There are no packet listeners associated with this server");
		
		serverSocket = new ServerSocket();
		
		serverSocket.setSoTimeout(timeout);
		
		running = true;
		
		connectionListener = new SKConnectionListener(this);
		connectionListenerThread = new Thread(connectionListener);
		connectionListenerThread.start();
		
		return this;
	}
	
	/**
	 * 
	 * Adds a client connection to this server and performs the initialization procedure.
	 * 
	 * @param socket the socket connection of the client
	 * @return this server instance
	 */
	public synchronized SKServer addConnection(Socket socket) {
		
		SKConnection connection = new SKConnection(SKNet.SK_SERVER, socket).setServer(this);
		
		int id = 0;
		while(id < maxConnections || maxConnections == 0) {
			
			if(connections.get(id) == null) {
				connection.init(new SKServerPacket(id));
				break;
			}
			
			id++;
		}
		
		connections.put(id, connection);
		
		for(SKPacketListener packetListener : packetListeners) {
			packetListener.connected(connection);
		}
		
		connection.startListening();
		
		return this;
	}
	
	/**
	 * 
	 * Removes the connection with the specified id from this server's client list.
	 * 
	 * @param id the id 
	 * @return false if there is no connection associated with the provided id
	 */
	protected boolean removeConnection(int id) {
		return connections.remove(id) != null;
	}
	
	/**
	 * 
	 * Sends a packet to the specified client.
	 * 
	 * @param id the id of the receiving client
	 * @param packet the packet to send
	 * @return true if the packet was sent successfully
	 */
	public boolean send(int id, Object packet) {
		try {
			connections.get(id).sendPacket(packet);
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * 
	 * Sends a packet to all clients on this server.
	 * 
	 * @param packet the packet to send
	 * @return the IDs of the connections that generated errors
	 */
	public int[] sendToAll(Object packet) {
		ArrayList<Integer> errors = new ArrayList<>();
		
		for(SKConnection connection : connections.values()) {
			try {
				connection.sendPacket(packet);
			} catch (IOException e) {
				errors.add(connection.getID());
			}
		}
		
		int[] errorStack = new int[errors.size()];
		
		for(int i = 0; i < errorStack.length; i++)
			errorStack[i] = errors.get(i);
		
		return errorStack;
	}
	
	/**
	 * 
	 * Sends a packet to all clients on this server except for the ones with the specified IDs. 
	 * 
	 * @param packet the packet to send
	 * @param ids the IDs of the clients to exclude
	 * @return the IDs of the connections that generated errors
	 */
	public int[] sendToAllExclude(Object packet, int... ids) {
		ArrayList<Integer> errors = new ArrayList<>();
		
		for(SKConnection connection : connections.values()) {
			boolean exclude = false;
			
			for(int i : ids) {
				if(connection.getID() == i) {
					exclude = true;
					break;
				}
			}
			
			if(exclude)
				continue;
			
			try {
				connection.sendPacket(packet);
			} catch (IOException e) {
				errors.add(connection.getID());
			}
		}
		
		int[] errorStack = new int[errors.size()];
		
		for(int i = 0; i < errorStack.length; i++)
			errorStack[i] = errors.get(i);
		
		return errorStack;
	}
	
	/**
	 * 
	 * Disconnects the client with the specified id.
	 * 
	 * @param clientID the id of the client to disconnect
	 * @param msg the disconnection message to send
	 */
	public void disconnect(int clientID, String msg) {
		try {
			connections.get(clientID).sendPacket(new SKDisconnectPacket(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		close(clientID, true, msg);
	}
	
	/**
	 * 
	 * Closes the socket of the specified client.
	 * 
	 * @param clientID the id of the client whose connection to close
	 * @param local true if the disconnection was fired locally
	 * @param msg the disconnection message
	 * @return this server instance
	 */
	protected SKServer close(int clientID, boolean local, String msg) {
		
		SKConnection connection = connections.get(clientID);
		
		for(SKPacketListener packetListener : packetListeners) {
			packetListener.disconnected(connection, local, msg);
		}
		
		connection.close();
		
		connections.remove(connection.getID());
		
		return this;
	}
	
	/**
	 * 
	 * Adds a packet listener to this server.
	 * 
	 * @param packetListener the packet listener to add
	 * @return this server instance
	 */
	public SKServer addPacketListener(SKPacketListener packetListener) {
		packetListeners.add(packetListener);
		return this;
	}
	
	/**
	 * 
	 * Binds the server socket to the specified host name and port.
	 * 
	 * @param hostname the host name of the server
	 * @param port the port of the server
	 * @return this server instance
	 * @throws IOException if the specified host name or port could not be bound
	 */
	public SKServer bind(String hostname, int port) throws IOException {
		return bind(new InetSocketAddress(hostname, port));
	}
	
	/**
	 * 
	 * Binds the server socket to the specified {@link InetSocketAddress}.
	 * 
	 * @param address the {@link InetSocketAddress} to use
	 * @return this server instance
	 * @throws IOException if the specified {@link InetSocketAddress} could not be bound
	 */
	public SKServer bind(InetSocketAddress address) throws IOException {
		serverSocket.bind(address);
		return this;
	}
	
	
	/**
	 * 
	 * Returns the connection associated with the specified id.
	 * If there is no connection with this id the method will return null.
	 * 
	 * @param id the id of the connection
	 * @return the connection associated with the specified it.
	 */
	public SKConnection getConnection(int id) {
		return connections.get(id);
	}
	
	/**
	 * 
	 * Returns the {@link ServerSocket} of this server.
	 * 
	 * @return the {@link ServerSocket} of this server
	 */
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	/**
	 * 
	 * Returns the host name of this server.
	 * 
	 * @return the host name of this server
	 */
	public String getHostName() {
		return serverSocket.getInetAddress().getHostName();
	}
	
	
	/**
	 * 
	 * Returns the host address of this server.
	 * 
	 * @return the host address of this server
	 */
	public String getHostAddress() {
		return serverSocket.getInetAddress().getHostAddress();
	}
	
	
	/**
	 * 
	 * Returns the {@link InetAddress} of this server.
	 * 
	 * @return the {@link InetAddress} of this server
	 */
	public InetAddress getInetAddress() {
		return serverSocket.getInetAddress();
	}
	
	
	/**
	 * 
	 * Sets the number of maximum client connections allowed.
	 * 
	 * @param maxConnections the number of client connections to accept. Set to 0 for an unlimited amount of connections
	 * @return this server instance
	 */
	public SKServer setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
		return this;
	}
	
	
	/**
	 * 
	 * Returns the maximum number of client connections allowed.
	 * 
	 * @return the maximum number of client connections to accept
	 */
	public int getMaxConnections() {
		return maxConnections;
	}
	
	
	/**
	 * 
	 * Returns the local port of this server.
	 * 
	 * @return the local port of this server
	 */
	public int getLocalPort() {
		return serverSocket.getLocalPort();
	}
	
	
	/**
	 * 
	 * Returns the SO-timeout for this server's {@link ServerSocket}.
	 * 
	 * @return the SO-timeout for this server's {@link ServerSocket}.
	 */
	public int getTimeout() {
		return timeout;
	}
	
	
	/**
	 * 
	 * Sets the SO-timeout for this server's {@link ServerSocket}.
	 * 
	 * @param ms the timeout specified in milliseconds
	 * @return this server instance
	 */
	public SKServer setTimeout(int ms) {
		timeout = ms;
		return this;
	}
	
	
	/**
	 * 
	 * Returns a list containing all packet listeners associated with this server.
	 * 
	 * @return a list of all packet listeners
	 */
	public ArrayList<SKPacketListener> getPacketListeners() {
		return packetListeners;
	}
	
	/**
	 * Stops this server.
	 * <p>
	 * Disconnects all clients and closes the server socket.
	 * 
	 * @param msg the disconnect message
	 */
	public void stop(String msg) {
		for(SKConnection c : connections.values())
			disconnect(c.getID(), msg);
		connections.clear();
		running = false;
	}
	
	/**
	 * 
	 * Returns whether or not this server is running.
	 * 
	 * @return true if the server is running
	 */
	public boolean isRunning() {
		return running;
	}
}