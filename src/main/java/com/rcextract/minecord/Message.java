package com.rcextract.minecord;

import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

public class Message {

	@XmlID
	private final UUID id = UUID.randomUUID();
	@XmlIDREF
	private Sendable sender;
	private String message;
	private final Date date;
	public Message(Sendable sender, String message, Date date) {
		this.sender = sender;
		this.message = message;
		this.date = date;
	}

	public UUID getIdentifier() {
		return id;
	}
	public Sendable getSender() {
		return sender;
	}
	public void setSender(Sendable sender) {
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
