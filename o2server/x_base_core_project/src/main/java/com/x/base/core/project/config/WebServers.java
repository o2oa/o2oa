package com.x.base.core.project.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class WebServers extends ConcurrentSkipListMap<String, WebServer> {

	private static final long serialVersionUID = -706102090064680898L;

	private static final Random RANDOM = new SecureRandom();

	public static final List<String> WEB_SERVER_FOLDERS = Arrays.asList("api", "o2_core", "o2_lib", "x_component_ANN",
			"x_component_AppCenter", "x_component_AppMarketV2", "x_component_AppMarketV2_Application",
			"x_component_appstore", "x_component_appstore_application", "x_component_Attendance",
			"x_component_attendancev2", "x_component_BAM", "x_component_Calendar", "x_component_cms_Column",
			"x_component_cms_ColumnManager", "x_component_cms_DictionaryDesigner", "x_component_cms_Document",
			"x_component_cms_FormDesigner", "x_component_cms_Index", "x_component_cms_Module",
			"x_component_cms_QueryViewDesigner", "x_component_cms_ScriptDesigner", "x_component_cms_ViewDesigner",
			"x_component_cms_Xform", "x_component_Collect", "x_component_Common", "x_component_ConfigDesigner",
			"x_component_Console", "x_component_ControlPanel", "x_component_CRM", "x_component_Deployment",
			"x_component_DesignCenter", "x_component_Empty", "x_component_FaceSet", "x_component_File",
			"x_component_FindDesigner", "x_component_Forum", "x_component_ForumCategory", "x_component_ForumDocument",
			"x_component_ForumPerson", "x_component_ForumSearch", "x_component_ForumSection", "x_component_ftsearch",
			"x_component_Homepage", "x_component_HotArticle", "x_component_IMV2", "x_component_LogViewer",
			"x_component_Meeting", "x_component_Minder", "x_component_MinderEditor", "x_component_Note",
			"x_component_OKR", "x_component_Org", "x_component_portal_DictionaryDesigner",
			"x_component_portal_PageDesigner", "x_component_portal_Portal", "x_component_portal_PortalExplorer",
			"x_component_portal_PortalManager", "x_component_portal_ScriptDesigner",
			"x_component_portal_WidgetDesigner", "x_component_process_Application",
			"x_component_process_ApplicationExplorer", "x_component_process_DictionaryDesigner",
			"x_component_process_FormDesigner", "x_component_process_ProcessDesigner",
			"x_component_process_ProcessManager", "x_component_process_ScriptDesigner",
			"x_component_process_StatDesigner", "x_component_process_TaskCenter", "x_component_process_ViewDesigner",
			"x_component_process_WidgetDesigner", "x_component_process_Work", "x_component_process_workcenter",
			"x_component_process_Xform", "x_component_Profile", "x_component_query_ImporterDesigner",
			"x_component_query_Query", "x_component_query_QueryExplorer", "x_component_query_QueryManager",
			"x_component_query_StatDesigner", "x_component_query_StatementDesigner", "x_component_query_TableDesigner",
			"x_component_query_ViewDesigner", "x_component_Search", "x_component_Selector",
			"x_component_service_AgentDesigner", "x_component_service_DictionaryDesigner",
			"x_component_service_InvokeDesigner", "x_component_service_ScriptDesigner",
			"x_component_service_ServiceManager", "x_component_Setting", "x_component_systemconfig",
			"x_component_Template", "x_component_ThreeMember", "x_desktop", "x_init");

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
		map.put("searchEnable", Config.query().index().getEnable() && Config.query().index().getSearchEnable());

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
		map.put("supportedLanguages", Config.general().getSupportedLanguages());
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
