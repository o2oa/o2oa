package com.x.server.console.action;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionVersion extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionVersion.class);

	public void execute() throws Exception {
		List<WrapVersion> wos = this.list();
		if (ListTools.isEmpty(wos)) {
			System.out.println("already the latest version!");
		} else {
			for (WrapVersion _o : wos) {
				System.out.println("version:" + _o.getVersion() + ", size:" + (_o.getSize() / 1024 / 1024) + "M");
				for (String _s : _o.getDescriptionList()) {
					System.out.println(_s);
				}
			}
		}
	}

	List<WrapVersion> list() throws Exception {
		List<WrapVersion> wos = new ArrayList<>();
		String address = Config.collect().url("/o2_collect_assemble/jaxrs/update/list/next/" + Config.version());
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
		connection.disconnect();
		Gson gson = XGsonBuilder.instance();
		JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
		if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (jsonObject.has("data")) {
				wos = gson.fromJson(jsonObject.get("data"), new TypeToken<List<WrapVersion>>() {
				}.getType());

			}
		}
		wos = wos.stream().sorted(Comparator.comparing(WrapVersion::getVersion)).collect(Collectors.toList());
		return wos;
	}

	public static class WrapVersion {

		private List<String> descriptionList = new ArrayList<>();

		private String version;

		private Long size;

		public List<String> getDescriptionList() {
			return descriptionList;
		}

		public void setDescriptionList(List<String> descriptionList) {
			this.descriptionList = descriptionList;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public Long getSize() {
			return size;
		}

		public void setSize(Long size) {
			this.size = size;
		}

	}
}
