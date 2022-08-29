package com.x.base.core.project.config;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class CenterServer extends ConfigObject {

	private static final long serialVersionUID = 8147826320846595611L;

	private static final Boolean DEFAULT_ENABLE = true;
	private static final Integer DEFAULT_PORT = 80;
	private static final Integer DEFAULT_ORDER = 0;

	public static CenterServer defaultInstance() {
		CenterServer o = new CenterServer();
		o.enable = DEFAULT_ENABLE;
		o.order = DEFAULT_ORDER;
		o.sslEnable = false;
		o.port = DEFAULT_PORT;
		o.httpProtocol = "";
		o.proxyHost = "";
		o.proxyPort = DEFAULT_PORT;
		return o;
	}

	@FieldDescribe("是否启用")
	private Boolean enable;
	@FieldDescribe("center节点顺序,顺序排列0,1,2...")
	private Integer order;
	@FieldDescribe("是否启用ssl传输加密,如果启用将使用config/keystore文件作为密钥文件.使用config/token.json文件中的sslKeyStorePassword字段为密钥密码,sslKeyManagerPassword为管理密码.")
	private Boolean sslEnable;
	@FieldDescribe("端口,center服务器端口,默认20030")
	private Integer port;
	@FieldDescribe("对外http访问协议,http/https")
	private String httpProtocol;
	@FieldDescribe("代理主机,当服务器是通过apache/nginx等代理服务器映射到公网或者通过路由器做端口映射,在这样的情况下需要设置此地址以标明公网访问地址.")
	private String proxyHost;
	@FieldDescribe("代理端口,当服务器是通过apache/nginx等代理服务器映射到公网或者通过路由器做端口映射,在这样的情况下需要设置此地址以标明公网访问端口.")
	private Integer proxyPort;

	public Boolean getEnable() {
		return enable == null ? DEFAULT_ENABLE : this.enable;
	}

	public String getHttpProtocol() {
		return StringUtils.equals("https", this.httpProtocol) ? "https" : "http";
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0 && this.port < 65535) {
			return this.port;
		}
		return DEFAULT_PORT;
	}

	public String getProxyHost() {
		return StringUtils.isNotEmpty(this.proxyHost) ? this.proxyHost : "";
	}

	public Integer getProxyPort() {
		if (null != this.proxyPort && this.proxyPort > 0) {
			return this.proxyPort;
		}
		return this.getPort();
	}

	public Integer getOrder() {
		return order == null ? DEFAULT_ORDER : this.order;
	}

	public void save() throws IOException, URISyntaxException {
		File file = new File(Config.base(), Config.PATH_CONFIG_CENTERSERVER);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setHttpProtocol(String httpProtocol) {
		this.httpProtocol = httpProtocol;
	}

}
