package sk.test;

import java.io.IOException;
import java.net.Socket;

public class SKConnectionHandler implements Runnable {
	
	private SKServer server;
	
	public SKConnectionHandler(SKServer server) {
		this.server = server;
	}
	
	protected void accept() {
		Socket socket = null;
		try {
			socket = server.serverSocket.accept();
			new Thread(new SKConnectionWorker(server, socket)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(server.isRunning())
			accept();
	}
	
	private class SKConnectionWorker implements Runnable {
		
		private SKServer server;
		private Socket socket;
		
		public SKConnectionWorker(SKServer server, Socket socket) {
			this.server = server;
			this.socket = socket;
		}
		
		@Override
		public void run() {
			int id = 0;
			boolean done = false;
			while(!done) {
				done = true;
				for(SKConnection c : server.connections.values()) {
					if(id == c.getID()) {
						done = false;
						id++;
						break;
					}
				}
			}
			
			server.addConnection(socket, id);
		}
	}
}
