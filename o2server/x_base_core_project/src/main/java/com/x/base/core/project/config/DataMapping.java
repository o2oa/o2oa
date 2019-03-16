package com.x.base.core.project.config;

import com.x.base.core.container.LogLevel;
import com.x.base.core.project.gson.GsonPropertyObject;

public class DataMapping extends GsonPropertyObject {

	private String url;
	private String username;
	private String password;
	private LogLevel toolLevel = LogLevel.WARN;
	private LogLevel runtimeLevel = LogLevel.WARN;
	private LogLevel dataCacheLevel = LogLevel.WARN;
	private LogLevel metaDataLevel = LogLevel.WARN;
	private LogLevel enhanceLevel = LogLevel.WARN;
	private LogLevel queryLevel = LogLevel.WARN;
	private LogLevel sqlLevel = LogLevel.WARN;
	private LogLevel jdbcLevel = LogLevel.ERROR;

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