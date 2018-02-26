package com.rcextract.minecord.utils;

import java.util.Map;

public class Pair<K, V> implements Map.Entry<K, V> {

	private K key;
	private V value;

	public Pair() {}
	
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
