package com.rcextract.minecord.utils;

import java.util.Map;

public class Pair<K, V> implements Map.Entry<K, V> {

	private K key;
	private V value;

	/**
	 * Constructs a blank new <code>Pair</code>.
	 */
	public Pair() {}
	
	/**
	 * Constructs a new <code>Pair</code> with default values.
	 * @param key The key.
	 * @param value The value.
	 */
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	/**
	 * Sets the key.
	 * @param key The key.
	 * @return The old key.
	 */
	public K setKey(K key) {
		K oldkey = this.key;
		this.key = key;
		return oldkey;
	}
	
	@Override
	public V setValue(V value) {
		V oldvalue = this.value;
		this.value = value;
		return oldvalue;
	}

}
