import java.io.IOException;
import java.util.Scanner;

import sk.net.SKClient;
import sk.net.SKServer;

public class Client {
	
	private volatile boolean running = false;
	
	public Client() {
		SKClient client = new SKClient();
		
		try {
			client.addPacketListener(new ClientListener());
			client.connect("localhost", 6969);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Scanner scanner = new Scanner(System.in);
		
		running = true;
		while(running) {
			String[] cmd = scanner.nextLine().split(" ");
			
			switch(cmd[0]) {
			case "send":
				client.send(new SKPacketMSG(cmd[1]));
				break;
			case "dc":
				client.disconnect(cmd[1]);
				break;
			case "closed":
				System.out.println(client.getConnection().isSocketClosed());
				break;
			}
		}
	}
	
	public static final void main(String[] args) {
		new Client();
	}
	
}