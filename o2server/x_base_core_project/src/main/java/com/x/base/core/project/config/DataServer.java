package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.LogLevel;
import com.x.base.core.project.annotation.FieldDescribe;

public class DataServer extends ConfigObject {

	private static final Integer default_tcpPort = 20050;
	private static final Integer default_webPort = 20051;
	private static final Integer default_cacheSize = 512;
	private static final Boolean default_jmxEnable = false;

	public static DataServer defaultInstance() {
		return new DataServer();
	}

	public DataServer() {
		this.enable = true;
		this.tcpPort = default_tcpPort;
		this.webPort = default_webPort;
		this.includes = new ArrayList<>();
		this.excludes = new ArrayList<>();
		this.cacheSize = default_cacheSize;
		this.jmxEnable = default_jmxEnable;
	}

	@FieldDescribe("是否启用,如果没有可用的externalDataSources.json文件,那么默认会在节点中启用本地的H2数据库作为默认的数据库.")
	private Boolean enable;
	@FieldDescribe("H2数据库jdbc连接端口,登录的用户名:sa,密码为xadmin的密码.数据库创建在/o2server/local/repository/data/X.mv.db,一旦数据库文件被创建,那么该数据库的密码被创建.")
	private Integer tcpPort;
	@FieldDescribe("H2数据库web端口,H2提供一个web端的client,此端口为web端client的访问端口.用户名sa,密码为xadmin数据库初始创建的密码.")
	private Integer webPort;
	@FieldDescribe("设置此数据库存储的类,默认情况下存储所有类型,如果需要对每个类进行单独的控制以达到高性能,可以将不同的类存储到不同的节点上提高性能.可以使用通配符*")
	private List<String> includes;
	@FieldDescribe("在此节点上不存储的类,和includes一起设置实际存储的类,可以使用通配符*")
	private List<String> excludes;
	@FieldDescribe("是否启动jmx,如果启用,可以通过本地的jmx客户端进行访问,不支持远程jmx客户端.")
	private Boolean jmxEnable;
	@FieldDescribe("H2数据库缓存大小,设置H2用于作为缓存的内存大小,以M作为单位,这里默认为512M.")
	private Integer cacheSize;
	@FieldDescribe("默认日志级别")
	private LogLevel logLevel = LogLevel.WARN;

	public LogLevel getLogLevel() {
		return this.logLevel == null ? LogLevel.WARN : this.logLevel;
	}

	public Boolean getJmxEnable() {
		return BooleanUtils.isTrue(this.jmxEnable);
	}

	public Integer getCacheSize() {
		return (this.cacheSize == null || this.cacheSize < default_cacheSize) ? default_cacheSize : this.cacheSize;
	}

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

	public void setJmxEnable(Boolean jmxEnable) {
		this.jmxEnable = jmxEnable;
	}

	public void setCacheSize(Integer cacheSize) {
		this.cacheSize = cacheSize;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

}
