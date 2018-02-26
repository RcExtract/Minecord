package com.rcextract.minecord.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComparativeSet<T> extends HashSet<T> {

	private static final long serialVersionUID = -2222981329974385110L;

	private final List<Pair<Method, Boolean>> methods = new ArrayList<Pair<Method, Boolean>>();
	
	@SafeVarargs
	public ComparativeSet(Class<T> clazz, Pair<String, Boolean> ... methods) throws NoSuchMethodException, SecurityException {
		super();
		for (Pair<String, Boolean> pair : methods) {
			Method method = clazz.getMethod(pair.getKey());
			if (method.getReturnType() == null) throw new IllegalArgumentException();
			this.methods.add(new Pair<Method, Boolean>(method, pair.getValue()));
		}
	}
	
	@SafeVarargs
	public ComparativeSet(Class<T> clazz, Collection<? extends T> collection, Pair<String, Boolean> ... methods) throws NoSuchMethodException, SecurityException {
		super(collection);
		for (Pair<String, Boolean> pair : methods) {
			Method method = clazz.getMethod(pair.getKey());
			if (method.getReturnType() == null) throw new IllegalArgumentException();
			this.methods.add(new Pair<Method, Boolean>(method, pair.getValue()));
		}
	}

	public List<Pair<Method, Boolean>> getMethods() {
		return methods;
	}

	public Collection<T> getElements(List<? extends Object> objects) {
		if (objects.size() != methods.size()) throw new IllegalArgumentException();
		Set<T> set = new HashSet<T>();
		for (T t : this) 
			try {
				boolean add = true;
				for (int i = 0; i < objects.size(); i++) {
					Pair<Method, Boolean> pair = methods.get(i);
					add = add && pair.getValue() ? pair.getKey().invoke(t) == objects.get(i) : pair.getKey().invoke(t).equals(objects.get(i));
				}
				if (add) set.add(t);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				continue;
			}
		return set;
	}
	
	@Override
	public boolean add(T t) {
		try {
			List<Object> objects = new ArrayList<Object>();
			for (Pair<Method, Boolean> pair : methods) 
				objects.add(pair.getKey().invoke(t));
			boolean empty = getElements(objects).isEmpty();
			if (empty) super.add(t);
			return empty;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return false;
		}
	}
	
}
