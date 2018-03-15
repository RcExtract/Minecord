package com.rcextract.minecord.sql;

import java.util.ArrayList;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		List<String[]> list = new ArrayList<String[]>();
		list.add(new String[] {
			"hi", 
			"whatsup"
		});
		list.add(new String[] {
				"bye", 
				"is this working?"
		});
		print(list);
	}
	public static void print(Object object) {
		List<?> list = (List<?>) object;
		list.forEach(o -> {
			System.out.println(o.getClass().getComponentType());
		});
	}
}
