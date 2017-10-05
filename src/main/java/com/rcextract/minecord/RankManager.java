package com.rcextract.minecord;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.rcextract.minecord.event.RankCreateEvent;
import com.rcextract.minecord.permissions.Permission;

/**
 * The rank manager helps to manage permission of each user. There is currently no plan on how 
 * it will work, or even it will be removed. It is deprecated temporarily due to no usage.
 */
public class RankManager {

	protected Set<Rank> ranks;
	private Rank main;
	
	public RankManager(Rank ... ranks) {
		this.ranks = new HashSet<Rank>(Arrays.asList(ranks));
	}
	public Server getServer() {
		for (Server server : Minecord.getServerManager().getServers()) 
			if (server.getRankManager() == this) return server;
		return null;
	}
	public Rank getRank(String name) {
		for (Rank rank : ranks) 
			if (rank.getName().equals(name)) 
				return rank;
		return null;
	}
	public Rank getRankByTag(String tag) {
		for (Rank rank : ranks) 
			if (rank.getTag().equals(tag)) 
				return rank;
		return null;
	}
	public Set<Rank> getRanks() {
		return ranks;
	}
	public Rank getMain() {
		return main;
	}
	public void setMain(Rank main) {
		this.main = main;
	}
	public Rank createRank(String name, String desc, String tag, boolean admin, boolean override, Permission ... permissions) throws DuplicatedException {
		Validate.notNull(name);
		if (tag == null) tag = name;
		if (getRankByTag(tag) != null) throw new DuplicatedException();
		if (desc == null) desc = "A default rank description.";
		Rank rank = new Rank(name, desc, tag, admin, override, new HashSet<Permission>(Arrays.asList(permissions)));
		RankCreateEvent event = new RankCreateEvent(getServer(), rank);
		if (!(event.isCancelled())) {
			ranks.add(rank);
			if (main == null) main = rank;
		}
		return rank;
	}
	public boolean deleteRank(Rank target, Rank main) {
		if (target == this.main) this.main = main;
		return ranks.remove(target);
	}
	public Rank initialize() {
		if (ranks.isEmpty()) {
			try {
				Rank rank = createRank("member", null, null, false, false);
				return rank;
			} catch (DuplicatedException e) {
				//This exception is never thrown.
				e.printStackTrace();
			}
		}
		return null;
	}
}
