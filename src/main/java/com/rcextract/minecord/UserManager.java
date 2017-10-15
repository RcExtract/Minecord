package com.rcextract.minecord;

import java.util.Set;

import org.bukkit.OfflinePlayer;

public interface UserManager {

	public Set<User> getUsers();
	public User getUser(int id);
	public Set<User> getUsers(String name);
	public User getUser(OfflinePlayer player);
	public boolean isRegistered(OfflinePlayer player);
	/**
	 * Registers a player and returns the fresh generated user instance.
	 * @param player The target player.
	 * @param channel The default channel.
	 * @param rank The default rank.
	 * @return The fresh generated user instance associated to the player. Null if the player is registered.
	 */
	public User registerPlayer(OfflinePlayer player, Channel channel, Rank rank) throws IllegalStateException;
	/**
	 * Registers a player and returns the fresh generated user instance.
	 * @param player The target player.
	 * @param channel The default channel.
	 * @param rank The default rank.
	 * @return The user instance associated to the player regardless to freshly generated.
	 */
	public User registerPlayerIfAbsent(OfflinePlayer player, Channel channel, Rank rank);
	@Deprecated
	public void unregisterPlayer(User user);
}
