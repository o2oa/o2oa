package com.x.base.core.project.scripting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class JsonScriptingExecutor {

	private JsonScriptingExecutor() {
		// nothing
	}

	private static Logger logger = LoggerFactory.getLogger(JsonScriptingExecutor.class);

	private static Type stringsType = new TypeToken<ArrayList<String>>() {
	}.getType();

	public static Optional<JsonElement> jsonElement(CompiledScript cs, ScriptContext scriptContext) {
		Objects.requireNonNull(cs);
		Objects.requireNonNull(scriptContext);
		try {
			Object o = cs.eval(scriptContext);
			String json = Objects.toString(o, "");
			JsonElement element = XGsonBuilder.instance().fromJson(json, JsonElement.class);
			if (null != element && (!element.isJsonNull())) {
				return Optional.of(element);
			}
		} catch (ScriptException e) {
			logger.error(e);
		}
		return Optional.empty();
	}

	public static void jsonElement(CompiledScript cs, ScriptContext scriptContext, Consumer<JsonElement> consumer) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent()) {
			consumer.accept(optional.get());
		}
	}

	public static Optional<JsonArray> jsonArray(CompiledScript cs, ScriptContext scriptContext) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent() && optional.get().isJsonArray()) {
			JsonArray jsonArray = optional.get().getAsJsonArray();
			if ((null != jsonArray) && (jsonArray.size() > 0)) {
				return Optional.of(jsonArray);
			}
		}
		return Optional.of(new JsonArray());
	}

	public static void jsonArray(CompiledScript cs, ScriptContext scriptContext, Consumer<JsonArray> consumer) {
		Optional<JsonArray> optional = jsonArray(cs, scriptContext);
		consumer.accept(optional.get());
	}

	public static Optional<JsonObject> jsonObject(CompiledScript cs, ScriptContext scriptContext) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent() && optional.get().isJsonObject()) {
			JsonObject jsonObject = optional.get().getAsJsonObject();
			if (null != jsonObject) {
				return Optional.of(jsonObject);
			}
		}
		return Optional.empty();
	}

	public static void jsonObject(CompiledScript cs, ScriptContext scriptContext, Consumer<JsonObject> consumer) {
		Optional<JsonObject> optional = jsonObject(cs, scriptContext);
		if (optional.isPresent()) {
			consumer.accept(optional.get().getAsJsonObject());
		}
	}

	public static Optional<String> evalString(CompiledScript cs, ScriptContext scriptContext) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent() && optional.get().isJsonPrimitive()) {
			String value = optional.get().getAsJsonPrimitive().getAsString();
			if (StringUtils.isNotEmpty(value)) {
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}

	public static void evalString(CompiledScript cs, ScriptContext scriptContext, Consumer<String> consumer) {
		Optional<String> optional = evalString(cs, scriptContext);
		if (optional.isPresent()) {
			consumer.accept(optional.get());
		}
	}

	public static Optional<Integer> evalInteger(CompiledScript cs, ScriptContext scriptContext) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent() && optional.get().isJsonPrimitive()) {
			Integer value = optional.get().getAsJsonPrimitive().getAsInt();
			if (null != value) {
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}

	public static void evalInteger(CompiledScript cs, ScriptContext scriptContext, Consumer<Integer> consumer) {
		Optional<Integer> optional = evalInteger(cs, scriptContext);
		if (optional.isPresent()) {
			consumer.accept(optional.get());
		}
	}

	public static Optional<Double> evalDouble(CompiledScript cs, ScriptContext scriptContext) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent() && optional.get().isJsonPrimitive()) {
			Double value = optional.get().getAsJsonPrimitive().getAsDouble();
			if (null != value) {
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}

	public static void evalDouble(CompiledScript cs, ScriptContext scriptContext, Consumer<Double> consumer) {
		Optional<Double> optional = evalDouble(cs, scriptContext);
		if (optional.isPresent()) {
			consumer.accept(optional.get());
		}
	}

	public static Optional<Float> evalFloat(CompiledScript cs, ScriptContext scriptContext) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent() && optional.get().isJsonPrimitive()) {
			Float value = optional.get().getAsJsonPrimitive().getAsFloat();
			if (null != value) {
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}

	public static void evalFloat(CompiledScript cs, ScriptContext scriptContext, Consumer<Float> consumer) {
		Optional<Float> optional = evalFloat(cs, scriptContext);
		if (optional.isPresent()) {
			consumer.accept(optional.get());
		}
	}

	public static Optional<Number> evalNumber(CompiledScript cs, ScriptContext scriptContext) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent() && optional.get().isJsonPrimitive()) {
			Number value = optional.get().getAsJsonPrimitive().getAsNumber();
			if (null != value) {
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}

	public static void evalNumber(CompiledScript cs, ScriptContext scriptContext, Consumer<Number> consumer) {
		Optional<Number> optional = evalNumber(cs, scriptContext);
		if (optional.isPresent()) {
			consumer.accept(optional.get());
		}
	}

	public static Optional<Boolean> evalBoolean(CompiledScript cs, ScriptContext scriptContext) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent() && optional.get().isJsonPrimitive()) {
			Boolean value = optional.get().getAsJsonPrimitive().getAsBoolean();
			if (null != value) {
				return Optional.of(value);
			}
		}
		return Optional.empty();
	}

	public static void evalBoolean(CompiledScript cs, ScriptContext scriptContext, Consumer<Boolean> consumer) {
		Optional<Boolean> optional = evalBoolean(cs, scriptContext);
		if (optional.isPresent()) {
			consumer.accept(optional.get());
		}
	}

	public static Optional<List<String>> evalStrings(CompiledScript cs, ScriptContext scriptContext) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent() && optional.get().isJsonArray()) {
			List<String> list = XGsonBuilder.instance().fromJson(optional.get(), stringsType);
			if (!list.isEmpty()) {
				return Optional.of(list);
			}
		}
		return Optional.of(new ArrayList<>());
	}

	public static void evalStrings(CompiledScript cs, ScriptContext scriptContext, Consumer<List<String>> consumer) {
		Optional<List<String>> optional = evalStrings(cs, scriptContext);
		consumer.accept(optional.get());
	}

	public static Optional<List<String>> evalDistinguishedNames(CompiledScript cs, ScriptContext scriptContext) {
		List<String> list = new ArrayList<>();
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
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
			if (!list.isEmpty()) {
				return Optional.of(list);
			}

		}
		return Optional.of(new ArrayList<>());
	}

	public static void evalDistinguishedNames(CompiledScript cs, ScriptContext scriptContext,
			Consumer<List<String>> consumer) {
		Optional<List<String>> optional = evalDistinguishedNames(cs, scriptContext);
		consumer.accept(optional.get());
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

	public static <T> Optional<T> eval(CompiledScript cs, ScriptContext scriptContext, Class<T> clz) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent()) {
			return Optional.of(XGsonBuilder.instance().fromJson(optional.get(), clz));
		}
		return Optional.empty();
	}

	public static <T> Optional<T> eval(CompiledScript cs, ScriptContext scriptContext, Supplier<T> supplier) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent()) {
			T t = supplier.get(optional.get());
			if (null != t) {
				return Optional.of(t);
			}
		}
		return Optional.empty();
	}

	public static <T> void eval(CompiledScript cs, ScriptContext scriptContext, Class<T> clz, Consumer<T> consumer) {
		Optional<T> optional = eval(cs, scriptContext, clz);
		if (optional.isPresent()) {
			consumer.accept(optional.get());
		}
	}

	public static <T> void eval(CompiledScript cs, ScriptContext scriptContext, Supplier<T> supplier,
			Consumer<T> consumer) {
		Optional<JsonElement> optional = jsonElement(cs, scriptContext);
		if (optional.isPresent()) {
			T t = supplier.get(optional.get());
			if (null != t) {
				consumer.accept(t);
			}
		}
	}

}
