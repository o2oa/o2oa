package com.x.server.console.action;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.HttpMediaType;

public class ActionBase {

	protected String getUpdateAddress(String base) throws Exception {
		String url = "http://update.o2server.io:20080/o2_update_assemble";
		return url;
	}

	protected String getVersion(String base) throws Exception {
		File file = new File(base, "version.o2");
		if (!file.exists()) {
			throw new Exception("can not get version.o2.");
		}
		String str = FileUtils.readFileToString(file);
		return str;
	}

	protected WrapOutCheck check(String base) throws Exception {
		String address = this.getUpdateAddress(base);
		address += "/jaxrs/update/check/" + this.getVersion(base);
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", HttpMediaType.APPLICATION_JSON_UTF_8);
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		String json = "";
		try (InputStream input = connection.getInputStream()) {
			json = IOUtils.toString(input, StandardCharsets.UTF_8);
		}
		Gson gson = XGsonBuilder.instance();
		JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
		if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (jsonObject.has("data")) {
				return gson.fromJson(jsonObject.get("data"), WrapOutCheck.class);
			}
		}
		return null;
	}

}
