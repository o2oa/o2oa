package com.x.build;

import java.io.File;
import java.security.MessageDigest;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;

public class Publish {

	private static final String endPoint = "obs.cn-east-2.myhwclouds.com";

	private static final String ak = "67CEL8RL5I3QP20IYBIE";

	private static final String sk = "eyWMRgA2rxhZS4uPcZ4sFGX2T0wAercYgXRKcXEX";

//	private static ObsClient obsClient;
//
//	private static String bucketName = "obs-o2public";

	public static void main(String... args) throws Exception {
		String version = args[0];
		String dir = args[1];
		Date now = new Date();
		// File file_update = new File(dir, version + ".zip");
		File file_windows = new File(dir, "o2server_" + version + "_windows.zip");
		File file_linux = new File(dir, "o2server_" + version + "_linux.zip");
		File file_macos = new File(dir, "o2server_" + version + "_macos.zip");
		File file_aix = new File(dir, "o2server_" + version + "_aix.zip");
		File file_neokylin_loongson = new File(dir, "o2server_" + version + "_neokylin_loongson.zip");
		// File version_dir = new
		// File("D:/download.o2oa.net/o2server/servers/webServer/download/versions");
		// File update_dir = new File("D:/o2collect/servers/webServer/o2server/update");
		// FileUtils.copyFile(file_update, new File(version_dir,
		// file_update.getName()));
//		FileUtils.copyFile(file_windows, new File(version_dir, file_windows.getName()));
//		FileUtils.copyFile(file_linux, new File(version_dir, file_linux.getName()));
//		FileUtils.copyFile(file_macos, new File(version_dir, file_macos.getName()));
//		FileUtils.copyFile(file_aix, new File(version_dir, file_aix.getName()));

//		LinkedHashMap<String, Object> map_update = new LinkedHashMap<>();
//		map_update.put("version", version);
//		map_update.put("size", file_update.length());
//		map_update.put("url", "http://download.o2oa.net/download/versions/" + file_update.getName());

//		FileUtils.write(new File(update_dir, FilenameUtils.getBaseName(file_update.getName()) + ".json"),
//				XGsonBuilder.toJson(map_update), false);
		LinkedHashMap<String, Object> map_windows = new LinkedHashMap<>();
		LinkedHashMap<String, Object> map_linux = new LinkedHashMap<>();
		LinkedHashMap<String, Object> map_macos = new LinkedHashMap<>();
		LinkedHashMap<String, Object> map_aix = new LinkedHashMap<>();
		LinkedHashMap<String, Object> map_neokylin_loongson = new LinkedHashMap<>();
		map_windows.put("system", "windows");
		map_windows.put("name", "o2server_" + version + "_windows");
		map_windows.put("fileName", FilenameUtils.getName(file_windows.getName()));
		map_windows.put("fileSize", file_windows.length() / 1024 / 1024 + "MB");
		map_windows.put("updateTime", DateTools.format(now));
		// map_windows.put("url", updateToObs(file_windows.getName(),
		// file_windows.getParent()));
		map_windows.put("url", "http://download.o2oa.net/download/" + file_windows.getName());
		map_windows.put("sha256", sha256(file_windows));
		map_linux.put("system", "linux");
		map_linux.put("name", "o2server_" + version + "_linux");
		map_linux.put("fileName", FilenameUtils.getName(file_linux.getName()));
		map_linux.put("fileSize", file_linux.length() / 1024 / 1024 + "MB");
		map_linux.put("updateTime", DateTools.format(now));
		// map_linux.put("url", updateToObs(file_linux.getName(),
		// file_linux.getParent()));
		map_linux.put("url", "http://download.o2oa.net/download/" + file_linux.getName());
		map_linux.put("sha256", sha256(file_linux));
		map_macos.put("system", "macos");
		map_macos.put("name", FilenameUtils.getBaseName(file_macos.getName()));
		map_macos.put("fileName", FilenameUtils.getName(file_macos.getName()));
		map_macos.put("fileSize", file_macos.length() / 1024 / 1024 + "MB");
		map_macos.put("updateTime", DateTools.format(now));
		// map_macos.put("url", updateToObs(file_macos.getName(),
		// file_macos.getParent()));
		map_macos.put("url", "http://download.o2oa.net/download/" + file_macos.getName());
		map_macos.put("sha256", sha256(file_macos));
		map_aix.put("system", "aix");
		map_aix.put("name", FilenameUtils.getBaseName(file_aix.getName()));
		map_aix.put("fileName", FilenameUtils.getName(file_aix.getName()));
		map_aix.put("fileSize", file_aix.length() / 1024 / 1024 + "MB");
		map_aix.put("updateTime", DateTools.format(now));
		// map_aix.put("url", updateToObs(file_aix.getName(), file_aix.getParent()));
		map_aix.put("url", "http://download.o2oa.net/download/" + file_aix.getName());
		map_aix.put("sha256", sha256(file_aix));
		map_neokylin_loongson.put("system", "neokylin_loongson");
		map_neokylin_loongson.put("name", FilenameUtils.getBaseName(file_neokylin_loongson.getName()));
		map_neokylin_loongson.put("fileName", FilenameUtils.getName(file_neokylin_loongson.getName()));
		map_neokylin_loongson.put("fileSize", file_neokylin_loongson.length() / 1024 / 1024 + "MB");
		map_neokylin_loongson.put("updateTime", DateTools.format(now));
		// map_aix.put("url", updateToObs(file_aix.getName(), file_aix.getParent()));
		map_neokylin_loongson.put("url", "http://download.o2oa.net/download/" + file_neokylin_loongson.getName());
		map_neokylin_loongson.put("sha256", sha256(file_neokylin_loongson));
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("windows", map_windows);
		map.put("linux", map_linux);
		map.put("macos", map_macos);
		map.put("aix", map_aix);
		map.put("neokylin_loongson", map_neokylin_loongson);
		File file_download = new File("D:/download.o2oa.net/o2server/servers/webServer/download",
				"download_preview.json");
		FileUtils.writeStringToFile(file_download, XGsonBuilder.toJson(map));
		File file_download_jsonp = new File("D:/download.o2oa.net/o2server/servers/webServer/download",
				"download_preview.jsonp");
		String jsonp = "callback(" + StringUtils.LF + XGsonBuilder.toJson(map) + StringUtils.LF + ")";
		FileUtils.writeStringToFile(file_download_jsonp, jsonp);
	}

	private static String sha256(File file) throws Exception {
		MessageDigest messageDigest;
		messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] hash = messageDigest.digest(FileUtils.readFileToByteArray(file));
		return Hex.encodeHexString(hash);
	}

	@Test
	public void test1() throws Exception {
		System.out.println(sha256(new File("d:/o2server_20180615160351_x86.zip")));
	}
}
