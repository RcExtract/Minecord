package com.rcextract.minecord.event;

import java.util.Date;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.rcextract.minecord.ServerManager;

/**
 * Represents an event of a {@link ServerManager}, also a super type of all Minecord events.
 * <p>
 * Records are no longer saved into SQL database due to unknown fields and complicated 
 * deserialization. Instead, they are saved into records.yml. in Yaml format.
 * <p>
 * This class is abstract and cannot be instantiated due to not exactly representing an action. 
 * It is just a superclass and defines the fields that must exist for any other Minecord related 
 * events.
 * <p>
 * This class should not be inherited outside of {@code com.rcextract.minecord} because this 
 * superclass is over general. All subclazz should represents an event related to a part of 
 * Minecord.
 */
public abstract class MinecordEvent extends Event implements Cancellable {

	private final Date date;
	private boolean cancelled;
	{
		//Minecord.getRecordManager().record(this);
	}
	/**
	 * Constructs a new MinecordEvent with default values: current time and not cancelled.
	 */
	public MinecordEvent() {
		this.date = new Date();
	}
	/**
	 * Gets the construction time.
	 * @return The construction time.
	 */
	public Date getDate() {
		return date;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
