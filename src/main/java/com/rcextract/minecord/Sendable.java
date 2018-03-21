package com.rcextract.minecord;

import java.util.Set;

import com.rcextract.minecord.sql.DatabaseSerializable;

public interface Sendable extends DatabaseSerializable {
	
	public int getIdentifier();
	public String getName();
	public void setName(String name);
	public String getDescription();
	public void setDescription(String desc);
	public Set<Server> getServers();
	public Channel getMain();
	public void setMain(Channel channel);
	public void chat(String message);
}
