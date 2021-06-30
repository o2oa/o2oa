package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageProtocol;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.Crypto;

public class ExternalStorageSource extends ConfigObject {

	private static final long serialVersionUID = 5926439816241094368L;
	
	// 无需保存
	private transient String _password;

	public ExternalStorageSource() {
		this.protocol = DEFAULT_PROTOCOL;
		this.username = DEFAULT_USERNAME;
		this.password = DEFAULT_PASSWORD;
		this.host = DEFAULT_HOST;
		this.port = DEFAULT_PORT;
		this.prefix = DEFAULT_PREFIX;
		this.enable = DEFAULT_ENABLE;
		this.weight = DEFAULT_WEIGHT;
		this.name = DEFAULT_NAME;
		this.deepPath = DEFAULT_DEEPPATH;
	}

	public static ExternalStorageSource defaultInstance() {
		return new ExternalStorageSource();

	}

	public static final StorageProtocol DEFAULT_PROTOCOL = StorageProtocol.webdav;
	public static final String DEFAULT_USERNAME = "admin";
	public static final String DEFAULT_PASSWORD = "admin";
	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final Integer DEFAULT_PORT = 8080;
	public static final String DEFAULT_PREFIX = "";
	public static final Integer DEFAULT_WEIGHT = 100;
	public static final Boolean DEFAULT_ENABLE = true;
	public static final String DEFAULT_NAME = "251";
	public static final Boolean DEFAULT_DEEPPATH = false;

	@FieldDescribe("协议,可选值ftp,webdav...")
	private StorageProtocol protocol;
	@FieldDescribe("登录用户名.")
	private String username;
	@FieldDescribe("登录密码.")
	private String password;
	@FieldDescribe("主机地址.")
	private String host;
	@FieldDescribe("端口.")
	private Integer port;
	@FieldDescribe("前缀路径.")
	private String prefix;
	@FieldDescribe("是否启用")
	private Boolean enable;
	@FieldDescribe("设置权重.")
	private Integer weight;
	@FieldDescribe("存储节点名,对应存储名称,谨慎修改.")
	private String name;
	@FieldDescribe("是否使用更深的路径.")
	private Boolean deepPath;

	public StorageProtocol getProtocol() {
		return protocol;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		if (StringUtils.isEmpty(this._password)) {
			this._password = Crypto.plainText(this.password);
		}
		return this._password;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getPrefix() {
		return prefix;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public Integer getWeight() {
		return weight;
	}

	public String getName() {
		return name;
	}

	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

}