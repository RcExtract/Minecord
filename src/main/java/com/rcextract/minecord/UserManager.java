package com.rcextract.minecord;

import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

public interface UserManager {

	public Set<User> getUsers();
	public User getUser(int id);
	public Set<User> getUsers(String name);
	public User getUser(UUID player);
	public boolean isRegistered(UUID player);
	public User registerPlayer(OfflinePlayer player, Channel channel, Rank rank);
	@Deprecated
	public void unregisterPlayer(User user);
}
