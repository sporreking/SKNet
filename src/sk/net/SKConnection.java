package sk.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sk.serializer.SKSerializer;

public class SKConnection {
	
	private final int TYPE;
	
	private int id = -1;
	
	private Socket socket;
	
	private SKServer server;
	private SKClient client;
	
	private DataInputStream in;
	private DataOutputStream out;
	
	private SKCommunicationListener communicationListener;
	private Thread communicationListenerThread;
	
	/**
	 * 
	 * Constructs a new connection over the specified socket.
	 * The socket side of this connection must be specified with a type (SK_SERVER or SK_CLIENT).
	 * 
	 * @param type the type of this connection. One of SK_SERVER or SK_CLIENT
	 * @param socket the socket of this connection
	 */
	public SKConnection(int type, Socket socket) {
		this.TYPE = type;
		this.socket = socket;
	}
	
	
	/**
	 * 
	 * Sends the session initialization packets between client and server.
	 * The procedure depends on what type this connection.
	 * <p>
	 * This method should only be called internally from the SKNet API.
	 * 
	 * @param packet the packet to send to the other end of this connection. Either {@link SKClientPacket} or {@link SKServerPacket}
	 * @return this connection instance
	 * @throws IllegalArgumentException if the argument was not of type {@link SKClientPacket} or {@link SKServerPacket}. Also thrown if the argument does not correspond to this connection type.
	 * @throws IOException if an I/O error occurs while writing stream header
	 * @throws IllegalStateException if the end-point responded an invalid session initialization packet or if a client failed to validate a packet.
	 */
	protected SKConnection init(Object packet) {
		
		try {
			out = new DataOutputStream(socket.getOutputStream());
			out.flush();
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.err.println("An I/O error occured while reading the stream header");
			e.printStackTrace();
		}
		
		
		
		if(packet instanceof SKServerPacket) {
			
			if(TYPE == SKNet.SK_CLIENT)
				throw new IllegalArgumentException("Cannot send \"" + packet.getClass().getSimpleName() + "\" Packet from connection of type SK_CLIENT");
			
			try {
				
				//Sends the server packet to the client
				sendPacket(packet);
				
				//Waits for response from the client
				Object clientPacket = receivePacket();
				
				//Is the received packet the of the correct class?
				if(!(clientPacket instanceof SKClientPacket))
					throw new IllegalStateException("Invalid packet received, expected a client side session initialization packet");
				
				//Is the packet valid?
				if(!((SKClientPacket)clientPacket).isValid())
					throw new IllegalStateException("The client failed to validate the connection");
				
				//The procedure was successful
				
				this.id = ((SKServerPacket)packet).ID;
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		} else if(packet instanceof SKClientPacket) {
			if(TYPE == SKNet.SK_SERVER)
				throw new IllegalArgumentException("Cannot send \"" + packet.getClass().getSimpleName() + "\" Packet from connection of type SK_SERVER");
			
			try {
				
				//Waits for response from server
				Object serverPacket = receivePacket();
				
				if(!(serverPacket instanceof SKServerPacket))
					throw new IllegalStateException("Invalid packet received, expected a server side session initialization packet");
				
				this.id = ((SKServerPacket)serverPacket).ID;
				
				((SKClientPacket)packet).validate();
				
				//Sends a validated client packet back to the server
				sendPacket(packet);
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		} else {
			throw new IllegalArgumentException("Packet must be of type SKClientPacket or SKServerPacket");
		}
		
		
		return this;
	}
	
	
	/**
	 * 
	 * Closes the socket and I/O streams of this connection.
	 * 
	 */
	protected void close() {
		try {
			socket.close();
//			System.out.println("Connection " + id + " was closed");
		} catch (IOException e) {
			System.err.println("Failed to close connection " + id);
		}
	}
	
	/**
	 * 
	 * Starts the communication listener thread for this connection.
	 * 
	 */
	protected void startListening() {
		communicationListener = new SKCommunicationListener(this);
		communicationListenerThread = new Thread(communicationListener);
		
		communicationListenerThread.start();
	}
	
	/**
	 * 
	 * Sends a packet to the end-point of this connection's socket.
	 * 
	 * @param packet the packet to be sent
	 * @return this connection instance
	 * @throws IOException if the output stream failed to operate.
	 */
	public SKConnection sendPacket(Object packet) throws IOException {
		byte[] data = SKSerializer.serialize(packet);
		out.writeInt(data.length);
		out.write(data);
		out.flush();
		return this;
	}
	
	
	/**
	 * 
	 * Waits for a packet to be received.
	 * 
	 * @return the received packet
	 * @throws IOException if the InputStream failed to operate.
	 * @throws ClassNotFoundException if the received packet could not be identified as an existing class
	 */
	protected Object receivePacket() throws IOException, ClassNotFoundException {
		byte[] data = new byte[in.readInt()];
		in.read(data);
		return SKSerializer.deserialize(data);
	}
	
	
	/**
	 * 
	 * Sets the server associated with with this connection. This will only work if the connection is of type SK_SERVER.
	 * 
	 * Otherwise, an IllegalStateException will be thrown.
	 * 
	 * @param server the server instance
	 * @return this connection instance
	 * @throws IllegalStateException if this connection is not of type SK_SERVER
	 */
	protected SKConnection setServer(SKServer server) {
		if(TYPE == SKNet.SK_CLIENT)
			throw new IllegalStateException("Cannot set server to a connection of type SK_CLIENT");
		this.server = server;
		return this;
	}
	
	/**
	 * 
	 * Returns the server associated with with this connection. This will only work if the connection is of type SK_SERVER.
	 * 
	 * Otherwise, an IllegalStateException will be thrown.
	 * 
	 * @return the server instance of this connection
	 * @throws IllegalStateException if this connection is not of type SK_SERVER
	 */
	protected SKServer getServer() {
		if(TYPE == SKNet.SK_CLIENT)
			throw new IllegalStateException("Cannot get server from a connection of type SK_CLIENT");
		return server;
	}
	
	/**
	 * 
	 * Sets the client associated with with this connection. This will only work if the connection is of type SK_CLIENT.
	 * 
	 * Otherwise, an IllegalStateException will be thrown.
	 * 
	 * @param client the client instance
	 * @return this connection instance
	 * @throws IllegalStateException if this connection is not of type SK_CLIENT
	 */
	protected SKConnection setClient(SKClient client) {
		if(TYPE == SKNet.SK_SERVER)
			throw new IllegalStateException("Cannot set client to a connection of type SK_SERVER");
		this.client = client;
		return this;
	}
	
	/**
	 * 
	 * Returns the client associated with with this connection. This will only work if the connection is of type SK_CLIENT.
	 * 
	 * Otherwise, an IllegalStateException will be thrown.
	 * 
	 * @return the client instance of this connection
	 * @throws IllegalStateException if this connection is not of type SK_CLIENT
	 */
	protected SKClient getClient() {
		if(TYPE == SKNet.SK_SERVER)
			throw new IllegalStateException("Cannot get client from a connection of type SK_SERVER");
		return client;
	}
	
	
	/**
	 * 
	 * Returns the type of this connection. One of SK_CLIENT or SK_SERVER.
	 * 
	 * @return the type of this connection
	 */
	public int getType() {
		return TYPE;
	}
	
	
	/**
	 * 
	 * Returns the host address of this connection represented as a string.
	 * 
	 * @return the host address of this connection
	 */
	public String getHostAddress() {
		return socket.getInetAddress().getHostAddress();
	}
	
	
	/**
	 * 
	 * Returns the id of this connection.
	 * 
	 * If the id has not yet been assigned, or if the session initialization process failed, it will return -1.
	 * 
	 * @return the id of this connection instance
	 */
	public int getID() {
		return id;
	}
	
	
	/**
	 * 
	 * Returns whether or not this connections socket has been closed.
	 * 
	 * @return whether or not this connections socket has been closed
	 */
	public boolean isSocketClosed() {
		return socket.isClosed();
	}
	
	/**
	 * 
	 * Returns whether or not the connection streams have been closed.
	 * 
	 * @return whether or not the connection streams have been closed
	 */
	public boolean isIOClosed() {
		try {
			sendPacket(new SKConnectionTestPacket());
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	/**
	 * 
	 * Returns the active port of this connection.
	 * 
	 * @return the active port of this connection
	 */
	public int getPort() {
		return socket.getPort();
	}
}