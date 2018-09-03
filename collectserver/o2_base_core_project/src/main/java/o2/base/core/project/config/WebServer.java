package o2.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.Host;

public class WebServer extends GsonPropertyObject {

	public static WebServer defaultInstance() {
		return new WebServer();
	}

	public WebServer() {
		this.enable = true;
		this.host = "";
		this.port = default_port;
		this.sslEnable = false;
		this.proxyHost = "";
		this.proxyPort = default_port;
	}

	private static final Integer default_port = 20060;

	private Boolean enable;
	private Integer port;
	private String host;
	private Boolean sslEnable;
	private String proxyHost;
	private Integer proxyPort;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0) {
			return this.port;
		}
		return default_port;
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public String getProxyHost() {
		if (StringUtils.isNotEmpty(this.proxyHost)) {
			return this.proxyHost;
		}
		return Host.ROLLBACK_IPV4;
	}

	public Integer getProxyPort() {
		if (null != this.proxyPort && this.proxyPort > 0) {
			return this.proxyPort;
		}
		return default_port;
	}

	public String getHost() {
		if (StringUtils.isNotEmpty(this.host)) {
			return this.host;
		}
		return "";
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

	public void setHost(String host) {
		this.host = host;
	}

}
