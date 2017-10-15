package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Server;

public class ServerLockEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	public ServerLockEvent(Server server) {
		super(server);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
