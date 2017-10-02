package com.rcextract.minecord;

public interface Permission {

	public static enum Channel implements Permission {
		
		CREATE, RENAME, REDESCRIBE, LOCK, UNLOCK, JOIN_LOCKED, STAY_IN_LOCKED, DISBAND;
	}
	public static enum Server implements Permission {
		
		RENAME, REDESCRIBE, CHANGE_INVITATION_REQUIREMENT, CHANGE_APPROVEMENT_REQUIREMENT, SET_PERMANENT, LOCK, UNLOCK, JOIN_LOCKED, STAY_IN_LOCKED;
	}
	public static enum Rank implements Permission {
		
		CREATE, RENAME, REDESCRIBE, RETAG, SET_ADMIN, SET_OVERRIDE_OBJECT_PERMISSIONS, ADD_PERMISSION, REVOKE_PERMISSION, MODIFY_PERMISSION;
	}
}
