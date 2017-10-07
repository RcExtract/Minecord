package com.rcextract.minecord;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.rcextract.minecord.event.ChannelSwitchEvent;
import com.rcextract.minecord.event.UserEvent;

public class User implements RecordManager<UserEvent> {

	private int id;
	private String name;
	private String nickname;
	private String desc;
	private UUID player;
	private Channel channel;
	private Rank rank;
	protected User(int id, String name, String nickname, String desc, UUID player, Channel channel, Rank rank) {
		this.id = id;
		this.name = name;
		this.nickname = nickname == null ? name : nickname;
		this.desc = desc;
		this.player = player;
		this.channel = channel;
		this.setRank(rank);
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
	public Channel getChannel() {
		return channel;
	}
	public UUID getUUID() {
		return player;
	}
	public void setUUID(UUID player) {
		this.player = player;
	}
	/**
	 * Assigns a user to this channel. This will override the current location of the user, 
	 * meaning the user will be removed from the previous channel, after the user successfully 
	 * joined the channel, defined by the return value of this method.
	 * @param user The target user.
	 * @return If the process succeeded.
	 */
	public boolean setChannel(Channel channel) {
		ChannelSwitchEvent event = new ChannelSwitchEvent(channel == null ? Minecord.getServerManager().getServer("default").getChannelManager().getMainChannel() : channel, this);
		Bukkit.getPluginManager().callEvent(event);
		if (!(event.isCancelled())) {
			this.channel = event.getChannel();
			return true;
		}
		return false;
	}
	public Rank getRank() {
		return rank;
	}
	public void setRank(Rank rank) {
		if (rank == null) rank = Minecord.getServerManager().getServer("default").getRankManager().getMain();
		this.rank = rank;
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
