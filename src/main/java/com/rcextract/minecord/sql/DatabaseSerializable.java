package com.rcextract.minecord.sql;

import com.rcextract.minecord.utils.ArrayMap;

public interface DatabaseSerializable {

	public ArrayMap<String, Object> serialize();
}
