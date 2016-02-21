import java.io.IOException;
import java.util.Scanner;

import sk.net.SKServer;

public class Server {
	
	private volatile boolean running = false;
	
	public Server() {
		SKServer server = new SKServer();
		
		try {
			server.addPacketListener(new ServerListener());
			server.start();
			server.bind("localhost", 6969);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Scanner scanner = new Scanner(System.in);
		
		running = true;
		while(running) {
			String[] cmd = scanner.nextLine().split(" ");
			
			switch(cmd[0]) {
			case "send":
				server.sendToAll(new SKPacketMSG(cmd[1]));
				break;
			case "go":
				try {
					server.getConnection(0).sendPacket(new SKPacketMSG("go!"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "dc":
				server.disconnect(Integer.parseInt(cmd[1]), cmd[2]);
				break;
			case "closed":
				System.out.println(server.getConnection(0).isSocketClosed());
				break;
			}
		}
	}
	
	public static final void main(String[] args) {
		new Server();
	}
}