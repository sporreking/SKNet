package sk.test;

public class ClientListener implements SKPacketListener {
	
	private SKClient client;
	
	public ClientListener(SKClient client) {
		this.client = client;
	}
	
	@Override
	public void received(SKConnection connection, SKPacket packet) {
		System.out.println(((SKPacketMessage)packet).MSG);
	}

	@Override
	public void connected(SKConnection connection) {
		
	}

	@Override
	public void disconnected(SKConnection connection) {
		
	}
}