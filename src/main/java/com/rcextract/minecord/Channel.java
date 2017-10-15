package com.rcextract.minecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rcextract.minecord.event.ChannelEvent;
import com.rcextract.minecord.event.UserMessageEvent;

/**
 * In Minecord, a Channel groups up a set of users, usually by small topic of a big topic. Main 
 * settings and customizations are done in the server, but a channel can has its own settings and 
 * customizations, unfollowing the server.
 * <p>
 * Both identifier and name are primary keys for developers and users to refer so they are 
 * unique. Identifier is unchangeable, name is changeable but must not be used by other channels. 
 * However, channels with the same identifier or name can appear in different servers.
 * <p>
 * If locked, all players are kicked from the channel and no one can join it. Only players 
 * with custom permission {@code MANAGE_CHANNELS} can access, lock or unlock the channel.
 * <p>
 * A {@link ChannelRecordManager} helps a server to manage records. It is separated into a class
 * for merging purposes which feature will be provided in the future.
 */
public class Channel implements RecordManager<ChannelEvent> {

	private int id;
	private String name;
	private String desc;
	private boolean locked;
	protected List<UserMessageEvent> recent;
	/**
	 * This constructor is reserved for initialization.
	 */
	protected Channel(int id, String name, String desc, boolean locked) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.locked = locked;
		this.recent = new ArrayList<UserMessageEvent>();
	}
	/**
	 * Gets the identifier of the channel.
	 * @return The identifier of the channel.
	 */
	public int getIdentifier() {
		return id;
	}
	/**
	 * Gets the name of the channel.
	 * @return The name of the channel.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Renames the channel.
	 * @param name The new name of the channel.
	 * @throws DuplicatedException If the name is used by another channel.
	 */
	public void setName(String name) throws DuplicatedException {
		ChannelManager cm = getChannelManager();
		if (cm != null && cm.getChannel(name) != null) throw new DuplicatedException();
		this.name = name;
	}
	/**
	 * Gets the description of the channel.
	 * @return The description of the channel.
	 */
	public String getDescription() {
		return desc;
	}
	/**
	 * Sets the description of the channel.
	 * @param desc The new description of the channel.
	 */
	public void setDescription(String desc) {
		this.desc = desc;
	}
	/**
	 * Determines if the channel is ready to join.
	 * @return The reversed boolean of locked.
	 */
	public boolean ready() {
		return !locked;
	}
	/**
	 * Locks the channel. All functions should be disabled for normal users and they all should 
	 * be kicked.
	 */
	public void lock() {
		this.locked = true;
	}
	/**
	 * Unlocks the channel. All functions should be enabled for normal users according to the 
	 * rank manager. Only users with custom permission {@code MANAGE_CHANNELS} should have
	 * access to unlocking.
	 */
	public void unlock() {
		this.locked = false;
	}
	/**
	 * Gets all online players joined the channel. Modifying this HashSet does not affect anything.
	 * @return All online players joined the channel.
	 */
	public Set<User> getMembers() {
		Set<User> users = new HashSet<User>();
		for (User user : Minecord.getUserManager().getUsers()) 
			if (user.getChannel() == this) 
				users.add(user);
		return users;
	}
	/**
	 * Gets the channel manager managing this channel.
	 * @return The channel manager managing this channel.
	 */
	public ChannelManager getChannelManager() {
		for (Server server : Minecord.getServerManager().getServers()) if (server.getChannelManager().getChannels().contains(this)) return server.getChannelManager();
		return null;
	}

	/**
	 * Determines if this channel is main in the channel manager defined.
	 * @return If this channel is main in the channel manager defined.
	 */
	public boolean isMain() {
		return getChannelManager().getMainChannel() == this;
	}
	@Override
	public List<ChannelEvent> getRecords() {
		return Minecord.getRecordManager().getRecords(ChannelEvent.class);
	}
	@Override
	public <E extends ChannelEvent> List<E> getRecords(Class<E> clazz) {
		return Minecord.getRecordManager().getRecords(clazz);
	}
	@Override
	public ChannelEvent getLatestRecord() {
		return Minecord.getRecordManager().getLatestRecord(ChannelEvent.class);
	}
	@Override
	public <E extends ChannelEvent> E getLatestRecord(Class<E> clazz) {
		return Minecord.getRecordManager().getLatestRecord(clazz);
	}
	@Override
	public ChannelEvent getOldestRecord() {
		return Minecord.getRecordManager().getOldestRecord(ChannelEvent.class);
	}
	@Override
	public <E extends ChannelEvent> E getOldestRecord(Class<E> clazz) {
		return Minecord.getRecordManager().getOldestRecord(clazz);
	}
	public List<UserMessageEvent> getMessages() {
		return recent;
	}
	public UserMessageEvent getLatestMessage() {
		if (recent.isEmpty()) return null;
		return recent.get(recent.size() - 1);
	}
	public UserMessageEvent getOldestMessage() {
		if (recent.isEmpty()) return null;
		return recent.get(0);
	}
	public UserMessageEvent getMessage(String message) {
		for (UserMessageEvent event : recent) 
			if (event.getMessage().equals(message)) 
				return event;
		return null;
	}
}
