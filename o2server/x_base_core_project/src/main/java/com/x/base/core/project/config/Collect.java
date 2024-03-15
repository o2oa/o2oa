package com.x.base.core.project.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionCollectConnectError;
import com.x.base.core.project.exception.ExceptionCollectDisable;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;

public class Collect extends ConfigObject {

	private static String Default_title = "企业办公平台";
	private static String Default_footer = "www.o2oa.net";
	private static String Default_name = "www.o2oa.net";
	private static String Default_appUrl = "https://app.o2oa.net/download/download.html";
	static String Default_server = "collect.o2oa.net";
	static String Default_appPackServerHost = "apppack.o2oa.net";
	private static Integer Default_port = 20080;
	private static Integer Default_appPackServerPort = 30088;
	public static String ADDRESS_COLLECT_LOGIN = "/o2_collect_assemble/jaxrs/authentication/captcha/key/o2/answer/o2";
	public static String ADDRESS_COLLECT_ECHO = "/o2_collect_assemble/jaxrs/echo";
	public static String ADDRESS_COLLECT_VALIDATE = "/o2_collect_assemble/jaxrs/unit/validate";
	public static String ADDRESS_COLLECT_VALIDATE_CODE = "/o2_collect_assemble/jaxrs/unit/validate/codeanswer";
	public static String ADDRESS_COLLECT_APPLICATION_LIST = "/o2_collect_assemble/jaxrs/application/list";
	public static String ADDRESS_COLLECT_MARKET = "/market";
	public static String ADDRESS_COLLECT_APPLICATION_DOWN = "/o2_collect_assemble/jaxrs/application2/download";
	public static String COLLECT_TOKEN = "c-token";
	public static String ADDRESS_APPPACK_AUTH = "/auth/collect";
	// public static String ADDRESS_APPPACK_SAVE = "/pack/info/save";
	public static String ADDRESS_APPPACK_SAVE = "/pack/info/flutter/save";
	public static String ADDRESS_APPPACK_INFO = "/pack/info/collect/%s";
	public static String ADDRESS_APPPACK_INFO_RESTART = "/pack/info/restart/collect/%s";
	public static String ADDRESS_APPPACK_DOWNLOAD_APK = "/pack/download/apk/%s";

	public static Collect defaultInstance() {
		return new Collect();
	}

	public Collect() {
		this.enable = false;
		this.title = "";
		this.footer = "";
		this.name = "";
		this.password = "";
		this.appUrl = "";
		this.server = "";
		this.port = Default_port;
		this.sslEnable = false;
		this.appPackServerHost = Default_appPackServerHost;
		this.appPackServerPort = Default_appPackServerPort;
		this.isAppPackCoverAppUrl = false;
	}

	@FieldDescribe("是否启用连接到云平台")
	private Boolean enable;
	@FieldDescribe("云平台账户名称,同时显示在登录页面底部.")
	private String name;
	@FieldDescribe("云平台密码")
	private String password;
	@FieldDescribe("系统标题,同时显示在登录页面上部.")
	private String title;
	@FieldDescribe("底部申明")
	private String footer;
	@FieldDescribe("app下载地址")
	private String appUrl;
	@FieldDescribe("云平台服务器地址")
	private String server;
	@FieldDescribe("云平台端口")
	private Integer port;
	@FieldDescribe("云平台连接是否启用ssl")
	private Boolean sslEnable;
	@FieldDescribe("推送消息secret")
	private String secret;
	@FieldDescribe("推送消息key")
	private String key;
	@FieldDescribe("app打包服务器域名")
	private String appPackServerHost;
	@FieldDescribe(("app打包服务器端口"))
	private Integer appPackServerPort;
	@FieldDescribe("是否用app打包的地址覆盖o2oa的appUrl")
	private Boolean isAppPackCoverAppUrl;


	public Boolean getAppPackCoverAppUrl() {
		return isAppPackCoverAppUrl;
	}

