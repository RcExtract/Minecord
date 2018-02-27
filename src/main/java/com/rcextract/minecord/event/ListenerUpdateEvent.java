package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.ChannelPreference;

public class ListenerUpdateEvent extends MinecordEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private final ChannelPreference ChannelPreference;
	private boolean notify;
	private int index;
	public ListenerUpdateEvent(ChannelPreference ChannelPreference, boolean notify, int index) {
		this.ChannelPreference = ChannelPreference;
		this.notify = notify;
		this.index = index;
	}
	public ChannelPreference getChannelPreference() {
		return ChannelPreference;
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
