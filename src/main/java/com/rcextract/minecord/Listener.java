package com.rcextract.minecord;

import java.util.List;

public class Listener implements Cloneable {

	private final Channel channel;
	private boolean notify;
	private int index;
	public Listener(Channel channel, boolean notify, int index) {
		this.channel = channel;
		this.notify = notify;
		this.index = index;
	}
	public Channel getChannel() {
		return channel;
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
	public User getUser() {
		for (User user : Minecord.getUserManager().getUsers()) 
			if (user.getIdentity(this) != null) 
				return user;
		return null;
	}
	public boolean isMain() {
		return getUser().getMain() == this;
	}
	public Listener clone() {
		try {
			return (Listener) super.clone();
		} catch (CloneNotSupportedException e) {
			//This exception is never thrown.
			return null;
		}
	}
}
