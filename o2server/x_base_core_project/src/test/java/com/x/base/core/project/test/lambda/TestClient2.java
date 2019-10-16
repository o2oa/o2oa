package com.x.base.core.project.test.lambda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;

public class TestClient2 {

	@Test
	public void test1() {

		HashMap<String, Integer> hash = new HashMap<>();
		ConcurrentSkipListMap<String, Integer> map = new ConcurrentSkipListMap<>();
		hash.put("AAA", 9);
		hash.put("DDD", 8);
		hash.put("BBB", 7);
		hash.put("CCC", 6);
		hash.entrySet().stream().sorted((o1, o2) -> {
			return o1.getValue() - o2.getValue();
		}).forEachOrdered(o -> {
			System.out.println("key:" + o.getKey());
			map.put(o.getKey(), o.getValue());
		});
		System.out.println(XGsonBuilder.toJson(map));

		List<String> list = Collections.synchronizedList(new ArrayList<String>());
		list.add("aaa");
		list.add("ccc");
		list.add("bbb");
		Iterator<String> i = list.iterator(); // Must be in synchronized block
		while (i.hasNext()) {
			System.out.println(i.next());
		}
	}

}
