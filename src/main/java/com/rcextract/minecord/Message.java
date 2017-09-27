package com.rcextract.minecord;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a message with some more information.
 * @deprecated
 */
@Deprecated
public class Message {

	private int id;
	private String message;
	private User sender;
	private Set<User> recipients;
	private Date date;
	protected Message(int id, String message, User sender, Set<User> recipients, Date date) {
		this.id = id;
		this.message = message;
		this.sender = sender;
		this.recipients = recipients == null ? new HashSet<User>() : recipients;
		this.date = date == null ? new Date() : date;
	}
	/**
	 * Gets the identifier of the message object.
	 * @return The identifier of the message object.
	 */
	public int getIdentifier() {
		return id;
	}
	/**
	 * Gets the message in String form.
	 * @return The message in String form.
	 * @deprecated The message sent is no longer a String.
	 */
	@Deprecated
	public String getMessage() {
		return message;
	}
	/**
	 * Sets the message in String form.
	 * @param message The message.
	 * @deprecated The message sent is no longer a String.
	 */
	@Deprecated
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * Gets the sender.
	 * @return The sender.
	 */
	public User getSender() {
		return sender;
	}
	/**
	 * Gets the users that are supposed to receive the message.
	 * @return The users that are supposed to receive the message.
	 */
	public Set<User> getRecipients() {
		return recipients;
	}
	/**
	 * Gets the time the message is sent.
	 * @return The time the message is sent.
	 */
	public Date getDate() {
		return date;
	}
}
