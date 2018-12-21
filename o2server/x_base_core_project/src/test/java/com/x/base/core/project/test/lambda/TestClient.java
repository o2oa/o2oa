package com.x.base.core.project.test.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class TestClient {

	@Test
	public void test3() {
		Map<String, List<Book>> map = new ConcurrentHashMap<>();
		List<Book> list = new ArrayList<>();
		IntStream.rangeClosed(1, 100).forEach(i -> {
			IntStream.rangeClosed(1, 100).forEach(j -> {
				int x = (i + j);
				Book book = new Book(x + "", x);
				list.add(book);
			});
		});
		list.parallelStream().peek(b -> {
			List<Book> bs = map.get(b.getName());
			if (null == bs) {
				bs = new CopyOnWriteArrayList<Book>();
				map.put(b.getName(), bs);
			}
			bs.add(b);
		}).anyMatch(b -> false);
		map.entrySet().stream().peek(e -> {
			System.out.println(e.getKey() + ":" + e.getValue().size());
		}).anyMatch(b -> false);
		System.out.println(map.values().stream().mapToInt(List::size).sum());
	}

	@Test
	public void test4() {
		List<String> list = new ArrayList<>();
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");
		list.add("ddd");
		list.add("eee");
		Optional<String> obj = list.stream().filter(o -> {
			return StringUtils.equals(o, "ddd11");
		}).findFirst();
		System.out.println(obj.orElse(null));

	}

	@Test
	public void test() {
		List<String> list = new ArrayList<>();
		System.out.println("allMatch:" + list.stream().allMatch(o -> StringUtils.length(o) > 1));
		System.out.println("noneMatch:" + list.stream().noneMatch(o -> StringUtils.length(o) > 1));
	}

}
