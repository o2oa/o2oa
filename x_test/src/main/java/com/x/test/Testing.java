package com.x.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;

import org.junit.Test;

public class Testing {

	private static int tag = 1;

	private static String path = "src/main/resources/Testing.properties";

	private static String getServer() {
		if (tag == 1) {
			return "http://was.ray.local:9080";
		}
		if (tag == 2) {
			return "http://wass.ray.local:9080";
		}
		if (tag == 3) {
			return "http://xa01.zoneland.net:9080";
		} else {
			return "";
		}
	}

	public static String getUrl(String app) {
		return getServer() + "/" + app;
	}

	public static String getPath() {
		return path;
	}

	public static String getProperty(String name) {
		String str = null;
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(Testing.getPath()));
			str = prop.getProperty(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String setProperty(String name, String value) {
		String str = null;
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(Testing.getPath()));
			prop.setProperty(name, value);
			prop.store(new FileOutputStream(Testing.getPath()), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	@Test
	public void test() {
		MimetypesFileTypeMap map = new MimetypesFileTypeMap();
		System.out.println(map.getContentType("aaa.mp3"));
	}
	

}
