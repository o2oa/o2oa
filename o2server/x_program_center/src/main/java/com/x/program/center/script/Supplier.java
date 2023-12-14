package com.x.program.center.script;

import com.google.gson.JsonElement;

@FunctionalInterface
public interface Supplier<T> {
	T get(JsonElement jsonElement);
}