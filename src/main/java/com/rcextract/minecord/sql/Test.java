package com.rcextract.minecord.sql;

import java.util.ArrayList;
import java.util.List;

import com.google.common.reflect.TypeToken;

public class Test {

	public static void main(String[] args) {
		print(new ArrayList<String>());
	}
	public static <T> void print(List<T> t) {
		@SuppressWarnings("serial")
		TypeToken<List<T>> token1 = new TypeToken<List<T>>() {};
		TypeToken<?> token2 = token1.resolveType(List.class.getTypeParameters()[0]);
		System.out.println(token2.getType().getTypeName());
	}
}
