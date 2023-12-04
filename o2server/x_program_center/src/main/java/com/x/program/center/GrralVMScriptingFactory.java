package com.x.program.center;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class GrralVMScriptingFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(GrralVMScriptingFactory.class);

	private GrralVMScriptingFactory() {

	}

	private static ReentrantLock lock = new ReentrantLock();

	private static Engine engine;

	private static Source initialScriptSource;

	public static Value eval(String text) {
		try (Context context = Context.newBuilder().engine(getEngine()).build()) {
			context.eval(getInitialScriptSource());
			Source source = Source.create("js", functionalization(text));
			return context.eval(source);
		}
	}

	private static Engine getEngine() {
		if (null == engine) {
			lock.lock();
			try {
				engine = Engine.newBuilder("js").build();
			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				lock.unlock();
			}
		}
		return engine;
	}

	private static Source getInitialScriptSource() {
		if (null == initialScriptSource) {
			lock.lock();
			try {
				initialScriptSource = Source.create("js", Config.initialScriptText());
			} catch (Exception e) {
				LOGGER.error(e);
			} finally {
				lock.unlock();
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

	public static String functionalization(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("var o = (function(){").append(System.lineSeparator());
		sb.append(Objects.toString(text, "")).append(System.lineSeparator());
		sb.append("}.apply(this));").append(System.lineSeparator());
		sb.append("if (this.data && this.data.commit) this.data.commit();");
		sb.append(
				"(o && (o !== false) && o.getClass && (typeof o == 'object')) ? Java.type('com.x.base.core.project.gson.XGsonBuilder').toJson(o) : JSON.stringify(toJsJson(o));");
		return sb.toString();
	}

}