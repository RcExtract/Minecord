package com.rcextract.minecord.event.user;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.rcextract.minecord.User;

public class UserTagEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private User user;
	private User target;
	public UserTagEvent(User user, User target) {
		this.target = target;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public User getUser() {
		return user;
	}

	public User getTarget() {
		return target;
	}
	
	public void setTarget(User target) {
		this.target = target;
	}
}
