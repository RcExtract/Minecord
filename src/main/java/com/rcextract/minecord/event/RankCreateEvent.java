package com.rcextract.minecord.event;

import org.bukkit.event.HandlerList;

import com.rcextract.minecord.Rank;
import com.rcextract.minecord.Server;
import com.rcextract.minecord.event.server.ServerEvent;

public class RankCreateEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
	private Rank rank;
	public RankCreateEvent(Server server, Rank rank) {
		super(server);
		this.rank = rank;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public Rank getRank() {
		return rank;
	}

}
