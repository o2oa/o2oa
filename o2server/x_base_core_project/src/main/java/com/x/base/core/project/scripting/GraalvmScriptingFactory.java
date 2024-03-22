package com.x.base.core.project.scripting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class GraalvmScriptingFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraalvmScriptingFactory.class);

	private static Gson gson = XGsonBuilder.instance();

	private GraalvmScriptingFactory() {

	}

	private static final String LANGUAGE_ID_JS = "js";
	private static final String THEN = "then";
	private static final String CATCH = "catch";

	private static final ReentrantLock LOCK = new ReentrantLock();

	private static final Engine ENGINE = Engine.newBuilder(LANGUAGE_ID_JS).build();
	private static Source commonScriptSource;
	private static Set<String> scriptingBlockedClasses;

	private static Type stringsType = new TypeToken<ArrayList<String>>() {
	}.getType();

	public static void flush() {
		LOCK.lock();
		try {
			commonScriptSource = null;
			scriptingBlockedClasses = null;
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			LOCK.unlock();
		}

	}

	public static JsonElement eval(Source source, Bindings bindings) throws ExceptionEvalPromiseScript {
		try (Context context = Context.newBuilder().engine(ENGINE).allowHostClassLoading(true)
				.allowHostAccess(HostAccess.ALL).allowHostClassLookup(GraalvmScriptingFactory::allowClass).build()) {
			Value bind = context.getBindings(LANGUAGE_ID_JS);
			Map<String, Class<?>> dataAssignDataEmbedDataClasses = new HashMap<>();
			if (null != bindings) {
				dataAssignDataEmbedDataClasses = Stream.of(BINDING_NAME_EMBEDDATA, BINDING_NAME_DATA)
						.filter(bindings::containsKey).filter(o -> Objects.nonNull(bindings.get(o)))
						.collect(Collectors.toMap(Function.identity(), o -> bindings.get(o).getClass()));
				bindings.entrySet().forEach(en -> bind.putMember(en.getKey(), en.getValue()));
			}
			context.eval(getcommonScriptSource());
			Value value = context.eval(source);
			if ((null != bindings) && (!dataAssignDataEmbedDataClasses.isEmpty())) {
				dataAssignDataEmbedDataClasses.entrySet().forEach(o -> {
					Value v = bind.getMember(o.getKey().substring(o.getKey().indexOf("_") + 1));
					try {
						MethodUtils.invokeExactMethod(bindings.get(o.getKey()), "replaceContent",
								bind.getMember("JSON").invokeMember("stringify", v).asString());
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						LOGGER.error(e);
					}
				});
			}
			return promise(context, value);
		}
	}

	public static void eval(Source source, Bindings bindings, Consumer<JsonElement> consumer)
			throws ExceptionEvalPromiseScript {
		consumer.accept(eval(source, bindings));
	}

	private static JsonElement promise(Context context, Value v) throws ExceptionEvalPromiseScript {
		final AtomicReference<Object> reference = new AtomicReference<>();
		if (BooleanUtils.isTrue(Config.general().getGraalvmEvalAsPromise())) {
			final AtomicReference<String> message = new AtomicReference<>();
			Consumer<Object> javaThen = reference::set;
			Consumer<Object> javaCatch = e -> message.set(Objects.toString(e.toString(), ""));
			if (v.canInvokeMember(THEN)) {
				v.invokeMember(THEN, javaThen);
			}
			if (v.canInvokeMember(CATCH)) {
				v.invokeMember(CATCH, javaCatch);
				if (!Objects.isNull(message.get())) {
					throw new ExceptionEvalPromiseScript(message.get());
				}
			}
		} else {
			reference.set(v);
		}
		if (Objects.isNull(reference.get())) {
			return JsonNull.INSTANCE;
		}
		if ((reference.get() instanceof Value) && ((Value) reference.get()).isHostObject()) {
			return gson.toJsonTree(((Value) reference.get()).asHostObject());
		} else {
			return gson.fromJson(context.getBindings(LANGUAGE_ID_JS).getMember("JSON")
					.invokeMember("stringify", reference.get()).asString(), JsonElement.class);
		}
	}

	private static boolean allowClass(String className) {
		return !getScriptingBlockedClasses().contains(className);
	}

	public static Optional<Boolean> evalAsBoolean(Source source, Bindings bindings) throws ExceptionEvalPromiseScript {
		JsonElement jsonElement = eval(source, bindings);
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isBoolean()) {
				return Optional.of(jsonElement.getAsBoolean());
			}
		}
		return Optional.empty();
	}

	public static Optional<String> evalAsString(Source source, Bindings bindings) throws ExceptionEvalPromiseScript {
		JsonElement jsonElement = eval(source, bindings);
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isString()) {
				return Optional.of(jsonElement.getAsString());
			}
		}
		return Optional.empty();
	}

	public static Optional<Integer> evalAsInteger(Source source, Bindings bindings) throws ExceptionEvalPromiseScript {
		JsonElement jsonElement = eval(source, bindings);
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isNumber()) {
				return Optional.of(jsonPrimitive.getAsInt());
			}
		}
		return Optional.empty();
	}

	public static Optional<Double> evalAsDouble(Source source, Bindings bindings) throws ExceptionEvalPromiseScript {
		JsonElement jsonElement = eval(source, bindings);
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isNumber()) {
				return Optional.of(jsonPrimitive.getAsDouble());
			}
		}
		return Optional.empty();
	}

	public static Optional<Float> evalAsFloat(Source source, Bindings bindings) throws ExceptionEvalPromiseScript {
		JsonElement jsonElement = eval(source, bindings);
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isNumber()) {
				return Optional.of(jsonPrimitive.getAsFloat());
			}
		}
		return Optional.empty();
	}

	public static <T> T eval(Source source, Bindings bindings, Class<T> clz) throws ExceptionEvalPromiseScript {
		JsonElement jsonElement = eval(source, bindings);
		return gson.fromJson(jsonElement, clz);
	}

	public static <T> T eval(Source source, Bindings bindings, Type type) throws ExceptionEvalPromiseScript {
		JsonElement jsonElement = eval(source, bindings);
		return gson.fromJson(jsonElement, type);
	}

	public static <R> R eval(Source source, Bindings bindings, Function<JsonElement, R> function)
			throws ExceptionEvalPromiseScript {
		return function.apply(eval(source, bindings));
	}

	public static List<String> evalAsDistinguishedNames(Source source, Bindings bindings)
			throws ExceptionEvalPromiseScript {
		return Helper.stringOrDistinguishedNameAsList(eval(source, bindings));
	}

	public static List<String> evalAsStrings(Source source, Bindings bindings) throws ExceptionEvalPromiseScript {
		JsonElement jsonElement = eval(source, bindings);
		if (jsonElement.isJsonArray()) {
			return XGsonBuilder.instance().fromJson(jsonElement, stringsType);
		}
		return new ArrayList<>();
	}

	private static Set<String> getScriptingBlockedClasses() {
		if (null == scriptingBlockedClasses) {
			scriptingBlockedClasses = new HashSet<>();
			LOCK.lock();
			try {
				scriptingBlockedClasses.addAll(Config.general().getScriptingBlockedClasses());
			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				LOCK.unlock();
			}
		}
		return scriptingBlockedClasses;
	}

	private static Source getcommonScriptSource() {
		if (null == commonScriptSource) {
			LOCK.lock();
			try {
				commonScriptSource = Source.create(LANGUAGE_ID_JS, Config.commonScript());
			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				LOCK.unlock();
			}
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
	public static final String BINDING_NAME_SERVICE_TEXT = "text";

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
		if (BooleanUtils.isTrue(Config.general().getGraalvmEvalAsPromise())) {
			sb.append("(async function(){ ").append(Objects.toString(text, ""))
					.append(" }.apply(globalThis)).catch((e)=>{ throw e.stack; });");
		} else {
			sb.append("(function(){ ").append(Objects.toString(text, "")).append(" }.apply(globalThis));");
		}
		return Source.create(LANGUAGE_ID_JS, sb.toString());
	}

	public static Source source(String text) {
		return Source.create(LANGUAGE_ID_JS, text);
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