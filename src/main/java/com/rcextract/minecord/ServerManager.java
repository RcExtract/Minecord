package com.rcextract.minecord;

import com.rcextract.minecord.getters.ServerGetter;

/**
 * A management system for Minecord Servers. This class only allows one instance to exist in the 
 * JVM, therefore instantiation is reserved for the main Minecord class.
 */
public interface ServerManager extends ServerGetter {
	
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
	public Server createServer(String name, String desc, Boolean approvement, Boolean invitation, RankManager rankManager, Channel main, Channel ... channels) throws DuplicatedException;
}
