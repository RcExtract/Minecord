package com.rcextract.minecord.sql;

import com.rcextract.minecord.utils.ArrayMap;

public abstract class FinalTypeConverter<I> extends TypeConverter<I, ArrayMap<String, Object>> {

	@SuppressWarnings("unchecked")
	public FinalTypeConverter(Class<I> input) {
		super(input, (Class<ArrayMap<String, Object>>) new ArrayMap<String, Object>().getClass());
	}

	@Override
	public abstract ArrayMap<String, Object> serialize(I input);

	@Override
	public abstract I deserialize(ArrayMap<String, Object> output);

}
