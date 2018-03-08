package com.rcextract.minecord.sql;

import java.util.ArrayList;
import java.util.Collection;

public class SQLList<T extends DatabaseSerializable> extends ArrayList<T> {

	private static final long serialVersionUID = 790505730580995123L;
	
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
