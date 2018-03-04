package com.rcextract.minecord;

import java.util.List;

public class ChannelOptions implements Cloneable {

	private final Channel channel;
	private boolean notify;
	private int index;
	public ChannelOptions(Channel channel, boolean notify, int index) {
		this.channel = channel;
		this.notify = notify;
		this.index = index;
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
	/*public User getUser() {
		for (User user : Minecord.getUserManager().getUsers()) 
			if (user.getChannelOptions().contains(this)) 
				return user;
		return null;
	}*/
	public ChannelOptions clone() {
		try {
			return (ChannelOptions) super.clone();
		} catch (CloneNotSupportedException e) {
			//This exception is never thrown.
			return null;
		}
	}
}
