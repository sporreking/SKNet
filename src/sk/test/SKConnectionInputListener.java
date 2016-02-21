package sk.test;


public class SKConnectionInputListener implements Runnable {
	
	private volatile boolean running = false;
	
	private SKConnection connection;
	private SKPacketListener packetListener;
	
	public SKConnectionInputListener(SKConnection connection, SKPacketListener packetListener) {
		this.connection = connection;
		this.packetListener = packetListener;
	}
	
	@Override
	public void run() {
		running = true;
		while (running) {
			SKPacket packet = connection.receivePacket();
			if(packet instanceof SKPacketMessage) {
				if(((SKPacketMessage)packet).MSG.equals("dc")) {
					packetListener.disconnected(connection);
					connection.close();
					break;
				}
			}
			
			new Thread(new SKConnectionInputHandler(connection, packetListener, packet)).start();
		}
	}
	
	private class SKConnectionInputHandler implements Runnable {
		
		private SKConnection connection;
		private SKPacketListener packetListener;
		private SKPacket packet;
		
		public SKConnectionInputHandler(SKConnection connection, SKPacketListener packetListener, SKPacket packet) {
			this.connection = connection;
			this.packetListener = packetListener;
			this.packet = packet;
		}
		
		@Override
		public void run() {
			packetListener.received(connection, packet);
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void stop() {
		running = false;
	}
}