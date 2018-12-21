package com.x.base.core.project.tools;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

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

	public static <T> T readObject(String path, Class<T> cls) throws Exception {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if ((!file.exists()) || file.isDirectory()) {
			return null;
		}
		String json = FileUtils.readFileToString(file, DefaultCharset.charset);
		return (new Gson()).fromJson(json, cls);
	}

	public static <T> T readObject(String path, String otherPath, Class<T> cls) throws Exception {
		String base = BaseTools.getBasePath();
		File file = new File(base, path);
		if ((!file.exists()) || file.isDirectory()) {
			file = new File(base, otherPath);
		}
		if ((!file.exists()) || file.isDirectory()) {
			throw new Exception("can not get file with path:" + path + ", otherPath:" + otherPath + ".");
		}
		String json = FileUtils.readFileToString(file, DefaultCharset.charset);
		return (new Gson()).fromJson(json, cls);
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