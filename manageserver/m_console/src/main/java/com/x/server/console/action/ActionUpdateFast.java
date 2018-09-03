package com.x.server.console.action;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

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
import com.x.server.console.NodeAgent;

public class ActionUpdateFast extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionUpdateFast.class);

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	private static final String LATEST = "latest";

	public boolean execute(String password) throws Exception {
		this.init();
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			System.out.println("password not match.");
			return false;
		}
		WrapUpdateVersion wrapUpdateVersion = this.get();
		if (StringUtils.equals(LATEST, wrapUpdateVersion.getVersion())) {
			System.out.println("already the latest version!");
			return false;
		} else {
			this.backup();
			byte[] bytes = this.getPack(wrapUpdateVersion.getUrl());
			this.unzip(bytes);
			System.out.println("update completed in " + (((new Date()).getTime() - start.getTime()) / 1000)
					+ " seconds, restart server to continue update!");
			return true;
		}
	}

	private void backup() throws Exception {
		File dir = new File(Config.base(), "local/backup");
		FileUtils.forceMkdir(dir);
		String tag = DateTools.compact(new Date());
		File dest = new File(dir, tag + ".zip");
		logger.print("backup current version to {}.", dest.getAbsolutePath());
		List<File> files = new ArrayList<>();
		files.add(new File(Config.base(), "commons"));
		files.add(new File(Config.base(), "config"));
		files.add(new File(Config.base(), "jvm"));
		files.add(new File(Config.base(), "servers"));
		files.add(new File(Config.base(), "store"));
		files.add(new File(Config.base(), "console.jar"));
		files.add(new File(Config.base(), "index.html"));
		files.add(new File(Config.base(), "version.o2"));
		FileFilter fileFilter = new RegexFileFilter("^(start_|stop_)(aix|windows|linux|macos).(sh|bat)$");
		for (File _f : new File(Config.base()).listFiles(fileFilter)) {
			files.add(_f);
		}
		JarTools.jar(files, dest);
		logger.print("backup current version completed.");
	}

	private WrapUpdateVersion get() throws Exception {
		String address = Config.collect().url("/o2_collect_assemble/jaxrs/update/next/" + Config.version());
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

	private byte[] getPack(String address) throws Exception {
		logger.print("download upate pack form url: {}.", address);
		URL url = new URL(address);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", HttpMediaType.APPLICATION_JSON_UTF_8);
		connection.setRequestMethod("GET");
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		byte[] bytes;
		try (InputStream input = connection.getInputStream()) {
			bytes = IOUtils.toByteArray(input);
		}
		System.out.println("download upate pack completed.");
		return bytes;
	}

	private void unzip(byte[] bytes) throws Exception {
		File dir = new File(Config.base(), "local/update");
		FileUtils.forceMkdir(dir);
		FileUtils.cleanDirectory(dir);
		JarTools.unjar(bytes, "", dir, true);
	}

	private void updateFiles(String base, String updateVersion) throws Exception {
		File file = new File(base, "local/updates/" + updateVersion + "/commons");
		if (file.exists() && file.isDirectory()) {
			FileUtils.copyDirectory(file, new File(base, "commons"));
		}
		file = new File(base, "local/updates/" + updateVersion + "/servers");
		if (file.exists() && file.isDirectory()) {
			FileUtils.copyDirectory(file, new File(base, "servers"));
		}
		file = new File(base, "local/updates/" + updateVersion + "/store");
		if (file.exists() && file.isDirectory()) {
			FileUtils.copyDirectory(file, new File(base, "store"));
		}
		file = new File(base, "local/updates/" + updateVersion + "/console.jar");
		if (file.exists() && file.isFile()) {
			FileUtils.copyFile(file, new File(base, "console.jar"));
		}
	}

	private void executeBeforeScript(String base, String updateVersion) throws Exception {
		File file = new File(base, "local/updates/" + updateVersion + "/script/update" + updateVersion + "before.jar");
		if (file.exists() && file.isFile()) {
			System.out.println("executing before update script.");
			File tempFile = new File(base, "local/temp/" + file.getName());
			FileUtils.copyFile(file, tempFile);
			addJar(tempFile);
			Class<?> clz = Class.forName("update" + updateVersion + "before.Main");
			MethodUtils.invokeStaticMethod(clz, "main", new Object[] { new String[] {} });
		}
	}

	private void changeVersion(String base, String updateVersion) throws Exception {
		File file = new File(base, "version.o2");
		FileUtils.writeStringToFile(file, updateVersion, "UTF-8");
	}

	private void addJar(File file) throws Exception {
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(urlClassLoader, new Object[] { file.toURI().toURL() });
	}

}