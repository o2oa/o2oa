package com.x.base.core.project.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

public class ConnectionAction {

	public static final String Access_Control_Allow_Credentials = "Access-Control-Allow-Credentials";
	public static final String Access_Control_Allow_Credentials_Value = "true";
	public static final String Access_Control_Allow_Headers = "Access-Control-Allow-Headers";
	public static final String Access_Control_Allow_Headers_Value = "x-requested-with, x-request, x-token,Content-Type, x-cipher, x-client";
	public static final String Access_Control_Allow_Methods = "Access-Control-Allow-Methods";
	public static final String Access_Control_Allow_Methods_Value = "GET, POST, OPTIONS, PUT, DELETE, HEAD, TRACE";

	public static final String Cache_Control = "Cache-Control";
	public static final String Cache_Control_Value = "no-cache, no-transform";
	public static final String Content_Type = "Content-Type";
	public static final String Content_Type_Value = "application/json;charset=UTF-8";

	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_DELETE = "DELETE";

	private static Gson gson = XGsonBuilder.instance();

	public static ActionResponse get(String address, List<NameValuePair> heads) throws Exception {
		ActionResponse response = new ActionResponse();
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction get create connection error, address:" + address + ", because:" + e.getMessage());
			return response;
		}
		addHeads(connection, heads);
		connection.setRequestMethod(METHOD_GET);
		connection.setUseCaches(false);
		connection.setDoOutput(false);
		connection.setDoInput(true);
		try {
			/** 访问主机上的端口 */
			connection.connect();
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction get connect error, address:" + address + ", because:" + e.getMessage());
			return response;
		}
		return read(response, connection);
	}

	public static ActionResponse delete(String address, List<NameValuePair> heads) throws Exception {
		ActionResponse response = new ActionResponse();
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage("ConnectionAction delete create connection error, address:" + address + ", because:"
					+ e.getMessage());
			return response;
		}
		addHeads(connection, heads);
		connection.setRequestMethod(METHOD_DELETE);
		connection.setUseCaches(false);
		connection.setDoOutput(false);
		connection.setDoInput(true);
		try {
			/** 访问主机上的端口 */
			connection.connect();
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction delete connect error, address:" + address + ", because:" + e.getMessage());
			return response;
		}
		return read(response, connection);
	}

	public static ActionResponse post(String address, List<NameValuePair> heads, Object body) throws Exception {
		ActionResponse response = new ActionResponse();
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage("create connection error, address:" + Objects.toString(connection.getURL())
					+ ", method:" + connection.getRequestMethod() + ", because:" + e.getMessage() + ".");
			return response;
		}
		addHeads(connection, heads);
		connection.setRequestMethod(METHOD_POST);
		connection.setUseCaches(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		try {
			/** 访问主机上的端口 */
			connection.connect();
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage("connect error, address:" + Objects.toString(connection.getURL()) + ", method:"
					+ connection.getRequestMethod() + ", because:" + e.getMessage() + ".");
			return response;
		}
		try (OutputStream output = connection.getOutputStream()) {
			if (null != body) {
				if (body instanceof CharSequence) {
					IOUtils.write(Objects.toString(body), output, StandardCharsets.UTF_8);
				} else {
					IOUtils.write(gson.toJson(body), output, StandardCharsets.UTF_8);
				}
			}
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage("output error, address:" + Objects.toString(connection.getURL()) + ", method:"
					+ connection.getRequestMethod() + ", because:" + e.getMessage() + ".");
			return response;
		}
		return read(response, connection);
	}

	public static ActionResponse put(String address, List<NameValuePair> heads, Object body) throws Exception {
		ActionResponse response = new ActionResponse();
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction put create connection error, address:" + address + ", because:" + e.getMessage());
			return response;
		}
		addHeads(connection, heads);
		connection.setRequestMethod(METHOD_PUT);
		connection.setUseCaches(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		try {
			/** 访问主机上的端口 */
			connection.connect();
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction put connect error, address:" + address + ", because:" + e.getMessage());
			return response;
		}
		try (OutputStream output = connection.getOutputStream()) {
			if (null != body) {
				if (body instanceof CharSequence) {
					IOUtils.write(Objects.toString(body), output, StandardCharsets.UTF_8);
				} else {
					IOUtils.write(gson.toJson(body), output, StandardCharsets.UTF_8);
				}
			}
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction put output error: [" + address + "], " + e.getClass().getName() + ".");
			return response;
		}
		return read(response, connection);
	}

	private static String extractErrorMessageIfExist(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}
		try {
			JsonElement jsonElement = gson.fromJson(str, JsonElement.class);
			if (jsonElement.isJsonObject()) {
				ActionResponse ar = gson.fromJson(jsonElement, ActionResponse.class);
				if (StringUtils.isNotEmpty(ar.getMessage())) {
					return ar.getMessage();
				}
			}
		} catch (JsonParseException e) {
			/*
			 * pass
			 */
		}
		return str;
	}

	private static ActionResponse read(ActionResponse response, HttpURLConnection connection) throws IOException {
		int code = connection.getResponseCode();
		if (code >= 500) {
			try (InputStream input = connection.getErrorStream()) {
				byte[] buffer = IOUtils.toByteArray(input);
				response.setMessage(extractErrorMessageIfExist(new String(buffer, DefaultCharset.name)));
				response.setType(ActionResponse.Type.error);
			} catch (Exception e) {
				response.setType(ActionResponse.Type.connectFatal);
				response.setMessage("read input error, address:" + Objects.toString(connection.getURL()) + ", method:"
						+ connection.getRequestMethod() + ", because:" + e.getMessage() + ".");
			}
		} else if (code >= 400) {
			response.setMessage(" url invalid error, address:" + Objects.toString(connection.getURL()) + ", method:"
					+ connection.getRequestMethod() + ".");
			response.setType(ActionResponse.Type.error);
		} else if (code == 200) {
			try (InputStream input = connection.getInputStream()) {
				byte[] buffer = IOUtils.toByteArray(input);
				String value = new String(buffer, DefaultCharset.name);
				try {
					response = gson.fromJson(value, ActionResponse.class);
				} catch (Exception e) {
					response.setType(ActionResponse.Type.connectFatal);
					response.setMessage("convert to json error, address:" + Objects.toString(connection.getURL())
							+ ", method:" + connection.getRequestMethod() + ", because:" + e.getMessage() + ", value:"
							+ value + ".");
				}
			} catch (Exception e) {
				response.setType(ActionResponse.Type.connectFatal);
				response.setMessage("read input error, address:" + Objects.toString(connection.getURL()) + ", method:"
						+ connection.getRequestMethod() + ", because:" + e.getMessage() + ".");
			}
		}
		connection.disconnect();
		return response;
	}

	private static void addHeads(HttpURLConnection connection, List<NameValuePair> heads) {
		connection.setRequestProperty(Access_Control_Allow_Credentials, Access_Control_Allow_Credentials_Value);
		connection.setRequestProperty(Access_Control_Allow_Headers, Access_Control_Allow_Headers_Value);
		connection.setRequestProperty(Access_Control_Allow_Methods, Access_Control_Allow_Methods_Value);
		connection.setRequestProperty(Cache_Control, Cache_Control_Value);
		connection.setRequestProperty(Content_Type, Content_Type_Value);
		if (ListTools.isNotEmpty(heads)) {
			String name;
			String value;
			for (NameValuePair o : heads) {
				name = Objects.toString(o.getName(), "");
				value = Objects.toString(o.getValue(), "");
				if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
					connection.setRequestProperty(name, value);
				}
			}
		}
	}

}