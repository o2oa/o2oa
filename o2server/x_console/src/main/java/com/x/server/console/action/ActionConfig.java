package com.x.server.console.action;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.tools.JarTools;

public class ActionConfig extends ActionBase {

	public boolean execute() throws Exception {
		if (!Config.currentNode().getIsPrimaryCenter()) {
			byte[] bytes = this.getZip();
			this.unzip(bytes);
			System.out.println("synchronize config success, should to restart server.");
			return true;
		} else {
			System.out.println("config command only synchronize config from primary center.");
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	private byte[] getZip() throws Exception {
		String center = Config.nodes().primaryCenterNode();
		String address = "http://" + center + ":" + Config.centerServer().getPort() + "/x_program_center/jaxrs/config";
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", HttpMediaType.APPLICATION_JSON_UTF_8);
		EffectivePerson effectivePerson = EffectivePerson.cipher(Config.token().getCipher());
		connection.setRequestProperty(HttpToken.X_Token, effectivePerson.getToken());
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		String json = null;
		try (InputStream input = connection.getInputStream()) {
			json = IOUtils.toString(input);
		}
		JsonElement jsonElement = XGsonBuilder.instance().fromJson(json, JsonElement.class);
		if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (jsonObject.has("type")) {
				if (StringUtils.equals("success", jsonObject.get("type").getAsString())) {
					String value = jsonObject.get("data").getAsString();
					byte[] bytes = Base64.decodeBase64(value);
					return bytes;
				} else {
					throw new Exception("return type not success.");
				}
			} else {
				throw new Exception("can not read return type.");
			}
		} else {
			throw new Exception("return object is not jsonObject.");
		}
	}

	private void unzip(byte[] bytes) throws Exception {
		File dir = new File(Config.base(), "config");
		FileUtils.forceMkdir(dir);
		JarTools.unjar(bytes, "", dir, true);
	}

}