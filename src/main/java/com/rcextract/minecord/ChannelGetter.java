package com.rcextract.minecord;

import com.rcextract.minecord.utils.ComparativeSet;

public interface ChannelGetter {

	public ComparativeSet<Channel> getChannels();
	/**
	 * Gets a Channel by its identifier.
	 * @param id The identifier of the target Channel.
	 * @return The Channel identified by the integer. Null if not found.
	 */
	public Channel getChannel(int id);
	/**
	 * Gets a Channel by its name.
	 * @param name The name of the target Channel.
	 * @return The Channel named by the string. Null if not found.
	 */
	public Channel getChannel(String name);
}
