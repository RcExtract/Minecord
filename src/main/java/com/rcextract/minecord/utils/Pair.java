package com.rcextract.minecord.utils;

import java.util.Map;

public class Pair<K, V> implements Map.Entry<K, V> {

	private K key;
	private V value;
	@Override
	public K getKey() {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public V getValue() {
		// TODO Auto-generated method stub
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
