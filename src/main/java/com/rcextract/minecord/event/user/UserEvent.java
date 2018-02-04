package com.rcextract.minecord.event.user;

import com.rcextract.minecord.User;
import com.rcextract.minecord.event.MinecordEvent;

public abstract class UserEvent extends MinecordEvent {

	private final User user;
	public UserEvent(User user) {
		super();
		this.user = user;
	}
	public User getUser() {
		return user;
	}
}
