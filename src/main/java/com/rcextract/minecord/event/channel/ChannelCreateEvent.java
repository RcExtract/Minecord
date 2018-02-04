package com.rcextract.minecord.event.channel;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Server;
import com.rcextract.minecord.event.server.ServerEvent;

public class ChannelCreateEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private String name;
	private String desc;
	public ChannelCreateEvent(Server server, String name, String desc) {
		super(server);
		this.name = name;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return desc;
	}
	public void setDescription(String desc) {
		this.desc = desc;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
