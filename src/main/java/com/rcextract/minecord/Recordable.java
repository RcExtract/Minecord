package com.rcextract.minecord;

/**
 * Represents a class that it records the action related to itself.
 * @param <T> The type of the event representing the action.
 */
@Deprecated
public interface Recordable<T> extends RecordManager<T> {

	/**
	 * Writes a new record.
	 * @param t
	 */
	public void record(T t);
	public void clear();
}
