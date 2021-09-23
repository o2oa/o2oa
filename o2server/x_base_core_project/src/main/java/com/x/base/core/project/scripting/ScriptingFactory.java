package com.x.base.core.project.scripting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ScriptingFactory {

	private static Logger logger = LoggerFactory.getLogger(ScriptingFactory.class);

	private ScriptingFactory() {

	}

	public static final ScriptEngine scriptEngine = (new ScriptEngineManager())
			.getEngineByName(Config.SCRIPTING_ENGINE_NAME);

	private static CompiledScript compiledScriptInitialServiceScriptText;
	private static CompiledScript compiledScriptInitialScriptText;
	public static final String BINDING_NAME_RESOURCES = "resources";
	public static final String BINDING_NAME_EFFECTIVEPERSON = "effectivePerson";
	public static final String BINDING_NAME_WORKCONTEXT = "workContext";
	public static final String BINDING_NAME_GSON = "gson";
	public static final String BINDING_NAME_DATA = "data";
	public static final String BINDING_NAME_ORGANIZATION = "organization";
	public static final String BINDING_NAME_WEBSERVICESCLIENT = "webservicesClient";
	public static final String BINDING_NAME_DICTIONARY = "dictionary";
	public static final String BINDING_NAME_ROUTES = "routes";
	public static final String BINDING_NAME_ROUTE = "routes";
	public static final String BINDING_NAME_APPLICATIONS = "applications";

	public static final String BINDING_NAME_ASSIGNDATA = "assignData";

	public static final String BINDING_NAME_IDENTITY = "identity";

	public static final String BINDING_NAME_PARAMETERS = "parameters";
	public static final String BINDING_NAME_JAXRSRESPONSE = "jaxrsResponse";
	public static final String BINDING_NAME_JAXWSRESPONSE = "jaxwsResponse";

	public static final String BINDING_NAME_JAXRSBODY = "jaxrsBody";
	public static final String BINDING_NAME_JAXRSHEAD = "jaxrsHead";

	public static final String BINDING_NAME_SERVICEVALUE = "serviceValue";
	public static final String BINDING_NAME_TASK = "task";
	public static final String BINDING_NAME_EXPIRE = "expire";
	public static final String BINDING_NAME_SERIAL = "serial";
	public static final String BINDING_NAME_PROCESS = "process";

	private static Type stringsType = new TypeToken<ArrayList<String>>() {
	}.getType();

	public static ScriptEngine newScriptEngine() {
		return (new ScriptEngineManager()).getEngineByName(Config.SCRIPTING_ENGINE_NAME);
	}

	public static synchronized CompiledScript initialServiceScriptText() throws Exception {
		if (compiledScriptInitialServiceScriptText == null) {
			String text = Config.initialServiceScriptText();
			compiledScriptInitialServiceScriptText = ((Compilable) scriptEngine).compile(text);
		}
		return compiledScriptInitialServiceScriptText;
	}

	public static CompiledScript compile(String text) throws ScriptException {
		return ((Compilable) scriptEngine).compile(text);
	}

	public static synchronized CompiledScript initialScriptText() throws Exception {
		if (compiledScriptInitialScriptText == null) {
			String text = Config.initialScriptText();
			compiledScriptInitialScriptText = ((Compilable) scriptEngine).compile(text);
		}
		return compiledScriptInitialScriptText;
	}

	public static String functionalization(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("(function(){").append(System.lineSeparator());
		sb.append(Objects.toString(text, "")).append(System.lineSeparator());
		sb.append("})();");
		return sb.toString();
	}

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
		try {
			Optional<JsonElement> optional = evalResultToJsonElement(cs, scriptContext);
			if (optional.isPresent()) {
				optional.get().getAsJsonObject()
			}
		} catch (ScriptException e) {
			logger.error(e);
		}
		return Optional.empty();
	}

	private static bfsDistinguishedNames(JsonObject jsonObject, List<String> list) {
		jsonObject.
	}

	public static <T extends Object> Optional<T> evalResultTo(CompiledScript cs, ScriptContext scriptContext,
			Class<T> clz) {
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

}