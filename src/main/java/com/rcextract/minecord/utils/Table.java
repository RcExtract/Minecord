package com.rcextract.minecord.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A flexible <code>Table<code> with three columns. Unlike 
 * {@see com.google.common.collect.Table}, this table does not treat entries like it or
 * {@see java.util.Map}. Instead, it allows all entries except for ones that has the 
 * same A, B, and C values in another entries, which means if two entries are compared 
 * with {@link Object#equals(Object)} and it returns true, the entry will not be put.
 * This leads to a situation which a table can have entries with the same A, same B, 
 * same C, same A and same B, same B and same C, ans same A and same C.
 *
 * @param <A> Column a.
 * @param <B> Column b.
 * @param <C> Column c.
 */
public class Table<A, B, C> {

	/**
	 * An entry object dedicated for <code>Table</code>, used to store values.
	 *
	 * @param <A> Column a.
	 * @param <B> Column b.
	 * @param <C> Column c.
	 */
	public static class Entry<A, B, C> {
		private A a;
		private B b;
		private C c;
		/**
		 * Constructs a new entry with default values
		 * @param a The value at column a.
		 * @param b The value at column b.
		 * @param c The value at column c.
		 */
		public Entry(A a, B b, C c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
		/**
		 * Gets the value at column a.
		 * @return The value at column a.
		 */
		public A getA() {
			return a;
		}
		/**
		 * Sets the value at column a.
		 * @param a The value at column a.
		 * @return The old value at column a.
		 */
		public A setA(A a) {
			A old = this.a;
			this.a = a;
			return old;
		}
		/**
		 * Gets the value at column b.
		 * @return The value at column b.
		 */
		public B getB() {
			return b;
		}
		/**
		 * Sets the value at column b.
		 * @param b The value at column b.
		 * @return The old value at column b.
		 */
		public B setB(B b) {
			B old = this.b;
			this.b = b;
			return old;
		}
		/**
		 * Gets the value at column c.
		 * @return The value at column c.
		 */
		public C getC() {
			return c;
		}
		/**
		 * Sets the value at column c.
		 * @param c The value at column c.
		 * @return The old value at column c.
		 */
		public C setC(C c) {
			C old = this.c;
			this.c = c;
			return old;
		}
	}
	
	private final Set<Entry<A, B, C>> entries;
	
	/**
	 * Constructs a blank new Table.
	 */
	public Table() {
		this.entries = new HashSet<Entry<A, B, C>>();
	}
	/**
	 * Constructs a new Table with default entries imported from another Table.
	 */
	public Table(Table<A, B, C> table) {
		this.entries = new HashSet<Entry<A, B, C>>(table.getEntries());
	}

	/**
	 * Get all entries.
	 * @return All entries.
	 */
	public Set<Entry<A, B, C>> getEntries() {
		return Collections.unmodifiableSet(entries);
	}

	/**
	 * Gets all values at column a. The integer followed is the count of the value 
	 * appeared in such column.
	 * @return All values at column a.
	 */
	public Map<A, Integer> aSet() {
		Map<A, Integer> am = new HashMap<A, Integer>();
		for (Entry<A, B, C> entry : entries) 
			if (am.containsKey(entry.getA())) 
				am.put(entry.getA(), am.get(entry.getA()) + 1);
			else
				am.put(entry.getA(), 1);
		return am;
	}

	/**
	 * Gets all values at column b. The integer followed is the count of the value 
	 * appeared in such column.
	 * @return All values at column b.
	 */
	public Map<B, Integer> BSet() {
		Map<B, Integer> bm = new HashMap<B, Integer>();
		for (Entry<A, B, C> entry : entries) 
			if (bm.containsKey(entry.getB())) 
				bm.put(entry.getB(), bm.get(entry.getB()) + 1);
			else
				bm.put(entry.getB(), 1);
		return bm;
	}

	/**
	 * Gets all values at column c. The integer followed is the count of the value 
	 * appeared in such column.
	 * @return All values at column c.
	 */
	public Map<C, Integer> cSet() {
		Map<C, Integer> cm = new HashMap<C, Integer>();
		for (Entry<A, B, C> entry : entries) 
			if (cm.containsKey(entry.getC())) 
				cm.put(entry.getC(), cm.get(entry.getC()) + 1);
			else
				cm.put(entry.getC(), 1);
		return cm;
	}

	/**
	 * Gets all values at column b and c, provided that their associating entry has the
	 * same value at column a as provided.
	 * @param a The parameter.
	 * @return All values at column b and c, provided that their associating entry has the
	 * same value at column a as provided.
	 */
	public Map<B, C> a(A a) {
		Map<B, C> map = new HashMap<B, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a) 
				map.put(entry.getB(), entry.getC());
		return map;
	}

	/**
	 * Gets all values at column a and c, provided that their associating entry has the
	 * same value at column b as provided.
	 * @param b The parameter.
	 * @return All values at column a and c, provided that their associating entry has the
	 * same value at column b as provided.
	 */
	public Map<A, C> b(B b) {
		Map<A, C> map = new HashMap<A, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getB() == b) 
				map.put(entry.getA(), entry.getC());
		return map;
	}

	/**
	 * Gets all values at column a and b, provided that their associating entry has the
	 * same value at column c as provided.
	 * @param c The parameter.
	 * @return All values at column a and b, provided that their associating entry has the
	 * same value at column c as provided.
	 */
	public Map<A, B> c(C c) {
		Map<A, B> map = new HashMap<A, B>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getC() == c) 
				map.put(entry.getA(), entry.getB());
		return map;
	}

	/**
	 * Gets all values at column c, provided that their associating entry has the same 
	 * values at column a and b as provided.
	 * @param a The parameter.
	 * @param b The parameter.
	 * @return All values at column c, provided that their associating entry has the
	 * same value at column a and b as provided.
	 */
	public Set<C> ab(A a, B b) {
		Set<C> set = new HashSet<C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getB() == b) 
				set.add(entry.getC());
		return set;
	}

	/**
	 * Gets all values at column a, provided that their associating entry has the same 
	 * values at column b and c as provided.
	 * @param b The parameter.
	 * @param c The parameter.
	 * @return All values at column a, provided that their associating entry has the
	 * same value at column b and c as provided.
	 */
	public Set<A> bc(B b, C c) {
		Set<A> set = new HashSet<A>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getB() == b && entry.getC() == c) 
				set.add(entry.getA());
		return set;
	}

	/**
	 * Gets all values at column b, provided that their associating entry has the same 
	 * values at column a and c as provided.
	 * @param a The parameter.
	 * @param c The parameter.
	 * @return All values at column b, provided that their associating entry has the
	 * same value at column a and c as provided.
	 */
	public Set<B> ac(A a, C c) {
		Set<B> set = new HashSet<B>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getC() == c) 
				set.add(entry.getB());
		return set;
	}
	
	/**
	 * Determines if there is an entry with the same values as provided.
	 * <p>
	 * This method is like the following:
	 * <code>
	 * public boolean abc(A a, B b, C c);
	 * </code>
	 * It is like the other value getting methods. But this one returns a boolean 
	 * value because there is no more value of columns to return.
	 * @param a The parameter.
	 * @param b The parameter.
	 * @param c The parameter.
	 * @return If there is an entry with teh same values as provided.
	 */
	public boolean contains(A a, B b, C c) {
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getB() == b && entry.getC() == c) 
				return true;
		return false;
	}

	/**
	 * Removes all entries, provided that there value at column a is the same as 
	 * provided.
	 * @param a The parameter.
	 * @return The removed entries.
	 */
	public Table<A, B, C> removeA(A a) {
		Table<A, B, C> table = new Table<A, B, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a) {
				table.put(entry.getA(), entry.getB(), entry.getC());
				entries.remove(entry);
			}
		return table;
	}

	/**
	 * Removes all entries, provided that there value at column b is the same as 
	 * provided.
	 * @param b The parameter.
	 * @return The removed entries.
	 */
	public Table<A, B, C> removeB(B b) {
		Table<A, B, C> table = new Table<A, B, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getB() == b) {
				table.put(entry.getA(), entry.getB(), entry.getC());
				entries.remove(entry);
			}
		return table;
	}
	
	/**
	 * Removes all entries, provided that there value at column c is the same as 
	 * provided.
	 * @param c The parameter.
	 * @return The removed entries.
	 */
	public Table<A, B, C> removeC(C c) {
		Table<A, B, C> table = new Table<A, B, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getC() == c) {
				table.put(entry.getA(), entry.getB(), entry.getC());
				entries.remove(entry);
			}
		return table;
	}
	
	/**
	 * Removes all entries, provided that there value at column a and b is the same as 
	 * provided.
	 * @param a The parameter.
	 * @param b The parameter.
	 * @return The removed entries.
	 */
	public Table<A, B, C> removeAB(A a, B b) {
		Table<A, B, C> table = new Table<A, B, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getB() == b) {
				table.put(entry.getA(), entry.getB(), entry.getC());
				entries.remove(entry);
			}
		return table;
	}
	
	/**
	 * Removes all entries, provided that there value at column a and c is the same as 
	 * provided.
	 * @param a The parameter.
	 * @param c The parameter.
	 * @return The removed entries.
	 */
	public Table<A, B, C> removeAC(A a, C c) {
		Table<A, B, C> table = new Table<A, B, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getC() == c) {
				table.put(entry.getA(), entry.getB(), entry.getC());
				entries.remove(entry);
			}
		return table;
	}
	
	/**
	 * Removes all entries, provided that there value at column b and c is the same as 
	 * provided.
	 * @param b The parameter.
	 * @param c The parameter.
	 * @return The removed entries.
	 */
	public Table<A, B, C> removeBC(B b, C c) {
		Table<A, B, C> table = new Table<A, B, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getB() == b && entry.getC() == c) {
				table.put(entry.getA(), entry.getB(), entry.getC());
				entries.remove(entry);
			}
		return table;
	}

	/**
	 * Removes an entry provided that all values are the same as provided.
	 * @param a The parameter.
	 * @param b The parameter.
	 * @param c The parameter.
	 * @return The removed entries.
	 */
	public boolean remove(A a, B b, C c) {
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getB() == b && entry.getC() == c) 
				return entries.remove(entry);
		return false;
	}

	/**
	 * Removes all entries, provided that all of their values match the requirements 
	 * provided. Put null for the predicate if you don't want to limit the value on 
	 * that column.
	 * @param a The predicate for column a.
	 * @param b The predicate for column b.
	 * @param c The predicate for column c.
	 * @return The removed entries.
	 */
	public Table<A, B, C> removeIf(Predicate<? super A> a, Predicate<? super B> b, Predicate<? super C> c) {
		Table<A, B, C> table = new Table<A, B, C>();
		for (Entry<A, B, C> entry : entries) {
			boolean remove = a == null ? true : a.test(entry.getA());
			if (b != null) remove = remove && b.test(entry.getB());
			if (c != null) remove = remove && c.test(entry.getC());
			if (a == null && b == null && c == null) remove = false;
			if (!(remove)) continue;
			entries.remove(entry);
		}
		return table;
	}
	
	/**
	 * Puts a new entry into the table, provided that there isn't any existing entry 
	 * that has the same values as provided.
	 * @param a The value at column a.
	 * @param b The value at column b.
	 * @param c The value at column c.
	 * @return Whether if an entry with the same values as provided exists, meaning 
	 * whether if the a new entry with the values provided is put.
	 */
	public boolean put(A a, B b, C c) {
		if (contains(a, b, c)) return false;
		entries.add(new Entry<A, B, C>(a, b, c));
		return true;
	}
	
	/**
	 * Puts all entries inside the table provided to this table. Any entry that has 
	 * the same value as an entry in this table will not be added.
	 * <p>
	 * This method simply loops through all the entries in the table provided and call 
	 * {@link #put(Object, Object, Object)}.
	 * @param table
	 */
	public void putAll(Table<? extends A, ? extends B, ? extends C> table) {
		table.getEntries().forEach(entry -> {
			put(entry.getA(), entry.getB(), entry.getC());
		});
	}
	
	/**
	 * Returns the entries count of this table. Same as 
	 * <code>getEntries().size()</code>.
	 * @return The entries count of this table.
	 */
	public int size() {
		return entries.size();
	}
	
    /**
     * Performs the given actions for each value of each column in an entry of the 
     * {@code Table} until all entries have been processed or the action throws an
     * exception. Put null on the Consumer if you don't want to perform any action on 
     * values in that column.
     * @param a The action to be performed for values at column a.
     * @param b The action to be performed for values at column b.
     * @param c The action to be performed for values at column c.
     * @throws NullPointerException if the specified action is null.
     */
	public void forEach(Consumer<? super A> a, Consumer<? super B> b, Consumer<? super C> c) {
		entries.forEach(entry -> {
			if (a != null) a.accept(entry.getA());
			if (b != null) b.accept(entry.getB());
			if (c != null) c.accept(entry.getC());
		});
	}
}
