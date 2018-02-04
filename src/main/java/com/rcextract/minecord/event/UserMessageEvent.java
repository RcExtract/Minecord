package com.rcextract.minecord.event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.JSONMessage;
import com.rcextract.minecord.User;

public class UserMessageEvent extends UserEvent {

	private static final HandlerList handlers = new HandlerList();
	@Deprecated
	public static final Set<Integer> REGISTERED_IDENTIFIERS = new HashSet<Integer>();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	@Deprecated
	private int id;
	private String message;
	private final Channel channel;
	private final Set<User> recipients;
	private List<UserTagEvent> tags;
	@Deprecated
	private JSONMessage finalmessage;
	@Deprecated
	public UserMessageEvent(int id, Channel channel, User sender, String message, Set<User> recipients, List<UserTagEvent> tags, JSONMessage finalmessage) {
		super(sender);
		this.id = id;
		REGISTERED_IDENTIFIERS.add(id);
		this.message = message;
		this.channel = channel;
		this.recipients = recipients;
		this.tags = tags;
		this.finalmessage = finalmessage;
	}
	public UserMessageEvent(String message, Channel channel, User sender, Set<User> recipients, UserTagEvent ... tags) {
		super(sender);
		this.message = message;
		this.channel = channel;
		this.recipients = recipients;
		this.tags = Arrays.asList(tags);
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	@Deprecated
	public int getIdentifier() {
		return id;
	}
	public String getMessage() {
		return message;
	}
	public Channel getChannel() {
		return channel;
	}
	public Set<User> getRecipients() {
		return recipients;
	}
	public UserTagEvent[] getTags() {
		return tags.toArray(new UserTagEvent[tags.size()]);
	}
	@Deprecated
	public JSONMessage getFinalMessage() {
		return finalmessage;
	}
}
