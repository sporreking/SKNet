package sk.net;

public class SKServerPacket {
	
	public final int ID;
	
	/**
	 * 
	 * Constructs a new server packet for session initialization.
	 * 
	 * @param id the id dedicated to the receiving client
	 */
	public SKServerPacket(int id) {
		this.ID = id;
	}
	
	public String getName() {
		return "Server Session Initialization Packet";
	}
}