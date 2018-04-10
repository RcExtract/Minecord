package com.rcextract.minecord;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@SerializableAs("server")
public class Server implements DatabaseSerializable {

	@XmlID
	private final UUID id = UUID.randomUUID();
	private String name;
	private String desc;
	private boolean approvement;
	private boolean invitation;
	private boolean permanent;
	private boolean locked;
	@XmlIDREF
	private final Set<Channel> channels;
	@XmlIDREF
	private Channel channel;
	@XmlIDREF
	private final Set<Rank> ranks;
	@XmlIDREF
	private Rank rank;
	@XmlIDREF
	private final ComparativeSet<SendableOptions> options;

	public Server(String name, String desc, boolean approvement, boolean invitation, boolean permanent, boolean locked, Channel channel, Collection<Channel> channels, Rank rank, Collection<Rank> ranks, Collection<SendableOptions> options) {
		this.name = name;
		this.desc = desc;
		this.approvement = approvement;
		this.invitation = invitation;
		this.permanent = permanent;
		this.locked = locked;
		this.channels = new HashSet<Channel>(channels);
		this.channel = channel;
		this.ranks = new HashSet<Rank>(ranks);
		this.rank = rank;
		this.options = new ComparativeSet<SendableOptions>(options);
		this.options.setFilter(option -> this.options.getIf(o -> o.getSendable() == option.getSendable()).isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	public Server(ArrayMap<String, Object> map) {
		Map<String, Object> internal = map.toMap();
		this.name = (String) internal.get("name");
		this.desc = (String) internal.get("desc");
		this.approvement = (boolean) internal.get("approvement");
		this.invitation = (boolean) internal.get("invitation");
		this.permanent = (boolean) internal.get("permanent");
		this.locked = (boolean) internal.get("locked");
		this.channels = new HashSet<Channel>((Collection<Channel>) internal.get("channels"));
		this.channel = (Channel) internal.get("channel");
		this.ranks = new HashSet<Rank>((Collection<Rank>) internal.get("ranks"));
		this.rank = (Rank) internal.get("rank");
		this.options = new ComparativeSet<SendableOptions>((Collection<SendableOptions>) internal.get("options"));
		options.setFilter(option -> options.getIf(o -> o.getSendable() == option.getSendable()).isEmpty());
	}
	/**
	 * Gets the identifier of the server.
	 * @return The identifier of the server.
	 */
	public UUID getIdentifier() {
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

	public Set<Channel> getChannels() {
		return channels;
	}
	
	public Channel getMainChannel() {
		return channel;
	}
	
	public void setMainChannel(Channel main) {
		if (!(channels.contains(main))) throw new IllegalArgumentException();
		this.channel = main;
	}
	
	public Set<Rank> getRanks() {
		return ranks;
	}
	public Rank getMainRank() {
		return rank;
	}
	public void setMainRank(Rank main) {
		if (!(ranks.contains(main))) throw new IllegalArgumentException();
		this.rank = main;
	}
	public ComparativeSet<SendableOptions> getSendableOptions() {
		return options;
	}
	public Channel initialize() {
		if (channels.isEmpty()) {
			Channel channel = new Channel("general", "A default channel description.", false);
			channels.add(channel);
			return channel;
		}
		return null;
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
		map.put("channel", channel);
		map.put("ranks", ranks);
		map.put("rank", rank);
		map.put("options", options);
		return map;
	}
}
