package com.rcextract.minecord.event;

import com.rcextract.minecord.User;

public abstract class UserEvent extends MinecordEvent {

	private User user;
	public UserEvent(User user) {
		super();
		this.user = user;
	}
	public User getUser() {
		return user;
	}
}
