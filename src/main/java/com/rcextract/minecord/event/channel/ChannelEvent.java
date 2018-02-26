package com.rcextract.minecord.event.channel;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.event.server.ServerEvent;

public abstract class ChannelEvent extends ServerEvent {

	private final Channel channel;
	public ChannelEvent(Channel channel) {
		super(channel.getServer());
		this.channel = channel;
	}
	public Channel getChannel() {
		return channel;
	}
}
