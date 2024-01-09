package com.x.base.core.project.scripting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class GraalVMScriptingFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraalVMScriptingFactory.class);

	private static Gson gson = XGsonBuilder.instance();

	private GraalVMScriptingFactory() {

	}

	private static final String LANGUAGE_ID_JS = "js";
	private static final String THEN = "then";
	private static final String LEFT_SQUARE_BRACKETS = "[";

	private static final ReentrantLock COMMONSCRIPTLOCK = new ReentrantLock();
//	private static final String NATIVE_JS_OBJECT_BINDING_TO_STRINGIFY = "nativeJsObjectBindingToStringify";
//	private static final Source STRINGIFYSOURCE = Source.create(LANGUAGE_ID_JS,
//			"JSON.stringify(" + NATIVE_JS_OBJECT_BINDING_TO_STRINGIFY + ")");

	private static final Engine ENGINE = Engine.newBuilder(LANGUAGE_ID_JS).build();;
	private static Source commonScriptSource;

	public static JsonElement eval(Source source, Bindings bindings) {
		try (Context context = Context.newBuilder().engine(ENGINE).allowHostClassLoading(true)
				.allowHostAccess(HostAccess.ALL).allowHostClassLookup(GraalVMScriptingFactory::notBlockedClass)
				.build()) {
			Value bind = context.getBindings(LANGUAGE_ID_JS);
			bindings.entrySet().forEach(en -> bind.putMember(en.getKey(), en.getValue()));
			context.eval(getcommonScriptSource());
			return promise(context.eval(source));
		}
	}

	private static JsonElement promise(Value v) {
		final AtomicReference<Value> reference = new AtomicReference<>();
		Consumer<Value> javaThen = reference::set;
		v.invokeMember(THEN, javaThen);
		if (reference.get().isHostObject()) {
			return gson.toJsonTree(reference.get().asHostObject());
		} else {
			String txt = reference.get().toString();
			if (reference.get().hasArrayElements()) {
				txt = txt.substring(txt.indexOf(LEFT_SQUARE_BRACKETS) - 1);
			}
			return gson.fromJson(txt, JsonElement.class);
//			context.getBindings(LANGUAGE_ID_JS).putMember(NATIVE_JS_OBJECT_BINDING_TO_STRINGIFY, reference.get());
//			return gson.fromJson(context.eval(STRINGIFYSOURCE).asString(), JsonElement.class);
		}
	}

	private static boolean notBlockedClass(String className) {
		return !Config.general().getScriptingBlockedClasses().contains(className);
	}

	public static Optional<Boolean> evalAsBoolean(Source source, Bindings bindings) {
		JsonElement jsonElement = eval(source, bindings);
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isBoolean()) {
				return Optional.of(jsonElement.getAsBoolean());
			}
		}
		return Optional.empty();
	}

	public static Optional<String> evalAsString(Source source, Bindings bindings) {
		JsonElement jsonElement = eval(source, bindings);
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isString()) {
				return Optional.of(jsonElement.getAsString());
			}
		}
		return Optional.empty();
	}

	public static <T> T eval(Source source, Bindings bindings, Class<T> clz) {
		JsonElement jsonElement = eval(source, bindings);
		return gson.fromJson(jsonElement, clz);
	}

	public static <T> T eval(Source source, Bindings bindings, Type type) {
		JsonElement jsonElement = eval(source, bindings);
		return gson.fromJson(jsonElement, type);
	}

	public static <R> R eval(Source source, Bindings bindings, Function<JsonElement, R> function) {
		return function.apply(eval(source, bindings));
	}

	public static List<String> evalAsDistinguishedNames(Source source, Bindings bindings) {
		return Helper.stringOrDistinguishedNameAsList(eval(source, bindings));
	}

	private static Source getcommonScriptSource() {
		// TODO
		// 临时修改为不缓存
//		if (null == commonScriptSource) {
//			COMMONSCRIPTLOCK.lock();
//			try {
//				commonScriptSource = Source.create(LANGUAGE_ID_JS, Config.commonScript());
//			} catch (Exception e) {
//				LOGGER.error(e);
//			} finally {
//				COMMONSCRIPTLOCK.unlock();
//			}
//		}
		COMMONSCRIPTLOCK.lock();
		try {
			commonScriptSource = Source.create(LANGUAGE_ID_JS, Config.commonScript());
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			COMMONSCRIPTLOCK.unlock();
		}
		return commonScriptSource;
	}

	public static final String BINDING_NAME_RESOURCES = "java_resources";

	public static final String BINDING_NAME_WORKCONTEXT = "java_workContext";
	public static final String BINDING_NAME_ASSIGNDATA = "java_assignData";
	public static final String BINDING_NAME_JAXWSPARAMETERS = "java_jaxwsParameters";
	public static final String BINDING_NAME_JAXWSRESPONSE = "java_jaxwsResponse";
	public static final String BINDING_NAME_JAXRSPARAMETERS = "java_jaxrsParameters";
	public static final String BINDING_NAME_JAXRSRESPONSE = "java_jaxrsResponse";
	public static final String BINDING_NAME_JAXRSHEADERS = "java_jaxrsHeaders";
	public static final String BINDING_NAME_JAXRSBODY = "java_jaxrsBody";
	public static final String BINDING_NAME_REQUESTTEXT = "java_requestText";
	public static final String BINDING_NAME_EXPIRE = "java_expire";
	public static final String BINDING_NAME_EFFECTIVEPERSON = "java_effectivePerson";

	public static final String BINDING_NAME_DATA = "java_data";
	// embedData
	public static final String BINDING_NAME_EMBEDDATA = "java_embedData";
	public static final String BINDING_NAME_SERIAL = "serial";
	public static final String BINDING_NAME_PROCESS = "process";

	public static final String BINDING_NAME_SERVICE_RESOURCES = "java_resources";
	public static final String BINDING_NAME_SERVICE_EFFECTIVEPERSON = "java_effectivePerson";
	public static final String BINDING_NAME_SERVICE_CUSTOMRESPONSE = "java_customResponse";
	public static final String BINDING_NAME_SERVICE_REQUESTTEXT = "java_requestText";
	public static final String BINDING_NAME_SERVICE_REQUEST = "java_request";
	public static final String BINDING_NAME_SERVICE_PARAMETERS = "java_parameters";
	public static final String BINDING_NAME_SERVICE_MESSAGE = "java_message";

	public static final String BINDING_NAME_SERVICE_PERSON = "person";

	private static final List<String> BINDING_NAMES = Stream
			.of(BINDING_NAME_RESOURCES, BINDING_NAME_WORKCONTEXT, BINDING_NAME_ASSIGNDATA, BINDING_NAME_JAXWSPARAMETERS,
					BINDING_NAME_JAXWSRESPONSE, BINDING_NAME_JAXRSPARAMETERS, BINDING_NAME_JAXRSRESPONSE,
					BINDING_NAME_JAXRSHEADERS, BINDING_NAME_JAXRSBODY, BINDING_NAME_REQUESTTEXT, BINDING_NAME_EXPIRE,
					BINDING_NAME_EFFECTIVEPERSON, BINDING_NAME_DATA, BINDING_NAME_EMBEDDATA, BINDING_NAME_SERIAL,
					BINDING_NAME_PROCESS, BINDING_NAME_SERVICE_RESOURCES, BINDING_NAME_SERVICE_EFFECTIVEPERSON,
					BINDING_NAME_SERVICE_CUSTOMRESPONSE, BINDING_NAME_SERVICE_REQUESTTEXT, BINDING_NAME_SERVICE_REQUEST,
					BINDING_NAME_SERVICE_PARAMETERS, BINDING_NAME_SERVICE_MESSAGE, BINDING_NAME_SERVICE_PERSON)
			.collect(Collectors.toList());

	public static Source functionalization(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("((async function(){").append(System.lineSeparator());
		sb.append(Objects.toString(text, "")).append(System.lineSeparator());
		sb.append("}.apply(globalThis));");
		return Source.create(LANGUAGE_ID_JS, sb.toString());
	}

	public static class Bindings extends LinkedHashMap<String, Object> {

		private static final long serialVersionUID = 304948629493499012L;

		public Bindings() {
			BINDING_NAMES.stream().forEach(o -> this.put(o, null));
		}

		public Bindings putMember(String key, Object value) {
			this.put(key, value);
			return this;
		}

	}

	private static class Helper {

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
		private static List<String> stringOrDistinguishedNameAsList(JsonElement jsonElement) {
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