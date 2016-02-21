package sk.net;

public interface SKPacketListener {
	
	
	/**
	 * 
	 * Called when a packet is received.
	 * 
	 * @param connection the {@link SKConnection} from which the packet was received
	 * @param packet the received packet
	 */
	public void received(SKConnection connection, Object packet);
	
	
	/**
	 * 
	 * Called when a new connection is established.
	 * 
	 * @param connection the new connection
	 */
	public void connected(SKConnection connection);
	
	
	/**
	 * 
	 * Called when a connection is closed.
	 * 
	 * @param connection the closed connection
	 * @param local true if the disconnection was fired locally
	 * @param msg the disconnection message
	 */
	public void disconnected(SKConnection connection, boolean local, String msg);
	
}