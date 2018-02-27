package com.rcextract.minecord.getters;

import java.util.Set;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.ChannelPreference;

public interface ChannelPreferenceGetter {

	public Set<ChannelPreference> getChannelPreferences();
	public ChannelPreference getChannelPreference(Channel channel);
	public Set<ChannelPreference> getChannelPreferences(boolean notify);
}
