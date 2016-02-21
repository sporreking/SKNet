import java.io.IOException;

import sk.net.SKConnection;
import sk.net.SKPacket;
import sk.net.SKPacketListener;


public class ClientListener implements SKPacketListener {

	@Override
	public void received(SKConnection connection, SKPacket packet) {
		if(packet instanceof SKPacketMSG) {
			System.out.println("Server: " + ((SKPacketMSG)packet).MSG);
		}
	}

	@Override
	public void connected(SKConnection connection) {
		
	}

	@Override
	public void disconnected(SKConnection connection, boolean local, String msg) {
		System.out.println("Disconnected from server: " + msg);
	}
	
}
