package com.rcextract.minecord.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
public class ComparativeList<T> extends ArrayList<T> {
	
	private static final long serialVersionUID = -2222981329974385110L;

	private T last;
	private final Predicate<? super T> filter;
	
	/**
	 * Constructs a ComparativeSet with a filter.
	 * @param filter The filter.
	 */
	public ComparativeList(Predicate<? super T> filter) {
		this.filter = filter;
	}

	/**
	 * Constructs a ComparativeSet with a collection of elements after filtering.
	 * @param filter The filter.
	 * @param collection The collection of elements.
	 */
	public ComparativeList(Predicate<? super T> filter, Collection<T> collection) {
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
		add(super.size(), t);
		return true;
	}
	
	@Override
	public void add(int index, T t) {
		if (filter.test(t)) 
			super.add(index, t);
	}

	/**
	 * Returns all the elements which match the requirements of the filter, in a range 
	 * of elements.
	 * @param filter The filter.
	 * @param fromIndex The inclusive starting index.
	 * @param toIndex The exclusive ending index.
	 * @return All the elements which match the requirements of the filter and in range.
	 */
	public List<T> getIf(Predicate<? super T> filter, int fromIndex, int toIndex) {
		List<T> list = new ArrayList<T>();
		forEach((T t) -> { if (filter.test(t) && indexOf(t) >= fromIndex && indexOf(t) < toIndex) list.add(t); });
		return list;
	}
	
	public List<T> getIf(Predicate<? super T> filter) {
		return getIf(filter, 0, super.size());
	}
	
	public T getLast() {
		return last;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComparativeList<T> clone() {
		return (ComparativeList<T>) super.clone();
	}
}
