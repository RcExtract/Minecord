package com.rcextract.minecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.rcextract.minecord.event.MinecordEvent;
import com.rcextract.minecord.event.ServerCreateEvent;
import com.rcextract.minecord.event.UserRegisterEvent;

/**
 * The control panel of Minecord system.
 */
public final class InternalManager implements ServerManager, UserManager, Recordable<MinecordEvent> {

	protected final Set<Server> servers = new HashSet<Server>();
	protected final Set<User> users = new HashSet<User>();
	private final List<MinecordEvent> records = new ArrayList<MinecordEvent>();

	protected InternalManager() {}
	
	public void initialize() {
		if (getServer("default") == null)
			try {
				createServer("default", null, null, null, null, null);
			} catch (DuplicatedException e) {
				//This exception is never thrown.
				e.printStackTrace();
			}
		for (Server server : getServers()) 
			server.getChannelManager().initialize();
	}
	@Override
	public Set<Server> getServers() {
		return new HashSet<Server>(servers);
	}

	@Override
	public Server getServer(int id) {
		for (Server server : servers) if (server.getIdentifier() == id) return server;
		return null;
	}

	@Override
	public Server getServer(String name) {
		for (Server server : servers) if (server.getName().equals(name)) return server;
		return null;
	}

	@Override
	public Server getServer(UUID player) {
		User user = getUser(player);
		if (user == null) return null;
		return getServer(user);
	}
	
	@Override
	public Server getServer(User user) {
		for (Server server : servers) if (server.getMembers().contains(user)) return server;
		return null;
	}
	
	@Override
	public Server getServer(Channel channel) {
		for (Server server : servers) if (server.getChannelManager().getChannels().contains(channel)) return server;
		return null;
	}
	@Override
	public Server createServer(String name, String desc, Boolean approvement, Boolean invitation, ChannelManager channelManager, RankManager rankManager) throws DuplicatedException {
		Validate.notNull(name);
		if (getServer(name) != null) throw new DuplicatedException();
		if (desc == null) desc = "A default server description.";
		if (approvement == null) approvement = true;
		if (invitation == null) invitation = false;
		if (channelManager == null) channelManager = new ChannelManager();
		if (rankManager == null) rankManager = new RankManager();
		int id = ThreadLocalRandom.current().nextInt();
		while (getServer(id) != null || id < 0) id = ThreadLocalRandom.current().nextInt();
		Server server = new Server(id, name, desc, approvement, invitation, false, false, channelManager, rankManager);
		server.getChannelManager().initialize();
		ServerCreateEvent event = new ServerCreateEvent(server);
		Bukkit.getPluginManager().callEvent(event);
		if (!(event.isCancelled())) servers.add(server);
		return server;
	}

	@Override
	public Set<User> getUsers() {
		return users;
	}
	
	@Override
	public User getUser(int id) {
		for (User user : users) if (user.getIdentifier() == id) return user;
		return null;
	}

	@Override
	public Set<User> getUsers(String name) {
		Set<User> users = new HashSet<User>();
		for (User user : this.users) if (user.getName() == name) users.add(user);
		return users;
	}

	@Override
	public User getUser(UUID player) {
		for (User user : users) 
			if (user.getUUID().equals(player)) 
				return user;
		return null;
	}

	@Override
	public boolean isRegistered(UUID player) {
		return getUser(player) != null;
	}
	
	@Override
	public User registerPlayer(OfflinePlayer player, Channel channel, Rank rank) {
		System.out.println("register player called");
		int id = ThreadLocalRandom.current().nextInt();
		while (getUser(id) != null || id < 0) id = ThreadLocalRandom.current().nextInt();
		String name = player.getName();
		String nickname = name;
		String desc = "A default user description";
		if (channel == null) channel = Minecord.getServerManager().getServer("default").getChannelManager().getMainChannel();
		if (rank == null) rank = channel.getChannelManager().getServer().getRankManager().getMain();
		else if (rank.getRankManager().getServer() != channel.getChannelManager().getServer()) throw new IllegalStateException("Both channel and rank must be in the same server!");
		User user = new User(id, name, nickname, desc, player.getUniqueId(), channel, rank);
		UserRegisterEvent event = new UserRegisterEvent(user);
		Bukkit.getPluginManager().callEvent(event);
		if (!(event.isCancelled())) users.add(user);
		return user;
	}

	@Override
	@Deprecated
	public void unregisterPlayer(User user) {
		users.remove(user);
	}

	public void record(MinecordEvent event) {
		records.add(event);
	}
	@Override
	public List<MinecordEvent> getRecords() {
		return new ArrayList<MinecordEvent>(records);
	}
	
	@Override
	public <E extends MinecordEvent> List<E> getRecords(Class<E> clazz) {
		List<E> records = new ArrayList<E>();
		for (MinecordEvent event : this.records) 
			if (clazz.isInstance(event)) records.add(clazz.cast(event));
		return records;
	}
	
	@Override
	public MinecordEvent getLatestRecord() {
		if (records.size() == 0) return null;
		return records.get(records.size() - 1);
	}

	@Override
	public <E extends MinecordEvent> E getLatestRecord(Class<E> clazz) {
		for (int i = records.size() - 1; i >= 0; i++) {
			MinecordEvent event = records.get(i);
			if (clazz.isInstance(event)) return clazz.cast(event);
		}
		return null;
	}

	@Override
	public MinecordEvent getOldestRecord() {
		if (records.size() == 0) return null;
		return records.get(0);
	}

	@Override
	public <E extends MinecordEvent> E getOldestRecord(Class<E> clazz) {
		for (MinecordEvent event : records) 
			if (clazz.isInstance(event)) return clazz.cast(event);
		return null;
	}

	@Override
	public void clear() {
		records.clear();
	}
}
