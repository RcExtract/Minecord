package com.rcextract.minecord.event.server;

import com.rcextract.minecord.Server;
import com.rcextract.minecord.event.MinecordEvent;

/**
 * Represents an event of a {@link Server}. This excludes the creation event of the server.
 */
public abstract class ServerEvent extends MinecordEvent {
	
	private final Server server;
	/**
	 * Constructs a new Server Event with a specific target.
	 * @param server The target server.
	 */
	public ServerEvent(Server server) {
		super();
		this.server = server;
	}
	/**
	 * Gets the target server.
	 * @return The target server.
	 */
	public Server getServer() {
		return server;
	}
}
