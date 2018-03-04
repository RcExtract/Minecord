package com.rcextract.minecord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.permissions.Permission;

public class SendableOptions {

	private final Sendable sendable;
	private JoinState state;
	private Rank rank;
	private final Set<Permission> permissions;
	
	public SendableOptions(Sendable sendable, JoinState state, Rank rank, Permission ... permissions) {
		Validate.notNull(sendable);
		Validate.notNull(state);
		this.sendable = sendable;
		this.state = state;
		this.rank = rank;
		this.permissions = new HashSet<Permission>(Arrays.asList(permissions));
	}
	
	public Sendable getSendable() {
		return sendable;
	}
	public JoinState getState() {
		return state;
	}
	public void setSate(JoinState state) {
		this.state = state;
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
			if (server.getSendableOptions().contains(this)) 
				return server;
		return null;
	}
	
}
