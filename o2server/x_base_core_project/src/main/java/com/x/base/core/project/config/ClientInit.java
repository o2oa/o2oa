package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class ClientInit extends ConfigObject {

	@FieldDescribe("是否启用.")
	private Boolean enable;

	@FieldDescribe("center节点信息.")
	private List<CenterAddress> center;

	@FieldDescribe("网页底部说明.")
	private String footer = "";

	@FieldDescribe("网页头部说明.")
	private String title = "";

	@FieldDescribe("APP使用协议,auto,http,https.")
	private String app_protocol = "auto";

	@FieldDescribe("登录页面配置.")
	private LoginPage loginPage;

	@FieldDescribe("是否启用webSocket")
	private Boolean webSocketEnable;

	public ClientInit() {
		this.enable = DEFAULT_ENABLE;
		this.center = new ArrayList<>();
		this.loginPage = new LoginPage();
	}

	public static ClientInit defaultInstance() {
		return new ClientInit();
	}

	public static final Boolean DEFAULT_ENABLE = false;

	public static class LoginPage extends ConfigObject {

		@FieldDescribe("是否启用定制的登录页面.")
		private Boolean enable;
		@FieldDescribe("登录的门户.")
		private String portal;
		@FieldDescribe("登录页面.")
		private String page;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getPortal() {
			return portal;
		}

		public void setPortal(String portal) {
			this.portal = portal;
		}

		public String getPage() {
			return page;
		}

		public void setPage(String page) {
			this.page = page;
		}
	}

	public static class CenterAddress extends ConfigObject {

		private String port;

		private String host;

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public List<CenterAddress> getCenter() {
		return center;
	}

	public void setCenter(List<CenterAddress> center) {
		this.center = center;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getApp_protocol() {
		return app_protocol;
	}

	public void setApp_protocol(String app_protocol) {
		this.app_protocol = app_protocol;
	}

	public LoginPage getLoginPage() {
		return loginPage;
	}

	public void setLoginPage(LoginPage loginPage) {
		this.loginPage = loginPage;
	}

	public Boolean getWebSocketEnable() {
		return webSocketEnable;
	}

	public void setWebSocketEnable(Boolean webSocketEnable) {
		this.webSocketEnable = webSocketEnable;
	}

}