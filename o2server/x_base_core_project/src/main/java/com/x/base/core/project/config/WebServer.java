package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class WebServer extends ConfigObject {

	public static WebServer defaultInstance() {
		return new WebServer();
	}

	public WebServer() {
		this.enable = true;
		this.port = null;
		this.sslEnable = false;
		this.proxyHost = "";
		this.proxyPort = null;
		this.weight = default_weight;
		this.dirAllowed = default_dirAllowed;
	}

	private static final Integer default_http_port = 80;
	private static final Integer default_https_port = 443;
	private static final Integer default_weight = 100;
	private static final Boolean default_dirAllowed = false;

	@FieldDescribe("是否启用")
	private Boolean enable;
	@FieldDescribe("http/https端口,用户输入网址后实际访问的第一个端口.http协议默认为80端口,https默认为443端口.")
	private Integer port;
	@FieldDescribe("是否启用ssl传输加密,如果启用将使用config/keystore文件作为密钥文件.使用config/token.json文件中的sslKeyStorePassword字段为密钥密码,sslKeyManagerPassword为管理密码.")
	private Boolean sslEnable;
	@FieldDescribe("代理主机,当服务器是通过apache/nginx等代理服务器映射到公网或者通过路由器做端口映射,在这样的情况下需要设置此地址以标明公网访问地址.")
	private String proxyHost;
	@FieldDescribe("代理端口,当服务器是通过apache/nginx等代理服务器映射到公网或者通过路由器做端口映射,在这样的情况下需要设置此地址以标明公网访问端口.")
	private Integer proxyPort;
	@FieldDescribe("设置权重.当前没有作用,")
	private Integer weight;
	@FieldDescribe("允许浏览目录,")
	private Boolean dirAllowed;

	public Boolean getDirAllowed() {
		return dirAllowed == null ? default_dirAllowed : dirAllowed;
	}

	public Integer getWeight() {
		if (weight == null || weight < 0) {
			return default_weight;
		}
		return weight;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public Integer getPort() {
		if ((null != this.port) && (this.port > 0) && (this.port < 65535)) {
			return this.port;
		} else {
			if (this.getSslEnable()) {
				return default_https_port;
			} else {
				return default_http_port;
			}

		}
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public String getProxyHost() throws Exception {
		return StringUtils.isNotEmpty(this.proxyHost) ? this.proxyHost : "";
	}

	public Integer getProxyPort() {
		if (null != this.proxyPort && this.proxyPort > 0 && this.proxyPort < 65535) {
			return this.proxyPort;
		} else {
			return this.getPort();
		}
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	// public void setHost(String host) {
	// this.host = host;
	// }

}
