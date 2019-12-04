package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.LogLevel;
import com.x.base.core.container.factory.SlicePropertiesBuilder;
import com.x.base.core.project.annotation.FieldDescribe;

public class ExternalDataSource extends ConfigObject {

	public ExternalDataSource() {
		this.enable = false;
		this.url = "";
		this.username = "";
		this.password = "";
		this.includes = new ArrayList<>();
		this.excludes = new ArrayList<>();
		this.driverClassName = "";
		this.dictionary = "";
		this.maxTotal = DEFAULT_MAXTOTAL;
		this.maxIdle = DEFAULT_MAXIDLE;
		this.logLevel = LogLevel.WARN;
		this.statEnable = DEFAULT_STATENABLE;
		this.statFilter = DEFAULT_STATFILTER;
		this.slowSqlMillis = DEFAULT_SLOWSQLMILLIS;
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
	@FieldDescribe("数据库驱动类名")
	private String driverClassName;
	@FieldDescribe("方言")
	private String dictionary;
	@FieldDescribe("最大使用连接数")
	private Integer maxTotal;
	@FieldDescribe("最大空闲连接数")
	private Integer maxIdle;
	@FieldDescribe("启用统计,默认启用")
	private Boolean statEnable;
	@FieldDescribe("统计方式配置,默认mergeStat")
	private String statFilter;
	@FieldDescribe("执行缓慢sql毫秒数,默认2000毫秒,执行缓慢的sql将被单独记录.")
	private Integer slowSqlMillis;

	@FieldDescribe("设置此数据库存储的类,默认情况下存储所有类型,如果需要对每个类进行单独的控制以达到高性能,可以将不同的类存储到不同的节点上提高性能.可以使用通配符*")
	private List<String> includes;
	@FieldDescribe("在此节点上不存储的类,和includes一起设置实际存储的类,可以使用通配符*")
	private List<String> excludes;
	@FieldDescribe("默认日志级别")
	private LogLevel logLevel = LogLevel.WARN;

	public static final Integer DEFAULT_MAXTOTAL = 50;

	public static final Integer DEFAULT_MAXIDLE = 0;

	public static final Boolean DEFAULT_STATENABLE = true;

	public static final String DEFAULT_STATFILTER = "mergeStat";

	public static final Integer DEFAULT_SLOWSQLMILLIS = 2000;

	public LogLevel getLogLevel() {
		return this.logLevel == null ? LogLevel.WARN : this.logLevel;
	}

	public String getDriverClassName() throws Exception {
		return StringUtils.isEmpty(this.driverClassName) ? SlicePropertiesBuilder.driverClassNameOfUrl(this.url)
				: this.driverClassName;
	}

	public String getDictionary() throws Exception {
		return StringUtils.isEmpty(this.dictionary) ? SlicePropertiesBuilder.dictionaryOfUrl(this.url)
				: this.dictionary;
	}

	public Integer getSlowSqlMillis() {
		return (null == this.slowSqlMillis || this.slowSqlMillis < 1) ? DEFAULT_SLOWSQLMILLIS : this.slowSqlMillis;
	}

	public String getStatFilter() {
		return StringUtils.isEmpty(this.statFilter) ? DEFAULT_STATFILTER : this.statFilter;
	}

	public Boolean getStatEnable() {
		return BooleanUtils.isNotFalse(this.statEnable);
	}

	public Integer getMaxIdle() {
		if ((this.maxIdle == null) || (this.maxIdle < 1)) {
			return DEFAULT_MAXIDLE;
		} else {
			return this.maxTotal;
		}
	}

	public Integer getMaxTotal() {
		if ((this.maxTotal == null) || (this.maxTotal < 0)) {
			return DEFAULT_MAXTOTAL;
		} else {
			return this.maxTotal;
		}
	}

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

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public void setDictionary(String dictionary) {
		this.dictionary = dictionary;
	}

	public void setMaxTotal(Integer maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

}
