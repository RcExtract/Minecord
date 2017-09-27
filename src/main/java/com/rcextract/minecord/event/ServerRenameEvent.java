package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Server;

public class ServerRenameEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private String name;
	public ServerRenameEvent(Server server, String name) {
		super(server);
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
