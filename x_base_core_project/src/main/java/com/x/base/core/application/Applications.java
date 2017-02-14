package com.x.base.core.application;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.HttpToken;
import com.x.base.core.project.server.Config;

public class Applications extends ConcurrentHashMap<String, CopyOnWriteArrayList<Application>> {

	private static Logger logger = LoggerFactory.getLogger(Applications.class);

	private static final long serialVersionUID = -2416559829493154858L;

	private volatile String token = UUID.randomUUID().toString();

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Application get(Class<?> clz, String token) throws Exception {
		List<Application> list = this.get(clz.getCanonicalName());
		if (null != list) {
			for (Application application : list) {
				if (StringUtils.equals(token, application.getToken())) {
					return application;
				}
			}
		}
		return null;
	}

	public List<Application> get(Class<?> clz) throws Exception {
		return this.get(clz.getCanonicalName());
	}

	public void add(Class<?> applicationClass, Application application) throws Exception {
		CopyOnWriteArrayList<Application> list = this.get(applicationClass.getCanonicalName());
		if (null == list) {
			list = new CopyOnWriteArrayList<Application>();
			this.put(applicationClass.getCanonicalName(), list);
		}
		list.add(application);
	}

	public JsonElement getQuery(Class<?> applicationClass, String uri) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.getQuery(application, uri);
	}

	public <T> T getQuery(Class<?> applicationClass, String uri, Class<T> clz) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.getQuery(application, uri, clz);
	}

	public <T> T getQuery(Class<?> applicationClass, String uri, Type type) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.getQuery(application, uri, type);
	}

	public <T> T getQuery(Application application, String uri, Class<T> clz) throws Exception {
		JsonElement jsonElement = this.getQuery(application, uri);
		return XGsonBuilder.instance().fromJson(jsonElement, clz);
	}

	public <T> T getQuery(Application application, String uri, Type type) throws Exception {
		JsonElement jsonElement = this.getQuery(application, uri);
		return XGsonBuilder.instance().fromJson(jsonElement, type);
	}

	public JsonElement getQuery(Application application, String uri) throws Exception {
		try {
			logger.debug("getQuery url:" + uri);
			String json = this.httpGet(application, uri);
			logger.debug("return json:" + json);
			return this.readResultJsonElement(json);
		} catch (Exception e) {
			throw new Exception("getQuery error, application{host:" + application.getHost() + ", port:"
					+ application.getPort() + ",contextPath:" + application.getContext() + ", uri:" + uri + "}.", e);
		}
	}

	public JsonElement deleteQuery(Class<?> applicationClass, String uri) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.deleteQuery(application, uri);
	}

	public <T> T deleteQuery(Class<?> applicationClass, String uri, Class<T> clz) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.deleteQuery(application, uri, clz);
	}

	public <T> T deleteQuery(Class<?> applicationClass, String uri, Type type) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.deleteQuery(application, uri, type);
	}

	public <T> T deleteQuery(Application application, String uri, Class<T> clz) throws Exception {
		JsonElement jsonElement = this.deleteQuery(application, uri);
		return XGsonBuilder.instance().fromJson(jsonElement, clz);
	}

	public <T> T deleteQuery(Application application, String uri, Type type) throws Exception {
		JsonElement jsonElement = this.deleteQuery(application, uri);
		return XGsonBuilder.instance().fromJson(jsonElement, type);
	}

	public JsonElement deleteQuery(Application application, String uri) throws Exception {
		try {
			String json = this.httpDelete(application, uri);
			return this.readResultJsonElement(json);
		} catch (Exception e) {
			throw new Exception("deleteQuery error, application{host:" + application.getHost() + ", port:"
					+ application.getPort() + ",contextPath:" + application.getContext() + ", uri:" + uri + "}.", e);
		}
	}

	public JsonElement postQuery(Class<?> applicationClass, String uri, Object o) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.postQuery(application, uri, o);
	}

	public <T> T postQuery(Class<?> applicatonClass, String uri, Object o, Class<T> clz) throws Exception {
		Application application = this.randomWithWeight(applicatonClass);
		return this.postQuery(application, uri, o, clz);
	}

	public <T> T postQuery(Class<?> applicationClass, String uri, Object o, Type type) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.postQuery(application, uri, o, type);
	}

	public <T> T postQuery(Application application, String uri, Object o, Class<T> clz) throws Exception {
		JsonElement jsonElement = this.postQuery(application, uri, o);
		return XGsonBuilder.instance().fromJson(jsonElement, clz);
	}

	public <T> T postQuery(Application application, String uri, Object o, Type type) throws Exception {
		JsonElement jsonElement = this.postQuery(application, uri, o);
		return XGsonBuilder.instance().fromJson(jsonElement, type);
	}

	public JsonElement postQuery(Application application, String uri, Object o) throws Exception {
		try {
			String json = this.httpPost(application, uri, o);
			return this.readResultJsonElement(json);
		} catch (Exception e) {
			throw new Exception("postQuery error, application{host:" + application.getHost() + ", port:"
					+ application.getPort() + ",contextPath:" + application.getContext() + ", uri:" + uri + "}.", e);
		}
	}

	public JsonElement putQuery(Class<?> applicationClass, String uri, Object o) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.putQuery(application, uri, o);
	}

	public <T> T putQuery(Class<?> applicationClass, String uri, Object o, Class<T> clz) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.putQuery(application, uri, o, clz);
	}

	public <T> T putQuery(Class<?> applicationClass, String uri, Object o, Type type) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return this.putQuery(application, uri, o, type);
	}

	public <T> T putQuery(Application application, String uri, Object o, Class<T> clz) throws Exception {
		JsonElement jsonElement = this.putQuery(application, uri, o);
		return XGsonBuilder.instance().fromJson(jsonElement, clz);
	}

	public <T> T putQuery(Application application, String uri, Object o, Type type) throws Exception {
		JsonElement jsonElement = this.putQuery(application, uri, o);
		return XGsonBuilder.instance().fromJson(jsonElement, type);
	}

	public JsonElement putQuery(Application application, String uri, Object o) throws Exception {
		try {
			String json = this.httpPut(application, uri, o);
			return this.readResultJsonElement(json);
		} catch (Exception e) {
			throw new Exception("putQuery error, application{host:" + application.getHost() + ",port:"
					+ application.getPort() + ",contextPath:" + application.getContext() + ", uri:" + uri + "}.", e);
		}
	}

	public Application randomWithWeight(Class<?> clz) throws Exception {
		List<Application> availabeApplications = new ArrayList<>();
		List<Application> list = this.get(clz.getCanonicalName());
		if (null != list) {
			for (Application app : list) {
				availabeApplications.add(app);
			}
		}
		if (availabeApplications.isEmpty()) {
			return null;
		}
		int total = 0;
		for (Application application : availabeApplications) {
			total += application.getWeight();
		}
		Random random = new Random();
		int rdm = random.nextInt(total);
		int current = 0;
		for (Application application : availabeApplications) {
			current += application.getWeight();
			if (rdm <= current) {
				return application;
			}
		}
		throw new Exception("randomWithWeight error.");
	}

	private String httpGet(Application application, String uri) throws Exception {
		HttpURLConnection connection = this.prepareConnection(application, uri);
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
	}

	private String httpDelete(Application application, String uri) throws Exception {
		HttpURLConnection connection = this.prepareConnection(application, uri);
		connection.setRequestMethod("DELETE");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		return this.readResultString(connection);
	}

	private String httpPost(Application application, String uri, Object data) throws Exception {
		HttpURLConnection connection = this.prepareConnection(application, uri);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.connect();
		this.doOutput(connection, data);
		return this.readResultString(connection);
	}

	private String httpPut(Application application, String uri, Object data) throws Exception {
		HttpURLConnection connection = this.prepareConnection(application, uri);
		connection.setRequestMethod("PUT");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.connect();
		this.doOutput(connection, data);
		return this.readResultString(connection);
	}

	private HttpURLConnection prepareConnection(Application application, String uri) throws Exception {
		if (null == application) {
			throw new Exception("application can not be null.");
		}
		URL url = new URL(application.getUrlRoot() + uri);
		HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.setRequestProperty("Content-Type", HttpMediaType.APPLICATION_JSON_UTF_8);
		EffectivePerson effectivePerson = EffectivePerson.cipher(Config.token().getCipher());
		httpUrlConnection.setRequestProperty(HttpToken.X_Token, effectivePerson.getToken());
		return httpUrlConnection;
	}

	private void doOutput(HttpURLConnection connection, Object data) throws Exception {
		try (OutputStream output = connection.getOutputStream()) {
			if (null != data) {
				IOUtils.write(XGsonBuilder.toJson(data), output, StandardCharsets.UTF_8);
				output.flush();
			}
		}
	}

	private JsonElement readResultJsonElement(String json) throws Exception {
		if (StringUtils.isEmpty(json)) {
			throw new Exception("return is empty.");
		}
		Gson gson = XGsonBuilder.instance();
		JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
		if (!jsonObject.has("type")) {
			throw new Exception("can not get type memeber of " + json);
		}
		if (!StringUtils.equalsIgnoreCase(jsonObject.get("type").getAsString(), "success")) {
			throw new Exception("applications connect failure, remote error:" + jsonObject.get("message"));
		}
		JsonElement jsonElement = null;
		if (jsonObject.has("data")) {
			jsonElement = gson.fromJson(jsonObject.get("data"), JsonElement.class);
		}
		return jsonElement;
	}

	private String readResultString(HttpURLConnection connection) throws Exception {
		String result = "";
		try (InputStream input = connection.getInputStream()) {
			result = IOUtils.toString(input, StandardCharsets.UTF_8);
		}
		return result;
	}
}