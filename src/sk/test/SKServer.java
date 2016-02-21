package sk.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class SKServer {
	
	public static final int DEFAULT_BACKLOGS = 50;
	
	protected volatile boolean running = false;
	
	protected String ip;
	protected int port;
	protected int backlog = DEFAULT_BACKLOGS;
	
	protected volatile HashMap<Integer, SKConnection> connections;
	
	protected volatile ServerSocket serverSocket;
	
	protected SKPacketListener packetListener;
	
	protected SKConnectionHandler connectionHandler;
	protected Thread connectionThread;
	
	public SKServer(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public void start(SKPacketListener packetListener) {
		try {
			System.out.println("SKServer v" + SKNet.VERSION + "\t" + ip + ":" + port);
			
			this.packetListener = packetListener;
			
			serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(ip));
			
			connections = new HashMap<>();
			
			connectionHandler = new SKConnectionHandler(this);
			connectionThread = new Thread(connectionHandler, "Connection Thread");
			
			connectionThread.start();
			
			running = true;
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection(int id) {
		SKConnection connection = connections.get(id);
		packetListener.disconnected(connection);
		connection.close();
	}
	
	protected synchronized void addConnection(Socket socket, int id) {
		SKConnection connection = new SKConnection(true, socket, id);
		connection.setServer(this);
		connection.sendInt(id);
		connections.put(id, connection);
		packetListener.connected(connections.get(id));
		connection.startListening(packetListener);
		
		System.out.println("Connected with client \"" + connection.getAddress() + ":" + connection.getPort() + "\" (id: " + id + ")");
	}
	
	public HashMap<Integer, SKConnection> getConnections() {
		return connections;
	}
	
	public SKConnection getConnection(int id) {
		return connections.get(id);
	}
	
	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}
	
	public boolean isRunning() {
		return running;
	}
}