package com.x.server.console.action;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.JarTools;

public class ActionUpdate extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionUpdate.class);

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	private static final String LATEST = "latest";

	public boolean execute(String password, boolean backup, boolean latest) {
		try {
			this.init();
			if (!StringUtils.equals(Config.token().getPassword(), password)) {
				logger.print("password not mactch.");
				return false;
			}
			WrapUpdateVersion wrapUpdateVersion = this.get(latest);
			if (StringUtils.equals(LATEST, wrapUpdateVersion.getVersion())) {
				logger.print("already the latest version.");
				return false;
			} else {
				if (backup) {
					this.backup();
				}
				File file = this.getPack(wrapUpdateVersion.getUrl());
				this.unzip(file);
				logger.print("update completed in {} seconds, restart server to continue update.",
						((new Date()).getTime() - start.getTime()) / 1000);
				FileUtils.forceDelete(file);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void backup() throws Exception {
		File dir = Config.dir_local_backup(true);
		String tag = DateTools.compact(new Date());
		File dest = new File(dir, tag + ".zip");
		logger.print("backup current version to {}.", dest.getAbsolutePath());
		List<File> files = new ArrayList<>();
		files.add(Config.dir_commons());
		files.add(Config.dir_config());
		files.add(Config.dir_configSample());
		files.add(Config.dir_localSample());
		files.add(Config.dir_jvm());
		files.add(Config.dir_servers());
		files.add(Config.dir_store());
		files.add(Config.dir_dynamic());
		files.add(Config.dir_custom());
		files.add(new File(Config.base(), "console.jar"));
		files.add(new File(Config.base(), "index.html"));
		files.add(new File(Config.base(), "version.o2"));
		FileFilter fileFilter = new RegexFileFilter("^(start_|stop_|console_)(aix|windows|linux|macos).(sh|bat)$");
		for (File _f : new File(Config.base()).listFiles(fileFilter)) {
			files.add(_f);
		}
		JarTools.jar(files, dest);
		logger.print("backup current version completed.");
	}

	private WrapUpdateVersion get(boolean toLatest) throws Exception {
		String address = "";
		if (toLatest) {
			address = Config.collect().url("/o2_collect_assemble/jaxrs/update/latest/version");
		} else {
			address = Config.collect().url("/o2_collect_assemble/jaxrs/update/next/" + Config.version());
		}
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
				return XGsonBuilder.instance().fromJson(jsonObject.get("data"), WrapUpdateVersion.class);
			}
		}
		return null;
	}

	public static class WrapUpdateVersion {

		private String version;

		private Long size;

		private String url;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Long getSize() {
			return size;
		}

		public void setSize(Long size) {
			this.size = size;
		}
	}

	private File getPack(String address) throws Exception {
		logger.print("download update pack form url: {}.", address);
		URL url = new URL(address);
		File file = new File(Config.base(), "local/update.zip");
		if (file.exists() && file.isFile()) {
			FileUtils.forceDelete(file);
		}
		FileUtils.copyURLToFile(url, file);
		logger.print("download update pack completed.");
		return file;
	}

	private void unzip(File file) throws Exception {
		File dir = Config.dir_local_update(true);
		FileUtils.cleanDirectory(dir);
		JarTools.unjar(file, "", dir, true);
		File dir_local = new File(dir, "local");
		if (dir_local.exists()) {
			FileUtils.forceDelete(dir_local);
		}
		File dir_config = new File(dir, "config");
		if (dir_config.exists()) {
			FileUtils.forceDelete(dir_config);
		}
	}

}