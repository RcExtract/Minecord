package com.rcextract.minecord;

import java.util.List;

@Deprecated
public interface RecordManager<T> {

	public List<T> getRecords();
	public <E extends T> List<E> getRecords(Class<E> clazz);
	public T getLatestRecord();
	public <E extends T> E getLatestRecord(Class<E> clazz);
	public T getOldestRecord();
	public <E extends T> E getOldestRecord(Class<E> clazz);
}
