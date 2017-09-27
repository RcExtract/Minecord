package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.Server;

public class ChannelCreateEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private Channel channel;
	public ChannelCreateEvent(Server server, Channel channel) {
		super(server);
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
