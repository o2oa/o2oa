package com.x.base.core.project.logger;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.helpers.MessageFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;

public class Logger {

	public static Gson gson = XGsonBuilder.instance();

	Logger(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	static int level = 20;

	final static int TRACE_INT = 00;
	final static int DEBUG_INT = 10;
	final static int INFO_INT = 20;
	final static int WARN_INT = 30;
	final static int ERROR_INT = 40;

	public final static String PRINT = "PRINT";
	public final static String TRACE = "TRACE";
	public final static String DEBUG = "DEBUG";
	public final static String INFO = "INFO";
	public final static String WARN = "WARN";
	public final static String ERROR = "ERROR";

	private final static String PARAMETER_ID = "id";
	private final static String PARAMETER_VERSION = "version";
	private final static String PARAMETER_OCCURTIME = "occurTime";
	private final static String PARAMETER_LOGGERNAME = "loggerName";
	private final static String PARAMETER_EXCEPTIONCLASS = "exceptionClass";
	private final static String PARAMETER_MESSAGE = "message";
	private final static String PARAMETER_STACKTRACE = "stackTrace";
	private final static String PARAMETER_PERSON = "person";
	private final static String PARAMETER_REQUESTURL = "requestUrl";
	private final static String PARAMETER_REQUESTMETHOD = "requestMethod";
	private final static String PARAMETER_REQUESTREMOTEADDR = "requestRemoteAddr";
	private final static String PARAMETER_REQUESTHOST = "requestRemoteHost";
	private final static String PARAMETER_REQUESTHEAD = "requestHead";
	private final static String PARAMETER_REQUESTBODY = "requestBody";
	private final static String PARAMETER_REQUESTBODYLENGTH = "requestBodyLength";

	private final static String HTTPMESSAGEFORMAT = " > person:{}, method:{}, request:{}, remote host:{} address:{}, head:{}, body:{}.";

	public boolean isDebug(EffectivePerson effectivePerson) {
		if (null != effectivePerson && BooleanUtils.isTrue(effectivePerson.getDebugger())) {
			return true;
		} else {
			return this.isDebug();
		}
	}

	public boolean isDebug() {
		if (level <= DEBUG_INT) {
			return true;
		}
		return false;
	}

	public void print(String message, Object... os) {
		this.log(PRINT, message, os);
	}

	public void trace(String message, Object... os) {
		if (level <= TRACE_INT) {
			this.log(TRACE, message, os);
		}
	}

	public void debug(EffectivePerson effectivePerson, String message, Object... os) {
		if (null != effectivePerson && BooleanUtils.isTrue(effectivePerson.getDebugger())) {
			this.log(DEBUG, message, os);
		} else {
			this.debug(message, os);
		}
	}

	public void debug(boolean debugger, String message, Object... os) {
		if (BooleanUtils.isTrue(debugger)) {
			this.log(DEBUG, message, os);
		} else {
			this.debug(message, os);
		}
	}

	public void debug(String message, Object... os) {
		if (level <= DEBUG_INT) {
			this.log(DEBUG, message, os);
		}
	}

	public void info(String message, Object... os) {
		if (level <= INFO_INT) {
			this.log(INFO, message, os);
		}
	}

	public void warn(String message, Object... os) {
		if (level <= WARN_INT) {
			String id = StringTools.uniqueToken();
			StringBuilder sb = this.create(WARN, id);
			String str = format(message, os);
			sb.append(str);
			System.out.println(sb.toString());
			String loggerName = this.getName();
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						Map<String, Object> parameters = new HashMap<>();
						parameters.put(PARAMETER_ID, id);
						parameters.put(PARAMETER_VERSION, Config.version());
						parameters.put(PARAMETER_OCCURTIME, DateTools.now());
						parameters.put(PARAMETER_LOGGERNAME, loggerName);
						parameters.put(PARAMETER_MESSAGE, str);
						String url = Config.x_program_centerUrlRoot() + "warnlog";
						CipherConnectionAction.post(false, url, parameters);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}
	}

	public int level() {
		return level;
	}

	public void error(Exception e) {
		String id = StringTools.uniqueToken();
		StringBuilder sb = this.create(ERROR, id);
		sb.append(e.getClass().getName());
		sb.append("[");
		sb.append(e.getMessage());
		sb.append("]");
		String stackTraceString = ExceptionUtils.getStackTrace(e);
		if (!(e instanceof PromptException)) {
			sb.append(System.lineSeparator());
			sb.append(stackTraceString);
		}
		System.err.println(sb.toString());
		String loggerName = this.getName();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Map<String, Object> parameters = new HashMap<>();
					parameters.put(PARAMETER_ID, id);
					parameters.put(PARAMETER_VERSION, Config.version());
					parameters.put(PARAMETER_OCCURTIME, DateTools.now());
					parameters.put(PARAMETER_LOGGERNAME, loggerName);
					parameters.put(PARAMETER_EXCEPTIONCLASS, e.getClass().getName());
					parameters.put(PARAMETER_MESSAGE, e.getMessage());
					parameters.put(PARAMETER_STACKTRACE, stackTraceString);
					if (e instanceof PromptException) {
						String url = Config.x_program_centerUrlRoot() + "prompterrorlog";
						CipherConnectionAction.post(false, url, parameters);
					} else {
						String url = Config.x_program_centerUrlRoot() + "unexpectederrorlog";
						CipherConnectionAction.post(false, url, parameters);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public void error(Exception e, EffectivePerson effectivePerson, HttpServletRequest request, JsonElement body) {
		String id = StringTools.uniqueToken();
		StringBuilder sb = this.create(ERROR, id);
		sb.append(e.getMessage());
		String headString = this.headToString(request);
		String bodyString = this.bodyToString(body);
		String requestUrl = request.getRequestURL().toString();
		String stackTraceString = ExceptionUtils.getStackTrace(e);
		Object[] arr = new String[] { effectivePerson.getDistinguishedName(), request.getMethod(), requestUrl,
				request.getRemoteHost(), request.getRemoteAddr(), headString, bodyString };
		sb.append(format(HTTPMESSAGEFORMAT, arr));
		if (!(e instanceof PromptException)) {
			sb.append(System.lineSeparator());
			sb.append(stackTraceString);
		}
		System.err.println(sb.toString());
		String loggerName = this.getName();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Map<String, Object> parameters = new HashMap<>();
					parameters.put(PARAMETER_ID, id);
					parameters.put(PARAMETER_VERSION, Config.version());
					parameters.put(PARAMETER_OCCURTIME, DateTools.now());
					parameters.put(PARAMETER_LOGGERNAME, loggerName);
					parameters.put(PARAMETER_EXCEPTIONCLASS, e.getClass().getName());
					parameters.put(PARAMETER_MESSAGE, e.getMessage());
					parameters.put(PARAMETER_STACKTRACE, stackTraceString);
					parameters.put(PARAMETER_PERSON,
							(null == effectivePerson) ? null : effectivePerson.getDistinguishedName());
					parameters.put(PARAMETER_REQUESTURL, requestUrl);
					parameters.put(PARAMETER_REQUESTMETHOD, request.getMethod());
					parameters.put(PARAMETER_REQUESTREMOTEADDR, request.getRemoteAddr());
					parameters.put(PARAMETER_REQUESTHOST, request.getRemoteHost());
					parameters.put(PARAMETER_REQUESTHEAD, headString);
					parameters.put(PARAMETER_REQUESTBODY, bodyString);
					parameters.put(PARAMETER_REQUESTBODYLENGTH, bodyString.length());
					if (e instanceof PromptException) {
						String url = Config.x_program_centerUrlRoot() + "prompterrorlog";
						CipherConnectionAction.post(false, url, parameters);
					} else {
						String url = Config.x_program_centerUrlRoot() + "unexpectederrorlog";
						CipherConnectionAction.post(false, url, parameters);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	private void log(String logLevel, String message, Object... os) {
		StringBuilder sb = this.create(logLevel, null);
		sb.append(format(message, os));
		System.out.println(sb.toString());
	}

	private StringBuilder create(String logLevel, String id) {
		StringBuilder o = new StringBuilder();
		o.append(DateTools.now()).append(" ").append(logLevel);
		o.append(" [").append(StringUtils.isEmpty(id) ? Thread.currentThread().getName() : id).append("] ");
		o.append(this.name).append(" - ");
		return o;
	}

	private static String format(String message, Object... os) {
		return MessageFormatter.arrayFormat(message, os).getMessage();
	}

	private String bodyToString(JsonElement body) {
		String str = "";
		if (null != body) {
			str = XGsonBuilder.toJson(body);
		}
		return str;
	}

	private String headToString(HttpServletRequest request) {
		Enumeration<String> en = request.getHeaderNames();
		StringBuilder sb = new StringBuilder();
		while (en.hasMoreElements()) {
			String name = en.nextElement();
			String value = request.getHeader(name);
			sb.append(name).append(":").append(value);
			if (en.hasMoreElements()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public Audit audit(EffectivePerson effectivePerson) {
		Audit o = new Audit(effectivePerson.getDistinguishedName(), effectivePerson.getRemoteAddress(),
				effectivePerson.getUri(), effectivePerson.getUserAgent(), this.name);
		return o;
	}

}