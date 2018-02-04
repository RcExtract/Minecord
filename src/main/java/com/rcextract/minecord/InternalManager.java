package com.rcextract.minecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;

import com.rcextract.minecord.event.MinecordEvent;

/**
 * The control panel of Minecord system.
 */
public final class InternalManager implements ServerManager, UserManager, Recordable<MinecordEvent> {

	protected final Set<Server> servers = new HashSet<Server>();
	protected final Set<User> users = new HashSet<User>();
	private final List<MinecordEvent> records = new ArrayList<MinecordEvent>();

	protected InternalManager() {}
	
	public void initialize() {
		for (Server server : getServers()) {
			server.getChannelManager().initialize();
			server.getRankManager().initialize();
		}
		for (User user : Minecord.getUserManager().getUsers()) 
			if (user.getListeners().isEmpty()) 
				user.join(getMain());
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
	public Server getServer(OfflinePlayer player) {
		User user = getUser(player);
		if (user == null) return null;
		return getServer(user);
	}
	
	@Override
	public Server getServer(User user) {
		for (Server server : servers) 
			if (server.getActiveMembers().contains(user)) 
				return server;
		return null;
	}
	
	@Override
	public Server getServer(Channel channel) {
		for (Server server : servers) if (server.getChannelManager().getChannels().contains(channel)) return server;
		return null;
	}
	@Override
	public Server getMain() {
		return getServer("default");
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
		server.getRankManager().initialize();
		servers.add(server);
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
	public User getUser(OfflinePlayer player) {
		for (User user : users) 
			if (user.getPlayer().getUniqueId().equals(player.getUniqueId())) 
				return user;
		return null;
	}

	@Override
	public boolean isRegistered(OfflinePlayer player) {
		return getUser(player) != null;
	}
	@Deprecated
	@Override
	public User registerPlayer(OfflinePlayer player, Channel channel, Rank rank) throws IllegalStateException {
		int id = ThreadLocalRandom.current().nextInt();
		while (getUser(id) != null || id < 0) id = ThreadLocalRandom.current().nextInt();
		//String name = player.getName();
		//String nickname = name;
		//String desc = "A default user description";
		if (channel == null) channel = getMain().getChannelManager().getMainChannel();
		if (rank == null) rank = channel.getChannelManager().getServer().getRankManager().getMain();
		else if (rank.getRankManager().getServer() != channel.getChannelManager().getServer()) throw new IllegalStateException("Both channel and rank must be in the same server!");
		/*User user = new User(id, name, nickname, desc, player, rank, new Listener(channel, ListenerStatus.VIEW, 0));
		user.applyRank();
		users.add(user);*/
		return /*user*/null;
	}
	@Override
	public User registerPlayer(String name, String nickname, String desc, final OfflinePlayer player, Listener main, ServerIdentity ... identities) {
		User user = getUser(player);
		if (user != null) return user;
		int id = new Random().nextInt();
		if (getUser(id) != null || id < 0) id = new Random().nextInt();
		if (name == null) name = player.getName();
		if (nickname == null) nickname = name;
		if (desc == null) desc = "A default user description.";
		if (identities.length == 0) 
			if (main == null) {
				identities[0] = new ServerIdentity(this.getMain(), true, null);
				main = new Listener(getMain().getChannelManager().getMainChannel(), true, 0);
			} else 
				identities[0] = new ServerIdentity(main.getChannel().getChannelManager().getServer(), true, null, main);
		boolean contains = false;
		for (ServerIdentity identity : identities) 
			contains = contains || identity.getListeners().contains(main);
		if (!(contains)) throw new IllegalArgumentException();
		user = new User(id, name, nickname, desc, player, main, identities);
		users.add(user);
		return user;
	}
	@Deprecated
	@Override
	public User registerPlayerIfAbsent(OfflinePlayer player, Channel channel, Rank rank) {
		if (getUser(player) == null) return registerPlayer(player, channel, rank);
		return getUser(player);
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
