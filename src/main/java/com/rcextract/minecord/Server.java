package com.rcextract.minecord;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.rcextract.minecord.getters.ChannelGetter;
import com.rcextract.minecord.getters.SendableOptionsGetter;
import com.rcextract.minecord.sql.DatabaseSerializable;
import com.rcextract.minecord.sql.SerializableAs;
import com.rcextract.minecord.utils.ArrayMap;
import com.rcextract.minecord.utils.ComparativeSet;

/**
 * In Minecord, a Server groups up a set of channels, usually by target or topic, and provides 
 * advanced settings and customizations for users to manage their channels easily.
 * <p>
 * Both identifier and name are primary keys for developers and users to refer so they are 
 * unique. Identifier is unchangeable, name is changeable but must not be used by other servers.
 * <p>
 * If approvement is true, players must be accepted to join the server. If invitation is true, 
 * player must be invited to join the server. If both are true, then the player must be invited 
 * to attempt to join the server. When attempting, the player must be accepted to join the server.
 * <p>
 * If permanent is true, the server cannot be removed from the index in {@link ServerManager}.
 * <p>
 * If locked, all players are kicked from the server and no one can join it. Only players 
 * with permission {@code minecord.managelock.server} can access, lock or unlock the server.
 * <p>
 * A {@link ChannelManager} helps a server to manage channels, and a {@link RankManager} helps 
 * to manage ranks. They must stick to a server to work.
 * <p>
 * A {@link ServerRecordManager} helps a server to manage records. It is separated into a class
 * for merging purposes which feature will be provided in the future.
 * <p>
 * This class uses a very tricky method to prevent {@link StackOverflowError} when 
 * serializing and deserializing, is to deal with channels first, so the cycle loop 
 * from server to sendable options to sendable to channel options to channel to server 
 * will be broken.
 */
@SerializableAs("server")
public class Server implements ChannelGetter, SendableOptionsGetter, DatabaseSerializable {

	private final int id;
	private String name;
	private String desc;
	private boolean approvement;
	private boolean invitation;
	private boolean permanent;
	private boolean locked;
	private final ComparativeSet<Channel> channels;
	private Channel main;
	@Deprecated
	private RankManager rankManager;
	private final ComparativeSet<SendableOptions> options;

