package com.x.base.core.project.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult.Type;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;

public class ConnectionAction {

	private static final int DEFAULT_CONNECTTIMEOUT = 2000;
	private static final int DEFAULT_READTIMEOUT = 5 * 60 * 1000;

	private ConnectionAction() {
	}

	public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE = "true";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS_VALUE = "x-requested-with, x-request, Content-Type, x-cipher, x-client";
	public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public static final String ACCESS_CONTROL_ALLOW_METHODS_VALUE = "GET, POST, OPTIONS, PUT, DELETE, HEAD, TRACE";

	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String CACHE_CONTROL_VALUE = "no-cache, no-transform";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";
	public static final String CONTENT_LENGTH = "Content-Length";

	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_DELETE = "DELETE";

	public static final String HEAD_LOCATION = "location";

	private static Gson gson = XGsonBuilder.instance();

	private static ActionResponse getDelete(int connectTimeout, int readTimeout, String address, String method,
			List<NameValuePair> heads) throws Exception {
		HttpConnection.checkAddress(address);
		ActionResponse response = new ActionResponse();
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			addHeads(connection, heads);
			connection.setRequestMethod(method);
			connection.setUseCaches(false);
			connection.setDoOutput(false);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.connect();
			int status = connection.getResponseCode();
			if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM) {
				String redirect = connection.getHeaderField("Location");
				if (StringUtils.isNotBlank(redirect)) {
					return getDelete(connectTimeout, readTimeout, redirect, method, heads);
				}
			}
			return read(response, connection);
		} catch (Exception e) {
			response.setType(Type.connectFatal);
			response.setMessage(String.format("%s connect connection error, address: %s, because: %s.", method, address,
					e.getMessage()));
			return response;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static ActionResponse get(int connectTimeout, int readTimeout, String address, List<NameValuePair> heads)
			throws Exception {
		return getDelete(connectTimeout, readTimeout, address, METHOD_GET, heads);
	}

	public static ActionResponse get(String address, List<NameValuePair> heads) throws Exception {
		return getDelete(DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT, address, METHOD_GET, heads);
	}

	public static ActionResponse delete(int connectTimeout, int readTimeout, String address, List<NameValuePair> heads)
			throws Exception {
		return getDelete(connectTimeout, readTimeout, address, METHOD_DELETE, heads);
	}

	public static ActionResponse delete(String address, List<NameValuePair> heads) throws Exception {
		return getDelete(DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT, address, METHOD_DELETE, heads);
	}

	private static byte[] getDeleteBinary(int connectTimeout, int readTimeout, String address, String method,
			List<NameValuePair> heads) throws Exception {
		HttpConnection.checkAddress(address);
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			addHeadsNoContentType(connection, heads);
			connection.setRequestMethod(method);
			connection.setUseCaches(false);
			connection.setDoOutput(false);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.connect();
			int status = connection.getResponseCode();
			if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM) {
				String redirect = connection.getHeaderField("Location");
				if (StringUtils.isNotBlank(redirect)) {
					return getDeleteBinary(connectTimeout, readTimeout, redirect, method, heads);
				}
			}
			return readBinary(connection);
		} catch (Exception e) {
			throw new ExceptionGetBinary(e, connection);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static byte[] getBinary(int connectTimeout, int readTimeout, String address, List<NameValuePair> heads)
			throws Exception {
		return getDeleteBinary(connectTimeout, readTimeout, address, METHOD_GET, heads);
	}

	public static byte[] getBinary(String address, List<NameValuePair> heads) throws Exception {
		return getDeleteBinary(DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT, address, METHOD_GET, heads);
	}

	public static byte[] deleteBinary(int connectTimeout, int readTimeout, String address, List<NameValuePair> heads)
			throws Exception {
		return getDeleteBinary(connectTimeout, readTimeout, address, METHOD_DELETE, heads);
	}

	public static byte[] deleteBinary(String address, List<NameValuePair> heads) throws Exception {
		return getDeleteBinary(DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT, address, METHOD_DELETE, heads);
	}

	private static ActionResponse postPut(int connectTimeout, int readTimeout, String address, String method,
			List<NameValuePair> heads, Object body) throws Exception {
		HttpConnection.checkAddress(address);
		ActionResponse response = new ActionResponse();
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			addHeads(connection, heads);
			connection.setRequestMethod(method);
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.connect();
			try (OutputStream output = connection.getOutputStream()) {
				if (null != body) {
					if (body instanceof CharSequence) {
						IOUtils.write(Objects.toString(body), output, StandardCharsets.UTF_8);
					} else {
						IOUtils.write(gson.toJson(body), output, StandardCharsets.UTF_8);
					}
				}
			}
			int status = connection.getResponseCode();
			if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM) {
				String redirect = connection.getHeaderField(HEAD_LOCATION);
				if (StringUtils.isNotBlank(redirect)) {
					return postPut(connectTimeout, readTimeout, redirect, method, heads, body);
				}
			}
			return read(response, connection);
		} catch (Exception e) {
			response.setType(Type.connectFatal);
			response.setMessage(String.format("%s connect connection error, address: %s, because: %s.", method, address,
					e.getMessage()));
			return response;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static ActionResponse post(int connectTimeout, int readTimeout, String address, List<NameValuePair> heads,
			Object body) throws Exception {
		return postPut(connectTimeout, readTimeout, address, METHOD_POST, heads, body);
	}

	public static ActionResponse post(String address, List<NameValuePair> heads, Object body) throws Exception {
		return postPut(DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT, address, METHOD_POST, heads, body);
	}

	public static ActionResponse put(int connectTimeout, int readTimeout, String address, List<NameValuePair> heads,
			Object body) throws Exception {
		return postPut(connectTimeout, readTimeout, address, METHOD_PUT, heads, body);
	}

	public static ActionResponse put(String address, List<NameValuePair> heads, Object body) throws Exception {
		return postPut(DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT, address, METHOD_PUT, heads, body);
	}

	private static byte[] postPutBinary(int connectTimeout, int readTimeout, String address, String method,
			List<NameValuePair> heads, Object body) throws Exception {
		HttpConnection.checkAddress(address);
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			addHeads(connection, heads);
			connection.setRequestMethod(method);
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.connect();
			try (OutputStream output = connection.getOutputStream()) {
				if (null != body) {
					if (body instanceof CharSequence) {
						IOUtils.write(Objects.toString(body), output, StandardCharsets.UTF_8);
					} else {
						IOUtils.write(gson.toJson(body), output, StandardCharsets.UTF_8);
					}
				}
			}
			return readBinary(connection);
		} catch (Exception e) {
			throw new ExceptionBinary(e, connection);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static byte[] postBinary(int connectTimeout, int readTimeout, String address, List<NameValuePair> heads,
			Object body) throws Exception {
		return postPutBinary(connectTimeout, readTimeout, address, METHOD_POST, heads, body);
	}

	public static byte[] postBinary(String address, List<NameValuePair> heads, Object body) throws Exception {
		return postPutBinary(DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT, address, METHOD_POST, heads, body);
	}

	public static byte[] putBinary(int connectTimeout, int readTimeout, String address, List<NameValuePair> heads,
			Object body) throws Exception {
		return postPutBinary(connectTimeout, readTimeout, address, METHOD_PUT, heads, body);
	}

	public static byte[] putBinary(String address, List<NameValuePair> heads, Object body) throws Exception {
		return postPutBinary(DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT, address, METHOD_PUT, heads, body);
	}

	private static byte[] postPutMultiPartBinary(int connectTimeout, int readTimeout, String address, String method,
			List<NameValuePair> heads, Collection<FormField> formFields, Collection<FilePart> fileParts)
			throws Exception {
		HttpURLConnection connection = null;
		String boundary = StringTools.TWO_HYPHENS + StringTools.TWO_HYPHENS + System.currentTimeMillis();
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			byte[] bytes = null;
			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
				if (null != fileParts) {
					for (FilePart filePart : fileParts) {
						writeFilePart(byteArrayOutputStream, filePart, boundary);
					}
				}
				if (null != formFields) {
					for (FormField formField : formFields) {
						writeFormField(byteArrayOutputStream, formField, boundary);
					}
				}
				IOUtils.write(StringTools.TWO_HYPHENS + boundary + StringTools.TWO_HYPHENS, byteArrayOutputStream,
						DefaultCharset.charset_utf_8);
				bytes = byteArrayOutputStream.toByteArray();
			}
			addHeadsMultiPart(connection, heads, boundary);
			connection.setRequestProperty(CONTENT_LENGTH, bytes.length + "");
			connection.setRequestMethod(method);
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.connect();
			try (OutputStream output = connection.getOutputStream()) {
				IOUtils.write(bytes, output);
			}
			return readBinary(connection);
		} catch (Exception e) {
			throw new ExceptionMultiPartBinary(e, connection);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static byte[] postMultiPartBinary(int connectTimeout, int readTimeout, String address,
			List<NameValuePair> heads, Collection<FormField> formFields, Collection<FilePart> fileParts)
			throws Exception {
		return postPutMultiPartBinary(connectTimeout, readTimeout, address, METHOD_POST, heads, formFields, fileParts);
	}

	public static byte[] postMultiPartBinary(String address, List<NameValuePair> heads,
			Collection<FormField> formFields, Collection<FilePart> fileParts) throws Exception {
		return postPutMultiPartBinary(DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT, address, METHOD_POST, heads,
				formFields, fileParts);
	}

	public static byte[] putMultiPartBinary(int connectTimeout, int readTimeout, String address,
			List<NameValuePair> heads, Collection<FormField> formFields, Collection<FilePart> fileParts)
			throws Exception {
		return postPutMultiPartBinary(connectTimeout, readTimeout, address, METHOD_PUT, heads, formFields, fileParts);
	}

	public static byte[] putMultiPartBinary(String address, List<NameValuePair> heads, Collection<FormField> formFields,
			Collection<FilePart> fileParts) throws Exception {
		return postPutMultiPartBinary(DEFAULT_CONNECTTIMEOUT, DEFAULT_READTIMEOUT, address, METHOD_PUT, heads,
				formFields, fileParts);
	}

	private static void writeFormField(OutputStream output, FormField formField, String boundary) throws IOException {
		IOUtils.write(StringTools.TWO_HYPHENS + boundary, output, StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write("Content-Disposition: form-data; name=\"" + formField.getName() + "\"", output,
				StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write("Content-Length: " + formField.getValue().getBytes(StandardCharsets.UTF_8).length, output,
				StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write("Content-Type: text/plain; charset=" + StandardCharsets.UTF_8.name(), output,
				StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write(formField.getValue().getBytes(StandardCharsets.UTF_8), output);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
	}

	public static void writeFilePart(OutputStream output, FilePart filePart, String boundary) throws IOException {
		IOUtils.write(StringTools.TWO_HYPHENS + boundary, output, StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write(
				"Content-Disposition: form-data; name=\"" + filePart.getName() + "\"; filename=\""
						+ URLEncoder.encode(filePart.getFileName(), StandardCharsets.UTF_8) + "\"",
				output, StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write("Content-Length: " + filePart.getBytes().length, output, StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write("Content-Type: " + filePart.getContentType(), output, StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write(String.format("Content-Length: %d", filePart.getBytes().length), output, StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
		IOUtils.write(filePart.getBytes(), output);
		IOUtils.write(StringTools.CRLF, output, StandardCharsets.UTF_8);
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
			// nothing
		}
		return str;
	}

	private static ActionResponse read(ActionResponse response, HttpURLConnection connection) throws IOException {
		int code = connection.getResponseCode();
		if (code >= 500) {
			try (InputStream input = connection.getErrorStream()) {
				byte[] buffer = IOUtils.toByteArray(input);
				response.setMessage(extractErrorMessageIfExist(new String(buffer, DefaultCharset.name)));
				response.setType(Type.error);
			}
		} else if (code >= 400) {
			response.setMessage(String.format("url invalid error, address: %s, method: %s, code: %d.",
					Objects.toString(connection.getURL()), connection.getRequestMethod(), code));
			response.setType(Type.error);
		} else if (code == 200) {
			try (InputStream input = connection.getInputStream()) {
				byte[] buffer = IOUtils.toByteArray(input);
				String value = new String(buffer, DefaultCharset.name);
				response = gson.fromJson(value, ActionResponse.class);
			} catch (Exception e) {
				response.setType(Type.connectFatal);
				response.setMessage(String.format(
						"convert input to json error, address: %s, method: %s, code: %d, because: %s.",
						Objects.toString(connection.getURL()), connection.getRequestMethod(), code, e.getMessage()));
			}
		}
		return response;
	}

	private static byte[] readBinary(HttpURLConnection connection) throws ExceptionReadBinary, IOException {
		int code = connection.getResponseCode();
		byte[] bytes = null;
		if (code >= 500) {
			try (InputStream input = connection.getErrorStream()) {
				byte[] buffer = IOUtils.toByteArray(input);
				throw new ExceptionReadBinary(connection.getURL(), connection.getRequestMethod(), code, buffer);
			}
		} else if (code >= 400) {
			throw new ExceptionReadBinary(connection.getURL(), connection.getRequestMethod(), code);
		} else if (code == 200) {
			try (InputStream input = connection.getInputStream()) {
				bytes = IOUtils.toByteArray(input);
			} catch (Exception e) {
				throw new ExceptionReadBinary(e, connection, code);
			}
		}
		return bytes;
	}

	private static void addHeads(HttpURLConnection connection, List<NameValuePair> heads) throws Exception {
		Map<String, String> map = new TreeMap<>();
		map.put(ACCESS_CONTROL_ALLOW_CREDENTIALS, ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE);
		map.put(ACCESS_CONTROL_ALLOW_HEADERS,
				ACCESS_CONTROL_ALLOW_HEADERS_VALUE + ", " + Config.person().getTokenName());
		map.put(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_VALUE);
		map.put(CACHE_CONTROL, CACHE_CONTROL_VALUE);
		map.put(CONTENT_TYPE, CONTENT_TYPE_VALUE);
		if (ListTools.isNotEmpty(heads)) {
			String value;
			for (NameValuePair o : heads) {
				value = Objects.toString(o.getValue(), "");
				if (StringUtils.isNotEmpty(o.getName()) && StringUtils.isNotEmpty(value)) {
					map.put(o.getName(), value);
				}
			}
		}
		map.entrySet().forEach((o -> connection.addRequestProperty(o.getKey(), o.getValue())));
	}

	private static void addHeadsNoContentType(HttpURLConnection connection, List<NameValuePair> heads)
			throws Exception {
		Map<String, String> map = new TreeMap<>();
		map.put(ACCESS_CONTROL_ALLOW_CREDENTIALS, ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE);
		map.put(ACCESS_CONTROL_ALLOW_HEADERS,
				ACCESS_CONTROL_ALLOW_HEADERS_VALUE + ", " + Config.person().getTokenName());
		map.put(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_VALUE);
		map.put(CACHE_CONTROL, CACHE_CONTROL_VALUE);
		if (ListTools.isNotEmpty(heads)) {
			String value;
			for (NameValuePair o : heads) {
				value = Objects.toString(o.getValue(), "");
				if (StringUtils.isNotEmpty(o.getName()) && StringUtils.isNotEmpty(value)) {
					map.put(o.getName(), value);
				}
			}
		}
		map.entrySet().forEach((o -> connection.addRequestProperty(o.getKey(), o.getValue())));
	}

	private static void addHeadsMultiPart(HttpURLConnection connection, List<NameValuePair> heads, String boundary)
			throws Exception {
		Map<String, String> map = new TreeMap<>();
		map.put(ACCESS_CONTROL_ALLOW_CREDENTIALS, ACCESS_CONTROL_ALLOW_CREDENTIALS_VALUE);
		map.put(ACCESS_CONTROL_ALLOW_HEADERS,
				ACCESS_CONTROL_ALLOW_HEADERS_VALUE + ", " + Config.person().getTokenName());
		map.put(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_VALUE);
		map.put(CACHE_CONTROL, CACHE_CONTROL_VALUE);
		connection.setRequestProperty(CONTENT_TYPE, String.format("multipart/form-data; boundary=%s", boundary));
		if (ListTools.isNotEmpty(heads)) {
			String value;
			for (NameValuePair o : heads) {
				value = Objects.toString(o.getValue(), "");
				if (StringUtils.isNotEmpty(o.getName()) && StringUtils.isNotEmpty(value)) {
					map.put(o.getName(), value);
				}
			}
		}
		map.entrySet().forEach((o -> connection.addRequestProperty(o.getKey(), o.getValue())));
	}

}
