package com.x.base.core.logger;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.helpers.MessageFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.exception.PromptException;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.DateTools;

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

	final static String TRACE = "TRACE";
	final static String DEBUG = "DEBUG";
	final static String INFO = "INFO";
	final static String WARN = "WARN";
	final static String ERROR = "ERROR";

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

	private final static String HTTPMESSAGEFORMAT = "person:{}, method:{}, request:{}, remote host:{} address:{}, head:{}, body:{}.";

	public void trace(String message, Object... os) {
		if (level <= TRACE_INT) {
			this.log(TRACE, message, os);
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
			StringBuilder sb = this.create(WARN);
			String str = format(message, os);
			sb.append(str);
			System.out.println(sb.toString());
			String loggerName = this.getName();
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						Map<String, Object> parameters = new HashMap<>();
						parameters.put(PARAMETER_VERSION, Config.version());
						parameters.put(PARAMETER_OCCURTIME, DateTools.now());
						parameters.put(PARAMETER_LOGGERNAME, loggerName);
						parameters.put(PARAMETER_MESSAGE, str);
						String url = Config.x_program_centerUrlRoot() + "warnlog";
						CipherConnectionAction.post(url, parameters);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}
	}

	public void error(Exception e) {
		StringBuilder sb = this.create(ERROR);
		sb.append(e.getMessage());
		String stackTraceString = ExceptionUtils.getStackTrace(e);
		if (!(e instanceof PromptException)) {
			sb.append(SystemUtils.LINE_SEPARATOR);
			sb.append(stackTraceString);
		}
		System.err.println(sb.toString());
		String loggerName = this.getName();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Map<String, Object> parameters = new HashMap<>();
					parameters.put(PARAMETER_VERSION, Config.version());
					parameters.put(PARAMETER_OCCURTIME, DateTools.now());
					parameters.put(PARAMETER_LOGGERNAME, loggerName);
					parameters.put(PARAMETER_EXCEPTIONCLASS, e.getClass().getName());
					parameters.put(PARAMETER_MESSAGE, e.getMessage());
					parameters.put(PARAMETER_STACKTRACE, stackTraceString);
					if (e instanceof PromptException) {
						String url = Config.x_program_centerUrlRoot() + "prompterrorlogs";
						CipherConnectionAction.post(url, parameters);
					} else {
						String url = Config.x_program_centerUrlRoot() + "unexpectederrorlog";
						CipherConnectionAction.post(url, parameters);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public void error(Exception e, EffectivePerson effectivePerson, HttpServletRequest request, JsonElement body) {
		StringBuilder sb = this.create(ERROR);
		sb.append(e.getMessage());
		String headString = this.headToString(request);
		String bodyString = this.bodyToString(body);
		String requestUrl = request.getRequestURL().toString();
		String stackTraceString = ExceptionUtils.getStackTrace(e);
		Object[] arr = new String[] { effectivePerson.getName(), request.getMethod(), requestUrl,
				request.getRemoteHost(), request.getRemoteAddr(), headString, bodyString };
		sb.append(format(HTTPMESSAGEFORMAT, arr));
		if (!(e instanceof PromptException)) {
			sb.append(SystemUtils.LINE_SEPARATOR);
			sb.append(stackTraceString);
		}
		System.err.println(sb.toString());
		String loggerName = this.getName();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Map<String, Object> parameters = new HashMap<>();
					parameters.put(PARAMETER_VERSION, Config.version());
					parameters.put(PARAMETER_OCCURTIME, DateTools.now());
					parameters.put(PARAMETER_LOGGERNAME, loggerName);
					parameters.put(PARAMETER_EXCEPTIONCLASS, e.getClass().getName());
					parameters.put(PARAMETER_MESSAGE, e.getMessage());
					parameters.put(PARAMETER_STACKTRACE, stackTraceString);
					parameters.put(PARAMETER_PERSON, (null == effectivePerson) ? null : effectivePerson.getName());
					parameters.put(PARAMETER_REQUESTURL, requestUrl);
					parameters.put(PARAMETER_REQUESTMETHOD, request.getMethod());
					parameters.put(PARAMETER_REQUESTREMOTEADDR, request.getRemoteAddr());
					parameters.put(PARAMETER_REQUESTHOST, request.getRemoteHost());
					parameters.put(PARAMETER_REQUESTHEAD, headString);
					parameters.put(PARAMETER_REQUESTBODY, bodyString);
					parameters.put(PARAMETER_REQUESTBODYLENGTH, bodyString.length());
					if (e instanceof PromptException) {
						String url = Config.x_program_centerUrlRoot() + "prompterrorlog";
						CipherConnectionAction.post(url, parameters);
					} else {
						String url = Config.x_program_centerUrlRoot() + "unexpectederrorlog";
						CipherConnectionAction.post(url, parameters);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	private void log(String logLevel, String message, Object... os) {
		StringBuilder sb = this.create(logLevel);
		sb.append(format(message, os));
		System.out.println(sb.toString());
	}

	private StringBuilder create(String logLevel) {
		StringBuilder o = new StringBuilder();
		o.append(DateTools.now()).append(" ").append(logLevel);
		o.append(" [").append(Thread.currentThread().getName()).append("] ");
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
				sb.append(";");
			}
		}
		return sb.toString();
	}

}