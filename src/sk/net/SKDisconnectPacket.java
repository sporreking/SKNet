package sk.net;

public class SKDisconnectPacket {
	
	public final String MSG;
	
	/**
	 * 
	 * Constructs a new disconnection packet with the specified message.
	 * 
	 * @param msg the message of this disconnection packet
	 */
	public SKDisconnectPacket(String msg) {
		this.MSG = msg;
	}
	
	public String getName() {
		return "Disconnect";
	}
}