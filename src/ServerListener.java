import java.io.IOException;

import sk.net.SKConnection;
import sk.net.SKPacket;
import sk.net.SKPacketListener;


public class ServerListener implements SKPacketListener {

	@Override
	public void received(SKConnection connection, SKPacket packet) {
		if(packet instanceof SKPacketMSG) {
			System.out.println("Client " + connection.getID() + ": " + ((SKPacketMSG)packet).MSG);
		}
	}

	@Override
	public void connected(SKConnection connection) {
		System.out.println("Client " + connection.getHostAddress() + " connected with id " + connection.getID());
	}

	@Override
	public void disconnected(SKConnection connection, boolean local, String msg) {
		System.out.println("Client " + connection.getID() + " disconnected");
	}
	
}
