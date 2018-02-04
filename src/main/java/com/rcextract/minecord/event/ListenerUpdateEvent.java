package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Listener;

public class ListenerUpdateEvent extends MinecordEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private final Listener listener;
	private boolean notify;
	private int index;
	public ListenerUpdateEvent(Listener listener, boolean notify, int index) {
		this.listener = listener;
		this.notify = notify;
		this.index = index;
	}
	public Listener getListener() {
		return listener;
	}
	public boolean isNotify() {
		return notify;
	}
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
