package com.x.base.core.project.scripting;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ScriptingFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScriptingFactory.class);

	private ScriptingFactory() {

	}

	public static final ScriptEngine scriptEngine = (new ScriptEngineManager())
			.getEngineByName(Config.SCRIPTING_ENGINE_NAME);

	private static CompiledScript compiledScriptInitialServiceScriptText;
	private static CompiledScript compiledScriptInitialScriptText;

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

	public static final String BINDING_NAME_DATA = "data";
	public static final String BINDING_NAME_SERIAL = "serial";
	public static final String BINDING_NAME_PROCESS = "process";

	public static final String BINDING_NAME_SERVICE_RESOURCES = "java_resources";
	public static final String BINDING_NAME_SERVICE_EFFECTIVEPERSON = "java_effectivePerson";
	public static final String BINDING_NAME_SERVICE_CUSTOMRESPONSE = "java_customResponse";
	public static final String BINDING_NAME_SERVICE_REQUESTTEXT = "java_requestText";
	public static final String BINDING_NAME_SERVICE_REQUEST = "java_request";
	public static final String BINDING_NAME_SERVICE_PARAMETERS = "java_parameters";

	public static final String BINDING_NAME_SERVICE_PERSON = "person";
	public static final String BINDING_NAME_SERVICE_BODY = "body";
	public static final String BINDING_NAME_SERVICE_MESSAGE = "message";

	public static ScriptEngine newScriptEngine() {
		return (new ScriptEngineManager()).getEngineByName(Config.SCRIPTING_ENGINE_NAME);
	}

	public static synchronized CompiledScript initialServiceScript()
			throws IOException, ScriptException, URISyntaxException {
		if (compiledScriptInitialServiceScriptText == null) {
			String text = Config.initialServiceScriptText();
			compiledScriptInitialServiceScriptText = ((Compilable) scriptEngine).compile(text);
		}
		return compiledScriptInitialServiceScriptText;
	}

	public static synchronized CompiledScript initialScript() throws IOException, ScriptException, URISyntaxException {
		if (compiledScriptInitialScriptText == null) {
			String text = Config.initialScriptText();
			compiledScriptInitialScriptText = ((Compilable) scriptEngine).compile(text);
		}
		return compiledScriptInitialScriptText;
	}

	public static CompiledScript compile(String text) throws ScriptException {
		return ((Compilable) scriptEngine).compile(text);
	}

	public static String functionalization(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("var o = (function(){").append(System.lineSeparator());
		sb.append(Objects.toString(text, "")).append(System.lineSeparator());
		sb.append("}.apply(this));").append(System.lineSeparator());
		sb.append(
				"(o && (o !== false) && o.getClass && (typeof o == 'object')) ? Java.type('com.x.base.core.project.gson.XGsonBuilder').toJson(o) : JSON.stringify(o);");
		return sb.toString();
//		StringBuilder sb = new StringBuilder();
//		sb.append("JSON.stringify((function(){").append(System.lineSeparator());
//		sb.append("}).apply(this));");
//		return sb.toString();
	}

	public static CompiledScript functionalizationCompile(String text) throws ScriptException {
		return ((Compilable) scriptEngine).compile(functionalization(text));
	}

	public static ScriptContext scriptContextEvalInitialServiceScript() {
		ScriptContext scriptContext = new SimpleScriptContext();
		try {
			ScriptingFactory.initialServiceScript().eval(scriptContext);
		} catch (ScriptException | URISyntaxException | IOException e) {
			LOGGER.error(e);
		}
		return scriptContext;
	}

	public static ScriptContext scriptContextEvalInitialScript() {
		ScriptContext scriptContext = new SimpleScriptContext();
		try {
			ScriptingFactory.initialScript().eval(scriptContext);
		} catch (ScriptException | URISyntaxException | IOException e) {
			LOGGER.error(e);
		}
		return scriptContext;
	}

}