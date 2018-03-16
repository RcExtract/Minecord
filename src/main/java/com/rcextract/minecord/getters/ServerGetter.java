package com.rcextract.minecord.getters;

import java.util.Set;

import com.rcextract.minecord.Channel;
import com.rcextract.minecord.Sendable;
import com.rcextract.minecord.Server;
import com.rcextract.minecord.utils.ComparativeSet;

public interface ServerGetter {

	/**
	 * Gets all registered servers.
	 * @return All registered servers.
	 */
	public ComparativeSet<Server> getServers();
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
	 * Gets a server by if the user is inside.
	 * @param user The parameter of members of the target server.
	 * @return The server the user is inside, regardless of its online state. Null if not found.
	 */
	public Set<Server> getServers(Sendable sendable);
	/**
	 * Gets a server by if the channel is inside.
	 * @param channel The parameter of members of the target server.
	 * @return The server the channel is inside. Null if not found.
	 */
	public Server getServer(Channel channel);
}
