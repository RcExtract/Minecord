package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Server;

public class ServerSetApprovementEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private boolean approvement;
	public ServerSetApprovementEvent(Server server, boolean approvement) {
		super(server);
		this.approvement = approvement;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public boolean needApprovement() {
		return approvement;
	}

	public void setApprovement(boolean approvement) {
		this.approvement = approvement;
	}

}
