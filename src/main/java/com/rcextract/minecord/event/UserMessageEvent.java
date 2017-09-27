package com.rcextract.minecord.event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.JSONMessage;
import com.rcextract.minecord.User;

public class UserMessageEvent extends UserEvent {

	private static final HandlerList handlers = new HandlerList();
	public static final Set<Integer> REGISTERED_IDENTIFIERS = new HashSet<Integer>();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private int id;
	private String message;
	private Channel channel;
	private Set<User> recipients;
	private List<UserTagEvent> tags;
	private JSONMessage finalmessage;
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
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
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
	public JSONMessage getFinalMessage() {
		return finalmessage;
	}
}
