package com.rcextract.minecord;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.rcextract.minecord.event.UserEvent;

public class User implements RecordManager<UserEvent> {

	private int id;
	private String name;
	private String nickname;
	private String desc;
	private OfflinePlayer player;
	private Channel channel;
	private Rank rank;
	protected User(int id, String name, String nickname, String desc, OfflinePlayer player, Channel channel, Rank rank) {
		this.id = id;
		this.name = name;
		this.nickname = nickname == null ? name : nickname;
		this.desc = desc;
		this.player = player;
		this.channel = channel;
		this.rank = rank;
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
	public OfflinePlayer getPlayer() {
		return player;
	}
	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}
	public boolean isOnline() {
		return player.isOnline();
	}
	public Player getOnlinePlayer() {
		return player.getPlayer();
	}
	/**
	 * Assigns a user to this channel. This will override the current location of the user, 
	 * meaning the user will be removed from the previous channel, after the user successfully 
	 * joined the channel, defined by the return value of this method.
	 * @param user The target user.
	 */
	public void setChannel(Channel channel) {
		if (channel == null) channel = Minecord.getServerManager().getMain().getChannelManager().getMainChannel();
		this.channel = channel;
		Minecord.updateMessage(this, true);
	}
	public Rank getRank() {
		return rank;
	}
	public void setRank(Rank rank) {
		if (rank == null) rank = Minecord.getServerManager().getMain().getRankManager().getMain();
		this.rank = rank;
	}
	public void clear() throws IllegalStateException {
		if (!(player.isOnline())) throw new IllegalStateException();
		for (int i = 0; i < 25; i++) player.getPlayer().sendMessage("");
	}
	public void applyRank() throws IllegalStateException {
		for (Permission permission : rank.getPermissions()) 
			Minecord.getPermissionManager().playerAdd(null, player, permission.getName());
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
