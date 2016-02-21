package sk.net;


public class SKClientPacket {
	
	private boolean valid = false;
	
	/**
	 * 
	 * Called by the server to verify a successful initialization.
	 * 
	 */
	public void validate() {
		valid = true;
	}
	
	/**
	 * 
	 * Returns whether or not this packet has been verified by the server.
	 * 
	 * @return whether or not this packet has been verified by the server 
	 */
	public boolean isValid() {
		return valid;
	}
}