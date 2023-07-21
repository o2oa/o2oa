package com.x.base.core.project.config;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class ApplicationServer extends ConfigObject {

	private static final long serialVersionUID = 4182216855396608843L;

	public static ApplicationServer defaultInstance() {
		ApplicationServer o = new ApplicationServer();
		o.enable = true;
		o.port = DEFAULT_PORT;
		o.sslEnable = false;
		o.proxyHost = "";
		o.proxyPort = DEFAULT_PORT;
		o.includes = new CopyOnWriteArrayList<>();
		o.excludes = new CopyOnWriteArrayList<>();
		return o;
	}

	private static final Integer DEFAULT_PORT = 80;

	@FieldDescribe("是否启用")
	private Boolean enable;
	@FieldDescribe("http/https端口,负责向前端提供数据访问接口.默认为20020端口.")
	private Integer port;
	@FieldDescribe("是否启用ssl传输加密,如果启用将使用config/keystore文件作为密钥文件.使用config/token.json文件中的sslKeyStorePassword字段为密钥密码,sslKeyManagerPassword为管理密码.")
	private Boolean sslEnable;
	@FieldDescribe("代理主机,当服务器是通过apache/nginx等代理服务器映射到公网或者通过路由器做端口映射,在这样的情况下需要设置此地址以标明公网访问地址.")
	private String proxyHost;
	@FieldDescribe("代理端口,当服务器是通过apache/nginx等代理服务器映射到公网或者通过路由器做端口映射,在这样的情况下需要设置此地址以标明公网访问端口.")
	private Integer proxyPort;
	@FieldDescribe("承载的应用,在集群环境下可以选择仅承载部分应用以降低服务器负载,可以使用*作为通配符.")
	private CopyOnWriteArrayList<String> includes;
	@FieldDescribe("选择不承载的应用,和includes的值配合使用可以选择或者排除承载的应用,可以使用*作为通配符.")
	private CopyOnWriteArrayList<String> excludes;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0 && this.port < 65535) {
			return this.port;
		}
		return DEFAULT_PORT;
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public String getProxyHost() throws Exception {
		return StringUtils.isNotEmpty(this.proxyHost) ? this.proxyHost : "";
	}

	public Integer getProxyPort() {
		if (null != this.proxyPort && this.proxyPort > 0) {
			return this.proxyPort;
		}
		return this.getPort();
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

	public CopyOnWriteArrayList<String> getIncludes() {
		return includes;
	}

	public void setIncludes(CopyOnWriteArrayList<String> includes) {
		this.includes = includes;
	}

	public CopyOnWriteArrayList<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(CopyOnWriteArrayList<String> excludes) {
		this.excludes = excludes;
	}

}
