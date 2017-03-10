package com.x.base.core.project.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.gson.GsonPropertyObject;

public class DataServer extends GsonPropertyObject {

	private static final Integer default_tcpPort = 20050;
	private static final Integer default_webPort = 20051;

	public static DataServer defaultInstance() {
		return new DataServer();
	}

	public DataServer() {
		this.enable = true;
		this.tcpPort = default_tcpPort;
		this.webPort = default_webPort;
		this.includes = new ArrayList<>();
		this.excludes = new ArrayList<>();
	}

	private Boolean enable;
	private Integer tcpPort;
	private Integer webPort;
	private List<String> includes;
	private List<String> excludes;

	// /**
	// *
	// * @return
	// * @throws Exception
	// * 计算后的密码，如果没有设置那么使用Administrator的密码
	// *
	// */
	// public String getCalculatedPassword() throws Exception {
	// if (StringUtils.isEmpty(password)) {
	// return Config.token().getPassword();
	// }
	// return password;
	// }

	public Integer getTcpPort() {
		if (null != this.tcpPort && this.tcpPort > 0) {
			return this.tcpPort;
		}
		return default_tcpPort;
	}

	public Integer getWebPort() {
		if (null != this.webPort && this.webPort > 0) {
			return this.webPort;
		}
		return default_webPort;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public List<String> getIncludes() {
		if (null != this.includes) {
			return this.includes;
		}
		return new ArrayList<String>();
	}

	public List<String> getExcludes() {
		if (null != this.excludes) {
			return this.excludes;
		}
		return new ArrayList<String>();
	}

	public void setTcpPort(Integer tcpPort) {
		this.tcpPort = tcpPort;
	}

	public void setWebPort(Integer webPort) {
		this.webPort = webPort;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

}
