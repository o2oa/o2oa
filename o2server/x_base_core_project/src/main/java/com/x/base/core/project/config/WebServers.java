package com.x.base.core.project.config;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.Host;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.project.tools.ListTools;
import org.apache.commons.lang3.StringUtils;

public class WebServers extends ConcurrentSkipListMap<String, WebServer> {

	private static final long serialVersionUID = -706102090064680898L;

	private static final String MAP_LOGINPAGE = "loginPage";

	public WebServers() {
		super();
	}

	public WebServers(Nodes nodeConfigs) {
		for (Entry<String, Node> o : nodeConfigs.entrySet()) {
			WebServer server = o.getValue().getWeb();
			if (null != server) {
				if (BooleanUtils.isTrue(server.getEnable())) {
					this.put(o.getKey(), server);
				}
			}
		}
	}

	public Entry<String, WebServer> getRandom() throws Exception {
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
		Random random = new Random();
		int rdm = random.nextInt(total);
		int current = 0;
		for (Entry<String, WebServer> o : list) {
			current += o.getValue().getWeight();
			if (rdm <= current) {
				return o;
			}
		}
		throw new Exception("randomWithWeight error.");
	}

	private void sortWithWeight(List<Entry<String, WebServer>> list) {
		Collections.sort(list, new Comparator<Entry<String, WebServer>>() {
			public int compare(Entry<String, WebServer> o1, Entry<String, WebServer> o2) {
				return ObjectUtils.compare(o1.getValue().getWeight(), o2.getValue().getWeight(), true);
			}
		});
	}

	public static void updateWebServerConfigJson() throws Exception {
		File dir = new File(Config.base(), "servers/webServer/x_desktop/res/config");
		FileUtils.forceMkdir(dir);
		File file = new File(dir, "config.json");

		Gson gson = XGsonBuilder.instance();

		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		/** 覆盖掉配置的参数 */
		com.x.base.core.project.config.CenterServer centerServerConfig = Config.nodes().centerServers().first()
				.getValue();
		map.putAll(centerServerConfig.getConfig());
		List<Map<String, String>> centers = new ArrayList<>();
		map.put("center", centers);
		/** 写入center地址 */
		Map<String, String> center = new HashMap<String, String>();
		center = new HashMap<String, String>();
		center.put("host", "");
		center.put("port", centerServerConfig.getPort().toString());
		centers.add(center);
		if (!Objects.equals(centerServerConfig.getProxyPort(), centerServerConfig.getPort())) {
			center = new HashMap<String, String>();
			center.put("host", "");
			center.put("port", centerServerConfig.getProxyPort().toString());
			centers.add(center);
		}
		String host = Config.nodes().primaryCenterNode();
		if (!Host.isRollback(host)) {
			center = new HashMap<String, String>();
			center.put("host", host);
			center.put("port", centerServerConfig.getPort().toString());
			centers.add(center);
		}
		/** 写入proxy地址 */
		if (StringUtils.isNotEmpty(centerServerConfig.getProxyHost())) {
			center = new HashMap<String, String>();
			center.put("host", centerServerConfig.getProxyHost());
			center.put("port", centerServerConfig.getProxyPort().toString());
			centers.add(center);
		}

		/** 写入systemName */
		map.put("footer", Config.collect().getFooter());
		map.put("title", Config.collect().getTitle());
		map.put("appUrl", Config.collect().getAppUrl());
		/***/
		if (centerServerConfig.getSslEnable()) {
			map.put("app_protocol", "https:");
		} else {
			map.put("app_protocol", "http:");
		}
		/* 上面的无效 */
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

		/* 密码规则 */
		map.put("passwordRegex", Config.person().getPasswordRegex());
		map.put("passwordRegexHint", Config.person().getPasswordRegexHint());
		/* 平台语言 */
		map.put("language", Config.person().getLanguage());

		/* RSA */
		File publicKeyFile = new File(Config.base(), "config/public.key");
		if (publicKeyFile.exists() && publicKeyFile.isFile()) {
			String publicKey = FileUtils.readFileToString(publicKeyFile, "utf-8");
			byte[] publicKeyB = org.apache.commons.codec.binary.Base64.decodeBase64(publicKey);
			publicKey = new String(Base64.encodeBase64(publicKeyB));
			map.put("publicKey", publicKey);
		}

		for (Entry<String, JsonElement> en : Config.web().entrySet()) {
			map.put(en.getKey(), en.getValue());
		}
		for (Entry<String, JsonElement> en : Config.mock().entrySet()) {
			map.put(en.getKey(), en.getValue());
		}
		FileUtils.writeStringToFile(file, gson.toJson(map), DefaultCharset.charset);
	}

}
