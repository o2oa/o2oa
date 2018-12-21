package com.x.base.core.project.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClassTools {

	public static List<Class<?>> forName(List<String> names) throws Exception {
		List<Class<?>> list = new ArrayList<>();
		for (String str : names) {
			Class<?> clz = Class.forName(str);
			list.add(clz);
		}
		return list;
	}

	public static List<Class<?>> forName(final boolean asc, List<String> names) throws Exception {
		List<Class<?>> list = forName(names);
		Collections.sort(list, new Comparator<Class<?>>() {
			public int compare(Class<?> c1, Class<?> c2) {
				if (asc) {
					return c1.getName().compareTo(c2.getName());
				} else {
					return c2.getName().compareTo(c1.getName());
				}
			}
		});
		return list;
	}

	public static boolean isClass(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
