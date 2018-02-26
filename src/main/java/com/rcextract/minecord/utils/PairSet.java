package com.rcextract.minecord.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PairSet<T> extends Pair<T, Collection<T>> {
	
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	public PairSet() {}

	@SafeVarargs
	public PairSet(T main, T ... elements) {
		super(main, new HashSet<T>(Arrays.asList(elements)));
	}
	
	public int size(boolean main) {
		return super.getValue().size() + (main ? 1 : 0);
	}

	public boolean isEmpty(boolean main) {
		return size(main) == 0;
	}

	public boolean contains(Object o, boolean main) {
		return (main ? super.getKey() == o : false) || super.getValue().contains(o);
	}

	public Iterator<T> iterator(boolean main) {
		if (!(main)) return super.getValue().iterator();
		return completeView().iterator();
	}

	public Object[] toArray(boolean main) {
        Object[] r = new Object[size(main)];
        Iterator<T> it = iterator(main);
        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) 
                return Arrays.copyOf(r, i);
            r[i] = it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
	}

	@SuppressWarnings("unchecked")
	public <E> E[] toArray(E[] type, boolean main) {
        int size = size(main);
        E[] r = type.length >= size ? type : (E[]) Array.newInstance(type.getClass().getComponentType(), size);
        Iterator<T> it = iterator(main);
        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) {
                if (type == r) {
                    r[i] = null;
                } else if (type.length < i) {
                    return Arrays.copyOf(r, i);
                } else {
                    System.arraycopy(r, 0, type, 0, i);
                    if (type.length > i) {
                        type[i] = null;
                    }
                }
                return type;
            }
            r[i] = (E) it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
		int i = r.length;
		while (it.hasNext()) {
			int cap = r.length;
			if (i == cap) {
				int newCap = cap + (cap >> 1) + 1;
				if (newCap - MAX_ARRAY_SIZE > 0) 
					newCap = hugeCapacity(cap + 1);
				r = Arrays.copyOf(r, newCap);
				}
			r[i++] = (T)it.next();
			}
		return (i == r.length) ? r : Arrays.copyOf(r, i);
	}
	
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError
                ("Required array size too large");
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
	
	public boolean add(T t) {
		return super.getValue().add(t);
	}

	public boolean remove(Object o) {
		return super.getValue().remove(o);
	}

	public boolean containsAll(Collection<?> c, boolean main) {
		return main ? completeView().containsAll(c) : super.getValue().containsAll(c);
	}

	public boolean addAll(Collection<? extends T> c) {
		return super.getValue().addAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clear(boolean main) {
		super.getValue().clear();
		if (main) super.setKey(null);
	}

	public Set<T> completeView() {
		Set<T> set = new HashSet<T>(super.getValue());
		set.add(super.getKey());
		return set;
	}
}
