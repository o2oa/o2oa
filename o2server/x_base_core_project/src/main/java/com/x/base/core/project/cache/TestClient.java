package com.x.base.core.project.cache;

import net.fortuna.ical4j.util.Optional;

public class TestClient {

	public static void main(String... strings) {
		Optional<?> o =  Optional.ofNullable(null);
		System.out.println(o);
		System.out.println(o.isPresent());
	}
}
