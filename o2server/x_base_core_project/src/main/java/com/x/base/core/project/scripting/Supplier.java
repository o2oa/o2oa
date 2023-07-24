package com.x.base.core.project.scripting;

import com.google.gson.JsonElement;

@FunctionalInterface
public interface Supplier<T> {
	T get(JsonElement jsonElement);
}