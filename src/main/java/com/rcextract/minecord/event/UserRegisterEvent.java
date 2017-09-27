package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.User;

public class UserRegisterEvent extends MinecordEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private User user;
	public UserRegisterEvent(User user) {
		this.user = user;
	}
	public User getUser() {
		return user;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
