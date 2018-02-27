package com.rcextract.minecord.getters;

import java.util.Set;

import org.bukkit.OfflinePlayer;

import com.rcextract.minecord.User;

public interface UserGetter {

	public Set<User> getUsers();
	public User getUser(int id);
	public User getUser(OfflinePlayer player);
	public Set<User> getUsers(String name);
}
