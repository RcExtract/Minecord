package com.rcextract.minecord.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This is a subclass of HashSet<T> which allows implementation to add filter and 
 * prevent some elements from being added. If the element matches the requirements of 
 * the filter, it will be added. The filter also applies on the pre-added elements. 
 * Also, this class provides {@link #getIf(Predicate)} to get elements that matches 
 * the requirements of the filter provided through the parameter.
 * 
 * @param <T> The type of elements.
 * 
 * @since 1.1.0 SNAPSHOT 1.0
 */
public class ComparativeSet<T> extends HashSet<T> {
	
	private static final long serialVersionUID = -2222981329974385110L;

	private final Predicate<? super T> filter;
	
	/**
	 * Constructs a ComparativeSet with a filter.
	 * @param filter The filter.
	 */
	public ComparativeSet(Predicate<? super T> filter) {
		this.filter = filter;
	}

	/**
	 * Constructs a ComparativeSet with a collection of elements after filtering.
	 * @param filter The filter.
	 * @param collection The collection of elements.
	 */
	public ComparativeSet(Predicate<? super T> filter, Collection<T> collection) {
		this.filter = filter;
		collection.forEach(element -> {
			if (!(filter.test(element))) super.add(element);
		});
	}
	
	/**
	 * Gets the filter.
	 * @return The filter.
	 */
	public Predicate<? super T> getFilter() {
		return filter;
	}

	/**
	 * Adds a new element after filtering. If the element matches the requirements of 
	 * the filter, it will be added.
	 * @return Whether the element is added.
	 */
	@Override
	public boolean add(T t) {
		if (filter.test(t)) return super.add(t);
		return false;
	}

	/**
	 * Returns all the elements which match the requirements of the filter.
	 * @param filter The filter.
	 * @return All the elements which match the requirements of the filter.
	 */
	public Set<T> getIf(Predicate<? super T> filter) {
		Set<T> set = new HashSet<T>();
		forEach((T t) -> { if (filter.test(t)) set.add(t); });
		return set;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ComparativeSet<T> clone() {
		return (ComparativeSet<T>) super.clone();
	}
}
