package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.User;

public class ChannelSwitchEvent extends UserEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private Channel channel;
	public ChannelSwitchEvent(Channel channel, User user) {
		super(user);
		this.channel = channel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

}
