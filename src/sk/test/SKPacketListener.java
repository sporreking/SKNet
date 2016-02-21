package sk.test;

public interface SKPacketListener {
	
	public void received(SKConnection connection, SKPacket obj);
	public void connected(SKConnection connection);
	public void disconnected(SKConnection connection);
	
}