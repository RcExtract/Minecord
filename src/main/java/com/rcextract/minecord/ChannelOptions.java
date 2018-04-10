package com.rcextract.minecord;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import com.rcextract.minecord.sql.DatabaseSerializable;
import com.rcextract.minecord.sql.SerializableAs;
import com.rcextract.minecord.utils.ArrayMap;

@XmlAccessorType(XmlAccessType.FIELD)
@SerializableAs("coptions")
public class ChannelOptions implements Cloneable, DatabaseSerializable {

	@XmlID
	@XmlIDREF
	private final Channel channel;
	private boolean notify;
	private int index;
	
	public ChannelOptions(Channel channel, boolean notify, int index) {
		this.channel = channel;
		this.notify = notify;
		this.index = index;
	}
	
	public ChannelOptions(ArrayMap<String, Object> map) {
		Map<String, Object> internal = map.toMap();
		this.channel = (Channel) internal.get("channel");
		this.notify = (boolean) internal.get("notify");
		this.index = (int) internal.get("index");
	}
	
	public Channel getChannel() {
		return channel;
	}
	public Server getServer() {
		return channel.getServer();
	}
	public boolean isNotify() {
		return notify;
	}
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public void increment() {
		index++;
	}
	public void decrement() {
		index--;
	}
	public Message getLatestReadMessage() {
		if (index == 0) return null;
		return channel.getMessages().get(index - 1);
	}
	public Message getOldestUnreadMessage() {
		return channel.getMessages().get(index);
	}
	public List<Message> getUnreadMessages() {
		return channel.getMessages().subList(index, channel.getMessages().size() - 1);
	}
	public int getIndex() {
		return index;
	}
	public int unreadMessagesCount() {
		return channel.getMessages().size() - 1 - index;
	}

	public Conversable getConversable() {
		for (Sendable sendable : Minecord.getSendables()) 
			if (sendable instanceof Conversable && ((Conversable) sendable).getChannelOptions().contains(this)) 
				return (Conversable) sendable;
		return null;
	}
	public ChannelOptions clone() {
		try {
			return (ChannelOptions) super.clone();
		} catch (CloneNotSupportedException e) {
			//This exception is never thrown.
			return null;
		}
	}
	@Override
	public ArrayMap<String, Object> serialize() {
		ArrayMap<String, Object> map = new ArrayMap<String, Object>();
		map.put("channel", channel);
		map.put("notify", notify);
		map.put("index", index);
		return map;
	}
	
}
