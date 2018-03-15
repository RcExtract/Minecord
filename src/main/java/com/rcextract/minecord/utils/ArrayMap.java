package com.rcextract.minecord.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.apache.commons.lang.Validate;

public class ArrayMap<K, V> implements Serializable {

	private static final long serialVersionUID = -8140432280629411740L;

	public static <A, B> ArrayMap<A, B> create(Map<A, B> map) {
		return new ArrayMap<A, B>(map);
	}
	
	private List<Pair<K, V>> list;
	
	public ArrayMap() {
		list = new ArrayList<Pair<K, V>>();
	}
	
	public ArrayMap(Map<? extends K, ? extends V> map) {
		list = new ArrayList<Pair<K, V>>();
		putAll(map);
	}
	
	public int size() {
		return list.size();
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public boolean containsKey(Object key) {
		for (Pair<K, V> pair : list) 
			if (pair.getKey() == key) 
				return true;
		return false;
	}
	
	public boolean containsValue(Object value) {
		for (Pair<K, V> pair : list) 
			if (pair.getValue() == value) 
				return true;
		return false;
	}
	
	public int indexOf(Object key, Object value) {
		for (Pair<K, V> pair : list) 
			if (pair.getKey() == key && pair.getValue() == value) 
				return list.indexOf(pair);
		return -1;
	}
	
	public int lastIndexOf(Object key, Object value) {
		for (int i = list.size() - 1; i >= 0; i--) {
			Pair<K, V> pair = list.get(i);
			if (pair.getKey() == key && pair.getValue() == value) 
				return i;
		}
		return -1;
	}
	
	public boolean containsAll(Map<?, ?> map) {
		for (Map.Entry<?, ?> entry : map.entrySet()) 
			if (indexOf(entry.getKey(), entry.getValue()) == -1) 
				return false;
		return true;
	}
	
	public Pair<K, V> get(int index) {
		return list.get(index);
	}
	
	public int indexOf(Object key) {
		return keyList().indexOf(key);
	}
	
	public int lastIndexOf(Object key) {
		return keyList().lastIndexOf(key);
	}
	
	public void put(K key, V value) {
		put(list.size(), key, value);
	}
	
	public void put(int index, K key, V value) {
		list.add(index, new Pair<K, V>(key, value));
	}
	
	public void putAll(Map<? extends K, ? extends V> map) {
		map.forEach((key, value) -> put(key, value));
	}
	
	public void putAll(int index, Map<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(index, entry.getKey(), entry.getValue());
			index++;
		}
	}
	
	public List<V> removeAll(Object key) {
		List<V> vs = new ArrayList<V>();
		list.forEach(pair -> {
			if (pair.getKey() == key) {
				vs.add(pair.getValue());
				remove(indexOf(key));
			}
		});
		return vs;
	}

	public ArrayMap<K, V> removeAll(int fromIndex, int toIndex) {
		ArrayMap<K, V> map = subMap(fromIndex, toIndex);
		for (;fromIndex <= toIndex; fromIndex++) 
			list.remove(fromIndex);
		return map;
	}
	
	public Pair<K, V> remove(int index) {
		return list.remove(index);
	}
	
	public void clear() {
		list.clear();
	}
	
	public List<K> keyList() {
		List<K> keys = new ArrayList<K>();
		list.forEach(pair -> keys.add(pair.getKey()));
		return keys;
	}
	
	public List<V> valueList() {
		List<V> values = new ArrayList<V>();
		list.forEach(pair -> values.add(pair.getValue()));
		return values;
	}
	
	public List<Pair<K, V>> entryList() {
		return list;
	}
	
	public void removeAll(Map<? extends K, ? extends V> map) {
		list.removeIf(pair -> map.containsKey(pair.getKey()) && map.containsValue(pair.getValue()));
	}
	
	public void removeIf(BiPredicate<? super K, ? super V> filter) {
		list.removeIf(pair -> filter.test(pair.getKey(), pair.getValue()));
	}
	
	public void retainAll(Map<? extends K, ? extends V> map) {
		list.removeIf(pair -> !(map.containsKey(pair.getKey()) || map.containsValue(pair.getValue())));
	}
	
	public void retainIf(BiPredicate<? super K, ? super V> filter) {
		list.removeIf(pair -> !(filter.test(pair.getKey(), pair.getValue())));
	}
	
	public K setKey(int index, K key) {
		K oldkey = get(index).getKey();
		list.get(index).setKey(key);
		return oldkey;
	}
	
	public V setValue(int index, V value) {
		V oldvalue = get(index).getValue();
		list.get(index).setValue(value);
		return oldvalue;
	}
	
	public List<V> setAll(K key, V value) {
		List<V> vs = new ArrayList<V>();
		list.forEach(pair -> {
			if (pair.getKey() == key) {
				vs.add(pair.getValue());
				pair.setValue(value);
			}
		});
		return vs;
	}
	
	public ArrayMap<K, V> subMap(int fromIndex, int toIndex) {
		ArrayMap<K, V> map = new ArrayMap<K, V>();
		for (;fromIndex <= toIndex; fromIndex++) 
			map.put(get(fromIndex).getKey(), get(fromIndex).getValue());
		return map;
	}
	
	public <F, S> ArrayMap<F, S> apply(Function<? super K, F> key, Function<? super V, S> value) {
		Validate.notNull(key);
		Validate.notNull(value);
		ArrayMap<F, S> map = new ArrayMap<F, S>();
		list.forEach(pair -> 
			map.put(key.apply(pair.getKey()), value.apply(pair.getValue()))
		);
		return map;
	}
	
	public boolean equalsIgnoreOrder(Map<?, ?> map) {
		return containsAll(map) && map.entrySet().containsAll(list);
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ArrayMap)) return false;
		return list.equals(((ArrayMap<?, ?>) object).entryList());
	}
	
	public Map<K, V> toMap() {
		Map<K, V> map = new HashMap<K, V>();
		list.forEach(pair -> map.put(pair.getKey(), pair.getValue()));
		return map;
	}
	
	public void forEach(BiConsumer<? super K, ? super V> action) {
		list.forEach(pair -> action.accept(pair.getKey(), pair.getValue()));
	}
	
	public ArrayMap<K, V> getIf(BiPredicate<? super K, ? super V> filter) {
		ArrayMap<K, V> map = new ArrayMap<K, V>();
		list.forEach(pair -> {
			if (filter.test(pair.getKey(), pair.getValue())) 
				map.put(pair.getKey(), pair.getValue());
		});
		return map;
	}
}
