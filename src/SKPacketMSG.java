import sk.net.SKPacket;


public class SKPacketMSG extends SKPacket {
	
	public final String MSG;
	
	public SKPacketMSG(String msg) {
		this.MSG = msg;
	}
	
	@Override
	public String getName() {
		return "Message";
	}
}