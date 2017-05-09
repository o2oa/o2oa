package com.x.base.core.project.connection;

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
import org.junit.Test;

import com.google.gson.Gson;
import com.x.base.core.DefaultCharset;
import com.x.base.core.bean.NameValuePair;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.utils.ListTools;

public class ConnectionAction {

	private static String Access_Control_Allow_Credentials = "Access-Control-Allow-Credentials";
	private static String Access_Control_Allow_Credentials_Value = "true";
	private static String Access_Control_Allow_Headers = "Access-Control-Allow-Headers";
	private static String Access_Control_Allow_Headers_Value = "x-requested-with, x-request, x-token,Content-Type, x-cipher, x-client";
	private static String Access_Control_Allow_Methods = "Access-Control-Allow-Methods";
	private static String Access_Control_Allow_Methods_Value = "GET, POST, OPTIONS, PUT, DELETE, HEAD, TRACE";

	private static String Cache_Control = "Cache-Control";
	private static String Cache_Control_Value = "no-cache, no-transform";
	private static String Content_Type = "Content-Type";
	private static String Content_Type_Value = "application/json;charset=UTF-8";

	private static String METHOD_PUT = "PUT";
	private static String METHOD_POST = "POST";
	private static String METHOD_GET = "GET";
	private static String METHOD_DELETE = "DELETE";

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
		byte[] buffer = null;
		int code = connection.getResponseCode();
		if (code != 200) {
			if (code > 400 && code < 500) {
				response.setType(ActionResponse.Type.error);
				response.setMessage("ConnectionAction get responseCode error: [" + address + "], code: " + code
						+ (StringUtils.isEmpty(response.getPrompt()) ? "." : (", " + response.getPrompt())));
				return response;
			}
			if (code > 500) {
				try (InputStream input = connection.getErrorStream()) {
					buffer = IOUtils.toByteArray(input);
				} catch (Exception e) {
					response.setType(ActionResponse.Type.error);
					response.setMessage("ConnectionAction get read input error: [" + address + "], "
							+ e.getClass().getName() + ".");
					return response;
				}
			}
		} else {
			try (InputStream input = connection.getInputStream()) {
				buffer = IOUtils.toByteArray(input);
			} catch (Exception e) {
				response.setType(ActionResponse.Type.connectFatal);
				response.setMessage(
						"ConnectionAction get read input error: [" + address + "], " + e.getClass().getName() + ".");
				return response;
			}
		}
		try {
			response = gson.fromJson(new String(buffer, DefaultCharset.name), ActionResponse.class);
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction get convert to json error:[" + address + "], " + e.getClass().getName() + ".");
			return response;
		}

