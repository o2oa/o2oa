package com.x.server.console.action;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.x.base.core.http.HttpMediaType;
import com.x.base.core.utils.JarTools;
import com.x.base.core.utils.ListTools;

public class ActionUpdate extends ActionBase {

	public boolean execute(String base) throws Exception {
		WrapOutCheck wrap = this.check(base);
		if (ListTools.isNotEmpty(wrap.getFollowList())) {
			String updateVersion = wrap.getFollowList().get(0).getName();
			Long size = wrap.getFollowList().get(0).getSize();
			System.out.println("update to version:" + updateVersion);
			System.out.println("downloading size:" + (size / (1024 * 1024)) + "MB");
			byte[] bytes = this.getZip(base, updateVersion);
			System.out.println("extracting.");
			this.unzip(base, updateVersion, bytes);
			this.executeBeforeScript(base, updateVersion);
			System.out.println("update content.");
			this.updateFiles(base, updateVersion);
			this.changeVersion(base, updateVersion);
			System.out.println("update success, should to restart server.");
			return true;
		} else {
			System.out.println("no available update version.");
			return false;
		}
	}

	private byte[] getZip(String base, String updateVersion) throws Exception {
		String address = this.getUpdateAddress(base);
		address += "/servlet/update/download/" + updateVersion;
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
		return bytes;
	}

	private void unzip(String base, String updateVersion, byte[] bytes) throws Exception {
		File dir = new File(base, "local/updates/" + updateVersion);
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