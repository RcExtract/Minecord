package com.rcextract.minecord.event.channel;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Channel;

public class ChannelSetNameEvent extends ChannelEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private String name;
	public ChannelSetNameEvent(Channel channel, String name) {
		super(channel);
		this.name = name;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public String getNewName() {
		return name;
	}
	public void setNewName(String name) {
		this.name = name;
	}
}
