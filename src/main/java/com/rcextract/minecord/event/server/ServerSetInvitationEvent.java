package com.rcextract.minecord.event.server;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Server;

public class ServerSetInvitationEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private boolean invitation;
	public ServerSetInvitationEvent(Server server, boolean invitation) {
		super(server);
		this.invitation = invitation;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean needInvitation() {
		return invitation;
	}

	public void setInvitation(boolean invitation) {
		this.invitation = invitation;
	}

}
