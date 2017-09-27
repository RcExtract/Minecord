package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Server;

/**
 * Represents the creation of a {@link Server}.
 */
public class ServerCreateEvent extends MinecordEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private Server server;
	public ServerCreateEvent(Server server) {
		super();
		this.server = server;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Server getServer() {
		return server;
	}
}
