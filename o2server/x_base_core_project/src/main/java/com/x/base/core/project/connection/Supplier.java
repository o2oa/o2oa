package com.x.base.core.project.connection;

import java.net.HttpURLConnection;

@FunctionalInterface
public interface Supplier<T> {
	T get(HttpURLConnection connection);
}