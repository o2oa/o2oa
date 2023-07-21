package com.x.base.core.project.scripting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class JsonScriptingExecutor {

	private JsonScriptingExecutor() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonScriptingExecutor.class);

	private static Type stringsType = new TypeToken<ArrayList<String>>() {
	}.getType();

	private static Gson gson = XGsonBuilder.instance();

	/**
	 * 将脚本运行后的对象toJson成JsonElement返回.
	 * 
	 * @param cs
	 * @param scriptContext
	 * @return null 将返回JsonNull
	 */
	public static JsonElement jsonElement(CompiledScript cs, ScriptContext scriptContext) {
		Objects.requireNonNull(cs);
		Objects.requireNonNull(scriptContext);
		try {
			Object o = cs.eval(scriptContext);
			if (null != o) {
				// 脚本 return "123" 返回 \"123\", 如果都使用gson.toJson
				JsonElement value = (o.getClass().isAssignableFrom(String.class))
						? XGsonBuilder.instance().fromJson(o.toString(), JsonElement.class)
						: XGsonBuilder.instance().fromJson(gson.toJson(o), JsonElement.class);
				if (null != value) {
					return value;
				}
			}
		} catch (ScriptException e) {
			LOGGER.error(e);
		}
		return JsonNull.INSTANCE;
	}

	public static void jsonElement(CompiledScript cs, ScriptContext scriptContext, Consumer<JsonElement> consumer) {
		consumer.accept(jsonElement(cs, scriptContext));
	}

	public static JsonArray jsonArray(CompiledScript cs, ScriptContext scriptContext) {
		JsonElement jsonElement = jsonElement(cs, scriptContext);
		if (jsonElement.isJsonArray()) {
			return jsonElement.getAsJsonArray();
		}
		return new JsonArray();
	}

	public static void jsonArray(CompiledScript cs, ScriptContext scriptContext, Consumer<JsonArray> consumer) {
		consumer.accept(jsonArray(cs, scriptContext));
	}

	public static JsonObject jsonObject(CompiledScript cs, ScriptContext scriptContext) {
		JsonElement jsonElement = jsonElement(cs, scriptContext);
		if (jsonElement.isJsonObject()) {
			return jsonElement.getAsJsonObject();
		}
		return new JsonObject();
	}

	public static void jsonObject(CompiledScript cs, ScriptContext scriptContext, Consumer<JsonObject> consumer) {
		consumer.accept(jsonObject(cs, scriptContext));
	}

	public static String evalString(CompiledScript cs, ScriptContext scriptContext) {
		JsonElement jsonElement = jsonElement(cs, scriptContext);
		if (jsonElement.isJsonPrimitive()) {
			return jsonElement.getAsJsonPrimitive().getAsString();
		}
		return null;
	}

	public static void evalString(CompiledScript cs, ScriptContext scriptContext, Consumer<String> consumer) {
		consumer.accept(evalString(cs, scriptContext));
	}

	public static Integer evalInteger(CompiledScript cs, ScriptContext scriptContext) {
		JsonElement jsonElement = jsonElement(cs, scriptContext);
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isNumber()) {
				return jsonPrimitive.getAsInt();
			}
		}
		return null;
	}

	public static void evalInteger(CompiledScript cs, ScriptContext scriptContext, Consumer<Integer> consumer) {
		consumer.accept(evalInteger(cs, scriptContext));
	}

	public static Double evalDouble(CompiledScript cs, ScriptContext scriptContext) {
		JsonElement jsonElement = jsonElement(cs, scriptContext);
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isNumber()) {
				return jsonPrimitive.getAsDouble();
			}
		}
		return null;
	}

	public static void evalDouble(CompiledScript cs, ScriptContext scriptContext, Consumer<Double> consumer) {
		consumer.accept(evalDouble(cs, scriptContext));
	}

	public static Float evalFloat(CompiledScript cs, ScriptContext scriptContext) {
		JsonElement jsonElement = jsonElement(cs, scriptContext);
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isNumber()) {
				return jsonPrimitive.getAsFloat();
			}
		}
		return null;
	}

	public static void evalFloat(CompiledScript cs, ScriptContext scriptContext, Consumer<Float> consumer) {
		consumer.accept(evalFloat(cs, scriptContext));
	}

	public static Number evalNumber(CompiledScript cs, ScriptContext scriptContext) {
		JsonElement jsonElement = jsonElement(cs, scriptContext);
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isNumber()) {
				return jsonPrimitive.getAsNumber();
			}
		}
		return null;
	}

	public static void evalNumber(CompiledScript cs, ScriptContext scriptContext, Consumer<Number> consumer) {
		consumer.accept(evalNumber(cs, scriptContext));
	}

	/**
	 * 
	 * @param cs
	 * @param scriptContext
	 * @return
	 */
	public static Boolean evalBoolean(CompiledScript cs, ScriptContext scriptContext, Boolean defaultValue) {
		JsonElement jsonElement = jsonElement(cs, scriptContext);
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isBoolean()) {
				return jsonPrimitive.getAsBoolean();
			}
		}
		return defaultValue;
	}

	/**
	 * 非boolean值或者null返回Boolean.FALSE
	 * 
	 * @param cs
	 * @param scriptContext
	 * @return
	 */
	public static Boolean evalBoolean(CompiledScript cs, ScriptContext scriptContext) {
		return evalBoolean(cs, scriptContext, Boolean.FALSE);
	}

	public static void evalBoolean(CompiledScript cs, ScriptContext scriptContext, Consumer<Boolean> consumer,
			Boolean defaultValue) {
		consumer.accept(evalBoolean(cs, scriptContext, defaultValue));
	}

	public static List<String> evalStrings(CompiledScript cs, ScriptContext scriptContext) {
		JsonElement jsonElement = jsonElement(cs, scriptContext);
		if (jsonElement.isJsonArray()) {
			return XGsonBuilder.instance().fromJson(jsonElement, stringsType);
		}
		return new ArrayList<>();
	}

	public static void evalStrings(CompiledScript cs, ScriptContext scriptContext, Consumer<List<String>> consumer) {
		consumer.accept(evalStrings(cs, scriptContext));
	}

	public static List<String> evalDistinguishedNames(CompiledScript cs, ScriptContext scriptContext) {
		return Helper.stringOrDistinguishedNameAsList(jsonElement(cs, scriptContext));
	}

	public static void evalDistinguishedNames(CompiledScript cs, ScriptContext scriptContext,
			Consumer<List<String>> consumer) {
		consumer.accept(evalDistinguishedNames(cs, scriptContext));
	}

	public static void eval(CompiledScript cs, ScriptContext scriptContext) {
		Objects.requireNonNull(cs);
		Objects.requireNonNull(scriptContext);
		try {
			cs.eval(scriptContext);
		} catch (ScriptException e) {
			LOGGER.error(e);
		}
	}

	public static <T> T eval(CompiledScript cs, ScriptContext scriptContext, Class<T> clz) {
		return XGsonBuilder.instance().fromJson(jsonElement(cs, scriptContext), clz);
	}

	public static <T> T eval(CompiledScript cs, ScriptContext scriptContext, Type type) {
		return XGsonBuilder.instance().fromJson(jsonElement(cs, scriptContext), type);
	}

	public static <T> T eval(CompiledScript cs, ScriptContext scriptContext, Supplier<T> supplier) {
		JsonElement jsonElement = jsonElement(cs, scriptContext);
		return supplier.get(jsonElement);
	}

	public static <T> void eval(CompiledScript cs, ScriptContext scriptContext, Class<T> clz, Consumer<T> consumer) {
		T t = eval(cs, scriptContext, clz);
		consumer.accept(t);
	}

	public static <T> void eval(CompiledScript cs, ScriptContext scriptContext, Supplier<T> supplier,
			Consumer<T> consumer) {
		consumer.accept(supplier.get(jsonElement(cs, scriptContext)));
	}

	public static class Helper {

		private Helper() {
			// nothing
		}

		/**
		 * 对jsonElement抽取可能是身份,个人,组织,群组的文本值,不进行递归的抽取,仅抽取地一层
		 * 
		 * @param jsonElement
		 * @return
		 * 
		 */
		public static List<String> stringOrDistinguishedNameAsList(JsonElement jsonElement) {
			List<String> list = new ArrayList<>();
			if (null != jsonElement) {
				if (jsonElement.isJsonObject()) {
					objectStringOrDistinguishedNameAsList(jsonElement.getAsJsonObject(), list);
				} else if (jsonElement.isJsonArray()) {
					arrayStringOrDistinguishedNameAsList(jsonElement.getAsJsonArray(), list);
				} else if (jsonElement.isJsonPrimitive()) {
					primitiveStringOrDistinguishedNameAsList(jsonElement.getAsJsonPrimitive(), list);
				}
			}
			return list;
		}

		private static void primitiveStringOrDistinguishedNameAsList(JsonPrimitive primitive, List<String> list) {
			if (primitive.isString()) {
				list.add(primitive.getAsString());
			}
		}

		private static void arrayStringOrDistinguishedNameAsList(JsonArray arr, List<String> list) {
			for (JsonElement element : arr) {
				if (element.isJsonObject()) {
					objectStringOrDistinguishedNameAsList(element.getAsJsonObject(), list);
				} else if (element.isJsonPrimitive()) {
					primitiveStringOrDistinguishedNameAsList(element.getAsJsonPrimitive(), list);
				}
			}
		}

		private static void objectStringOrDistinguishedNameAsList(JsonObject jsonObject, List<String> list) {
			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				if (StringUtils.equals(entry.getKey(), JpaObject.DISTINGUISHEDNAME)
						&& entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()) {
					list.add((entry.getValue().getAsJsonPrimitive().getAsString()));
				}
			}
		}
	}

}