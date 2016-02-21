package sk.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SKClient {
	
	private SKConnection connection;
	
	private ArrayList<SKPacketListener> packetListeners;
	
	public SKClient() {
		packetListeners = new ArrayList<>();
	}
	
	/**
	 * 
	 * Connects to a server with the specified address and port.
	 * 
	 * The communication listener thread is also started.
	 * 
	 * @param address the host address to connect to
	 * @param port the active server port of the host
	 * @return this client instance
	 * @throws UnknownHostException if the IP address of the host could not be determined
	 * @throws IOException if an I/O error occurs when creating the socket
	 * @throws IllegalArgumentException if the port parameter is outside the specified range of valid port values, which is between 0 and 65535, inclusive
	 * @throws IllegalStateException if there are no packet listeners associated with this client
	 */
	public SKClient connect(String address, int port) throws UnknownHostException, IOException {
		
		if(packetListeners.size() < 1)
			throw new IllegalStateException("There are no packet listeners associated with this client");
		
		Socket socket = new Socket(address, port);
		
		connection = new SKConnection(SKNet.SK_CLIENT, socket).setClient(this);
		
		connection.init(new SKClientPacket());
		
		for(SKPacketListener packetListener : packetListeners) {
			packetListener.connected(connection);
		}
		
		connection.startListening();
		
		return this;
	}
	
	/**
	 * 
	 * Disconnects this client from the server host.
	 * <p>
	 * Sends a message to the server host indicating the disconnection.
	 * Then, calls {@link #close(boolean, String)}.
	 * 
	 * @param msg the message to send to the server host
	 * @return this client instance
	 */
	public SKClient disconnect(String msg) {
		
		try {
			if(!connection.isSocketClosed())
				connection.sendPacket(new SKDisconnectPacket(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(!connection.isSocketClosed())
			close(true, msg);
		
		return this;
	}
	
	/**
	 * 
	 * Sends a packet to the server host. Returns true if the packet was successfully delivered.
	 * 
	 * @param packet the packet to send
	 * @return true if the packet was delivered successfully
	 */
	public boolean send(Object packet) {
		try {
			
			connection.sendPacket(packet);
			
			return true;
			
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * 
	 * Closes the connection to the server host.
	 * <p>
	 * Calls {@link SKPacketListener#disconnected(SKConnection, boolean, String)}
	 * for each active packet listener of this client.
	 * Then, closes the connection by calling {@link SKConnection#close()}.
	 * 
	 * @param local true if the the disconnection was fired locally
	 * @param msg the message associated with this disconnection
	 * @return this client instance
	 */
	protected SKClient close(boolean local, String msg) {
		
		for(SKPacketListener packetListener : packetListeners) {
			packetListener.disconnected(connection, local, msg);
		}
		
		connection.close();
		
		return this;
	}
	
	
	/**
	 * 
	 * Adds a packet listener to this client.
	 * 
	 * @param packetListener the packet listener to add
	 * @return this client instance
	 */
	public SKClient addPacketListener(SKPacketListener packetListener) {
		packetListeners.add(packetListener);
		return this;
	}
	
	/**
	 * 
	 * Returns an array list containing all packet listeners associated with this client.
	 * 
	 * @return an array list containing all packet listeners
	 */
	public ArrayList<SKPacketListener> getPacketListeners() {
		return packetListeners;
	}
	
	/**
	 * 
	 * Returns the connection of this client or null if no connection has been established. 
	 * 
	 * @return the connection of this client
	 */
	public SKConnection getConnection() {
		return connection;
	}
}