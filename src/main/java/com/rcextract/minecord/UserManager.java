package com.rcextract.minecord;

import java.util.Set;

import org.bukkit.OfflinePlayer;

public interface UserManager {

	public Set<User> getUsers();
	public User getUser(int id);
	public Set<User> getUsers(String name);
	public User getUser(OfflinePlayer player);
	public boolean isRegistered(OfflinePlayer player);
	public void registerPlayer(OfflinePlayer player);
	@Deprecated
	public void unregisterPlayer(User user);
}
