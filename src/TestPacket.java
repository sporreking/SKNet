import sk.net.SKServer;

public class TestPacket {
	
	public final int I;
	
	private String str;
	
	public TestPacket(String str, int i) {
		this.I = i;
		this.str = str;
	}
	
	public String str() {
		return str;
	}
	
}