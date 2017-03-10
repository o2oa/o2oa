package com.x.base.core.application.jaxrs;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.exception.JsonElementConvertToWrapInException;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;

public abstract class AbstractJaxrsAction {

	protected static Gson gson = XGsonBuilder.instance();

	protected static String[] IMAGE_EXTENSIONS = new String[] { "jpg", "png", "bmp", "gif" };

	protected <T> T convertToWrapIn(JsonElement jsonElement, Class<T> clz) throws Exception {
		try {
			return gson.fromJson(jsonElement, clz);
		} catch (Exception e) {
			throw new JsonElementConvertToWrapInException(e, clz);
		}
	}

	protected EffectivePerson effectivePerson(HttpServletRequest request) {
		Object o = request.getAttribute(HttpToken.X_Person);
		if (null != o) {
			return (EffectivePerson) o;
		} else {
			return EffectivePerson.anonymous();
		}
	}

	// protected void error(Exception e, Logger logger, EffectivePerson
	// effectivePerson, HttpServletRequest request,
	// JsonElement body) {
	// String bodyString = this.bodyToString(body);
	// String requestUrl = request.getRequestURL().toString();
	// logger.error("message:{}, person:{}, request:{} remote:{},
	// parameter:{}.", e.getMessage(),
	// effectivePerson.getName(), request.getMethod() + " " + requestUrl,
	// request.getRemoteAddr(),
	// (null == body) ? null : gson.toJson(body));
	// if (!(e instanceof PromptException)) {
	// e.printStackTrace();
	// }
	// Thread thread = new Thread(new Runnable() {
	// public void run() {
	// try {
	// Map<String, Object> parameters = new HashMap<>();
	// parameters.put("version", Config.version());
	// parameters.put("occurTime", DateTools.now());
	// parameters.put("loggerName", logger.getName());
	// parameters.put("message", e.getMessage());
	// parameters.put("stackTrace", ExceptionUtils.getStackTrace(e));
	// parameters.put("person", (null == effectivePerson) ? null :
	// effectivePerson.getName());
	// parameters.put("requestUrl", requestUrl);
	// parameters.put("requestMethod", request.getMethod());
	// parameters.put("requestRemoteAddr", request.getRemoteAddr());
	// parameters.put("requestRemoteHost", request.getRemoteHost());
	// parameters.put("requestBody", bodyString);
	// parameters.put("requestBodyLength", bodyString.length());
	// AbstractThisApplication.applications.postQuery(x_instrument_service_express.class,
	// "promptexceptionlog", parameters, WrapOutBoolean.class);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// thread.start();
	// }
	//
	// private String bodyToString(JsonElement body) {
	// String str = "";
	// if (null != body) {
	// str = XGsonBuilder.toJson(body);
	// }
	// return str;
	// }

}