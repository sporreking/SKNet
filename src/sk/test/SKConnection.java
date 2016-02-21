package sk.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class SKConnection {
	
	public final boolean SERVER_SIDE;
	private SKServer server;
	
	private int id = -1;
	
	private Socket socket;
	
	private DataInputStream in;
	private DataOutputStream out;
	
	private ObjectInputStream inObj;
	private ObjectOutputStream outObj;
	
	private SKConnectionInputListener inputListener;
	private Thread inputListenerThread;
	
	private SKPacketListener packetListener;
	
	public SKConnection(boolean serverSide, Socket socket) {
		this(serverSide, socket, -1);
	}
	
	public SKConnection(boolean serverSide, Socket socket, int id) {
		this.socket = socket;
		this.id = id;
		SERVER_SIDE = serverSide;
		
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			outObj = new ObjectOutputStream(socket.getOutputStream());
			inObj = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void startListening(SKPacketListener packetListener) {
		this.packetListener = packetListener;
		inputListener = new SKConnectionInputListener(this, packetListener);
		inputListenerThread = new Thread(inputListener);
		inputListenerThread.start();
	}
	
	public SKPacket receivePacket() {
		SKPacket packet = null;
		
		try {
			Object obj = inObj.readObject();
			if(obj instanceof SKPacket)
				packet = (SKPacket)obj;
			else
				System.err.println("Received invalid packet!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			if(inputListener.isRunning())
				close();
			packetListener.disconnected(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return packet;
	}
	
	public boolean receiveBoolean() {
		try {
			return in.readBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public byte receiveByte() {
		try {
			if(in.available() > 0)
			return in.readByte();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public String receiveString() {
		try {
			return in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public char receiveChar() {
		try {
			return in.readChar();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public double receiveDouble() {
		try {
			return in.readDouble();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public float receiveFloat() {
		try {
			return in.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public int receiveInt() {
		try {
			return in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public long receiveLong() {
		try {
			return in.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public short receiveShort() {
		try {
			return in.readShort();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public void sendPacket(SKPacket packet) {
		try {
			outObj.writeObject(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendBoolean(boolean bool) {
		try {
			out.writeBoolean(bool);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendByte(byte b) {
		try {
			out.writeByte(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendString(String str) {
		try {
			out.writeBytes(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendChar(char c) {
		try {
			out.writeChar(c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendDouble(double d) {
		try {
			out.writeDouble(d);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendFloat(float f) {
		try {
			out.writeFloat(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendInt(int i) {
		try {
			out.writeInt(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendLong(long l) {
		try {
			out.writeLong(l);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendShort(short s) {
		try {
			out.writeByte(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			out.close();
			in.close();
			outObj.close();
			inObj.close();
			socket.close();
			inputListener.stop();
			
			if(SERVER_SIDE) {
				server.getConnections().remove(id);
				System.out.println("Disconnected from client \"" + getAddress() + ":" + getPort() + "\" (id: " + id + ")");
			} else {
				System.out.println("Disconnected from server \"" + getAddress() + ":" + getPort() + "\"");
			}
		} catch (IOException e) {}
	}
	
	protected void setServer(SKServer server) {
		this.server = server;
	}
	
	public int getPort() {
		return socket.getPort();
	}
	
	public String getAddress() {
		return socket.getInetAddress().getHostAddress();
	}
	
	public boolean isActive() {
		return !socket.isClosed();
	}
	
	protected void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
}