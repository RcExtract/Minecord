package com.rcextract.minecord;

/*import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;*/

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;

@Deprecated
public class ServerIdentity implements Cloneable/*, ListenerHolder*/ {

	private String name;
	private final Server server;
	private boolean joined;
	private Rank rank;
	//private final Set<Listener> listeners;
	public ServerIdentity(Server server, boolean joined, Rank rank/*, Listener ... listeners*/) {
		Validate.notNull(server);
		if (rank == null) rank = server.getRankManager().getMain();
		this.server = server;
		this.joined = joined;
		this.rank = rank;
		/*this.listeners = new HashSet<Listener>(Arrays.asList(listeners));
		if (this.listeners.isEmpty()) this.listeners.add(new Listener(server.getMain(), true, 0));
		validate();
		registerAllAbsentListeners(true, 0);*/
	}
	/*private void validate() {
		boolean sameserver = true;
		for (Listener listener : listeners) 
			sameserver = sameserver && listener.getChannel().getServer() == server;
		sameserver = sameserver && rank.getRankManager().getServer() == server;
		if (!(sameserver)) throw new IllegalArgumentException();
	}*/
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Server getServer() {
		return server;
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
		//validate();
	}
	public void applyRank(Rank old) {
		net.milkbowl.vault.permission.Permission pm = Minecord.getPermissionManager();
		OfflinePlayer player = getUser().getPlayer();
		for (Permission permission : old.getPermissions()) 
			if (pm.playerHas(null, player, permission.getName())) 
				pm.playerRemove(null, player, permission.getName());
		for (Permission permission : rank.getPermissions()) 
			pm.playerAdd(null, player, permission.getName());
	}
	public User getUser() {
		for (User user : Minecord.getUserManager().getUsers()) 
			if (user.getIdentities().contains(this)) 
				return user;
		return null;
	}
	@Override
	public ServerIdentity clone() {
		try {
			return (ServerIdentity) super.clone();
		} catch (CloneNotSupportedException e) {
			//This exception is never thrown.
			return null;
		}
	}
}
