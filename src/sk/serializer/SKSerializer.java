package sk.serializer;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class SKSerializer {
	
private static final HashMap<Integer, Class<?>> registry = new HashMap<>();
	
	/**
	 * 
	 * Registers a class for serialization.
	 * 
	 * <p>
	 * 
	 * Objects of unregistered classes cannot be serialized nor deserialized.
	 * 
	 * @param c the class to register
	 */
	public static final void register(Class<?> c) {
		registry.put(c.getName().hashCode(), c);
	}
	
	/**
	 * 
	 * Serializes an object into an array of bytes.
	 * 
	 * <p>
	 * 
	 * The class of the object must be registered. Otherwise, an IllegalArgumentException will be thrown.
	 * 
	 * @param obj the object to serialize
	 * @return the serialized data
	 * @throws IllegalArgumentException if the class of the object is not registered
	 * @throws IllegalStateException if the object contains invalid field types
	 */
	public static final byte[] serialize(Object obj) {
		
		if(!registry.containsValue(obj.getClass()))
			throw new IllegalArgumentException("Class " + obj.getClass().getName() + " is not registred");
		
		Class<?> c = obj.getClass();
		
		byte[] data = new byte[getSerializedLength(obj)];
		
		int pointer = 0;
		
		pointer = SKSerializationWriter.writeBytes(data, pointer, c.getName().hashCode());
		try {
			for(Field f : c.getDeclaredFields()) {
				if(!f.isAccessible())
					f.setAccessible(true);
				
				if(f.getType() == byte.class || f.getType() == Byte.class)
					pointer = SKSerializationWriter.writeBytes(data, pointer, (byte) f.get(obj));
				else if(f.getType() == short.class || f.getType() == Short.class)
					pointer = SKSerializationWriter.writeBytes(data, pointer, (short) f.get(obj));
				else if(f.getType() == char.class || f.getType() == Character.class)
					pointer = SKSerializationWriter.writeBytes(data, pointer, (char) f.get(obj));
				else if(f.getType() == int.class || f.getType() == Integer.class)
					pointer = SKSerializationWriter.writeBytes(data, pointer, (int) f.get(obj));
				else if(f.getType() == long.class || f.getType() == Long.class)
					pointer = SKSerializationWriter.writeBytes(data, pointer, (long) f.get(obj));
				else if(f.getType() == float.class || f.getType() == Float.class)
					pointer = SKSerializationWriter.writeBytes(data, pointer, (float) f.get(obj));
				else if(f.getType() == double.class || f.getType() == Double.class)
					pointer = SKSerializationWriter.writeBytes(data, pointer, (double) f.get(obj));
				else if(f.getType() == boolean.class || f.getType() == Boolean.class)
					pointer = SKSerializationWriter.writeBytes(data, pointer, (boolean) f.get(obj));
				else if(f.getType() == String.class)
					pointer = SKSerializationWriter.writeBytes(data, pointer, (String) f.get(obj));
				else
					throw new IllegalStateException("Packet may only contain primitive/string fields");
			}
		} catch (IllegalArgumentException e) { e.printStackTrace();
		} catch (IllegalAccessException e) { e.printStackTrace(); }
		
		return data;
	}
	
	/**
	 * 
	 * Deserializes an array of bytes into an object.
	 * 
	 * <p>
	 * 
	 * The data must deserialize into a registered class type. Otherwise, an IllegalArgumentException will be thrown.
	 * 
	 * <p>
	 * 
	 * All fields of the object will be initialized with the values held before serialization.
	 * No constructor will be called.
	 * 
	 * @param data the data to deserialize
	 * @return the deserialized object
	 * @throws IllegalArgumentException if the data could not be resolved into an registered class
	 * @throws IllegalStateException if the object contains invalid field types
	 */
	public static final Object deserialize(byte[] data) {
		
		int pointer = 4;
		
		Class<?> c = registry.get(SKSerializationReader.readInt(data, 0));
		
		if(c == null)
			throw new IllegalArgumentException("Data hash is not recognized");
		
		Object obj = null;
		
		try {
			
			Objenesis objenesis = new ObjenesisStd();
			
			obj = objenesis.newInstance(c);
			
			
			for(Field f : c.getDeclaredFields()) {
				if(!f.isAccessible())
					f.setAccessible(true);
				
				if(f.getType() == byte.class || f.getType() == Byte.class) {
					f.set(obj, SKSerializationReader.readByte(data, pointer));
					pointer += 1;
				} else if(f.getType() == short.class || f.getType() == Short.class) {
					f.set(obj, SKSerializationReader.readShort(data, pointer));
					pointer += 2;
				} else if(f.getType() == char.class || f.getType() == Character.class) {
					f.set(obj, SKSerializationReader.readChar(data, pointer));
					pointer += 2;
				} else if(f.getType() == int.class || f.getType() == Integer.class) {
					f.set(obj, SKSerializationReader.readInt(data, pointer));
					pointer += 4;
				} else if(f.getType() == long.class || f.getType() == Long.class) {
					f.set(obj, SKSerializationReader.readLong(data, pointer));
					pointer += 8;
				} else if(f.getType() == float.class || f.getType() == Float.class) {
					f.set(obj, SKSerializationReader.readFloat(data, pointer));
					pointer += 4;
				} else if(f.getType() == double.class || f.getType() == Double.class) {
					f.set(obj, SKSerializationReader.readDouble(data, pointer));
					pointer += 8;
				} else if(f.getType() == boolean.class || f.getType() == Boolean.class) {
					f.set(obj, SKSerializationReader.readBoolean(data, pointer));
					pointer += 1;
				} else if(f.getType() == String.class) {
					String str = SKSerializationReader.readString(data, pointer);
					f.set(obj, str);
					pointer += 2 + str.length();
				} else {
					throw new IllegalStateException("Packet may only contain primitive/string fields");
				}
			}
		} catch (IllegalAccessException e) { e.printStackTrace(); }
		
		return obj;
	}
	
	/**
	 * 
	 * Returns the serialized length of the specified object.
	 * 
	 * @param obj the object whose serialized length to calculate
	 * @return the serialized length represented as the amount of bytes
	 * @throws IllegalArgumentException if the class of the object is not registered
	 * @throws IllegalStateException if the object contains invalid field types 
	 */
	public static final int getSerializedLength(Object obj) {
		
		if(!registry.containsValue(obj.getClass()))
			throw new IllegalArgumentException("Class " + obj.getClass().getName() + " is not registred");
		
		Class<?> c = obj.getClass();
		
		int length = 0;
		
		length += 4;
		
		for(Field f : c.getDeclaredFields()) {
			if(f.getType() == byte.class || f.getType() == Byte.class)
				length += 1;
			else if(f.getType() == short.class || f.getType() == Short.class)
				length += 2;
			else if(f.getType() == char.class || f.getType() == Character.class)
				length += 2;
			else if(f.getType() == int.class || f.getType() == Integer.class)
				length += 4;
			else if(f.getType() == long.class || f.getType() == Long.class)
				length += 8;
			else if(f.getType() == float.class || f.getType() == Float.class)
				length += 4;
			else if(f.getType() == double.class || f.getType() == Double.class)
				length += 8;
			else if(f.getType() == boolean.class || f.getType() == Boolean.class)
				length += 1;
			else if(f.getType() == String.class)
				try {
					if(!f.isAccessible())
						f.setAccessible(true);
					length += 2 + ((String) f.get(obj)).length();
				} catch (IllegalArgumentException e) { e.printStackTrace();
				} catch (IllegalAccessException e) { e.printStackTrace(); }
			else
				throw new IllegalStateException("Packet may only contain primitive/string fields");
		}
		
		return length;
	}
}