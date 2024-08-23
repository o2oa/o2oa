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
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionNotSupportProtocol;
import com.x.base.core.project.exception.ExceptionUnlawfulAddress;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class HttpConnection {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpConnection.class);

	private HttpConnection() {
		// nothing
	}

	public static final int DEFAULT_CONNECTTIMEOUT = 2000;
	public static final int DEFAULT_READTIMEOUT = 5 * 60 * 1000;
	public static final String HTTP_PROTOCOL = "http";
	public static final String HTTPS_PROTOCOL = "https";

	public static HttpConnectionResponse get(String address, List<NameValuePair> heads, int connectTimeout,
			int readTimeout, Supplier<HttpConnectionResponse> supplier) {
		HttpURLConnection connection = null;
		try {
			connection = prepare(address, heads);
			connection.setRequestMethod(ConnectionAction.METHOD_GET);
			connection.setDoOutput(false);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			return supplier.get(connection);
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		return null;
	}

	public static String getAsString(String address, List<NameValuePair> heads) throws Exception {
		return getAsString(address, heads, DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT);
	}

	public static String getAsString(String address, List<NameValuePair> heads, int connectTimeout, int readTimeout)
			throws Exception {
		checkAddress(address);
		HttpURLConnection connection = null;
		try {
			connection = prepare(address, heads);
			connection.setRequestMethod(ConnectionAction.METHOD_GET);
			connection.setDoOutput(false);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			return readResultString(connection);
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		return null;
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

	public static HttpConnectionResponse post(String address, List<NameValuePair> heads, String body,
			int connectTimeout, int readTimeout, Supplier<HttpConnectionResponse> supplier) {
		HttpURLConnection connection = null;
		try {
			connection = prepare(address, heads);
			connection.setRequestMethod(ConnectionAction.METHOD_POST);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.connect();
			doOutput(connection, body);
			return supplier.get(connection);
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		return null;
	}

	public static String postAsString(String address, List<NameValuePair> heads, String body) throws Exception {
		return postAsString(address, heads, body, DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT);
	}

	public static String postAsString(String address, List<NameValuePair> heads, String body, int connectTimeout,
			int readTimeout) throws Exception {
		checkAddress(address);
		HttpURLConnection connection = null;
		try {
			connection = prepare(address, heads);
			connection.setRequestMethod(ConnectionAction.METHOD_POST);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.connect();
			doOutput(connection, body);
			return readResultString(connection);
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		return null;
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

	public static HttpConnectionResponse put(String address, List<NameValuePair> heads, String body, int connectTimeout,
			int readTimeout, Supplier<HttpConnectionResponse> supplier) {
		HttpURLConnection connection = null;
		try {
			connection = prepare(address, heads);
			connection.setRequestMethod(ConnectionAction.METHOD_PUT);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.connect();
			doOutput(connection, body);
			return supplier.get(connection);
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		return null;
	}

	public static String putAsString(String address, List<NameValuePair> heads, String body) throws Exception {
		return putAsString(address, heads, body, DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT);
	}

	public static String putAsString(String address, List<NameValuePair> heads, String body, int connectTimeout,
			int readTimeout) throws Exception {
		checkAddress(address);
		HttpURLConnection connection = null;
		try {
			connection = prepare(address, heads);
			connection.setRequestMethod(ConnectionAction.METHOD_PUT);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.connect();
			doOutput(connection, body);
			return readResultString(connection);
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		return null;
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

	public static HttpConnectionResponse delete(String address, List<NameValuePair> heads, int connectTimeout,
			int readTimeout, Supplier<HttpConnectionResponse> supplier) {
		HttpURLConnection connection = null;
		try {
			connection = prepare(address, heads);
			connection.setRequestMethod(ConnectionAction.METHOD_DELETE);
			connection.setDoOutput(false);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			return supplier.get(connection);
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		return null;
	}

	public static String deleteAsString(String address, List<NameValuePair> heads) throws Exception {
		return deleteAsString(address, heads, DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT);
	}

	public static String deleteAsString(String address, List<NameValuePair> heads, int connectTimeout, int readTimeout)
			throws Exception {
		checkAddress(address);
		HttpURLConnection connection = null;
		try {
			connection = prepare(address, heads);

			connection.setRequestMethod(ConnectionAction.METHOD_DELETE);
			connection.setDoOutput(false);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			return readResultString(connection);
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
		}
		return null;
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
		map.put(ConnectionAction.ACCESS_CONTROL_ALLOW_CREDENTIALS,
				ConnectionAction.ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE);
		try {
			map.put(ConnectionAction.ACCESS_CONTROL_ALLOW_HEADERS,
					ConnectionAction.ACCESS_CONTROL_ALLOW_HEADERS_VALUE + ", " + Config.person().getTokenName());
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(e.getMessage());
			}
		}
		map.put(ConnectionAction.ACCESS_CONTROL_ALLOW_METHODS, ConnectionAction.ACCESS_CONTROL_ALLOW_METHODS_VALUE);
		map.put(ConnectionAction.CACHE_CONTROL, ConnectionAction.CACHE_CONTROL_VALUE);
		map.put(ConnectionAction.CONTENT_TYPE, ConnectionAction.CONTENT_TYPE_VALUE);
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

	public static String readResultString(HttpURLConnection connection) throws Exception {
		String result = "";
		int code = connection.getResponseCode();
		if (code == HttpURLConnection.HTTP_OK) {
			try (InputStream input = connection.getInputStream()) {
				result = IOUtils.toString(input, StandardCharsets.UTF_8);
			}
		} else {
			try (InputStream input = connection.getErrorStream()) {
				result = IOUtils.toString(input, StandardCharsets.UTF_8);
			}
			throw new IllegalStateException("connection{url:" + connection.getURL() + "}, response error{responseCode:"
					+ code + "}, response:" + result + ".");
		}
		return result;
	}

	private static void doOutput(HttpURLConnection connection, String body) throws Exception {
		try (OutputStream output = connection.getOutputStream()) {
			if (StringUtils.isNotEmpty(body)) {
				IOUtils.write(body, output, StandardCharsets.UTF_8);
				output.flush();
			}
		}
	}

	public static void checkAddress(String address) throws Exception {
		final String addressLower = address.toLowerCase();
		if (addressLower.startsWith(HTTP_PROTOCOL) || addressLower.startsWith(HTTPS_PROTOCOL)) {
			List<String> httpWhiteList = null;
			try {
				httpWhiteList = Config.general().getHttpWhiteList();
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
			}
			if (ListTools.isNotEmpty(httpWhiteList)) {
				Optional<String> optional = httpWhiteList.stream().filter(o -> addressLower.indexOf("://" + o) > -1)
						.findFirst();
				if (!optional.isPresent()) {
					throw new ExceptionUnlawfulAddress(address);
				}
			}
		} else {
			throw new ExceptionNotSupportProtocol(address);
		}
	}

}
