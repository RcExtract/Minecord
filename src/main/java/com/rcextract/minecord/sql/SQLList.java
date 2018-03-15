package com.rcextract.minecord.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SQLList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 790505730580995123L;

	@SuppressWarnings("unchecked")
	protected static <E> SQLList<E> fromArray(E[] e) {
		return create((Class<E>) e.getClass().getComponentType(), Arrays.asList(e));
	}
	protected static <E> SQLList<E> create(Class<E> clazz, Collection<E> collection) {
		return new SQLList<E>(clazz, collection);
	}
	protected static <E> SQLList<E> create(Class<E> clazz) {
		return new SQLList<E>(clazz);
	}
	
	private final Class<T> clazz;
	public SQLList(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}
	public SQLList(Class<T> clazz, Collection<T> collection) {
		super(collection);
		this.clazz = clazz;
	}
	public Class<T> getDeclaringClass() {
		return clazz;
	}
}
