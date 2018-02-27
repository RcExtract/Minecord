package com.rcextract.minecord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.rcextract.minecord.event.user.UserEvent;

public class User extends Conversable implements RecordManager<UserEvent> {

	/*private final int id;
	private String name;*/
	private String nickName;
	//private String desc;
	private final OfflinePlayer player;
	//private final Set<ServerIdentity> identities;
	//private Listener main;
		public User(int id, String name, String desc, String nickName, OfflinePlayer player, Channel main, ChannelPreference ... ChannelPreferences) {
		super(id, name, desc, main, ChannelPreferences);
		this.nickName = nickName;
		this.player = player;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}
	public boolean isOnline() {
		return player.isOnline();
	}
	public Player getOnlinePlayer() {
		return player.getPlayer();
	}
	@Deprecated
	public Set<ServerIdentity> getIdentities() {
		return new HashSet<ServerIdentity>(/*identities*/);
	}
	@Deprecated
	public ServerIdentity getIdentity(Server server) {
		/*for (ServerIdentity identity : identities) 
			if (identity.getServer() == server) 
				return identity;*/
		return null;
	}
	public void clear() throws IllegalStateException {
		if (!(player.isOnline())) throw new IllegalStateException();
		for (int i = 0; i < 25; i++) player.getPlayer().sendMessage("");
	}
	public void applyMessage() {
		if (!(player.isOnline())) throw new IllegalStateException();
		Player player = getOnlinePlayer();
		ChannelPreference main = super.getChannelPreference(super.getMain());
		player.sendMessage(super.getMain().getName() + ":");
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