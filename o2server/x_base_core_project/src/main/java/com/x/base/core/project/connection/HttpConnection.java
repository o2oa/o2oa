package com.x.base.core.project.connection;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.ListTools;

public class HttpConnection {

	public static final String Access_Control_Allow_Credentials = "Access-Control-Allow-Credentials";
	public static final String Access_Control_Allow_Credentials_Value = "true";
	public static final String Access_Control_Allow_Headers = "Access-Control-Allow-Headers";
	public static final String Access_Control_Allow_Headers_Value = "x-requested-with, x-request, x-token,Content-Type, x-cipher";
	public static final String Access_Control_Allow_Methods = "Access-Control-Allow-Methods";
	public static final String Access_Control_Allow_Methods_Value = "GET, POST, OPTIONS, PUT, DELETE, HEAD, TRACE";
	public static final String Cache_Control = "Cache-Control";
	public static final String Cache_Control_Value = "no-cache, no-transform";
	public static final String Content_Type = "Content-Type";
	public static final String Content_Type_Value = "application/json;charset=UTF-8";

	public static String getAsString(String address, List<NameValuePair> heads) throws Exception {
		HttpURLConnection connection = prepare(address, heads);
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		String str = readResultString(connection);
		connection.disconnect();
		return str;
	}

	public static <T> T getAsObject(String address, List<NameValuePair> heads, Class<T> cls) throws Exception {
		String result = getAsString(address, heads);
		return XGsonBuilder.instance().fromJson(result, cls);
	}

	public static <T> List<T> getAsObjects(String address, List<NameValuePair> heads, Class<T> cls) throws Exception {
		Type collectionType = new TypeToken<ArrayList<T>>() {
		}.getType();
		String result = getAsString(address, heads);
		return XGsonBuilder.instance().fromJson(result, collectionType);
	}

	public static String postAsString(String address, List<NameValuePair> heads, String body) throws Exception {
		HttpURLConnection connection = prepare(address, heads);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.connect();
		doOutput(connection, body);
		String str = readResultString(connection);
		connection.disconnect();
		return str;
	}

	public static <T> T postAsObject(String address, List<NameValuePair> heads, String body, Class<T> cls)
			throws Exception {
		String result = postAsString(address, heads, body);
		return XGsonBuilder.instance().fromJson(result, cls);
	}

	public static <T> List<T> postAsObjects(String address, List<NameValuePair> heads, String body, Class<T> cls)
			throws Exception {
		Type collectionType = new TypeToken<ArrayList<T>>() {
		}.getType();
		String result = postAsString(address, heads, body);
		return XGsonBuilder.instance().fromJson(result, collectionType);
	}

	public static String putAsString(String address, List<NameValuePair> heads, String body) throws Exception {
		HttpURLConnection connection = prepare(address, heads);
		connection.setRequestMethod("PUT");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.connect();
		doOutput(connection, body);
		String str = readResultString(connection);
		connection.disconnect();
		return str;
	}

	public static <T> T putAsObject(String address, List<NameValuePair> heads, String body, Class<T> cls)
			throws Exception {
		String result = putAsString(address, heads, body);
		return XGsonBuilder.instance().fromJson(result, cls);
	}

	public static <T> List<T> putAsObjects(String address, List<NameValuePair> heads, String body, Class<T> cls)
			throws Exception {
		Type collectionType = new TypeToken<ArrayList<T>>() {
		}.getType();
		String result = putAsString(address, heads, body);
		return XGsonBuilder.instance().fromJson(result, collectionType);
	}

	public static String deleteAsString(String address, List<NameValuePair> heads) throws Exception {
		HttpURLConnection connection = prepare(address, heads);
		connection.setRequestMethod("DELETE");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		String str = readResultString(connection);
		connection.disconnect();
		return str;
	}

	public static <T> T deleteAsObject(String address, List<NameValuePair> heads, Class<T> cls) throws Exception {
		String result = deleteAsString(address, heads);
		return XGsonBuilder.instance().fromJson(result, cls);
	}

	public static <T> List<T> deleteAsObjects(String address, List<NameValuePair> heads, Class<T> cls)
			throws Exception {
		Type collectionType = new TypeToken<ArrayList<T>>() {
		}.getType();
		String result = deleteAsString(address, heads);
		return XGsonBuilder.instance().fromJson(result, collectionType);
	}

	public static HttpURLConnection prepare(String address, List<NameValuePair> heads) throws Exception {
		URL url = new URL(address);
		HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
		httpUrlConnection.setUseCaches(false);
		Map<String, String> map = new HashMap<>();
		map.put(Access_Control_Allow_Credentials, Access_Control_Allow_Credentials_Value);
		map.put(Access_Control_Allow_Headers, Access_Control_Allow_Headers_Value);
		map.put(Access_Control_Allow_Methods, Access_Control_Allow_Methods_Value);
		map.put(Cache_Control, Cache_Control_Value);
		map.put(Content_Type, Content_Type_Value);
		for (NameValuePair o : ListTools.nullToEmpty(heads)) {
			map.put(o.getName(), Objects.toString(o.getValue()));
		}
		for (Entry<String, String> en : map.entrySet()) {
			if (StringUtils.isNotEmpty(en.getValue())) {
				httpUrlConnection.setRequestProperty(en.getKey(), en.getValue());
			}
		}
		return httpUrlConnection;
	}

	private static String readResultString(HttpURLConnection connection) throws Exception {
		String result = "";
		try (InputStream input = connection.getInputStream()) {
			result = IOUtils.toString(input, StandardCharsets.UTF_8);
		}
		int code = connection.getResponseCode();
		if (code != 200) {
			throw new Exception("connection{url:" + connection.getURL() + "}, response error{responseCode:" + code
					+ "}, response:" + result + ".");
		}
		return result;
	}

	private static void doOutput(HttpURLConnection connection, String body) throws Exception {
		try (OutputStream output = connection.getOutputStream()) {
			if (StringUtils.isNotEmpty(body)) {
				IOUtils.write(body, output, StandardCharsets.UTF_8);
				// IOUtils.write(body, output);
				output.flush();
			}
		}
	}

	// private static void doOutput(HttpURLConnection connection, byte[] bytes)
	// throws Exception {
	// try (OutputStream output = connection.getOutputStream()) {
	// if (null != bytes) {
	// IOUtils.write(bytes, output);
	// output.flush();
	// }
	// }
	// }

	// private static void doOutput(HttpURLConnection connection, String body,
	// List<File> files) throws Exception {
	// try (OutputStream output = connection.getOutputStream()) {
	// if (StringUtils.isNotEmpty(body)) {
	// IOUtils.write(body, output, StandardCharsets.UTF_8);
	// // IOUtils.write(body, output);
	// output.flush();
	// }
	// }
	// }
}
