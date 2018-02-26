package com.rcextract.minecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;

import com.rcextract.minecord.event.MinecordEvent;
import com.rcextract.minecord.utils.ComparativeSet;
import com.rcextract.minecord.utils.Pair;

/**
 * The control panel of Minecord system.
 */
public final class InternalManager implements ServerManager, UserManager, Recordable<MinecordEvent> {

	protected final ComparativeSet<Server> servers;
	protected final Set<User> users = new HashSet<User>();
	private final List<MinecordEvent> records = new ArrayList<MinecordEvent>();

	protected InternalManager() {
		try {
			servers = new ComparativeSet<Server>(Server.class, new Pair<String, Boolean>("getIdentifier", true), new Pair<String, Boolean>("getName", false));
		} catch (NoSuchMethodException | SecurityException e) {
			//This exception is never thrown.
			throw new RuntimeException();
		}
	}
	
	public void initialize() {
		for (Server server : getServers()) {
			server.initialize();
			server.getRankManager().initialize();
		}
		for (User user : Minecord.getUserManager().getUsers()) 
			if (user.getListeners().isEmpty()) 
				user.join(getMain());
	}
	
	@Override
	public ComparativeSet<Server> getServers() {
		return servers;
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
	public Set<Server> getServers(OfflinePlayer player) {
		User user = getUser(player);
		if (user == null) return null;
		return getServers(user);
	}
	
	@Override
	public Set<Server> getServers(User user) {
		Set<Server> servers = new HashSet<Server>();
		for (Server server : servers) 
			if (server.getActiveMembers().contains(user)) 
				servers.add(server);
		return servers;
	}
	
	@Override
	public Server getServer(Channel channel) {
		for (Server server : servers) if (server.getChannels().contains(channel)) return server;
		return null;
	}
	@Override
	public Server getMain() {
		return getServer("default");
	}
	@Override
	public Server createServer(String name, String desc, Boolean approvement, Boolean invitation, RankManager rankManager, Channel main, Channel ... channels) throws DuplicatedException {
		Validate.notNull(name);
		if (getServer(name) != null) throw new DuplicatedException();
		if (desc == null) desc = "A default server description.";
		if (approvement == null) approvement = true;
		if (invitation == null) invitation = false;
		if (rankManager == null) rankManager = new RankManager();
		Set<Channel> channelset = new HashSet<Channel>(Arrays.asList(channels));
		if (!(channelset.contains(main))) throw new IllegalArgumentException();
		int id = new Random().nextInt();
		while (getServer(id) != null) id = ThreadLocalRandom.current().nextInt();
		Server server = new Server(id, name, desc, approvement, invitation, false, false, rankManager, main, channels);
		server.initialize();
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
		if (channel == null) channel = getMain().getMain();
		if (rank == null) rank = channel.getServer().getRankManager().getMain();
		else if (rank.getRankManager().getServer() != channel.getServer()) throw new IllegalStateException("Both channel and rank must be in the same server!");
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
		Set<ServerIdentity> identityset = new HashSet<ServerIdentity>(Arrays.asList(identities));
		if (identities.length == 0) 
			if (main == null) {
				identityset.add(new ServerIdentity(this.getMain(), true, null));
				main = new Listener(getMain().getMain(), true, 0);
			} else 
				identityset.add(new ServerIdentity(main.getChannel().getServer(), true, null, main));
		boolean contains = false;
		for (ServerIdentity identity : identityset) 
			contains = contains || identity.getListeners().contains(main);
		if (!(contains)) throw new IllegalArgumentException();
		user = new User(id, name, nickname, desc, player, main, identityset.toArray(new ServerIdentity[identityset.size()]));
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
