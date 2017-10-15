package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Server;

public class ServerChangePermanentEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private boolean permanent;
	public ServerChangePermanentEvent(Server server, boolean permanent) {
		super(server);
		this.permanent = permanent;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public boolean isPermanent() {
		return permanent;
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

}
