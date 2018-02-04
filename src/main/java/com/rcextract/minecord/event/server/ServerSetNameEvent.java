package com.rcextract.minecord.event.server;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Server;

public class ServerSetNameEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private String name;
	public ServerSetNameEvent(Server server, String name) {
		super(server);
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
