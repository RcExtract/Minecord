package com.rcextract.minecord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rcextract.minecord.event.ServerEvent;

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
 */
public class Server implements RecordManager<ServerEvent> {

	private int id;
	private String name;
	private String desc;
	private boolean approvement;
	private boolean invitation;
	private boolean permanent;
	private boolean locked;
	private ChannelManager channelManager;
	@Deprecated
	private RankManager rankManager;
	/**
	 * This constructor is reserved for initialization.
	 */
	protected Server(int id, String name, String desc, boolean approvement, boolean invitation, boolean permanent, boolean locked, ChannelManager channelManager, @SuppressWarnings("deprecation") RankManager rankManager) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.approvement = approvement;
		this.invitation = invitation;
		this.permanent = permanent;
		this.locked = locked;
		this.channelManager = channelManager;
		this.rankManager = rankManager;
		if (channelManager.getChannels().size() == 0)
			try {
				channelManager.createChannel("general", null);
			} catch (DuplicatedException e) {
				// This exception is never thrown.
				e.printStackTrace();
			}
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
	 * @throws DuplicatedException If the name is used by another server.
	 */
	public void setName(String name) throws DuplicatedException {
		if (Minecord.getServerManager().getServer(name) != null) throw new DuplicatedException();
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
	public boolean needApprovement() {
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
	public boolean needInvitation() {
		return invitation;
	}
	/**
	 * Changes the state of invitation requirement.
	 * @param approvement The new state of invitation requirement.
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
	 */
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}
	/**
	 * Determines if the server is ready to join.
	 * @return The reversed boolean of locked.
	 */
	public boolean ready() {
		return !locked;
	}
	/**
	 * Locks the server. All functions should be disabled for normal users and they all should 
	 * be kicked.
	 */
	public void lock() {
		this.locked = true;
	}
	/**
	 * Unlocks the server. All functions should be enabled for normal users according to the 
	 * rank manager. Only users with permission {@code minecord.managelock.server} should have
	 * access to unlocking.
	 */
	public void unlock() {
		this.locked = false;
	}
	/**
	 * Gets all online players joined the server. Modifying this HashSet does not affect anything.
	 * @return The total online players added from channels.
	 */
	public Set<User> getMembers() {
		Set<User> onlines = new HashSet<User>();
		for (Channel channel : channelManager.getChannels()) 
			onlines.addAll(channel.getMembers());
		return onlines;
	}
	/**
	 * Removes a player from this channel. This method simply invokes the User.switchChannel(Channel)
	 * method, redirecting the user to the default channel.
	 * @param user The target user.
	 * @return If the removal succeeded.
	 */
	public boolean remove(User user) {
		return user.switchChannel(null);
	}
	/**
	 * Gets the channel manager.
	 * @return The channel manager.
	 */
	public ChannelManager getChannelManager() {
		return channelManager;
	}
	/**
	 * Sets the channel manager.
	 * <p>
	 * This method is reserved for initialization.
	 * @param channelManager The channel manager.
	 */
	protected void setChannelManager(ChannelManager channelManager) {
		this.channelManager = channelManager;
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
	public List<ServerEvent> getRecords() {
		return Minecord.getRecordManager().getRecords(ServerEvent.class);
	}
	@Override
	public <E extends ServerEvent> List<E> getRecords(Class<E> clazz) {
		return Minecord.getRecordManager().getRecords(clazz);
	}
	@Override
	public ServerEvent getLatestRecord() {
		return Minecord.getRecordManager().getLatestRecord(ServerEvent.class);
	}
	@Override
	public <E extends ServerEvent> E getLatestRecord(Class<E> clazz) {
		return Minecord.getRecordManager().getLatestRecord(clazz);
	}
	@Override
	public ServerEvent getOldestRecord() {
		return Minecord.getRecordManager().getOldestRecord(ServerEvent.class);
	}
	@Override
	public <E extends ServerEvent> E getOldestRecord(Class<E> clazz) {
		return Minecord.getRecordManager().getOldestRecord(clazz);
	}
	
	
}
