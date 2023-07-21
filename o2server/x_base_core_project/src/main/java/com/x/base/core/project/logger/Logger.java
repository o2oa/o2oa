package com.x.base.core.project.logger;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;

public class Logger {

	private final org.slf4j.Logger internalLogger;

	Logger(String name) {
		this.name = name;
		internalLogger = org.slf4j.LoggerFactory.getLogger(name);
	}

	private String name;

	public String getName() {
		return this.name;
	}

	private static final String PARAMETER_ID = "id";
	private static final String PARAMETER_VERSION = "version";
	private static final String PARAMETER_OCCURTIME = "occurTime";
	private static final String PARAMETER_LOGGERNAME = "loggerName";
	private static final String PARAMETER_EXCEPTIONCLASS = "exceptionClass";
	private static final String PARAMETER_MESSAGE = "message";
	private static final String PARAMETER_STACKTRACE = "stackTrace";
	private static final String PARAMETER_PERSON = "person";
	private static final String PARAMETER_REQUESTURL = "requestUrl";
	private static final String PARAMETER_REQUESTMETHOD = "requestMethod";
	private static final String PARAMETER_REQUESTREMOTEADDR = "requestRemoteAddr";
	private static final String PARAMETER_REQUESTHOST = "requestRemoteHost";
	private static final String PARAMETER_REQUESTHEAD = "requestHead";
	private static final String PARAMETER_REQUESTBODY = "requestBody";
	private static final String PARAMETER_REQUESTBODYLENGTH = "requestBodyLength";

	private static final String ID_TAG = "id:";
	private static final String NAME_TAG = ", name:";
	private static final String MESSAGE_TAG = ", message:";
	private static final String EXCEPTION_TAG = ", exception:";

	private static final String HTTPMESSAGEFORMAT = "person:{}, method:{}, request:{}, remoteHost:{}, emoteAddr:{}, head:{}, body:{}";

	public boolean isTraceEnabled() {
		return internalLogger.isTraceEnabled();
	}

	public boolean isDebugEnabled() {
		return internalLogger.isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return internalLogger.isInfoEnabled();
	}

	public boolean isWarnEnabled() {
		return internalLogger.isWarnEnabled();
	}

	public boolean isErrorEnabled() {
		return internalLogger.isErrorEnabled();
	}

	@Deprecated
	public void debug(EffectivePerson noUse, String message, Object... os) {
		debug(message, os);
	}

	@Deprecated
	public void debug(boolean noUse, String message, Object... os) {
		debug(message, os);
	}

	public void trace(String message, Object... os) {
		if (internalLogger.isTraceEnabled()) {
			internalLogger.trace(message, os);
		}
	}

	public void trace(String message, Supplier<?>... suppliers) {
		if (internalLogger.isTraceEnabled()) {
			internalLogger.trace(message, getAll(suppliers));
		}
	}

	public void debug(String message, Object... os) {
		if (internalLogger.isDebugEnabled()) {
			internalLogger.debug(message, os);
		}
	}

	public void debug(String message, Supplier<?>... suppliers) {
		if (internalLogger.isDebugEnabled()) {
			internalLogger.debug(message, getAll(suppliers));
		}
	}

	public void info(String message, Object... os) {
		if (internalLogger.isInfoEnabled()) {
			internalLogger.info(message, os);
		}
	}

	public void info(String message, Supplier<?>... suppliers) {
		if (internalLogger.isInfoEnabled()) {
			internalLogger.info(message, getAll(suppliers));
		}
	}