	@Deprecated
	public Server(int id, String name, String desc, boolean approvement, boolean invitation, boolean permanent, boolean locked, /*ChannelManager channelManager, */RankManager rankManager, Channel main, Channel ... channels) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.approvement = approvement;
		this.invitation = invitation;
		this.permanent = permanent;
		this.locked = locked;
		this.channels = new ComparativeSet<Channel>((Channel element) -> getChannel(element.getIdentifier()) == null);
		this.main = main;
		this.rankManager = rankManager;
		this.options = new ComparativeSet<SendableOptions>((SendableOptions element) -> getSendableOption(element.getSendable()) == null);
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	public Server(ArrayMap<String, Object> map) {
		Map<String, Object> internal = map.toMap();
		this.id = Minecord.generateServerIdentifier();
		this.name = (String) internal.get("name");
		this.desc = (String) internal.get("desc");
		this.approvement = (boolean) internal.get("approvement");
		this.invitation = (boolean) internal.get("invitation");
		this.permanent = (boolean) internal.get("permanent");
		this.locked = (boolean) internal.get("locked");
		this.channels = (ComparativeSet<Channel>) internal.get("channels");
		this.main = (Channel) internal.get("main");
		this.rankManager = (RankManager) internal.get("rank_manager");
		this.options = (ComparativeSet<SendableOptions>) internal.get("options");
	}
	/**
	 * Gets the identifier of the server.
	 * @return The identifier of the server.
	 */
	public int getIdentifier() {
		return id;
	}
	/**
	 * Gets the name of the server.
	 * @return The name of the server.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Renames the server.
	 * @param name The new name of the server.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Gets the description of the server.
	 * @return The description of the server.
	 */
	public String getDescription() {
		return desc;
	}
	/**
	 * Sets the description of the server.
	 * @param desc The new description of the server.
	 */
	public void setDescription(String desc) {
		this.desc = desc;
	}
	/**
	 * Determines the approvement requirement to join this server.
	 * @return The approvement requirement to join this server.
	 */
	public boolean isApprovement() {
		return approvement;
	}
	/**
	 * Changes the state of approvement requirement.
	 * @param approvement The new state of approvement requirement.
	 */
	public void setApprovement(boolean approvement) {
		this.approvement = approvement;
	}
	/**
	 * Determines the invitation requirement to join this server.
	 * @return The invitation requirement to join this server.
	 */
	public boolean isInvitation() {
		return invitation;
	}
	/**
	 * Changes the state of invitation requirement.
	 * @param approvement The new state of invitation requirement.
	 * @return 
	 */
	public void setInvitation(boolean invitation) {
		this.invitation = invitation;
	}
	/**
	 * Determines if the server is deletable by users.
	 * @return If the server is deletable by users.
	 */
	public boolean isPermanent() {
		return permanent;
	}
	/**
	 * Sets if the users can delete the server.
	 * @param approvement The new state of if the users can delete the server.
	 * @return 
	 */
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}
	/**
	 * Determines if the server is locked.
	 * @return Whether if the server is locked.
	 */
	public boolean isLocked() {
		return locked;
	}
	/**
	 * Sets the locked state of the server.
	 * @param locked The locked state of the server.
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	/**
	 * Unlocks the server. All functions should be enabled for normal users according to the 
	 * rank manager. Only users with permission {@code minecord.managelock.server} should have
	 * access to unlocking.
	 */
	public void unlock() throws IllegalStateException {
		if (!(locked)) throw new IllegalStateException();
		this.locked = false;
	}
	public ComparativeSet<Channel> getChannels() {
		return channels;
	}
	
	public Channel getMain() {
		return main;
	}
	
	public void setMain(Channel main) {
		if (!(channels.contains(main))) throw new IllegalArgumentException();
		this.main = main;
	}
	/**
	 * Gets the rank manager. It currently does nothing and always return null.
	 * @return The rank manager.
	 */
	@Deprecated
	public RankManager getRankManager() {
		return rankManager;
	}
	/**
	 * Sets the rank manager.
	 * <p>
	 * This method is reserved for initialization.
	 * @param rankManager The rank manager.
	 */
	@Deprecated
	protected void setRankManager(RankManager rankManager) {
		this.rankManager = rankManager;
	}
	@Override
	public Set<Sendable> getSendables() {
		Set<Sendable> sendables = new HashSet<Sendable>();
		for (SendableOptions option : options) 
			sendables.add(option.getSendable());
		return sendables;
	}
	@Override
	public ComparativeSet<SendableOptions> getSendableOptions() {
		return options;
	}
	@Override
	public SendableOptions getSendableOption(Sendable sendable) {
		for (SendableOptions option : options) 
			if (option.getSendable() == sendable) 
				return option;
		return null;
	}
	@Override
	public Set<SendableOptions> getSendableOptions(JoinState state) {
		Set<SendableOptions> options = new HashSet<SendableOptions>();
		for (SendableOptions option : options) 
			if (option.getState() == state) 
				options.add(option);
		return options;
	}
	@Override
	public Set<SendableOptions> getSendableOptions(Rank rank) {
		Set<SendableOptions> options = new HashSet<SendableOptions>();
		for (SendableOptions option : options) 
			if (option.getRank() == rank) 
				options.add(option);
		return options;
	}
	@Override
	public Channel getChannel(int id) {
		for (Channel channel : channels) 
			if (channel.getIdentifier() == id) 
				return channel;
		return null;
	}
	@Override
	public Channel getChannel(String name) {
		for (Channel channel : channels) 
			if (channel.getName().equals(name)) 
				return channel;
		return null;
	}
	
	public Channel initialize() {
		if (channels.isEmpty()) {
			Channel channel = new Channel(new Random().nextInt(), "general", "A default channel description.", false);
			channels.add(channel);
			return channel;
		}
		return null;
	}
	
	public int generateChannelIdentifier() {
		int id = new Random().nextInt();
		while (getChannel(id) != null) id = new Random().nextInt();
		return id;
	}
	@Override
	public ArrayMap<String, Object> serialize() {
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		map.put("name", name);
		map.put("desc", desc);
		map.put("approvement", approvement);
		map.put("invitation", invitation);
		map.put("permanent", permanent);
		map.put("locked", locked);
		map.put("channels", channels);
		map.put("main", main);
		map.put("rank_manager", rankManager);
		map.put("options", options);
		return map;
	}
}
