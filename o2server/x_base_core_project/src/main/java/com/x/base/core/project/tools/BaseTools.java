package com.x.base.core.project.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class BaseTools {

	public static String getBasePath() throws Exception {
		return getBaseDirectory().getAbsolutePath();
	}

	public static File getBaseDirectory() throws Exception {
		String path = BaseTools.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path);
		if (!file.isDirectory()) {
			file = file.getParentFile();
		}
		while (null != file) {
			File versionFile = new File(file, "version.o2");
			if (versionFile.exists()) {
				return file;
			}
			file = file.getParentFile();
		}
		throw new Exception("can not define o2server base directory.");
	}

	public static <T> T readConfigObject(String path, Class<T> cls) throws Exception {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if ((!file.exists()) || file.isDirectory()) {
			return null;
		}
		String json = FileUtils.readFileToString(file, DefaultCharset.charset);

		Gson gson = new Gson();

		JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
		if ((null != jsonElement) && jsonElement.isJsonObject()) {
			LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
			map = new Gson().fromJson(jsonElement, map.getClass());
			removeComment(map);
			jsonElement = gson.toJsonTree(map);
		}
		return gson.fromJson(jsonElement, cls);
	}

	private static void removeComment(Map<Object, Object> map) {
		List<Entry<Object, Object>> entries = new ArrayList<>();
		for (Entry<Object, Object> entry : map.entrySet()) {
			if (StringUtils.startsWith(Objects.toString(entry.getKey()), "###")) {
				entries.add(entry);
				continue;
			} else {
				if (entry.getValue() instanceof Map) {
					removeComment((Map<Object, Object>) entry.getValue());
				}
			}
		}
		for (Entry<Object, Object> entry : entries) {
			map.remove(entry.getKey());
		}
	}

	public static <T> T readConfigObject(String path, String otherPath, Class<T> cls) throws Exception {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if (file.exists() && file.isFile()) {
			return readConfigObject(path, cls);
		}
		file = new File(base, otherPath);
		if (file.exists() && file.isFile()) {
			return readConfigObject(otherPath, cls);
		}
		throw new Exception("can not get file with path:" + path + ", otherPath:" + otherPath + ".");
	}

	public static void writeObject(String path, Object obj) throws Exception {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		String json = (new Gson()).toJson(obj);
		FileUtils.writeStringToFile(file, json, DefaultCharset.charset);
	}

	public static String readCfg(String path) throws Exception {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if ((!file.exists()) || file.isDirectory()) {
			return null;
		}
		String str = FileUtils.readFileToString(file, DefaultCharset.charset);
		return (StringUtils.trim(str));
	}

	public static String readCfg(String path, String defaultValue) throws Exception {
		String str = readCfg(path);
		if (StringUtils.isEmpty(str)) {
			str = defaultValue;
		}
		return (StringUtils.trim(str));
	}

	public static void writeCfg(String path, String value) throws Exception {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		FileUtils.writeStringToFile(file, StringUtils.trim(value), DefaultCharset.charset);
	}

	public static byte[] readBytes(String path) throws Exception {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if ((!file.exists()) || file.isDirectory()) {
			throw new Exception("can not get file with path:" + file.getAbsolutePath());
		}
		return FileUtils.readFileToByteArray(file);
	}

	public static String readString(String path) throws Exception {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if ((!file.exists()) || file.isDirectory()) {
			throw new Exception("can not get file with path:" + file.getAbsolutePath());
		}
		return FileUtils.readFileToString(file, DefaultCharset.charset);
	}
}