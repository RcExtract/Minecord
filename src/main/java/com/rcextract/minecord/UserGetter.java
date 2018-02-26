package com.rcextract.minecord;

import java.util.Set;

import org.bukkit.OfflinePlayer;

public interface UserGetter {

	public Set<User> getUsers();
	public User getUser(int id);
	public User getUser(OfflinePlayer player);
	public Set<User> getUsers(String name);
}
