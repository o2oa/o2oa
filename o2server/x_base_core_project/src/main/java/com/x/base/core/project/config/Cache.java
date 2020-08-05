package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.NumberTools;

public class Cache extends ConfigObject {

	public static final String TYPE_EHCACHE = "ehcache";
	public static final String TYPE_REDIS = "redis";

	public static Cache defaultInstance() {
		return new Cache();
	}

	public Cache() {
		this.type = TYPE_EHCACHE;
		this.redis = Redis.defaultInstance();
		this.ehcache = Ehcache.defaultInstance();
	}

	public String getType() {
		return StringUtils.equals(this.type, TYPE_EHCACHE) ? TYPE_EHCACHE : TYPE_REDIS;
	}

	@FieldDescribe("缓存类型:ehcache,type")
	private String type;

	@FieldDescribe("redis配置")
	private Redis redis;

	@FieldDescribe("ehcache配置")
	private Ehcache ehcache;

	public Redis getRedis() {
		return this.redis == null ? new Redis() : this.redis;
	}

	public Ehcache getEhcache() {
		return this.redis == null ? new Ehcache() : this.ehcache;
	}

	public static class Redis extends ConfigObject {
		private static final long serialVersionUID = 1L;
		public static final String DEFAULT_HOST = "127.0.0.1";
		public static final Integer DEFAULT_PORT = 6379;
		public static final String DEFAULT_USER = "";
		public static final String DEFAULT_PASSWORD = "";
		public static final Integer DEFAULT_CONNECTIONTIMEOUT = 3000;
		public static final Integer DEFAULT_SOCKETTIMEOUT = 3000;
		public static final Boolean DEFAULT_SSLENABLE = false;
		public static final Boolean DEFAULT_JMXENABLE = false;
		public static final Integer DEFAULT_INDEX = 0;

		public static Redis defaultInstance() {
			return new Redis();
		}

		public Redis() {
			this.host = DEFAULT_HOST;
			this.port = DEFAULT_PORT;
			this.connectionTimeout = DEFAULT_CONNECTIONTIMEOUT;
			this.socketTimeout = DEFAULT_SOCKETTIMEOUT;
			this.sslEnable = DEFAULT_SSLENABLE;
			this.user = DEFAULT_USER;
			this.password = DEFAULT_PASSWORD;
			this.index = DEFAULT_INDEX;

		}

		@FieldDescribe("redis服务器地址")
		private String host;

		@FieldDescribe("redis服务器端口")
		private Integer port;

		@FieldDescribe("认证用户")
		private String user;

		@FieldDescribe("认证口令")
		private String password;

		@FieldDescribe("连接等待时间")
		private Integer connectionTimeout;

		@FieldDescribe("response返回等待时间")
		private Integer socketTimeout;

		@FieldDescribe("是否启用ssl")
		private Boolean sslEnable;

		@FieldDescribe("数据库编号")
		private Integer index;

		public String getHost() {
			return StringUtils.isBlank(this.host) ? DEFAULT_HOST : this.host;
		}

		public Integer getPort() {
			return NumberTools.nullOrLessThan(this.port, 0) ? DEFAULT_PORT : this.port;
		}

		public Integer getConnectionTimeout() {
			return NumberTools.nullOrLessThan(this.connectionTimeout, 0) ? DEFAULT_CONNECTIONTIMEOUT
					: this.connectionTimeout;
		}

		public Integer getSocketTimeout() {
			return NumberTools.nullOrLessThan(this.socketTimeout, 0) ? DEFAULT_SOCKETTIMEOUT : this.socketTimeout;
		}

		public Boolean getSslEnable() {
			return BooleanUtils.isTrue(sslEnable);
		}

		public String getUser() {
			return StringUtils.isBlank(this.user) ? DEFAULT_USER : this.user;
		}

		public String getPassword() {
			return StringUtils.isBlank(this.password) ? DEFAULT_PASSWORD : this.password;
		}

		public Integer getIndex() {
			return NumberTools.nullOrLessThan(this.index, 0) ? DEFAULT_INDEX : this.index;
		}

	}

	public static class Ehcache extends ConfigObject {
		private static final long serialVersionUID = 1L;
		public static final Boolean DEFAULT_JMXENABLE = false;

		public static Ehcache defaultInstance() {
			return new Ehcache();
		}

		public Ehcache() {
			this.jmxEnable = DEFAULT_JMXENABLE;
		}

		@FieldDescribe("是否启用jmx")
		private Boolean jmxEnable;

		public Boolean getJmxEnable() {
			return BooleanUtils.isTrue(jmxEnable);
		}
	}

}