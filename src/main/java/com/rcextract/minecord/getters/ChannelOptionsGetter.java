package com.rcextract.minecord.getters;

import java.util.Set;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.ChannelOptions;

public interface ChannelOptionsGetter {

	public Set<ChannelOptions> getChannelOptions();
	public ChannelOptions getChannelOptions(Channel channel);
	public Set<ChannelOptions> getChannelOptions(boolean notify);
}
