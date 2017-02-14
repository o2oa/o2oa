package com.x.common.core.application;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.HttpToken;
import com.x.common.core.application.component.x_program_center;
import com.x.common.core.application.configuration.application.Applications;
import com.x.common.core.application.configuration.storage.StorageMappings;
import com.x.common.core.application.definition.LoadableDefinition;

public class AbstractThisApplication {
	/* 应用的磁盘路径 */
	public static volatile String webApplicationDirectory;
	/* 上下文根 */
	public static volatile String context;
	/* 应用名称 */
	public static String name;
	/* 应用显示名称 */
	public static String displayName;
	/* 应用类 */
	public static Class<?> clazz;
	/* 随机令牌 */
	public static volatile String token;
	/* 应用配置 */
	public static volatile Config config;
	/* 中心节点 */
	public static volatile Center center;
	/* Applications资源 */
	public static volatile Applications applications;
	/* Storage资源 */
	public static volatile StorageMappings storageMappings;
	/* 是否已经初始化完成 */
	public static volatile boolean initialized;

	private static final String CenterServer_Filename = "centerServer.json";
	private static final String Config_Filename = "config.json";

	public static void init(Class<?> clz, String webApplicationDirectory, String context) throws Exception {
		AbstractThisApplication.name = clz.getSimpleName();
		AbstractThisApplication.displayName = context + " under " + webApplicationDirectory;
		AbstractThisApplication.clazz = clz;
		AbstractThisApplication.webApplicationDirectory = webApplicationDirectory;
		AbstractThisApplication.context = context;
		AbstractThisApplication.token = UUID.randomUUID().toString();
		AbstractThisApplication.center = loadFile(CenterServer_Filename, Center.class);
		if (!clz.equals(x_program_center.class)) {
			AbstractThisApplication.config = loadFile(Config_Filename, Config.class);
		}
	}

	public static byte[] loadCenterResource(String name) throws Exception {
		String url = center.getUrlRoot() + "resource/read/" + name;
		String json = getHttpURLConnectionAsString(url);
		JsonObject jsonObject = XGsonBuilder.instance().fromJson(json, JsonObject.class);
		if (!StringUtils.equalsIgnoreCase(jsonObject.get("type").getAsString(), "success")) {
			throw new Exception(AbstractThisApplication.name + " loadCenter failure, name:" + name + ", url:" + url
					+ ", error:" + jsonObject.get("message").getAsString());
		}
		if (jsonObject.has("data")) {
			JsonElement jsonElement = jsonObject.get("data");
			String data = jsonElement.getAsString();
			return Base64.decodeBase64(data);
		} else {
			throw new Exception(AbstractThisApplication.name + " loadCenter failure, name:" + name + ", url:" + url
					+ ", does not contain data element.");
		}
	}

	public static String loadCenterResourceString(String name) throws Exception {
		byte[] bytes = loadCenterResource(name);
		return new String(bytes, "utf-8");
	}

	public static <T> T loadCenterResouceObject(String name, Class<T> clz) throws Exception {
		String data = loadCenterResourceString(name);
		return XGsonBuilder.instance().fromJson(data, clz);
	}

	public static <T extends LoadableDefinition> T loadCenterResouceObject(Class<T> clz) throws Exception {
		T t = loadCenterResouceObject(
				Objects.toString(FieldUtils.readStaticField(clz, LoadableDefinition.DEFINITION_FIELDNAME_FILE_NAME)),
				clz);
		FieldUtils.writeStaticField(clz, LoadableDefinition.DEFINITION_FIELDNAME_INSTANCE, t);
		return t;
	}

	public static <T> T loadCenterObject(String path, Class<T> clz) throws Exception {
		String url = center.getUrlRoot() + path;
		String json = getHttpURLConnectionAsString(url);
		JsonObject jsonObject = XGsonBuilder.instance().fromJson(json, JsonObject.class);
		if (!StringUtils.equalsIgnoreCase(jsonObject.get("type").getAsString(), "success")) {
			throw new Exception(AbstractThisApplication.name + " loadCenterObject failure, name:" + name + ", url:"
					+ url + ", error:" + jsonObject.get("message").getAsString());
		}
		if (jsonObject.has("data")) {
			JsonElement jsonElement = jsonObject.get("data");
			return XGsonBuilder.instance().fromJson(jsonElement, clz);
		} else {
			throw new Exception(AbstractThisApplication.name + " loadCenterObject failure, name:" + name + ", url:"
					+ url + ", does not contain data element.");
		}
	}

	public static String getHttpURLConnectionAsString(String address) throws Exception {
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setUseCaches(false);
		connection.setDoOutput(false);
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", HttpMediaType.APPLICATION_JSON_UTF_8);
		EffectivePerson effectivePerson = EffectivePerson.cipher(AbstractThisApplication.center.getCipher());
		connection.setRequestProperty(HttpToken.X_Token, effectivePerson.getToken());
		connection.connect();
		String result = "";
		try (InputStream input = connection.getInputStream()) {
			result = IOUtils.toString(input, StandardCharsets.UTF_8);
		}
		return result;
	}

	public static <T> T loadFile(String path, Class<T> clz) throws Exception {
		File file = new File(webApplicationDirectory + "/WEB-INF/classes/META-INF/" + path);
		return XGsonBuilder.instance().fromJson(FileUtils.readFileToString(file, "utf-8"), clz);
	}
}