	public void warn(String message, Object... os) {
		String id = StringTools.uniqueToken();
		String formattedMessage = format(message, os);
		String text = this.message(id, formattedMessage);
		if (internalLogger.isWarnEnabled()) {
			internalLogger.warn(text);
		}
		String loggerName = this.getName();
		new Thread(() -> {
			try {
				Map<String, Object> parameters = new HashMap<>();
				parameters.put(PARAMETER_ID, id);
				parameters.put(PARAMETER_VERSION, Config.version());
				parameters.put(PARAMETER_OCCURTIME, DateTools.now());
				parameters.put(PARAMETER_LOGGERNAME, loggerName);
				parameters.put(PARAMETER_MESSAGE, formattedMessage);
				String url = Config.url_x_program_center_jaxrs("warnlog");
				CipherConnectionAction.post(false, url, parameters);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, Logger.class.getName() + "-warn").start();
	}

	public void warn(String message, Supplier<?>... suppliers) {
		warn(message, getAll(suppliers));
	}

	public void error(Exception e) {
		String id = StringTools.uniqueToken();
		String formattedMessage = this.message(id, e);
		if (internalLogger.isErrorEnabled()) {
			internalLogger.error(formattedMessage, e);
		}
		new Thread(() -> {
			try {
				Map<String, Object> parameters = new HashMap<>();
				parameters.put(PARAMETER_ID, id);
				parameters.put(PARAMETER_VERSION, Config.version());
				parameters.put(PARAMETER_OCCURTIME, DateTools.now());
				parameters.put(PARAMETER_LOGGERNAME, this.getName());
				parameters.put(PARAMETER_EXCEPTIONCLASS, e.getClass().getName());
				parameters.put(PARAMETER_MESSAGE, e.getMessage());
				parameters.put(PARAMETER_STACKTRACE, ExceptionUtils.getStackTrace(e));
				if (e instanceof PromptException) {
					String url = Config.url_x_program_center_jaxrs("prompterrorlog");
					CipherConnectionAction.post(false, url, parameters);
				} else {
					String url = Config.url_x_program_center_jaxrs("unexpectederrorlog");
					CipherConnectionAction.post(false, url, parameters);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}, Logger.class.getName() + "-error").start();
	}

	public void error(Exception e, EffectivePerson effectivePerson, HttpServletRequest request, JsonElement body) {
		String id = StringTools.uniqueToken();
		String headString = this.headToString(request);
		String bodyString = this.bodyToString(body);
		String requestUrl = url(request);
		String formattedMessage = message(id, e, requestToString(effectivePerson, request, headString, bodyString));
		if (internalLogger.isErrorEnabled()) {
			internalLogger.error(this.message(id, e, formattedMessage), e);
		}
		new Thread(() -> {
			try {
				Map<String, Object> parameters = new HashMap<>();
				parameters.put(PARAMETER_ID, id);
				parameters.put(PARAMETER_VERSION, Config.version());
				parameters.put(PARAMETER_OCCURTIME, DateTools.now());
				parameters.put(PARAMETER_LOGGERNAME, getName());
				parameters.put(PARAMETER_EXCEPTIONCLASS, e.getClass().getName());
				parameters.put(PARAMETER_MESSAGE, e.getMessage());
				parameters.put(PARAMETER_STACKTRACE, ExceptionUtils.getStackTrace(e));
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
					String url = Config.url_x_program_center_jaxrs("prompterrorlog");
					CipherConnectionAction.post(false, url, parameters);
				} else {
					String url = Config.url_x_program_center_jaxrs("unexpectederrorlog");
					CipherConnectionAction.post(false, url, parameters);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}, Logger.class.getName() + "-error").start();
	}

	private String message(String id, String message) {
		StringBuilder o = new StringBuilder();
		o.append(ID_TAG).append(StringUtils.isEmpty(id) ? Thread.currentThread().getName() : id).append(NAME_TAG)
				.append(this.name).append(MESSAGE_TAG).append(message);
		return o.toString();
	}

	private String message(String id, Throwable th) {
		StringBuilder o = new StringBuilder();
		o.append(ID_TAG).append(StringUtils.isEmpty(id) ? Thread.currentThread().getName() : id).append(NAME_TAG)
				.append(this.name).append(MESSAGE_TAG).append(th.getMessage()).append(EXCEPTION_TAG)
				.append(th.getClass().getName()).append(".");
		return o.toString();
	}

	private String message(String id, Throwable th, String request) {
		StringBuilder o = new StringBuilder();
		o.append(ID_TAG).append(StringUtils.isEmpty(id) ? Thread.currentThread().getName() : id).append(NAME_TAG)
				.append(this.name).append(MESSAGE_TAG).append(th.getMessage()).append(EXCEPTION_TAG)
				.append(th.getClass().getName()).append(", ").append(request).append(".");
		return o.toString();
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
			String n = en.nextElement();
			String v = request.getHeader(n);
			sb.append(n).append(":").append(v);
			if (en.hasMoreElements()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public void print(String message, Object... os) {
		System.out.println(format(message, os));
	}

	public void print(String message, Supplier<?>... suppliers) {
		System.out.println(format(message, getAll(suppliers)));
	}

	private String requestToString(EffectivePerson effectivePerson, HttpServletRequest request, String headString,
			String bodyString) {
		return format(HTTPMESSAGEFORMAT, effectivePerson.getDistinguishedName(), request.getMethod(), this.url(request),
				request.getRemoteHost(), request.getRemoteAddr(), headString, bodyString);
	}

	private String url(HttpServletRequest request) {
		return request.getRequestURL().toString()
				+ (StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString());
	}

	private static Object[] getAll(final Supplier<?>... suppliers) {
		if (suppliers == null) {
			return new Object[0];
		}
		final Object[] result = new Object[suppliers.length];
		for (int i = 0; i < result.length; i++) {
			Supplier<?> supplier = suppliers[i];
			result[i] = (supplier == null) ? null : supplier.get();
		}
		return result;
	}

}