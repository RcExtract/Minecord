package com.rcextract.minecord;

import java.util.Set;

import org.bukkit.OfflinePlayer;

/**
 * A management system for Minecord Servers. This class only allows one instance to exist in the 
 * JVM, therefore instantiation is reserved for the main Minecord class.
 */
public interface ServerManager {

	/**
	 * Gets all registered servers.
	 * @return All registered servers.
	 */
	public Set<Server> getServers();
	/**
	 * Gets a server by its identifier.
	 * @param id The identifier of the target server.
	 * @return The server identified by the integer. Null if not found.
	 */
	public Server getServer(int id);
	/**
	 * Gets a server by its name.
	 * @param name The name of the target server.
	 * @return The server named by the string. Null if not found.
	 */
	public Server getServer(String name);
	/**
	 * Gets a server by if the player is inside.
	 * @param player The parameter of members of the target server.
	 * @return The server the player is inside, regardless of its online state. Null if not found.
	 */
	public Server getServer(OfflinePlayer player);
	/**
	 * Gets a server by if the user is inside.
	 * @param user The parameter of members of the target server.
	 * @return The server the user is inside, regardless of its online state. Null if not found.
	 */
	public Server getServer(User user);
	/**
	 * Gets a server by if the channel is inside.
	 * @param channel The parameter of members of the target server.
	 * @return The server the channel is inside. Null if not found.
	 */
	public Server getServer(Channel channel);
	/**
	 * Gets the default server in use. All players outside a server will be automatically assigned 
	 * to this server.
	 * @return The default server.
	 */
	public Server getMain();
	/**
	 * Creates a server.
	 * @param name The name of the server. Null value is not allowed.
	 * @param desc The description of the server. Null for default setting.
	 * @param approvement The approvement requirement to join the server. Null for default setting.
	 * @param invitation The invitation requirement to join the server. Null for default setting.
	 * @param channelManager The channel manager to help the server. Put null for new channel 
	 * manager. Putting a channel manager of another server makes two servers share the same 
	 * channel manager.
	 * @param rankManager The rank manager to help the server. Put null for new rank manager. 
	 * Putting a rank manager of another server makes two servers share the same rank manager.
	 * @return The server created.
	 * @throws DuplicatedException If the name is used by another server.
	 */
	public Server createServer(String name, String desc, Boolean approvement, Boolean invitation, ChannelManager channelManager, RankManager rankManager) throws DuplicatedException;
}
