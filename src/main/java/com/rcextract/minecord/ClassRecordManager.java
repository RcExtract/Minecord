package com.rcextract.minecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a record manager of any Object.
 */
@Deprecated
public class ClassRecordManager<T> {

	private List<T> events = new ArrayList<T>();
	public void record(T event) {
		events.add(event);
	}
	public void clear() {
		events.clear();
	}
	public List<T> getRecords() {
		return events;
	}
	public T getLatestRecord() {
		return events.get(events.size() - 1);
	}
	public T getLatestRecord(Class<? extends T> type) {
		for (int i = events.size() - 1; i >= 0; i++) {
			T event = events.get(i);
			if (type.isInstance(event)) {
				return event;
			}
		}
		return null;
	}
	public T getOldestRecord() {
		return events.get(0);
	}
	public T getOldestRecord(Class<? extends T> type) {
		for (T event : events) {
			if (type.isInstance(event)) {
				return event;
			}
		}
		return null;
	}
}