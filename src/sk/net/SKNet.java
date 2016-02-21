package sk.net;

import sk.serializer.SKSerializer;

public class SKNet {
	
	public static final int SK_CLIENT = 0;
	public static final int SK_SERVER = 1;
	
	public static final String SK_ERR_INTERRUPTED = "Connection interrupted";
	
	/**
	 * 
	 * Must be called once before using the SKNet API.
	 * 
	 */
	public static final void init() {
		SKSerializer.register(SKClientPacket.class);
		SKSerializer.register(SKServerPacket.class);
		SKSerializer.register(SKDisconnectPacket.class);
		SKSerializer.register(SKConnectionTestPacket.class);
	}
}