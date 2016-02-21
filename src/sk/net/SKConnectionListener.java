package sk.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

class SKConnectionListener implements Runnable {
	
	private SKServer server;
	
	
	/**
	 * 
	 * Constructs a new connection listener for accepting connections.
	 * 
	 * @param server the server associated with this connection listener
	 */
	protected SKConnectionListener(SKServer server) {
		this.server = server;
	}
	
	
	/**
	 * 
	 * Starts accepting client connections to this server. Called from the {@link SKServer#start()} method.
	 * 
	 */
	@Override
	public void run() {
		try {
			while(server.isRunning()) {
				
				if(!server.getServerSocket().isBound())
					continue;
				
				Socket socket;
				
				try {
					socket = server.getServerSocket().accept();
				} catch (SocketTimeoutException e) {
					continue;
				}
				
				new Thread(() -> {server.addConnection(socket);}).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			server.getServerSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}