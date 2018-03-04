package com.rcextract.minecord.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class Table<A, B, C> {

	public static class Entry<A, B, C> {
		private A a;
		private B b;
		private C c;
		public Entry(A a, B b, C c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
		public A getA() {
			return a;
		}
		public void setA(A a) {
			this.a = a;
		}
		public B getB() {
			return b;
		}
		public void setB(B b) {
			this.b = b;
		}
		public C getC() {
			return c;
		}
		public void setC(C c) {
			this.c = c;
		}
	}
	
	private final Set<Entry<A, B, C>> entries;
	
	public Table() {
		this.entries = new HashSet<Entry<A, B, C>>();
	}
	
	public Table(Table<A, B, C> table) {
		this.entries = new HashSet<Entry<A, B, C>>(table.getEntries());
	}

	public Set<Entry<A, B, C>> getEntries() {
		return entries;
	}

	public Map<A, Integer> aSet() {
		Map<A, Integer> am = new HashMap<A, Integer>();
		for (Entry<A, B, C> entry : entries) 
			if (am.containsKey(entry.getA())) 
				am.put(entry.getA(), am.get(entry.getA()) + 1);
			else
				am.put(entry.getA(), 1);
		return am;
	}
	
	public Map<B, Integer> BSet() {
		Map<B, Integer> bm = new HashMap<B, Integer>();
		for (Entry<A, B, C> entry : entries) 
			if (bm.containsKey(entry.getB())) 
				bm.put(entry.getB(), bm.get(entry.getB()) + 1);
			else
				bm.put(entry.getB(), 1);
		return bm;
	}
	
	public Map<C, Integer> cSet() {
		Map<C, Integer> cm = new HashMap<C, Integer>();
		for (Entry<A, B, C> entry : entries) 
			if (cm.containsKey(entry.getC())) 
				cm.put(entry.getC(), cm.get(entry.getC()) + 1);
			else
				cm.put(entry.getC(), 1);
		return cm;
	}
	
	public Map<B, C> a(A a) {
		Map<B, C> map = new HashMap<B, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a) 
				map.put(entry.getB(), entry.getC());
		return map;
	}
	
	public Map<A, C> b(B b) {
		Map<A, C> map = new HashMap<A, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getB() == b) 
				map.put(entry.getA(), entry.getC());
		return map;
	}
	
	public Map<A, B> c(C c) {
		Map<A, B> map = new HashMap<A, B>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getC() == c) 
				map.put(entry.getA(), entry.getB());
		return map;
	}
	
	public C ab(A a, B b) {
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getB() == b) 
				return entry.getC();
		return null;
	}
	
	public A bc(B b, C c) {
		for (Entry<A, B, C> entry : entries) 
			if (entry.getB() == b && entry.getC() == c) 
				return entry.getA();
		return null;
	}
	
	public B ac(A a, C c) {
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getC() == c) 
				return entry.getB();
		return null;
	}
	
	public boolean contains(A a, B b, C c) {
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getB() == b && entry.getC() == c) 
				return true;
		return false;
	}
	
	public Map<B, C> removeA(A a) {
		Map<B, C> map = new HashMap<B, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a) {
				map.put(entry.getB(), entry.getC());
				entries.remove(entry);
			}
		return map;
	}
	
	public Map<A, C> removeB(B b) {
		Map<A, C> map = new HashMap<A, C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getB() == b) {
				map.put(entry.getA(), entry.getC());
				entries.remove(entry);
			}
		return map;
	}
	
	public Map<A, B> removeC(C c) {
		Map<A, B> map = new HashMap<A, B>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getC() == c) {
				map.put(entry.getA(), entry.getB());
				entries.remove(entry);
			}
		return map;
	}
	
	public Set<C> removeAB(A a, B b) {
		Set<C> set = new HashSet<C>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getB() == b) {
				set.add(entry.getC());
				entries.remove(entry);
			}
		return set;
	}
	
	public Set<A> removeBC(B b, C c) {
		Set<A> set = new HashSet<A>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getB() == b && entry.getC() == c) {
				set.add(entry.getA());
				entries.remove(entry);
			}
		return set;
	}
	
	public Set<B> removeAC(A a, C c) {
		Set<B> set = new HashSet<B>();
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getC() == c) {
				set.add(entry.getB());
				entries.remove(entry);
			}
		return set;
	}
	
	public boolean remove(A a, B b, C c) {
		for (Entry<A, B, C> entry : entries) 
			if (entry.getA() == a && entry.getB() == b && entry.getC() == c) 
				return entries.remove(entry);
		return false;
	}
	
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
	
	public boolean put(A a, B b, C c) {
		if (contains(a, b, c)) return false;
		entries.add(new Entry<A, B, C>(a, b, c));
		return true;
	}
}
