package sk.net;

import java.io.IOException;

public class SKCommunicationListener implements Runnable {
	
	private final int TYPE;
	
	private SKServer server;
	private SKClient client;
	
	private SKConnection connection;
	
	/**
	 * 
	 * Constructs a new communication listener in order to read incoming traffic from the specified connection.
	 * 
	 * @param connection the connection to listen to
	 */
	public SKCommunicationListener(SKConnection connection) {
		this.connection = connection;
		this.TYPE = connection.getType();
		
		if(TYPE == SKNet.SK_SERVER) {
			this.server = connection.getServer();
		} else if(TYPE == SKNet.SK_CLIENT) {
			this.client = connection.getClient();
		}
	}
	
	@Override
	public void run() {
		
		while(!connection.isSocketClosed()) {
			
			try {
				Object packet = connection.receivePacket();
				
				//Connection test?
				if(packet instanceof SKConnectionTestPacket)
					continue;
				
				//Disconnect packet?
				if(packet instanceof SKDisconnectPacket) {
					if(TYPE == SKNet.SK_CLIENT) {
						client.close(false, ((SKDisconnectPacket)packet).MSG);
						break;
					} else if(TYPE == SKNet.SK_SERVER) {
						server.close(connection.getID(), false, ((SKDisconnectPacket)packet).MSG);
						break;
					}
				}
				
				
				//Handle received packet
				if(TYPE == SKNet.SK_SERVER) {
					for(SKPacketListener packetListener : server.getPacketListeners()) {
						new Thread(() -> packetListener.received(connection, packet)).start();
					}
				} else if(TYPE == SKNet.SK_CLIENT) {
					for(SKPacketListener packetListener : client.getPacketListeners()) {
						new Thread(() -> packetListener.received(connection, packet)).start();
					}
				}
				
			} catch (ClassNotFoundException e) {
				System.err.println("Unrecognized packet received!");
			} catch (IOException e) {
				if(connection.isIOClosed() && !connection.isSocketClosed()) {
					if(TYPE == SKNet.SK_CLIENT) {
						client.close(false, SKNet.SK_ERR_INTERRUPTED);
					} else if(TYPE == SKNet.SK_SERVER) {
						server.close(connection.getID(), false, SKNet.SK_ERR_INTERRUPTED);
					}
				}
			}
			
		}
		
//		System.out.println("Communication thread " + connection.getID() + " closed");
		
		if(TYPE == SKNet.SK_SERVER) {
			server.removeConnection(connection.getID());
		}
		
	}
}