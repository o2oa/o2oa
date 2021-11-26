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

	public static final String BINDING_NAME_REQUESTTEXT = "requestText";
	public static final String BINDING_NAME_REQUEST = "request";
	public static final String BINDING_NAME_CUSTOMRESPONSE = "customResponse";

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
		sb.append("JSON.stringify((function(){").append(System.lineSeparator());
		sb.append(Objects.toString(text, "")).append(System.lineSeparator());
		sb.append("}).apply(this));");
		return sb.toString();
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