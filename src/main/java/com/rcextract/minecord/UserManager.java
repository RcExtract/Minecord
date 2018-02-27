package com.rcextract.minecord;

import org.bukkit.OfflinePlayer;

import com.rcextract.minecord.getters.UserGetter;

public interface UserManager extends UserGetter {

	public boolean isRegistered(OfflinePlayer player);
	/**
	 * Registers a player and returns the fresh generated user instance.
	 * @param player The target player.
	 * @param channel The default channel.
	 * @param rank The default rank.
	 * @return The fresh generated user instance associated to the player. Null if the player is registered.
	 */
	@Deprecated
	public User registerPlayer(OfflinePlayer player, Channel channel, Rank rank) throws IllegalStateException;
	@Deprecated
	public User registerPlayer(String name, String nickname, String desc, OfflinePlayer player/*, Listener main, ServerIdentity ... identities*/);
	/**
	 * Registers a player and returns the fresh generated user instance.
	 * @param player The target player.
	 * @param channel The default channel.
	 * @param rank The default rank.
	 * @return The user instance associated to the player regardless to freshly generated.
	 */
	@Deprecated
	public User registerPlayerIfAbsent(OfflinePlayer player, Channel channel, Rank rank);
	@Deprecated
	public void unregisterPlayer(User user);
}
