package com.smanzana.dungeonmaster.session.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smanzana.dungeonmaster.utils.Displayable;

public abstract class Config<T extends Enum<T>> implements Displayable {
	
	protected static final String OUT_COMMENT_PREFIX = "#";
	protected static final String OUT_COMMENT_GUARD = "##########";
	

	public static enum ValueType {
		STRING,
		BOOL,
		INT,
		DOUBLE,
	}
	
	protected static class ConfigValue {
		private ValueType valueType;
		private Object value;
		
		public ConfigValue(ValueType type, Object value) {
			this.valueType = type;
			this.value = value;
		}
		
		public ConfigValue(Object value) {
			this.value = value;
			if (value instanceof String)
				this.valueType = ValueType.STRING;
			else if (value instanceof Integer)
				this.valueType = ValueType.INT;
			if (value instanceof Double)
				this.valueType = ValueType.DOUBLE;
			if (value instanceof Boolean)
				this.valueType = ValueType.BOOL;
		}
		
		public ValueType getType() {
			return valueType;
		}
		
		public boolean matchesType(Object o) {
			switch (valueType)
			{
			case STRING:
				return (o instanceof String);
			case INT:
				return (o instanceof Integer);
			case DOUBLE:
				return (o instanceof Double);
			case BOOL:
				return (o instanceof Boolean);
			}
			
			return false;
		}
		
		public String getStringValue() {
			if (valueType == ValueType.STRING)
				return (String) value;
			
			return null;
		}
		
		public Integer getIntValue() {
			if (valueType == ValueType.INT)
				return (Integer) value;
			
			return null;
		}
		
		public Double getDoubleValue() {
			if (valueType == ValueType.STRING)
				return (Double) value;
			
			return null;
		}
		
		public Boolean getBooleanValue() {
			if (valueType == ValueType.BOOL)
				return (Boolean) value;
			
			return null;
		}
		
		@Override
		public String toString() {
			return this.value.toString();
		}
		
		/**
		 * Sets value if types match. Otherwise does nothing
		 * @param newValue
		 */
		public void setValue(Object newValue) {
			if (this.matchesType(newValue))
				this.value = newValue;
		}
		
		/**
		 * Deserializes a config value.
		 * Uses set valueType to deserialize. If they don't match,
		 * nothing happens.
		 * @param serial
		 */
		public void setValueFromString(String serial) {
			switch (valueType) {
			case STRING:
				this.value = serial;
				break;
			case INT:
				try {
					this.value = Integer.parseInt(serial);
				} catch (NumberFormatException e) {
					; // do nothing
				}
				break;
			case DOUBLE:
				try {
					this.value = Double.parseDouble(serial);
				} catch (NumberFormatException e) {
					; // do nothing
				}
				break;
			case BOOL:
				if (serial.equalsIgnoreCase("true"))
					this.value = Boolean.TRUE;
				else if (serial.equalsIgnoreCase("false"))
					this.value = Boolean.FALSE;
				break;
			}
		}
	}
	
	protected Map<T, ConfigValue> values;
	protected Config() {
		values = new HashMap<T, ConfigValue>();
		setupDefaults();
	}
	
	protected void setValue(T key, Object value) {
		values.put(key, new ConfigValue(value));
	}
	
	public void setValue(String key, Object value) {
		T typeKey = getKey(key);
		if (typeKey == null)
			return;
		
		this.setValue(typeKey, value);
	}
	
	public boolean getBool(T key) {
		try {
			return values.get(key).getBooleanValue();
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("Caught null on key: " + key.name());
			return false;
		}
	}
	
	public String getString(T key) {
		return values.get(key).getStringValue();
	}
	
	public int getInt(T key) {
		return values.get(key).getIntValue();
	}
	
	public double getDouble(T key) {
		return values.get(key).getDoubleValue();
	}
	
	public Object getValue(String keyName) {
		T key = getKey(keyName);
		ValueType type = null;
		if (key != null)
			type = getFieldType(key);
		
		if (type == null)
			return null;
		
		switch (type) {
		case BOOL:
			return getBool(key);
		case DOUBLE:
			return getDouble(key);
		case INT:
			return getInt(key);
		case STRING:
			return getString(key);
		}
		
		return null;
	}
	
	protected abstract void setupDefaults();
	
	public void resetAllValues() {
		setupDefaults();
	}
	
	/**
	 * Parse serialized key and returns it.
	 * Return null on error.
	 * All keys are expected to have an entry in value map
	 * @param serial
	 * @return
	 */
	protected abstract T getKey(String serial);
	
	/**
	 * Return string version of the provided key
	 * @param key
	 * @return
	 */
	protected abstract String serializeKey(T key);
	
	/**
	 * Return list of all keys of values.
	 * This is a change to order them however they should be ordered.
	 * @return
	 */
	public abstract List<T> getKeyList();
	
	public List<String> getKeyNames() {
		List<T> l = getKeyList();
		List<String> ret = new ArrayList<>(l.size());
		for (T t : l)
			ret.add(serializeKey(t));
		
		return ret;
	}
	
	/**
	 * Returns any comments related to provided key
	 * @param key
	 * @return comments as a string, or null
	 */
	public abstract List<String> getComments(T key);
	
	public List<String> getComments(String keyName) {
		T key = getKey(keyName);
		if (key == null)
			return null;
		
		return getComments(key);
	}
	
	protected void readValue(String inputLine)
	{
		// Expects line in the format:
		// keyname: value
		int pos = inputLine.indexOf(':');
		
		if (-1 == pos)
		{
			System.err.println("Could not parse config line: " + inputLine);
			return;
		}
		
		String keyName = inputLine.substring(0, pos);
		String valueString =  inputLine.substring(pos + 1).trim();
		
		T key = getKey(keyName);
		
		if (null != key)
			values.get(key).setValueFromString(valueString);
	}
	
	protected String writeValue(T key)
	{
		String keyString = serializeKey(key);
		ConfigValue val = values.get(key);
		String valueString = val.toString();
		
		return keyString + ": " + valueString;
	}
	
	public void writeToFile(File outFile) throws FileNotFoundException
	{
		PrintWriter writer = new PrintWriter(outFile);
		
		List<T> keyList = getKeyList();
		List<String> comments;
		for (T key : keyList)
		{
			comments = getComments(key);
			if (comments != null && !comments.isEmpty())
			{
				writer.println(OUT_COMMENT_GUARD);
				
				for (String comment : comments)
				{
					writer.println(OUT_COMMENT_PREFIX + " " + comment);
				}

				writer.println(OUT_COMMENT_GUARD);
			}
			
			writer.println(writeValue(key));
			writer.println();
		}
		
		writer.close();
	}
	
	public void readFromFile(File inFile) throws IOException
	{
		// Assumes already have defaults created in values map!
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		
		String line;
		while (reader.ready())
		{
			line = reader.readLine().trim();
			if (line.isEmpty() || line.startsWith("//") || line.startsWith("#"))
				continue;
			readValue(line);
		}
		
		reader.close();
	}
	
	public ValueType getFieldType(String keyName) {
		T key = getKey(keyName);
		if (key == null)
			return null;
		
		return getFieldType(key);
	}
	
	public ValueType getFieldType(Object key) {
		if (values.containsKey(key))
			return values.get(key).getType();
		
		return null;
	}
	
}
