package com.x.server.console.test;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.config.Messages;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.server.console.CommandFactory;

public class TestClient {

	@Test
	public void test1() throws Exception {
		FileUtils.writeByteArrayToFile(new File("d:/111.png"), Base64.decodeBase64(CommandFactory.DEFAULT_STARTIMAGE));
	}

	@Test
	public void test2() throws Exception {
		Messages o = readConfigObject("messages.json", Messages.class);
		System.out.println(o);
	}

	@Test
	public void test3() {
		System.out.println(FilenameUtils.getExtension("aaa.txt"));
	}

	public static <T> T readConfigObject(String path, Class<T> cls) throws Exception {
		File file = new File("D:/O2/o2oa/o2server/configSample", path);
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

}
