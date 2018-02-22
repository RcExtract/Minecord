package com.rcextract.minecord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.rcextract.minecord.event.user.UserEvent;

public class User implements RecordManager<UserEvent>, ListenerHolder {

	private final int id;
	private String name;
	private String nickname;
	private String desc;
	private final OfflinePlayer player;
	private final Set<ServerIdentity> identities;
	private Listener main;
	@Deprecated
	private Rank rank;
	@Deprecated
	protected User(int id, String name, String nickname, String desc, OfflinePlayer player, Channel channel, Rank rank) {
		this.id = id;
		this.name = name;
		this.nickname = nickname == null ? name : nickname;
		this.desc = desc;
		this.player = player;
		//this.channel = channel;
		this.rank = rank;
		this.identities = new HashSet<ServerIdentity>();
	}
	protected User(int id, String name, String nickname, String desc, OfflinePlayer player, Listener main, ServerIdentity ... identities) throws IllegalArgumentException {
		this.id = id;
		this.name = name;
		this.nickname = nickname;
		this.desc = desc;
		this.player = player;
		this.main = main;
		this.identities = new HashSet<ServerIdentity>(Arrays.asList(identities));
	}
	public int getIdentifier() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickName() {
		return nickname;
	}
	public void setNickName(String nickname) {
		this.nickname = nickname;
	}
	public String getDescription() {
		return desc;
	}
	public void setDescription(String desc) {
		this.desc = desc;
	}
	public OfflinePlayer getPlayer() {
		return player;
	}
	@Deprecated
	public void setPlayer(OfflinePlayer player) {
		//this.player = player;
	}
	public boolean isOnline() {
		return player.isOnline();
	}
	public Player getOnlinePlayer() {
		return player.getPlayer();
	}
	public Set<ServerIdentity> getIdentities() {
		return new HashSet<ServerIdentity>(identities);
	}
	public ServerIdentity getIdentity(Server server) {
		for (ServerIdentity identity : identities) 
			if (identity.getServer() == server) 
				return identity;
		return null;
	}
	public ServerIdentity getIdentity(Listener ... listeners) {
		for (ServerIdentity identity : identities) 
			if (identity.getListeners().containsAll(Arrays.asList(listeners))) 
				return identity;
		return null;
	}
	/**
	 * Adds a preference towards the server. Does not affect the current situation of user.
	 * @param identity The preference.
	 */
	public boolean addIdentity(ServerIdentity identity) {
		return identities.add(identity);
	}
	/**
	 * Activates the current preference towards the server. Creates an activated preference towards the server if not existed.
	 * @param server The target server.
	 * <p>
	 * This method is not recommended to use because it contains statements that revoke the fact that server identities and listeners are just properties of users towards a server or channel.
	 */
	public void join(Server server) {
		ServerIdentity identity = getIdentity(server);
		if (identity == null) {
			ServerIdentity nidentity = new ServerIdentity(server, true, server.getRankManager().getMain());
			nidentity.registerAllAbsentListeners(true, 0);
			identities.add(nidentity);
			return;
		}
		if (identity.isJoined()) return;
		identity.setJoined(true);
		setMain(identity.getListener(identity.getServer().getChannelManager().getMainChannel()));
	}
	/**
	 * Deactivates the preference towards the server. If permanently, removes the preference towards the server.
	 * @param server The target server.
	 * @param permanent 
	 * <p>
	 * This method is not recommended to use because it contains statements that revoke the fact that server identities and listeners are just properties of users towards a server or channel.
	 */
	public void leave(Server server, boolean permanent) {
		ServerIdentity identity = getIdentity(server);
		if (identity == null) throw new IllegalArgumentException();
		if (permanent) 
			identities.remove(identity);
		else 
			identity.setJoined(false);
	}
	@Override
	public Set<Listener> getListeners() {
		Set<Listener> listeners = new HashSet<Listener>();
		for (ServerIdentity identity : identities) 
			listeners.addAll(identity.getListeners());
		return listeners;
	}
	@Override
	public Listener getListener(Channel channel) {
		for (Listener listener : getListeners()) 
			if (listener.getChannel() == channel) 
				return listener;
		return null;
	}
	@Override
	public Set<Listener> getListeners(boolean notify) {
		Set<Listener> listeners = new HashSet<Listener>();
		for (Listener listener : getListeners()) 
			if (listener.isNotify()) 
				listeners.add(listener);
		return listeners;
	}
	public Listener getMain() {
		return main;
	}
	public void setMain(Listener main) {
		if (getIdentity(main) == null) throw new IllegalArgumentException();
		this.main = main;
		if (!(player.isOnline())) return;
		Player player = this.player.getPlayer();
		player.sendMessage(main.getChannel().getName() + ":");
		for (Message message : main.getUnreadMessages()) 
			player.sendMessage(message.getMessage());
		main.setIndex(main.getChannel().getMessages().size());
		
	}
	/**
	 * Assigns a user to this channel. This will override the current location of the user, 
	 * meaning the user will be removed from the previous channel, after the user successfully 
	 * joined the channel, defined by the return value of this method.
	 * <p>
	 * This method no longer works.
	 * @param user The target user.
	 * @deprecated This method is deprecated due to Minecord now supports players joining multiple channels.
	 */
	@Deprecated
	public void setChannel(Channel channel) {
		if (channel == null) channel = Minecord.getServerManager().getMain().getChannelManager().getMainChannel();
		/*this.channel = channel;*/
		Minecord.updateMessage(this, true);
	}
	@Deprecated
	public Rank getRank() {
		return rank;
	}
	@Deprecated
	public void setRank(Rank rank) {
		if (rank == null) rank = main.getChannel().getChannelManager().getServer().getRankManager().getMain();
		this.rank = rank;
		applyRank();
	}
	public void clear() throws IllegalStateException {
		if (!(player.isOnline())) throw new IllegalStateException();
		for (int i = 0; i < 25; i++) player.getPlayer().sendMessage("");
	}
	/**
	 * Applies rank permissions to the user.
	 * @deprecated
	 */
	@Deprecated
	public void applyRank() {
		for (Permission permission : rank.getPermissions()) 
			Minecord.getPermissionManager().playerAdd(null, player, permission.getName());
	}
	public void applyMessage() {
		if (!(player.isOnline())) throw new IllegalStateException();
		Player player = getOnlinePlayer();
		player.sendMessage(main.getChannel().getName() + ":");
		for (Message message : main.getUnreadMessages()) 
			player.sendMessage(Minecord.applyFormat(message.getSender().getName(), message.getSender().getNickName(), message.getSender().getPlayer().getUniqueId().toString(), message.getMessage(), message.getDate().toString()));
		main.setIndex(main.getChannel().getMessages().size() - main.getUnreadMessages().size());
	}
	@Override
	public List<UserEvent> getRecords() {
		return Minecord.getRecordManager().getRecords(UserEvent.class);
	}
	@Override
	public <E extends UserEvent> List<E> getRecords(Class<E> clazz) {
		return Minecord.getRecordManager().getRecords(clazz);
	}
	@Override
	public UserEvent getLatestRecord() {
		return Minecord.getRecordManager().getLatestRecord(UserEvent.class);
	}
	@Override
	public <E extends UserEvent> E getLatestRecord(Class<E> clazz) {
		return Minecord.getRecordManager().getLatestRecord(clazz);
	}
	@Override
	public UserEvent getOldestRecord() {
		return Minecord.getRecordManager().getOldestRecord(UserEvent.class);
	}
	@Override
	public <E extends UserEvent> E getOldestRecord(Class<E> clazz) {
		return Minecord.getRecordManager().getOldestRecord(clazz);
	}
}