import sk.net.SKNet;
import sk.serializer.SKSerializer;
import sk.serializer.SKSerializationWriter;

public class SerializeTest {
	
	private static volatile boolean running;
	private static volatile int counter;
	
	public static final void main(String[] args) {
		
		SKSerializer.register(TestPacket.class);
		
		TestPacket tp = new TestPacket("Hello there people!", 9042234);
		
		Thread th = new Thread(() -> {
			while(running) {
				
				byte[] data = SKSerializer.serialize(tp);
				
				SKSerializer.deserialize(data);
				
				counter++;
			}
		});
		
		running = true;
		th.start();
		
		try {
			Thread.sleep(1000);
			running = false;
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(counter);
	}
}