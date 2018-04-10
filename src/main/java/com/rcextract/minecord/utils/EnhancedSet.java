package com.rcextract.minecord.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

public class EnhancedSet<T> extends HashSet<T> {

	private static final long serialVersionUID = -4004081408851334978L;
	
	public EnhancedSet() {
		super();
	}
	
	public EnhancedSet(Collection<T> collection) {
		super(collection);
	}

	/**
	 * Returns the unique value when the size of this set is 1, otherwise null.
	 * @return The unique value when the size of this set is 1, otherwise null.
	 */
	public T get() {
		if (super.size() == 1) return super.iterator().next();
		return null;
	}

	/**
	 * Returns all the elements which match the requirements of the filter.
	 * @param filter The filter.
	 * @return All the elements which match the requirements of the filter.
	 */
	public EnhancedSet<T> getIf(Predicate<? super T> filter) {
		EnhancedSet<T> set = new EnhancedSet<T>();
		super.forEach(t -> { if (filter.test(t)) set.add(t); });
		return set;
	}
	
	public boolean containsIf(Predicate<? super T> filter) {
		for (T t : this) if (filter.test(t)) return true;
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public EnhancedSet<T> clone() {
		return (EnhancedSet<T>) super.clone();
	}

}
