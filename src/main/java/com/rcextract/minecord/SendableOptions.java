package com.rcextract.minecord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.permissions.Permission;

import com.rcextract.minecord.sql.DatabaseSerializable;
import com.rcextract.minecord.sql.SerializableAs;
import com.rcextract.minecord.utils.ArrayMap;

@SerializableAs("soptions")
public class SendableOptions implements DatabaseSerializable {

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
	
	@SuppressWarnings("unchecked")
	public SendableOptions(ArrayMap<String, Object> map) {
		Map<String, Object> internal = map.toMap();
		this.sendable = (Sendable) internal.get("sendable");
		this.state = JoinState.valueOf((String) internal.get("state"));
		this.rank = (Rank) internal.get("rank");
		this.permissions = (Set<Permission>) internal.get("permissions");
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
		for (Server server : Minecord.getServers()) 
			if (server.getSendableOptions().contains(this)) 
				return server;
		return null;
	}

	@Override
	public ArrayMap<String, Object> serialize() {
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		map.put("sendable", sendable);
		map.put("state", state.toString());
		map.put("rank", rank);
		map.put("permissions", permissions);
		return map;
	}
	
}