		if (!Objects.equals(ActionResponse.Type.success, response.getType())) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction get not get success return :[" + address + "], " + response.getMessage()
							+ (StringUtils.isEmpty(response.getPrompt()) ? "." : (", " + response.getPrompt())));
		}
		response.setType(ActionResponse.Type.success);
		return response;
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
		byte[] buffer = null;
		int code = connection.getResponseCode();
		if (code != 200) {
			if (code > 400 && code < 500) {
				response.setType(ActionResponse.Type.error);
				response.setMessage("ConnectionAction delete responseCode error: [" + address + "], code: " + code
						+ (StringUtils.isEmpty(response.getPrompt()) ? "." : (", " + response.getPrompt())));
				return response;
			}
			if (code > 500) {
				try (InputStream input = connection.getErrorStream()) {
					buffer = IOUtils.toByteArray(input);
				} catch (Exception e) {
					response.setType(ActionResponse.Type.error);
					response.setMessage("ConnectionAction delete read input error: [" + address + "], "
							+ e.getClass().getName() + ".");
					return response;
				}
			}
		} else {
			try (InputStream input = connection.getInputStream()) {
				buffer = IOUtils.toByteArray(input);
			} catch (Exception e) {
				response.setType(ActionResponse.Type.connectFatal);
				response.setMessage(
						"ConnectionAction delete read input error: [" + address + "], " + e.getClass().getName() + ".");
				return response;
			}
		}
		try {
			response = gson.fromJson(new String(buffer, DefaultCharset.name), ActionResponse.class);
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction delete convert to json error:[" + address + "], " + e.getClass().getName() + ".");
			return response;
		}

		if (!Objects.equals(ActionResponse.Type.success, response.getType())) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction delete not get success return :[" + address + "], " + response.getMessage()
							+ (StringUtils.isEmpty(response.getPrompt()) ? "." : (", " + response.getPrompt())));
		}
		response.setType(ActionResponse.Type.success);
		return response;
	}

	public static ActionResponse post(String address, List<NameValuePair> heads, Object body) throws Exception {
		ActionResponse response = new ActionResponse();
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage("ConnectionAction post create connection error, address:" + address + ", because:"
					+ e.getMessage());
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
			response.setMessage(
					"ConnectionAction post connect error, address:" + address + ", because:" + e.getMessage());
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
					"ConnectionAction post output error: [" + address + "], " + e.getClass().getName() + ".");
			return response;
		}
		byte[] buffer = null;
		int code = connection.getResponseCode();
		if (code != 200) {
			if (code > 400 && code < 500) {
				response.setType(ActionResponse.Type.error);
				response.setMessage("ConnectionAction post responseCode error: [" + address + "], code: " + code
						+ (StringUtils.isEmpty(response.getPrompt()) ? "." : (", " + response.getPrompt())));
				return response;
			}
			if (code > 500) {
				try (InputStream input = connection.getErrorStream()) {
					buffer = IOUtils.toByteArray(input);
				} catch (Exception e) {
					response.setType(ActionResponse.Type.error);
					response.setMessage("ConnectionAction post read input error: [" + address + "], "
							+ e.getClass().getName() + ".");
					return response;
				}
			}
		} else {
			try (InputStream input = connection.getInputStream()) {
				buffer = IOUtils.toByteArray(input);
			} catch (Exception e) {
				response.setType(ActionResponse.Type.connectFatal);
				response.setMessage(
						"ConnectionAction post read input error: [" + address + "], " + e.getClass().getName() + ".");
				return response;
			}
		}
		try {
			response = gson.fromJson(new String(buffer, DefaultCharset.name), ActionResponse.class);
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction post convert to json error:[" + address + "], " + e.getClass().getName() + ".");
			return response;
		}

		if (!Objects.equals(ActionResponse.Type.success, response.getType())) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction post not get success return :[" + address + "], " + response.getMessage()
							+ (StringUtils.isEmpty(response.getPrompt()) ? "." : (", " + response.getPrompt())));
		}
		response.setType(ActionResponse.Type.success);
		return response;
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
		byte[] buffer = null;
		int code = connection.getResponseCode();
		if (code != 200) {
			if (code > 400 && code < 500) {
				response.setType(ActionResponse.Type.error);
				response.setMessage("ConnectionAction put responseCode error: [" + address + "], code: " + code
						+ (StringUtils.isEmpty(response.getPrompt()) ? "." : (", " + response.getPrompt())));
				return response;
			}
			if (code > 500) {
				try (InputStream input = connection.getErrorStream()) {
					buffer = IOUtils.toByteArray(input);
				} catch (Exception e) {
					response.setType(ActionResponse.Type.error);
					response.setMessage("ConnectionAction put read input error: [" + address + "], "
							+ e.getClass().getName() + ".");
					return response;
				}
			}
		} else {
			try (InputStream input = connection.getInputStream()) {
				buffer = IOUtils.toByteArray(input);
			} catch (Exception e) {
				response.setType(ActionResponse.Type.connectFatal);
				response.setMessage(
						"ConnectionAction put read input error: [" + address + "], " + e.getClass().getName() + ".");
				return response;
			}
		}
		try {
			response = gson.fromJson(new String(buffer, DefaultCharset.name), ActionResponse.class);
		} catch (Exception e) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction put convert to json error:[" + address + "], " + e.getClass().getName() + ".");
			return response;
		}

		if (!Objects.equals(ActionResponse.Type.success, response.getType())) {
			response.setType(ActionResponse.Type.connectFatal);
			response.setMessage(
					"ConnectionAction put not get success return :[" + address + "], " + response.getMessage()
							+ (StringUtils.isEmpty(response.getPrompt()) ? "." : (", " + response.getPrompt())));
		}
		response.setType(ActionResponse.Type.success);
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

	@Test
	public void test() throws Exception {
		Map<String, Object> body = new HashMap<>();
		body.put("name", "dev.ray.local");
		body.put("password", "1");
		body.put("unexpectedEorrorLogList", "[]");
		String url = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/collect/unexpectederrorlog/receive";
		ActionResponse response = ConnectionAction.put(url, null, body);
		System.out.println(response.getData(WrapOutBoolean.class));
	}

}