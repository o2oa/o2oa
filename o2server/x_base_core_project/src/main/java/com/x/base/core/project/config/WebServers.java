package com.x.base.core.project.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

public class WebServers extends ConcurrentSkipListMap<String, WebServer> {

	private static final long serialVersionUID = -706102090064680898L;

	private static final String MAP_LOGINPAGE = "loginPage";

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
		if (ListTools.isEmpty(list)) {
			return null;
		}
		this.sortWithWeight(list);
		int total = 0;
		for (Entry<String, WebServer> o : list) {
			total += o.getValue().getWeight();
		}

		int rdm = RANDOM.nextInt(total);
		int current = 0;
		for (Entry<String, WebServer> o : list) {
			current += o.getValue().getWeight();
			if (rdm <= current) {
				return o;
			}
		}
		throw new IllegalStateException("randomWithWeight error.");
	}

	private void sortWithWeight(List<Entry<String, WebServer>> list) {
		Collections.sort(list,
				(o1, o2) -> ObjectUtils.compare(o1.getValue().getWeight(), o2.getValue().getWeight(), true));
	}

	public static void updateWebServerConfigJson() throws Exception {
		File dir = new File(Config.base(), "servers/webServer/x_desktop/res/config");
		FileUtils.forceMkdir(dir);
		File file = new File(dir, "config.json");

		Gson gson = XGsonBuilder.instance();

		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		/**
		 * 覆盖掉配置的参数
		 * <p>
		 * 先取本节点的center如果没有那么取第一个center
		 */
		com.x.base.core.project.config.CenterServer centerServerConfig = Config.currentNode().getCenter();
		List<Map<String, String>> centers = new ArrayList<>();
		map.put("center", centers);
		if ((null == centerServerConfig) || BooleanUtils.isNotTrue(centerServerConfig.getEnable())) {
			Entry<String, CenterServer> entry = Config.nodes().centerServers().orderedEntry().get(0);
			centerServerConfig = entry.getValue();
			Map<String, String> center = new HashMap<>();
			center.put("host", entry.getKey());
			center.put("port", centerServerConfig.getPort().toString());
			centers.add(center);
			if (StringUtils.isNotEmpty(centerServerConfig.getProxyHost())) {
				center = new HashMap<>();
				center.put("host", centerServerConfig.getProxyHost());
				center.put("port", centerServerConfig.getProxyPort().toString());
				centers.add(center);
			}
			if (!Objects.equals(centerServerConfig.getProxyPort(), centerServerConfig.getPort())) {
				center = new HashMap<>();
				center.put("host", entry.getKey());
				center.put("port", centerServerConfig.getProxyPort().toString());
				centers.add(center);
			}
		}
		Map<String, String> center = new HashMap<>();
		center.put("host", "");
		center.put("port", centerServerConfig.getPort().toString());
		centers.add(center);
		if (!Objects.equals(centerServerConfig.getProxyPort(), centerServerConfig.getPort())) {
			center = new HashMap<>();
			center.put("host", "");
			center.put("port", centerServerConfig.getProxyPort().toString());
			centers.add(center);
		}
		map.putAll(centerServerConfig.getConfig());

		/** 写入systemName */
		map.put("footer", Config.collect().getFooter());
		map.put("title", Config.collect().getTitle());
		map.put("version", Config.version());
		map.put("appUrl", Config.collect().getAppUrl());
		/**
		 * if (BooleanUtils.isTrue(centerServerConfig.getSslEnable())) { //
		 * map.put("app_protocol", "https:"); // } else { // map.put("app_protocol",
		 * "http:"); // } 上面的无效
		 */
		map.put("app_protocol", "auto");
		if ((null != Config.portal().getLoginPage())
				&& (BooleanUtils.isTrue(Config.portal().getLoginPage().getEnable()))) {
			map.put(MAP_LOGINPAGE, Config.portal().getLoginPage());
		} else if ((null != Config.person().getLoginPage())
				&& (BooleanUtils.isTrue(Config.person().getLoginPage().getEnable()))) {
			map.put(MAP_LOGINPAGE, Config.person().getLoginPage());
		} else {
			map.put(MAP_LOGINPAGE, Config.portal().getLoginPage());
		}
		map.put("indexPage", Config.portal().getIndexPage());
		map.put("webSocketEnable", Config.communicate().wsEnable());
		map.put("urlMapping", Config.portal().getUrlMapping());

		writeWebServerConfigPasswordPolicy(map);
		writeWebServerConfigLanguage(map);
		writeWebServerConfigTokenName(map);
		writeWebServerConfigRsa(map);
		writeWebServerConfigSafeLogout(map);

		for (Entry<String, JsonElement> en : Config.web().entrySet()) {
			map.put(en.getKey(), en.getValue());
		}
		for (Entry<String, JsonElement> en : Config.mock().entrySet()) {
			map.put(en.getKey(), en.getValue());
		}
		writeWebServerConfigProxyEnable(map, Config.currentNode().getWeb());
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
		File publicKeyFile = new File(Config.base(), "config/public.key");
		if (publicKeyFile.exists() && publicKeyFile.isFile()) {
			String publicKey = FileUtils.readFileToString(publicKeyFile, StandardCharsets.UTF_8.name());
			byte[] publicKeyB = org.apache.commons.codec.binary.Base64.decodeBase64(publicKey);
			publicKey = new String(Base64.encodeBase64(publicKeyB));
			map.put("publicKey", publicKey);
		}
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
	 * 写入密码规则
	 * 
	 * @param map
	 * @throws Exception
	 */
	private static void writeWebServerConfigPasswordPolicy(LinkedHashMap<String, Object> map) throws Exception {
		map.put("passwordRegex", Config.person().getPasswordRegex());
		map.put("passwordRegexHint", Config.person().getPasswordRegexHint());
	}

	/**
	 * 写入是否启用了center和application的代理
	 * 
	 * @param o
	 * @param webServer
	 */
	private static void writeWebServerConfigProxyEnable(Map<String, Object> o, WebServer webServer) {
		o.put("proxyApplicationEnable", webServer.getProxyApplicationEnable());
		o.put("proxyCenterEnable", webServer.getProxyCenterEnable());
	}
}
