package com.x.common.core.application;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.utils.net.Host;
import com.x.common.core.application.component.x_program_center;

public class Center {

	private String host;
	private Integer port;
	private String cipher;

	public String getUrlRoot() throws Exception {
		String url = "http://" + (StringUtils.isNotEmpty(host) ? host : Host.ROLLBACK_IPV4)
				+ ((port == null) ? ":30080" : (port == 80 ? "" : (":" + port))) + "/"
				+ x_program_center.class.getSimpleName() + "/jaxrs/";
		return url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getCipher() {
		return cipher;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

}