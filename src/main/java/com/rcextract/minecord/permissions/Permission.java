package com.rcextract.minecord.permissions;

public interface Permission {

	public static Permission valueOf(int id) {
		for (Server server : Server.class.getEnumConstants()) 
			if (server.getIdentifier() == id) 
				return server;
		for (Channel channel : Channel.class.getEnumConstants()) 
			if (channel.getIdentifier() == id) 
				return channel;
		for (Rank rank : Rank.class.getEnumConstants()) 
			if (rank.getIdentifier() == id) 
				return rank;
		return null;
	}
	public int getIdentifier();
}
