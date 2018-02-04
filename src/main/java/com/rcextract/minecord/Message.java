package com.rcextract.minecord;

import java.util.Date;

public class Message {

	private int id;
	private User sender;
	private String message;
	private final Date date;
	public Message(int id, User sender, String message, Date date) {
		this.id = id;
		this.sender = sender;
		this.message = message;
		this.date = date;
	}
	public int getIdentifier() {
		return id;
	}
	public void setIdentifier(int id) {
		this.id = id;
	}
	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getDate() {
		return date;
	}
}
