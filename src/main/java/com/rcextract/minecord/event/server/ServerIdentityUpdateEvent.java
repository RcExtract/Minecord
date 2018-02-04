package com.rcextract.minecord.event.server;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Listener;
import com.rcextract.minecord.Rank;
import com.rcextract.minecord.ServerIdentity;
import com.rcextract.minecord.User;
import com.rcextract.minecord.event.MinecordEvent;

public class ServerIdentityUpdateEvent extends MinecordEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private final ServerIdentity identity;
	private boolean activated;
	private Rank rank;
	private final Set<Listener> listeners;
	private final Set<User> users;
	public ServerIdentityUpdateEvent(ServerIdentity identity, boolean activated, Rank rank, Set<Listener> listeners, Set<User> users) {
		Validate.notNull(identity);
		Validate.notNull(listeners);
		Validate.notNull(users);
		this.identity = identity;
		this.activated = activated;
		this.rank = rank;
		this.listeners = listeners;
		this.users = users;
	}
	public ServerIdentity getIdentity() {
		return identity;
	}
	public boolean isActivated() {
		return activated;
	}
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	public Rank getRank() {
		return rank;
	}
	public void setRank(Rank rank) {
		this.rank = rank;
	}
	public Set<Listener> getListeners() {
		return listeners;
	}
	public Set<User> getAffectingUsers() {
		return users;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
