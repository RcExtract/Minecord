package com.rcextract.minecord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.rcextract.minecord.event.ChannelCreateEvent;

/**
 * A management system for channels in a server. Currently a channel manager can only helps a 
 * server, but servers can share the same channel manager in the future.
 */
public class ChannelManager {

	private Channel main;
	private Set<Channel> channels;
	/**
	 * Creates a channel manager.
	 * @param channels The channels to be imported at instantiation. There is no difference between
	 * importing channels and adding channels each.
	 */
	public ChannelManager(Channel ... channels) {
		this.channels = new HashSet<Channel>(Arrays.asList(channels));
	}
	/**
	 * Gets the server this channel manager helps.
	 * @return
	 */
	public Server getServer() {
		for (Server server : Minecord.getServerManager().getServers()) if (server.getChannelManager() == this) return server;
		return null;
	}
	/**
	 * Gets the default channel a user joins when first joins the server. Instant invites are 
	 * excepted.
	 * @return The default channel.
	 */
	public Channel getMainChannel() {
		return main;
	}
	/**
	 * Sets the default channel a user joins when first joins the server. Instant invites are 
	 * excepted.
	 * @param main The default channel.
	 * @throws IllegalArgumentException Thrown if the channel isn't managed by this channel manager.
	 */
	public void setMainChannel(Channel main) {
		if (!(channels.contains(main))) throw new IllegalArgumentException();
		this.main = main;
	}
	/**
	 * Gets all registered channels. Modifying the HashSet does not affect anything.
	 * @return All registered channels.
	 */
	public Set<Channel> getChannels() {
		return new HashSet<Channel>(channels);
	}
	protected Set<Channel> getModifiableChannels() {
		return channels;
	}
	/**
	 * Gets a channel by its identifier.
	 * @param id The identifier of the target channel.
	 * @return The channel identified by the integer. Null if not found.
	 */
	public Channel getChannel(int id) {
		for (Channel channel : channels) if (channel.getIdentifier() == id) return channel;
		return null;
	}
	/**
	 * Gets a channel by its name.
	 * @param name The name of the target channel.
	 * @return The channel named by the string. Null if not found.
	 */
	public Channel getChannel(String name) {
		for (Channel channel : channels) if (channel.getName().equals(name)) return channel;
		return null;
	}
	/**
	 * Gets a channel by if the player is inside.
	 * @param user The paramater of members of the target channel.
	 * @return The server the player is inside, regardless of its online state. Null if not found.
	 */
	public Channel getChannel(OfflinePlayer player) {
		User user = Minecord.getUserManager().getUser(player);
		if (user == null) return null;
		return getChannel(user);
	}
	/**
	 * Gets a channel by if the user is inside.
	 * @param user The paramater of members of the target channel.
	 * @return The server the user is inside, regardless of its online state. Null if not found.
	 */
	public Channel getChannel(User user) {
		for (Channel channel : channels) 
			if (channel.getMembers().contains(user)) 
				return channel;
		return null;
	}
	/**
	 * Creates a channel.
	 * @param name The name of the channel. Null value is not allowed.
	 * @param desc The description of the channel. Null for default setting.
	 * @return The channel created.
	 * @throws DuplicatedException If the name is used by another channel.
	 */
	public Channel createChannel(String name, String desc) throws DuplicatedException {
		Validate.notNull(name);
		if (getChannel(name) != null) throw new DuplicatedException();
		if (desc == null) desc = "A default server description.";
		int id = ThreadLocalRandom.current().nextInt();
		while (getChannel(id) != null || id < 0) id = ThreadLocalRandom.current().nextInt();
		Channel channel = new Channel(id, name, desc, false);
		ChannelCreateEvent event = new ChannelCreateEvent(getServer(), channel);
		Bukkit.getPluginManager().callEvent(event);
		if (!(event.isCancelled())) {
			channels.add(channel);
			if (main == null) channel = main;
		}
		return channel;
	}
	public boolean disbandChannel(Channel target, Channel main) {
		if (target == this.main) this.main = main;
		return channels.remove(target);
	}
	public Channel initialize() {
		if (channels.isEmpty()) 
			try {
				Channel channel = createChannel("general", null);
				return channel;
			} catch (DuplicatedException e) {
				//This exception is never thrown.
				e.printStackTrace();
			}
		return null;
	}
}