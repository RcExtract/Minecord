package com.rcextract.minecord.permissions;

public enum Server implements Permission {
	
	APPROVE(-3), INVITE(-2), BAN(-1), KICK(0), RENAME(1), REDESCRIBE(2), CHANGE_APPROVEMENT_REQUIREMENT(3), CHANGE_INVITATIOIN_REQUIREMENT(4), SET_PERMANENT(5), LOCK(6), UNLOCK(7), JOIN_LOCKED(8), STAY_IN_LOCKED(9);

	private int id;
	Server(int id) {
		this.id = id;
	}
	public int getIdentifier() {
		return id;
	}

	public static Server valueOf(int id) {
		for (Server server : Server.class.getEnumConstants()) 
			if (server.getIdentifier() == id) 
				return server;
		return null;
	}
}
