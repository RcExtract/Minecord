package com.rcextract.minecord;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.rcextract.minecord.utils.ArrayMap;

import me.clip.placeholderapi.PlaceholderAPI;

public class User extends Conversable {

	private String nickname;
	private final OfflinePlayer player;
	
	public User(int id, String name, String desc, String nickName, OfflinePlayer player, Channel main, ChannelOptions ... options) {
		super(id, name, desc, main, options);
		this.nickname = nickName;
		this.player = player;
	}
		
	public User(ArrayMap<String, Object> map) {
		super(map);
		this.nickname = (String) map.valueList().get(7);
		this.player = (OfflinePlayer) map.valueList().get(8);
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
			player.sendMessage(/*Minecord.getPlugin().applyFormat(message.getSender().getName(), message.getSender().getNickName(), message.getSender().getPlayer().getUniqueId().toString(), message.getMessage(), message.getDate().toString())*/
					PlaceholderAPI.setPlaceholders(player, Minecord.getFormat().replaceAll("%minecord_message%", message.getMessage())));
		main.setIndex(main.getChannel().getMessages().size() - main.getUnreadMessages().size());
	}
	@Override
	public void chat(String message) throws IllegalStateException {
		if (!(player.isOnline())) throw new IllegalStateException();
		player.getPlayer().chat(message);
	}

	@Override
	public ArrayMap<String, Object> serialize() {
		ArrayMap<String, Object> map = super.serialize();
		map.put("nickname", nickname);
		map.put("player", player);
		return map;
	}

}