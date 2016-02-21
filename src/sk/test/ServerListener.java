package sk.test;

public class ServerListener implements SKPacketListener {
	
	private SKServer server;
	
	public ServerListener(SKServer server) {
		this.server = server;
	}
	
	@Override
	public void received(SKConnection connection, SKPacket packet) {
		for(SKConnection c : server.getConnections().values()) {
			if(connection.getID() != c.getID())
				c.sendPacket(new SKPacketMessage("Client " + connection.getID() + ": " + ((SKPacketMessage)packet).MSG));
		}
	}

	@Override
	public void connected(SKConnection connection) {
		
	}

	@Override
	public void disconnected(SKConnection connection) {
		
	}
}