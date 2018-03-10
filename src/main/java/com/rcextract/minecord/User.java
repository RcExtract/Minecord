package com.rcextract.minecord;

import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.rcextract.minecord.event.user.UserEvent;

public class User extends Conversable implements RecordManager<UserEvent> {

	private String nickname;
	private final OfflinePlayer player;
	
	public User(int id, String name, String desc, String nickName, OfflinePlayer player, Channel main, ChannelOptions ... options) {
		super(id, name, desc, main, options);
		this.nickname = nickName;
		this.player = player;
	}
		
	public User(Map<String, Object> map) {
		super(map);
		this.nickname = (String) map.get("nickname");
		this.player = (OfflinePlayer) map.get("player");
	}

	public String getNickName() {
		return nickname;
	}

	public void setNickName(String nickName) {
		this.nickname = nickName;
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
	public void clear() throws IllegalStateException {
		if (!(player.isOnline())) throw new IllegalStateException();
		for (int i = 0; i < 25; i++) player.getPlayer().sendMessage("");
	}
	public void applyMessage() {
		if (!(player.isOnline())) throw new IllegalStateException();
		Player player = getOnlinePlayer();
		ChannelOptions main = super.getChannelOptions(super.getMain());
		player.sendMessage(super.getMain().getName() + ":");
		for (Message message : main.getUnreadMessages()) 
			player.sendMessage(Minecord.applyFormat(message.getSender().getName(), message.getSender().getNickName(), message.getSender().getPlayer().getUniqueId().toString(), message.getMessage(), message.getDate().toString()));
		main.setIndex(main.getChannel().getMessages().size() - main.getUnreadMessages().size());
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("nickname", nickname);
		map.put("player", player);
		return map;
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