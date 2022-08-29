package com.x.base.core.project.config;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class WebServers extends ConcurrentSkipListMap<String, WebServer> {

	private static final long serialVersionUID = -706102090064680898L;

	private static final Random RANDOM = new SecureRandom();

	public WebServers() {
		super();
	}

	public WebServers(Nodes nodeConfigs) {
		for (Entry<String, Node> o : nodeConfigs.entrySet()) {
			WebServer server = o.getValue().getWeb();
			if ((null != server) && BooleanUtils.isTrue(server.getEnable())) {
				this.put(o.getKey(), server);
			}
		}
	}

	public Entry<String, WebServer> getRandom() throws IllegalStateException {
		List<Entry<String, WebServer>> list = new ArrayList<>();
		for (Entry<String, WebServer> o : this.entrySet()) {
			if (BooleanUtils.isTrue(o.getValue().getEnable())) {
				list.add(o);
			}
		}
		if (!list.isEmpty()) {
			return list.get(RANDOM.nextInt(list.size()));
		}
		throw new IllegalStateException("randomWithWeight error.");
	}

	public static void updateWebServerConfigJson() throws Exception {
		File dir = new File(Config.base(), "servers/webServer/x_desktop/res/config");
		FileUtils.forceMkdir(dir);
		File file = new File(dir, "config.json");
		Gson gson = XGsonBuilder.instance();
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		// 三服务器合一运行不需要输出centers,否则需要输出centers
		if ((!Objects.equals(Config.currentNode().getWeb().getPort(), Config.currentNode().getApplication().getPort()))
				|| (!Objects.equals(Config.currentNode().getWeb().getPort(),
						Config.currentNode().getCenter().getPort()))) {
			List<Map<String, String>> centers = new ArrayList<>();
			map.put("center", centers);
			Map<String, String> center = new HashMap<>();
			center.put("host", "");
			center.put("port", Config.currentNode().getCenter().getPort().toString());
			centers.add(center);
			if (!Objects.equals(Config.currentNode().getCenter().getProxyPort(),
					Config.currentNode().getCenter().getPort())) {
				center = new HashMap<>();
				center.put("host", "");
				center.put("port", Config.currentNode().getCenter().getProxyPort().toString());
				centers.add(center);
			}
		}
		// 写入systemName
		map.put("footer", Config.collect().getFooter());
		map.put("title", Config.collect().getTitle());
		map.put("version", Config.version());
		map.put("appUrl", Config.collect().getAppUrl());
		map.put("app_protocol", "auto");

		map.put("loginPage", Config.portal().getLoginPage());
		map.put("indexPage", Config.portal().getIndexPage());
		map.put("urlMapping", Config.portal().getUrlMapping());

		writeWebServerConfigPasswordPolicy(map);
		writeWebServerConfigLanguage(map);
		writeWebServerConfigTokenName(map);
		writeWebServerConfigRsa(map);
		writeWebServerConfigSafeLogout(map);
		writeWebServerConfigWebSocketEnable(map);

		for (Entry<String, JsonElement> en : Config.web().entrySet()) {
			map.put(en.getKey(), en.getValue());
		}

		for (Entry<String, JsonElement> en : Config.mock().entrySet()) {
			map.put(en.getKey(), en.getValue());
		}

		FileUtils.writeStringToFile(file, gson.toJson(map), DefaultCharset.charset);
	}

	/**
	 * 是否启用安全注销
	 * 
	 * @param map
	 * @throws Exception
	 */
	private static void writeWebServerConfigSafeLogout(LinkedHashMap<String, Object> map) throws Exception {
		map.put("enableSafeLogout", Config.person().getEnableSafeLogout());
	}

	/**
	 * 如果启用了rsa加密,输出public.key
	 * 
	 * @param map
	 * @throws Exception
	 * @throws IOException
	 */
	private static void writeWebServerConfigRsa(LinkedHashMap<String, Object> map) throws Exception {
		map.put("publicKey", BooleanUtils.isTrue(Config.token().getRsaEnable()) ? Config.publicKey() : "");
	}

	/**
	 * 平台TokenName
	 * 
	 * @param map
	 * @throws Exception
	 */
	private static void writeWebServerConfigTokenName(LinkedHashMap<String, Object> map) throws Exception {
		map.put("tokenName", Config.person().getTokenName());
	}

	/**
	 * 平台语言
	 * 
	 * @param map
	 * @throws Exception
	 */
	private static void writeWebServerConfigLanguage(LinkedHashMap<String, Object> map) throws Exception {
		map.put("language", Config.person().getLanguage());
	}

	/**
	 * 是否启用webSocket链接
	 * 
	 * @param map
	 * @throws Exception
	 */
	private static void writeWebServerConfigWebSocketEnable(LinkedHashMap<String, Object> map) throws Exception {
		map.put("webSocketEnable", Config.general().getWebSocketEnable());
	}

	/**
	 * 写入密码规则
	 * 
	 * @param map
	 * @throws Exception
	 */
	private static void writeWebServerConfigPasswordPolicy(LinkedHashMap<String, Object> map) throws Exception {
		map.put("passwordRegex", Config.person().getPasswordRegex());
		map.put("passwordRegexHint", Config.person().getPasswordRegexHint());
	}

//	/**
//	 * 写入是否启用了center和application的代理
//	 * 
//	 * @param o
//	 * @param webServer
//	 */
//	private static void writeWebServerConfigProxyEnable(Map<String, Object> o, WebServer webServer) {
//		o.put("proxyApplicationEnable", webServer.getProxyApplicationEnable());
//		o.put("proxyCenterEnable", webServer.getProxyCenterEnable());
//	}
}
