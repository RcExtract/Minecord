package com.rcextract.minecord.permissions;

public enum Channel implements Permission {
	
	CREATE(10), RENAME(11), REDESCRIBE(12), LOCK(13), UNLOCK(14), JOIN_LOCKED(15), STAY_IN_LOCKED(16), DISBAND(17), CHAT(27);

	private int id;
	Channel(int id) {
		this.id = id;
	}
	public int getIdentifier() {
		return id;
	}

	public static Channel valueOf(int id) {
		for (Channel Channel : Channel.class.getEnumConstants()) 
			if (Channel.getIdentifier() == id) 
				return Channel;
		return null;
	}
}
