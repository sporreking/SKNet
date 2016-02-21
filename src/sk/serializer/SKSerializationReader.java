package sk.serializer;

public class SKSerializationReader {
	
	/**
	 * 
	 * Reads a byte from a byte array at the given offset.
	 * 
	 * @param src the byte array to read from
	 * @param offset the offset to read from
	 * @return the retrieved byte
	 */
	public static final byte readByte(byte[] src, int offset) {
		return src[offset];
	}
	
	/**
	 * 
	 * Reads a short from a byte array at the given offset.
	 * 
	 * @param src the byte array to read from
	 * @param offset the offset to read from
	 * @return the retrieved short
	 */
	public static final short readShort(byte[] src, int offset) {
		return (short) ((src[offset + 0] << 8) | (src[offset + 1] << 0));
	}
	
	/**
	 * 
	 * Reads a char from a byte array at the given offset.
	 * 
	 * @param src the byte array to read from
	 * @param offset the offset to read from
	 * @return the retrieved char
	 */
	public static final char readChar(byte[] src, int offset) {
		return (char) ((src[offset + 0] << 8) | (src[offset + 1] << 0));
	}
	
	/**
	 * 
	 * Reads an integer from a byte array at the given offset.
	 * 
	 * @param src the byte array to read from
	 * @param offset the offset to read from
	 * @return the retrieved integer
	 */
	public static final int readInt(byte[] src, int offset) {
		return (int) (((src[offset + 0] & 0xff) << 24) | ((src[offset + 1] & 0xff) << 16) |
						((src[offset + 2] & 0xff) << 8) | ((src[offset + 3] & 0xff) << 0));
	}
	
	/**
	 * 
	 * Reads a long from a byte array at the given offset.
	 * 
	 * @param src the byte array to read from
	 * @param offset the offset to read from
	 * @return the retrieved long
	 */
	public static final long readLong(byte[] src, int offset) {
		return (long) (((src[offset + 0] & 0xff) << 56) | ((src[offset + 1] & 0xff) << 48) |
						((src[offset + 2] & 0xff) << 40) | ((src[offset + 3] & 0xff) << 32) |
						((src[offset + 4] & 0xff) << 24) | ((src[offset + 5] & 0xff) << 16) |
						((src[offset + 6] & 0xff) << 8) | ((src[offset + 7] & 0xff) << 0));
	}
	
	/**
	 * 
	 * Reads a float from a byte array at the given offset.
	 * 
	 * @param src the byte array to read from
	 * @param offset the offset to read from
	 * @return the retrieved float
	 */
	public static final float readFloat(byte[] src, int offset) {
		return Float.intBitsToFloat(readInt(src, offset));
	}
	
	/**
	 * 
	 * Reads a double from a byte array at the given offset.
	 * 
	 * @param src the byte array to read from
	 * @param offset the offset to read from
	 * @return the retrieved double
	 */
	public static final double readDouble(byte[] src, int offset) {
		return Double.longBitsToDouble(readLong(src, offset));
	}
	
	/**
	 * 
	 * Reads a boolean from a byte array at the given offset.
	 * 
	 * @param src the byte array to read from
	 * @param offset the offset to read from
	 * @return the retrieved boolean
	 * @throws IllegalStateException if the byte at the given offset does not resolve into a value of 0x1 or 0x0
	 */
	public static final boolean readBoolean(byte[] src, int offset) {
		if(src[offset] != (byte) 0x0 && src[offset] != (byte) 0x1)
			throw new IllegalStateException("The byte at the given offset cannot represent a boolean");
		
		return src[offset] == 0x1;
	}
	
	/**
	 * 
	 * Reads a String from a byte array at the given offset.
	 * 
	 * <p>
	 * 
	 * Expects a two byte length prefix of the String.
	 * 
	 * @param src the byte array to read from
	 * @param offset the offset to read from
	 * @return the retrieved String
	 */
	public static final String readString(byte[] src, int offset) {
		byte[] data = new byte[readShort(src, offset)];
		
		for(int i = 0; i < data.length; i++) {
			data[i] = src[0x2 + offset + i];
		}
		
		return new String(data);
	}
}