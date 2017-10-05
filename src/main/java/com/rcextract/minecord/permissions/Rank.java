package com.rcextract.minecord.permissions;

public enum Rank implements Permission {
	
	CREATE(18), RENAME(19), REDESCRIBE(20), RETAG(21), SET_ADMIN(22), SET_OVERRIDE_OBJECT_PERMISSIONS(23), ADD_PERMISSION(24), REVOKE_PERMISSION(25), MODIFY_PERMISSION(26);

	private int id;
	Rank(int id) {
		this.id = id;
	}
	public int getIdentifier() {
		return id;
	}

	public static Rank valueOf(int id) {
		for (Rank rank : Rank.class.getEnumConstants()) 
			if (rank.getIdentifier() == id) 
				return rank;
		return null;
	}
}
