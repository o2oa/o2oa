package com.x.base.core.project.config;

import java.io.File;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class CenterServer extends ConfigObject {

	private static final Integer default_port = 20030;
	private static final Integer default_scanInterval = 0;
	private static final Boolean default_configApiEnable = true;

	public static CenterServer defaultInstance() {
		return new CenterServer();
	}

	public CenterServer() {
		this.sslEnable = false;
		this.redeploy = true;
		// this.host = "";
		this.port = default_port;
		this.httpProtocol = "";
		this.proxyHost = "";
		this.proxyPort = default_port;
		this.scanInterval = default_scanInterval;
		this.configApiEnable = default_configApiEnable;
	}

	@FieldDescribe("是否启用")
	private Boolean enable;
	@FieldDescribe("是否启用ssl传输加密,如果启用将使用config/keystore文件作为密钥文件.使用config/token.json文件中的sslKeyStorePassword字段为密钥密码,sslKeyManagerPassword为管理密码.")
	private Boolean sslEnable;
	@FieldDescribe("每次启动是否重新部署所有应用.")
	private Boolean redeploy;
	@FieldDescribe("端口,center服务器端口,默认20030")
	private Integer port;
	@FieldDescribe("对外http访问协议,http/https")
	private String httpProtocol;
	@FieldDescribe("代理主机,当服务器是通过apache/eginx等代理服务器映射到公网或者通过路由器做端口映射,在这样的情况下需要设置此地址以标明公网访问地址.")
	private String proxyHost;
	@FieldDescribe("代理端口,当服务器是通过apache/eginx等代理服务器映射到公网或者通过路由器做端口映射,在这样的情况下需要设置此地址以标明公网访问端口.")
	private Integer proxyPort;
	@FieldDescribe("重新扫描war包时间间隔(秒)")
	private Integer scanInterval;
	@FieldDescribe("其他参数")
	private LinkedHashMap<String, Object> config;
	@FieldDescribe("允许通过Api修改config")
	private Boolean configApiEnable;

	public Boolean getConfigApiEnable() {
		return configApiEnable == null ? default_configApiEnable : this.configApiEnable;
	}

	public String getHttpProtocol() {
		return StringUtils.equals("https", this.httpProtocol) ? "https" : "http";
	}

	public Integer getScanInterval() {
		if (null != this.scanInterval && this.scanInterval > 0) {
			return this.scanInterval;
		}
		return default_scanInterval;
	}

	public Boolean getRedeploy() {
		return BooleanUtils.isTrue(this.redeploy);
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0 && this.port < 65535) {
			return this.port;
		}
		return default_port;
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

	public LinkedHashMap<String, Object> getConfig() {
		if (null == this.config) {
			return new LinkedHashMap<String, Object>();
		}
		return this.config;
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

	public void setScanInterval(Integer scanInterval) {
		this.scanInterval = scanInterval;
	}

	public void setRedeploy(Boolean redeploy) {
		this.redeploy = redeploy;
	}

	public void setConfig(LinkedHashMap<String, Object> config) {
		this.config = config;
	}

	public void setHttpProtocol(String httpProtocol) {
		this.httpProtocol = httpProtocol;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_CENTERSERVER);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public void setConfigApiEnable(Boolean configApiEnable) {
		this.configApiEnable = configApiEnable;
	}

}
