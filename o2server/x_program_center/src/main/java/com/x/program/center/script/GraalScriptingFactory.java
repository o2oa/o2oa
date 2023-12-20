package com.x.program.center.script;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import javax.script.CompiledScript;
import javax.script.ScriptContext;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class GraalScriptingFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraalScriptingFactory.class);

	private static Gson gson = XGsonBuilder.instance();

	private GraalScriptingFactory() {

	}

	private static final String LANGUAGE_ID_JS = "js";
	private static final String NATIVE_OBJECT_BINDING_TO_STRINGIFY = "nativeObjectBindingToStringify";

	private static ReentrantLock engineLock = new ReentrantLock();
	private static ReentrantLock initialScriptSourceLock = new ReentrantLock();
	private static ReentrantLock stringifySourceLock = new ReentrantLock();

	private static Engine engine;

	private static Source initialScriptSource;
	private static Source stringifySource;

	public static JsonElement eval(Source source) {
		try (Context context = Context.newBuilder().engine(getEngine()).build()) {
			context.eval(getInitialScriptSource());
			Value value = context.eval(source);
			if (value.isHostObject()) {
				return gson.toJsonTree(value.asHostObject());
			} else {
				context.getBindings(LANGUAGE_ID_JS).putMember(NATIVE_OBJECT_BINDING_TO_STRINGIFY, value);
				String text = context.eval(getStringifySource()).asString();
				return gson.fromJson(text, JsonElement.class);
			}
		}
	}

	public static Optional<Boolean> evalAsBoolean(Source source) {
		JsonElement jsonElement = eval(source);
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isBoolean()) {
				return Optional.of(jsonElement.getAsBoolean());
			}
		}
		return Optional.empty();
	}

	public static Optional<String> evalAsString(Source source) {
		JsonElement jsonElement = eval(source);
		if (jsonElement != null && jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isString()) {
				return Optional.of(jsonElement.getAsString());
			}
		}
		return Optional.empty();
	}

	public static <T> T eval(Source source, Class<T> clz) {
		JsonElement jsonElement = eval(source);
		return gson.fromJson(jsonElement, clz);
	}

	public static <T> T eval(Source source, Type type) {
		JsonElement jsonElement = eval(source);
		return gson.fromJson(jsonElement, type);
	}

	public static <T> T eval(Source source, Supplier<T> supplier) {
		JsonElement jsonElement = eval(source);
		return supplier.get(jsonElement);
	}

	private static Engine getEngine() {
		if (null == engine) {
			engineLock.lock();
			try {
				engine = Engine.newBuilder(LANGUAGE_ID_JS).build();
			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				engineLock.unlock();
			}
		}
		return engine;
	}

	private static Source getInitialScriptSource() {
		if (null == initialScriptSource) {
			initialScriptSourceLock.lock();
			try {
				initialScriptSource = Source.create(LANGUAGE_ID_JS, Config.initialScriptText());
			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				initialScriptSourceLock.unlock();
			}
		}
		return initialScriptSource;
	}

	private static Source getStringifySource() {
		if (null == stringifySource) {
			stringifySourceLock.lock();
			try {
				stringifySource = Source.create(LANGUAGE_ID_JS,
						"JSON.stringify(" + NATIVE_OBJECT_BINDING_TO_STRINGIFY + ")");
			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				stringifySourceLock.unlock();
			}
		}
		return initialScriptSource;
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
	public static final String BINDING_NAME_SERVICE_BODY = "body";

//	public static Source functionalization(String text) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("var o = (function(){").append(System.lineSeparator());
//		sb.append(Objects.toString(text, "")).append(System.lineSeparator());
//		sb.append("}.apply(this));").append(System.lineSeparator());
//		sb.append("if (this.data && this.data.commit) this.data.commit();");
//		sb.append(
//				"(o && (o !== false) && o.getClass && (typeof o == 'object')) ? Java.type('com.x.base.core.project.gson.XGsonBuilder').toJson(o) : JSON.stringify(toJsJson(o));");
//		return Source.create("js", sb.toString());
//	}

	public static Source functionalization(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("var o = (function(){").append(System.lineSeparator());
		sb.append(Objects.toString(text, "")).append(System.lineSeparator());
		sb.append("}.apply(this));").append(System.lineSeparator());
		sb.append("o;");
		return Source.create(LANGUAGE_ID_JS, sb.toString());
	}

}