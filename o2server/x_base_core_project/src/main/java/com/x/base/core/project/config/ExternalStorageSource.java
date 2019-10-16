package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.entity.StorageProtocol;
import com.x.base.core.project.annotation.FieldDescribe;

public class ExternalStorageSource extends ConfigObject {

	public ExternalStorageSource() {
		this.protocol = default_protocol;
		this.username = default_username;
		this.password = default_password;
		this.host = default_host;
		this.port = default_port;
		this.prefix = default_prefix;
		this.enable = default_enable;
		this.weight = default_weight;
		this.name = default_name;
		this.deepPath = default_deepPath;

	}

	public static ExternalStorageSource defaultInstance() {
		return new ExternalStorageSource();

	}

	public static final StorageProtocol default_protocol = StorageProtocol.webdav;
	public static final String default_username = "admin";
	public static final String default_password = "admin";
	public static final String default_host = "127.0.0.1";
	public static final Integer default_port = 8080;
	public static final String default_prefix = "";
	public static final Integer default_weight = 100;
	public static final Boolean default_enable = true;
	public static final String default_name = "251";
	public static final Boolean default_deepPath = false;

	@FieldDescribe("协议,可选值ftp,webdav")
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
		return password;
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