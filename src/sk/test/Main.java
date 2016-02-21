package sk.test;

import java.util.Scanner;

public class Main {
	
	public static volatile boolean running = false;
	
	private final Scanner scanner;
	
	private SKServer server;
	private SKClient client;
	
	public Main() {
		scanner = new Scanner(System.in);
		
		System.out.println("SKNet v" + SKNet.VERSION);
		System.out.println("----------------------------\n");
		System.out.println("client/server?");
		
		String type = input();
		System.out.println();
		
		switch(type) {
		case "c":
		case "client":
			client();
			break;
		case "s":
		case "server":
			server();
			break;
		default:
			System.err.println("Must be run as \"client\"/\"server\"");
			exit(0);
		}
	}
	
	private final void client() {
		client = new SKClient();
		
		
		running = true;
		while(running) {
			
			String[] input = input().split(" ");
			
			switch(input[0]) {
			case "disconnect":
				client.disconnect();
				break;
			case "connect":
				client.connect(input[1], Integer.parseInt(input[2]), new ClientListener(client));
				break;
			case "send":
				client.getConnection().sendPacket(new SKPacketMessage(input[1]));
				break;
			case "exit":
				try {
					exit(Integer.parseInt(input[1]));
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					exit(0);
				}
				break;
			}
		}
	}
	
	private final void server() {
		server = new SKServer("localhost", 6969);
		SKPacketListener listener = new ServerListener(server);
		
		running = true;
		while(running) {
			
			String[] input = input().split(" ");
			
			switch(input[0]) {
			case "connections":
				for(SKConnection c : server.getConnections().values())
					System.out.println("Client " + c.getID() + " | " + c.getAddress() + ":" + c.getPort());
				break;
			case "send":
				server.getConnection(Integer.parseInt(input[1])).sendPacket(new SKPacketMessage(input[2]));
				break;
			case "start":
				server.start(listener);
				break;
			case "exit":
				try {
					exit(Integer.parseInt(input[1]));
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					exit(0);
				}
				break;
			}
		}
	}
	
	public final void clear() {
		for(int i = 0; i < 20; i++)
			System.out.println();
	}
	
	public final String input() {
		return scanner.nextLine();
	}
	
	public SKServer getServer() {
		return server;
	}
	
	public static final void exit(int err) {
		System.exit(err);
	}
	
	public static final void main(String[] args) {
		new Main();
	}
}