package com.x.base.core.project.config;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionCollectConnectError;
import com.x.base.core.project.exception.ExceptionCollectDisable;
import com.x.base.core.project.exception.ExceptionCollectValidateFailure;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class Collect extends ConfigObject {

	private static String Default_title = "企业办公平台";
	private static String Default_footer = "www.o2oa.net";
	private static String Default_name = "www.o2oa.net";
	private static String Default_appUrl = "http://www.pgyer.com/ZhiHe_android";
	private static String Default_server = "collect.o2oa.net";
	private static Integer Default_port = 20080;

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

	public boolean validate()
			throws ExceptionCollectConnectError, ExceptionCollectDisable, ExceptionCollectValidateFailure, Exception {

		if (Config.collect().getEnable()) {
			throw new ExceptionCollectDisable();
		}

		try {
			URL url = new URL(this.url("/o2_collect_assemble/jaxrs/collect/validate"));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty(ConnectionAction.Access_Control_Allow_Credentials,
					ConnectionAction.Access_Control_Allow_Credentials_Value);
			connection.setRequestProperty(ConnectionAction.Access_Control_Allow_Headers,
					ConnectionAction.Access_Control_Allow_Headers_Value);
			connection.setRequestProperty(ConnectionAction.Access_Control_Allow_Methods,
					ConnectionAction.Access_Control_Allow_Methods_Value);
			connection.setRequestProperty(ConnectionAction.Cache_Control, ConnectionAction.Cache_Control_Value);
			connection.setRequestProperty(ConnectionAction.Content_Type, ConnectionAction.Content_Type_Value);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();
			try (OutputStream output = connection.getOutputStream()) {
				String req = "{\"name\":\"" + Config.collect().getName() + "\",\"password\":\""
						+ Config.collect().getPassword() + "\"}";
				IOUtils.write(req, output, StandardCharsets.UTF_8);
			}
			if (200 != connection.getResponseCode()) {
				throw new ExceptionCollectValidateFailure();
			}
			try (InputStream input = connection.getInputStream()) {
				byte[] buffer = IOUtils.toByteArray(input);
				String value = new String(buffer, DefaultCharset.name);
				if (!StringUtils.contains(value, "success")) {
					throw new ExceptionCollectValidateFailure();
				}
			}
			connection.disconnect();
		} catch (Exception e) {
			throw new ExceptionCollectConnectError();
		}
		return true;
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

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_COLLECT);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setKey(String key) {
		this.key = key;
	}

}