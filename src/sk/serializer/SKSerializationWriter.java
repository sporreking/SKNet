package sk.serializer;

public final class SKSerializationWriter {
	
	
	/**
	 * 
	 * Writes a byte into the destination byte array at the pointer's offset.
	 * 
	 * @param dest the byte array to write to
	 * @param pointer the pointer to write at
	 * @param value the byte to write
	 * @return the pointers new offset
	 */
	public static final int writeBytes(byte[] dest, int pointer, byte value) {
		dest[pointer++] = value;
		return pointer;
	}
	
	/**
	 * 
	 * Writes bytes into the destination byte array at the pointer's offset.
	 * 
	 * @param dest the byte array to write to
	 * @param pointer the pointer to write at
	 * @param src the bytes to write
	 * @return the pointers new offset
	 */
	public static final int writeBytes(byte[] dest, int pointer, byte[] src) {
		for(int i = 0; i < src.length; i++)
			dest[pointer++] = src[i];
		return pointer;
	}
	
	/**
	 * 
	 * Writes a short into the destination byte array at the pointer's offset.
	 * 
	 * @param dest the byte array to write to
	 * @param pointer the pointer to write at
	 * @param value the short to write
	 * @return the pointers new offset
	 */
	public static final int writeBytes(byte[] dest, int pointer, short value) {
		dest[pointer++] = (byte) ((value >> 8) & 0xff);
		dest[pointer++] = (byte) ((value >> 0) & 0xff);
		return pointer;
	}
	
	/**
	 * 
	 * Writes a char into the destination byte array at the pointer's offset.
	 * 
	 * @param dest the byte array to write to
	 * @param pointer the pointer to write at
	 * @param value the char to write
	 * @return the pointers new offset
	 */
	public static final int writeBytes(byte[] dest, int pointer, char value) {
		dest[pointer++] = (byte) ((value >> 8) & 0xff);
		dest[pointer++] = (byte) ((value >> 0) & 0xff);
		return pointer;
	}
	
	/**
	 * 
	 * Writes an integer into the destination byte array at the pointer's offset.
	 * 
	 * @param dest the byte array to write to
	 * @param pointer the pointer to write at
	 * @param value the integer to write
	 * @return the pointers new offset
	 */
	public static final int writeBytes(byte[] dest, int pointer, int value) {
		dest[pointer++] = (byte) ((value >> 24) & 0xff);
		dest[pointer++] = (byte) ((value >> 16) & 0xff);
		dest[pointer++] = (byte) ((value >> 8) & 0xff);
		dest[pointer++] = (byte) ((value >> 0) & 0xff);
		return pointer;
	}
	
	/**
	 * 
	 * Writes a long into the destination byte array at the pointer's offset.
	 * 
	 * @param dest the byte array to write to
	 * @param pointer the pointer to write at
	 * @param value the long to write
	 * @return the pointers new offset
	 */
	public static final int writeBytes(byte[] dest, int pointer, long value) {
		dest[pointer++] = (byte) ((value >> 56) & 0xff);
		dest[pointer++] = (byte) ((value >> 48) & 0xff);
		dest[pointer++] = (byte) ((value >> 40) & 0xff);
		dest[pointer++] = (byte) ((value >> 32) & 0xff);
		dest[pointer++] = (byte) ((value >> 24) & 0xff);
		dest[pointer++] = (byte) ((value >> 16) & 0xff);
		dest[pointer++] = (byte) ((value >> 8) & 0xff);
		dest[pointer++] = (byte) ((value >> 0) & 0xff);
		return pointer;
	}
	
	/**
	 * 
	 * Writes a float into the destination byte array at the pointer's offset.
	 * 
	 * @param dest the byte array to write to
	 * @param pointer the pointer to write at
	 * @param value the float to write
	 * @return the pointers new offset
	 */
	public static final int writeBytes(byte[] dest, int pointer, float value) {
		return writeBytes(dest, pointer, Float.floatToIntBits(value));
	}
	
	/**
	 * 
	 * Writes a double into the destination byte array at the pointer's offset.
	 * 
	 * @param dest the byte array to write to
	 * @param pointer the pointer to write at
	 * @param value the double to write
	 * @return the pointers new offset
	 */
	public static final int writeBytes(byte[] dest, int pointer, double value) {
		return writeBytes(dest, pointer, Double.doubleToLongBits(value));
	}
	
	/**
	 * 
	 * Writes a boolean into the destination byte array at the pointer's offset.
	 * 
	 * @param dest the byte array to write to
	 * @param pointer the pointer to write at
	 * @param value the boolean to write
	 * @return the pointers new offset
	 */
	public static final int writeBytes(byte[] dest, int pointer, boolean value) {
		dest[pointer++] = (byte) (value ? 0x1 : 0x0);
		return pointer;
	}
	
	/**
	 * 
	 * Writes a String into the destination byte array at the pointer's offset.
	 * 
	 * <p>
	 * 
	 * The String is prefixed with a short value indicating the length of the string.
	 * 
	 * @param dest the byte array to write to
	 * @param pointer the pointer to write at
	 * @param value the String to write
	 * @return the pointers new offset
	 */
	public static final int writeBytes(byte[] dest, int pointer, String value) {
		pointer = writeBytes(dest, pointer, (short) value.length());
		return writeBytes(dest, pointer, value.getBytes());
	}
}