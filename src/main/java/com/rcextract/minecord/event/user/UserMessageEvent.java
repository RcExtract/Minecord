package com.rcextract.minecord.event.user;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.Conversable;
import com.rcextract.minecord.User;

public class UserMessageEvent extends UserEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private String message;
	private final Channel channel;
	private final Set<Conversable> conversables;
	private List<UserTagEvent> tags;
	public UserMessageEvent(String message, Channel channel, User sender, Set<Conversable> conversables, UserTagEvent ... tags) {
		super(sender);
		this.message = message;
		this.channel = channel;
		this.conversables = conversables;
		this.tags = Arrays.asList(tags);
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public String getMessage() {
		return message;
	}
	public Channel getChannel() {
		return channel;
	}
	public Set<Conversable> getConversables() {
		return conversables;
	}
	public UserTagEvent[] getTags() {
		return tags.toArray(new UserTagEvent[tags.size()]);
	}
}
