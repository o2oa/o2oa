package com.x.base.core.project.scripting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ScriptingExecutor {

	private ScriptingExecutor() {
		// nothing
	}

	private static Logger logger = LoggerFactory.getLogger(ScriptingExecutor.class);

	private static Type stringsType = new TypeToken<ArrayList<String>>() {
	}.getType();

	public static Optional<JsonElement> evalResultToJsonElement(CompiledScript cs, ScriptContext scriptContext) {
		try {
			Object o = cs.eval(scriptContext);
			String json = Objects.toString(o, "");
			JsonElement jsonElement = XGsonBuilder.instance().fromJson(json, JsonElement.class);
			if (null != jsonElement && (!jsonElement.isJsonNull())) {
				return Optional.of(jsonElement);
			}
		} catch (ScriptException e) {
			logger.error(e);
		}
		return Optional.empty();
	}

	public static Optional<JsonArray> evalResultToJsonArray(CompiledScript cs, ScriptContext scriptContext) {
		try {
			Object o = cs.eval(scriptContext);
			String json = Objects.toString(o, "");
			if (StringUtils.isNotEmpty(json)) {
				JsonArray jsonArray = XGsonBuilder.instance().fromJson(json, JsonArray.class);
				if ((null != jsonArray) && (jsonArray.size() > 0)) {
					return Optional.of(jsonArray);
				}
			}
		} catch (ScriptException e) {
			logger.error(e);
		}
		return Optional.empty();
	}

	public static Optional<JsonObject> evalResultToJsonObject(CompiledScript cs, ScriptContext scriptContext) {
		try {
			Object o = cs.eval(scriptContext);
			String json = Objects.toString(o, "");
			if (StringUtils.isNotEmpty(json)) {
				JsonObject jsonObject = XGsonBuilder.instance().fromJson(json, JsonObject.class);
				if ((null != jsonObject) && (!jsonObject.isJsonNull())) {
					return Optional.of(jsonObject);
				}
			}
		} catch (ScriptException e) {
			logger.error(e);
		}
		return Optional.empty();
	}

	public static Optional<String> evalResultToString(CompiledScript cs, ScriptContext scriptContext) {
		try {
			Object o = cs.eval(scriptContext);
			String str = Objects.toString(o, "");
			if (StringUtils.isNotEmpty(str)) {
				return Optional.of(str);
			}
		} catch (ScriptException e) {
			logger.error(e);
		}
		return Optional.empty();
	}

	public static Optional<Number> evalResultToNumber(CompiledScript cs, ScriptContext scriptContext) {
		try {
			Object o = cs.eval(scriptContext);
			String json = Objects.toString(o, "");
			if (StringUtils.isNotEmpty(json)) {
				JsonPrimitive jsonPrimitive = XGsonBuilder.instance().fromJson(json, JsonPrimitive.class);
				if ((null != jsonPrimitive) && (jsonPrimitive.isNumber())) {
					return Optional.of(jsonPrimitive.getAsNumber());
				}
			}
		} catch (ScriptException e) {
			logger.error(e);
		}
		return Optional.empty();
	}

	public static Optional<Boolean> evalResultToBoolean(CompiledScript cs, ScriptContext scriptContext) {
		try {
			Object o = cs.eval(scriptContext);
			String json = Objects.toString(o, "");
			if (StringUtils.isNotEmpty(json)) {
				JsonPrimitive jsonPrimitive = XGsonBuilder.instance().fromJson(json, JsonPrimitive.class);
				if ((null != jsonPrimitive) && (jsonPrimitive.isBoolean())) {
					return Optional.of(jsonPrimitive.getAsBoolean());
				}
			}
		} catch (ScriptException e) {
			logger.error(e);
		}
		return Optional.empty();
	}

	public static Optional<List<String>> evalResultToStrings(CompiledScript cs, ScriptContext scriptContext) {
		try {
			Object o = cs.eval(scriptContext);
			String json = Objects.toString(o, "");
			if (StringUtils.isNotEmpty(json)) {
				List<String> list = XGsonBuilder.instance().fromJson(json, stringsType);
				if (null != list && (!list.isEmpty())) {
					return Optional.of(list);
				}
			}
		} catch (ScriptException e) {
			logger.error(e);
		}
		return Optional.empty();
	}

	public static Optional<List<String>> evalResultToDistinguishedNames(CompiledScript cs,
			ScriptContext scriptContext) {
		List<String> list = new ArrayList<>();
		Optional<JsonElement> optional = evalResultToJsonElement(cs, scriptContext);
		if (optional.isPresent()) {
			if (optional.get().isJsonObject()) {
				dfsDistinguishedNames(optional.get().getAsJsonObject(), list);
			} else if (optional.get().isJsonArray()) {
				for (JsonElement element : optional.get().getAsJsonArray()) {
					if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
						list.add(element.getAsJsonPrimitive().getAsString());
					} else if (element.isJsonObject()) {
						dfsDistinguishedNames(element.getAsJsonObject(), list);
					}
				}
			}

		}
		return Optional.empty();
	}

	private static void dfsDistinguishedNames(JsonObject jsonObject, List<String> list) {
		if ((null != jsonObject) && (!jsonObject.isJsonNull())) {
			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				if (StringUtils.equals(entry.getKey(), JpaObject.DISTINGUISHEDNAME)
						&& entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()) {
					list.add((entry.getValue().getAsJsonPrimitive().getAsString()));
				} else if (entry.getValue().isJsonObject()) {
					dfsDistinguishedNames(entry.getValue().getAsJsonObject(), list);
				}
			}
		}
	}

	public static <T> Optional<T> evalResultTo(CompiledScript cs, ScriptContext scriptContext, Class<T> clz) {
		try {
			Object o = cs.eval(scriptContext);
			String json = Objects.toString(o, "");
			if (StringUtils.isNotEmpty(json)) {
				T t = XGsonBuilder.instance().fromJson(json, clz);
				if (null != t) {
					return Optional.of(t);
				}
			}
		} catch (ScriptException e) {
			logger.error(e);
		}
		return Optional.empty();
	}

	public static <T> Optional<T> evalResultTo(CompiledScript cs, ScriptContext scriptContext, Supplier<T> supplier) {
		Optional<JsonElement> optional = evalResultToJsonElement(cs, scriptContext);
		if (optional.isPresent() && (!optional.get().isJsonNull())) {
			T t = supplier.get(optional.get());
			if (null != t) {
				return Optional.of(t);
			}
		}
		return Optional.empty();
	}

}
