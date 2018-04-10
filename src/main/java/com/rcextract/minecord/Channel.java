package com.rcextract.minecord;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import com.rcextract.minecord.sql.DatabaseSerializable;
import com.rcextract.minecord.sql.SerializableAs;
import com.rcextract.minecord.utils.ArrayMap;
import com.rcextract.minecord.utils.ComparativeList;

/**
 * In Minecord, a Channel groups up a set of users, usually by small topic of a big topic. Main 
 * settings and customizations are done in the server, but a channel can has its own settings and 
 * customizations, unfollowing the server.
 * <p>
 * Both identifier and name are primary keys for developers and users to refer so they are 
 * unique. Identifier is unchangeable, name is changeable but must not be used by other channels. 
 * However, channels with the same identifier or name can appear in different servers.
 * <p>
 * If locked, all players are kicked from the channel and no one can join it. Only players 
 * with custom permission {@code MANAGE_CHANNELS} can access, lock or unlock the channel.
 * <p>
 * A {@link ChannelRecordManager} helps a server to manage records. It is separated into a class
 * for merging purposes which feature will be provided in the future.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@SerializableAs("channel")
public class Channel implements DatabaseSerializable {

	@XmlID
	private final UUID id = UUID.randomUUID();
	private String name;
	private String desc;
	private boolean locked;
	@XmlIDREF
	private final ComparativeList<Message> messages = new ComparativeList<Message>(message -> getMessage(message.getIdentifier()) == null);
	
	public Channel(String name, String desc, boolean locked) {
		this.name = name;
		this.desc = desc;
		this.locked = locked;
	}
	
	public Channel(ArrayMap<String, Object> map) {
		Map<String, Object> internal = map.toMap();
		this.name = (String) internal.get("name");
		this.desc = (String) internal.get("desc");
		this.locked = (boolean) internal.get("locked");
	}
	
	/**
	 * Gets the identifier of the channel.
	 * @return The identifier of the channel.
	 */
	public UUID getIdentifier() {
		return id;
	}
	
	/**
	 * Gets the name of the channel.
	 * @return The name of the channel.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Renames the channel.
	 * @param name The new name of the channel.
	 * @throws DuplicatedException If the name is used by another channel.
	 */
	public void setName(String name) throws DuplicatedException {
		this.name = name;
	}
	
	/**
	 * Gets the description of the channel.
	 * @return The description of the channel.
	 */
	public String getDescription() {
		return desc;
	}
	
	/**
	 * Sets the description of the channel.
	 * @param desc The new description of the channel.
	 */
	public void setDescription(String desc) {
		this.desc = desc;
	}
	
	/**
	 * Determines if the channel is locked
	 * @return Whether if the channel is locked.
	 */
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Server getServer() {
		for (Server server : Minecord.getServers()) 
			if (server.getChannels().contains(this)) 
				return server;
		return null;
	}

	/**
	 * Determines if this channel is main in the channel manager defined.
	 * @return If this channel is main in the channel manager defined.
	 */
	public boolean isMain() {
		return getServer().getMainChannel() == this;
	}
	
	public List<Message> getMessages() {
		return messages;
	}
	
	public Message getLatestMessage() {
		if (messages.isEmpty()) return null;
		return messages.get(messages.size() - 1);
	}
	
	public Message getOldestMessage() {
		if (messages.isEmpty()) return null;
		return messages.get(0);
	}
	
	public Message getMessage(UUID id) {
		for (Message message : messages) 
			if (message.getIdentifier().equals(id)) 
				return message;
		return null;
	}
	
	public Message getMessage(String message) {
		for (Message messageobj : messages) 
			if (messageobj.getMessage().equals(message)) 
				return messageobj;
		return null;
	}
	
	public Set<Sendable> getViewers() {
		return Minecord.getSendables().getIf(sendable -> sendable.getMain() == this);
	}
	
	@Override
	public ArrayMap<String, Object> serialize() {
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		map.put("name", name);
		map.put("desc", desc);
		map.put("locked", locked);
		return map;
	}

}
