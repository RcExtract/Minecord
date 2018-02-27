package com.rcextract.minecord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.permissions.Permission;

public class ConversablePreference {

	private final Conversable conversable;
	private boolean joined;
	private Rank rank;
	private final Set<Permission> permissions;
	
	public ConversablePreference(Conversable conversable, boolean joined, Rank rank, Permission ... permissions) {
		this.conversable = conversable;
		this.joined = joined;
		this.rank = rank;
		this.permissions = new HashSet<Permission>(Arrays.asList(permissions));
	}
	
	public Conversable getConversable() {
		return conversable;
	}
	public boolean isJoined() {
		return joined;
	}
	public void setJoined(boolean joined) {
		this.joined = joined;
	}
	public Rank getRank() {
		return rank;
	}
	public void setRank(Rank rank) {
		this.rank = rank;
	}
	public Set<Permission> getPermissions() {
		return permissions;
	}
	public Server getServer() {
		for (Server server : Minecord.getServerManager().getServers()) 
			if (server.getConversablePreferences().contains(this)) 
				return server;
		return null;
	}
	
}