	public void setAppPackCoverAppUrl(Boolean appPackCoverAppUrl) {
		isAppPackCoverAppUrl = appPackCoverAppUrl;
	}

	public String getAppPackServerHost() {
		return StringUtils.isEmpty(appPackServerHost) ? Default_appPackServerHost : appPackServerHost;
	}

	public void setAppPackServerHost(String appPackServerHost) {
		this.appPackServerHost = appPackServerHost;
	}

	public Integer getAppPackServerPort() {
		return Objects.isNull(this.appPackServerPort) ? Default_appPackServerPort : this.appPackServerPort;
	}

	public void setAppPackServerPort(Integer appPackServerPort) {
		this.appPackServerPort = appPackServerPort;
	}

	public String getSecret() {
		return secret;
	}

	public String getKey() {
		return key;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public String getTitle() {
		return StringUtils.isEmpty(this.title) ? Default_title : this.title;
	}

	public String getFooter() {
		return StringUtils.isEmpty(this.footer) ? Default_footer : this.footer;
	}

	public String getName() {
		return StringUtils.isEmpty(this.name) ? Default_name : this.name;
	}

	public String getAppUrl() {
		return StringUtils.isEmpty(this.appUrl) ? Default_appUrl : this.appUrl;
	}

	public String getPassword() {
		return Objects.toString(this.password, "");
	}

	public String getServer() {
		/* 强制换掉老域名 */
		if (StringUtils.equalsIgnoreCase("collect.o2oa.io", this.server)) {
			return Default_server;
		} else {
			return StringUtils.isEmpty(this.server) ? Default_server : this.server;
		}
	}

	public Integer getPort() {
		return Objects.isNull(this.port) ? Default_port : this.port;
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable) ? true : false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public boolean validate() throws Exception {

		if (!Config.collect().getEnable()) {
			throw new ExceptionCollectDisable();
		}

		try {
			String url = Config.collect().url(Collect.ADDRESS_COLLECT_VALIDATE);
			Map<String, String> map = new HashMap<>();
			map.put("name", this.getName());
			map.put("password", this.getPassword());
			ActionResponse resp = ConnectionAction.post(url, null, map);
			return resp.getData(WrapBoolean.class).getValue();
		} catch (Exception e) {
			throw new ExceptionCollectConnectError();
		}
	}

	public boolean connect() throws Exception {
		if (!Config.collect().getEnable()) {
			throw new ExceptionCollectDisable();
		}

		String url = Config.collect().url("/o2_collect_assemble/jaxrs/echo");
		ActionResponse actionResponse = ConnectionAction.get(url, null);
		if (Objects.equals(ActionResult.Type.success, actionResponse.getType())) {
			return true;
		} else {
			return false;
		}
	}

	public String url() {
		String url = this.getSslEnable() ? "https://" : "http://";
		url += this.getServer();
		if ((this.getSslEnable() && this.getPort() != 443) || (this.getSslEnable() == false && this.getPort() != 80)) {
			url += ":" + this.getPort();
		}
		return url;
	}

	public String url(String path) {
		if (StringUtils.isNotBlank(path)) {
			if (StringUtils.startsWith(path, "/")) {
				return this.url() + path;
			} else {
				return this.url() + "/" + path;
			}
		}
		return this.url();
	}

	/**
	 * 获取app 打包服务器url地址
	 * @return
	 */
	public String appPackServerUrl() {
		String url = "http://";
		url += this.getAppPackServerHost();
		url += ":" + this.getAppPackServerPort();
		return url;
	}

	/**
	 * app 打包服务的 api 地址拼接
	 * @param path
	 * @return
	 */
	public String appPackServerApi(String path) {
		if (StringUtils.isNotBlank(path)) {
			if (StringUtils.startsWith(path, "/")) {
				return this.appPackServerUrl() + path;
			} else {
				return this.appPackServerUrl() + "/" + path;
			}
		}
		return this.appPackServerUrl();
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_COLLECT);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_COLLECT);
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
