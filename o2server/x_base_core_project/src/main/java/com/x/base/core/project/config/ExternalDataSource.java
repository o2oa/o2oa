package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.LogLevel;
import com.x.base.core.project.annotation.FieldDescribe;

public class ExternalDataSource extends ConfigObject {

	public ExternalDataSource() {
		this.enable = false;
		this.url = "";
		this.username = "";
		this.password = "";
		this.includes = new ArrayList<>();
		this.excludes = new ArrayList<>();
		this.toolLevel = LogLevel.ERROR;
		this.runtimeLevel = LogLevel.ERROR;
		this.dataCacheLevel = LogLevel.ERROR;
		this.metaDataLevel = LogLevel.ERROR;
		this.enhanceLevel = LogLevel.ERROR;
		this.queryLevel = LogLevel.ERROR;
		this.sqlLevel = LogLevel.ERROR;
		this.jdbcLevel = LogLevel.ERROR;
	}

	public static ExternalDataSource defaultInstance() {
		return new ExternalDataSource();
	}

	@FieldDescribe("是否启用,可以使用切片方式启用多个数据以提高性能,如果启用多个数据库,那么必须是相同类型的,不能混用,且用户名密码必须一致.")
	private Boolean enable;
	@FieldDescribe("jdbc连接地址")
	private String url;
	@FieldDescribe("数据库jdbc连接用户名")
	private String username;
	@FieldDescribe("数据库jdbc连接密码")
	private String password;
	@FieldDescribe("设置此数据库存储的类,默认情况下存储所有类型,如果需要对每个类进行单独的控制以达到高性能,可以将不同的类存储到不同的节点上提高性能.可以使用通配符*")
	private List<String> includes;
	@FieldDescribe("在此节点上不存储的类,和includes一起设置实际存储的类,可以使用通配符*")
	private List<String> excludes;
	@FieldDescribe("tool日志级别")
	private LogLevel toolLevel = LogLevel.ERROR;
	@FieldDescribe("runtime日志级别")
	private LogLevel runtimeLevel = LogLevel.ERROR;
	@FieldDescribe("dataCache日志级别")
	private LogLevel dataCacheLevel = LogLevel.ERROR;
	@FieldDescribe("metaData日志级别")
	private LogLevel metaDataLevel = LogLevel.ERROR;
	@FieldDescribe("enhance日志级别")
	private LogLevel enhanceLevel = LogLevel.ERROR;
	@FieldDescribe("query日志级别")
	private LogLevel queryLevel = LogLevel.ERROR;
	@FieldDescribe("sql日志级别")
	private LogLevel sqlLevel = LogLevel.ERROR;
	@FieldDescribe("jdbc日志级别")
	private LogLevel jdbcLevel = LogLevel.ERROR;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public LogLevel getToolLevel() {
		return toolLevel;
	}

	public void setToolLevel(LogLevel toolLevel) {
		this.toolLevel = toolLevel;
	}

	public LogLevel getRuntimeLevel() {
		return runtimeLevel;
	}

	public void setRuntimeLevel(LogLevel runtimeLevel) {
		this.runtimeLevel = runtimeLevel;
	}

	public LogLevel getDataCacheLevel() {
		return dataCacheLevel;
	}

	public void setDataCacheLevel(LogLevel dataCacheLevel) {
		this.dataCacheLevel = dataCacheLevel;
	}

	public LogLevel getMetaDataLevel() {
		return metaDataLevel;
	}

	public void setMetaDataLevel(LogLevel metaDataLevel) {
		this.metaDataLevel = metaDataLevel;
	}

	public LogLevel getEnhanceLevel() {
		return enhanceLevel;
	}

	public void setEnhanceLevel(LogLevel enhanceLevel) {
		this.enhanceLevel = enhanceLevel;
	}

	public LogLevel getQueryLevel() {
		return queryLevel;
	}

	public void setQueryLevel(LogLevel queryLevel) {
		this.queryLevel = queryLevel;
	}

	public LogLevel getSqlLevel() {
		return sqlLevel;
	}

	public void setSqlLevel(LogLevel sqlLevel) {
		this.sqlLevel = sqlLevel;
	}

	public LogLevel getJdbcLevel() {
		return jdbcLevel;
	}

	public void setJdbcLevel(LogLevel jdbcLevel) {
		this.jdbcLevel = jdbcLevel;
	}

}
