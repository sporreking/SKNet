package sk.test;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SKClient {
	
	private SKConnection connection;
	
	private SKPacketListener packetListener;
	
	public SKClient() {
		
	}
	
	public void connect(String ip, int port, SKPacketListener packetListener) {
		try {
			System.out.println("SKClient v" + SKNet.VERSION);
			
			this.packetListener = packetListener;
			
			Socket socket = new Socket(ip, port);
			
			connection = new SKConnection(false, socket);
			
			int id = connection.receiveInt();
			
			connection.setID(id);
			
			System.out.println("Connected to server \"" + connection.getAddress() + ":" + connection.getPort() + "\" (id: " + connection.getID() + ")");
			
			packetListener.connected(connection);
			connection.startListening(packetListener);
			
		} catch (UnknownHostException e) {
			System.err.println("Unknown host!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void disconnect() {
		connection.sendPacket(new SKPacketMessage("dc"));
		connection.close();
	}
	
	public SKConnection getConnection() {
		return connection;
	}
	
	public boolean isConnected() {
		return connection.isActive();
	}
}