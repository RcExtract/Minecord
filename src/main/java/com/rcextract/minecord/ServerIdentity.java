package com.rcextract.minecord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;

public class ServerIdentity implements Cloneable, ListenerHolder {

	private String name;
	private final Server server;
	private boolean activated;
	private Rank rank;
	private final Set<Listener> listeners;
	public ServerIdentity(Server server, boolean activated, Rank rank, Listener ... listeners) {
		Validate.notNull(server);
		boolean sameserver = true;
		for (Listener listener : listeners) 
			sameserver = sameserver && listener.getChannel().getChannelManager().getServer() == server;
		sameserver = sameserver && rank.getRankManager().getServer() == server;
		if (!(sameserver)) throw new IllegalArgumentException();
		if (rank == null) rank = server.getRankManager().getMain();
		if (listeners.length == 0) listeners[0] = new Listener(server.getChannelManager().getMainChannel(), true, 0);
		this.server = server;
		this.activated = activated;
		this.rank = rank;
		this.listeners = new HashSet<Listener>(Arrays.asList(listeners));
		registerAllAbsentListeners(true, 0);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Server getServer() {
		return server;
	}
	public Rank getRank() {
		return rank;
	}
	public boolean isActivated() {
		return activated;
	}
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	public void setRank(Rank rank) {
		this.rank = rank;
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
	@Override
	public Set<Listener> getListeners() {
		return new HashSet<Listener>(listeners);
	}
	@Override
	public Listener getListener(Channel channel) {
		for (Listener listener : listeners) 
			if (listener.getChannel() == channel) 
				return listener;
		return null;
	}
	@Override
	public Set<Listener> getListeners(boolean notify) {
		Set<Listener> listeners = new HashSet<Listener>();
		for (Listener listener : listeners) 
			if (listener.isNotify()) 
				listeners.add(listener);
		return listeners;
	}
	public Listener registerListener(Channel channel, boolean notify, int index) throws DuplicatedException {
		return registerListener(new Listener(channel, notify, index));
	}
	public Listener registerListener(Listener listener) throws DuplicatedException {
		if (getListener(listener.getChannel()) != null) throw new DuplicatedException();
		if (listener.getChannel().getChannelManager().getServer() != server) throw new IllegalArgumentException();
		listeners.add(listener);
		return listener;
	}
	public Set<Listener> registerAllAbsentListeners(boolean allNotify, int allIndex, Channel ... exceptions) {
		Set<Listener> listeners = new HashSet<Listener>();
		for (Channel channel : server.getChannelManager().getChannels()) 
			if (getListener(channel) == null && !(Arrays.asList(exceptions).contains(channel))) 
				listeners.add(new Listener(channel, allNotify, allIndex));
		this.listeners.addAll(listeners);
		return listeners;
	}
	public void registerAllListeners(Listener ... listeners) throws DuplicatedException {
		for (Listener listener : listeners) 
			registerListener(listener);
	}
	public boolean removeListener(Listener listener, Listener main) {
		if (!(listeners.contains(main))) throw new IllegalArgumentException();
		if (listener == getUser().getMain()) getUser().setMain(main);
		if (listener == main) throw new IllegalArgumentException();
		return listeners.remove(listener);
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
