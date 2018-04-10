package com.rcextract.minecord.utils;

import java.util.Collection;
import java.util.function.Predicate;

import org.apache.commons.lang.Validate;

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
public class ComparativeSet<T> extends EnhancedSet<T> implements Predicate<T>, AutoCloseable {
	
	private static final long serialVersionUID = -2222981329974385110L;

	private Predicate<? super T> filter;
	private boolean closed;
	
	/**
	 * Constructs a ComparativeSet with a filter.
	 */
	public ComparativeSet() {}

	/**
	 * Constructs a ComparativeSet with a collection of elements after filtering.
	 * @param collection The collection of elements.
	 */
	public ComparativeSet(Collection<T> collection) {
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
	 * Sets the filter.
	 * @param filter The filter.
	 * @throws IllegalStateException Thrown when this set is closed.
	 */
	public void setFilter(Predicate<? super T> filter) {
		if (closed) throw new IllegalStateException();
		Validate.notNull(filter);
		this.filter = filter;
	}

	/**
	 * Adds a new element after filtering. If the element matches the requirements of 
	 * the filter, it will be added.
	 * @return Whether the element is added.
	 */
	@Override
	public boolean add(T t) {
		if (filter.test(t)) 
			return super.add(t);
		return false;
	}

	@Override
	public ComparativeSet<T> clone() {
		return (ComparativeSet<T>) super.clone();
	}

	@Override
	public boolean test(T t) {
		return filter.test(t);
	}

	@Override
	public void close() {
		closed = true;
	}
	
}
