package com.rcextract.minecord;

import java.util.List;
import java.util.Set;

import com.rcextract.minecord.getters.ChannelOptionsGetter;

public interface Sendable extends ChannelOptionsGetter {
	
	public int getIdentifier();
	public String getName();
	public void setName(String name);
	public String getDescription();
	public void setDescription(String desc);
	public Set<Server> getServers();
	public Channel getMain();
	public void setMain(Channel channel);
	public List<Object> values();
}
