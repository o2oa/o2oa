package com.x.base.core.project.test.list;

import java.util.TreeMap;

import org.junit.Test;

public class TestClient {

	@Test
	public void test() {
		String a1 = "1";
		String a3 = "3";
		String a5 = "5";
		String a7 = "7";

		TreeMap<Integer, String> tree = new TreeMap<>();

		int cursor = 0;
		cursor = cursor + Integer.parseInt(a1);
		tree.put(cursor, a1);
		cursor = cursor + Integer.parseInt(a3);
		tree.put(cursor, a3);
		cursor = cursor + Integer.parseInt(a5);
		tree.put(cursor, a5);
		cursor = cursor + Integer.parseInt(a7);
		tree.put(cursor, a7);

		System.out.println(tree.tailMap(1, true).firstEntry().getValue());
	}

	@Test
	public void test1() {
		Integer a = 1;
		Integer b = 2;
		System.out.println(a.compareTo(b));

	}

}
