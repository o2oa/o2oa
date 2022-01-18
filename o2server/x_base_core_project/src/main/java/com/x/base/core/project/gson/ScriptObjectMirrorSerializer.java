package com.x.base.core.project.gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class ScriptObjectMirrorSerializer implements JsonSerializer<ScriptObjectMirror> {
	public JsonElement serialize(ScriptObjectMirror scriptObjectMirror, Type type, JsonSerializationContext context) {
		if (null == scriptObjectMirror) {
			return JsonNull.INSTANCE;
		}
		if (scriptObjectMirror.isArray()) {
			List<Object> list = new ArrayList<>();
			for (String str : scriptObjectMirror.getOwnKeys(false)) {
				list.add(scriptObjectMirror.get(str));
			}
			return XGsonBuilder.instance().toJsonTree(list);
		} else {
			LinkedHashMap<String, Object> map = new LinkedHashMap<>();
			for (String str : scriptObjectMirror.getOwnKeys(false)) {
				map.put(str, scriptObjectMirror.get(str));
			}
			return XGsonBuilder.instance().toJsonTree(map);
		}
	}
}