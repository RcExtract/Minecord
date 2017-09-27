package com.rcextract.minecord.event;

import com.rcextract.minecord.Channel;

public abstract class ChannelEvent extends ServerEvent {

	private Channel channel;
	public ChannelEvent(Channel channel) {
		super(channel.getChannelManager().getServer());
		this.channel = channel;
	}
	public Channel getChannel() {
		return channel;
	}
}
