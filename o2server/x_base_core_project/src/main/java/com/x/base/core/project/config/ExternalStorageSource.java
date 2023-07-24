package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageProtocol;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.Crypto;

/**
 * 第三方附件存储配置
 * 
 * @author sword
 */
public class ExternalStorageSource extends ConfigObject {

	private static final long serialVersionUID = 5926439816241094368L;

	// 无需保存
	private transient String _password;

	public static ExternalStorageSource defaultInstance() {
		ExternalStorageSource o = new ExternalStorageSource();
		o.protocol = DEFAULT_PROTOCOL;
		o.username = DEFAULT_USERNAME;
		o.password = DEFAULT_PASSWORD;
		o.host = DEFAULT_HOST;
		o.port = DEFAULT_PORT;
		o.prefix = DEFAULT_PREFIX;
		o.enable = DEFAULT_ENABLE;
		o.weight = DEFAULT_WEIGHT;
		o.name = DEFAULT_NAME;
		o.deepPath = DEFAULT_DEEPPATH;
		o.store = DEFAULT_STORE;
		return o;
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
	public static final String DEFAULT_STORE = "";

	@FieldDescribe("协议,可选值ftp、sftp、webdav、file、hdfs、ali")
	private StorageProtocol protocol;
	@FieldDescribe("登录用户名.")
	private String username;
	@FieldDescribe("登录密码.")
	private String password;
	@FieldDescribe("主机地址或阿里云endpoint.")
	private String host;
	@FieldDescribe("端口.")
	private Integer port;
	@FieldDescribe("前缀路径.")
	private String prefix;
	@FieldDescribe("是否启用")
	private Boolean enable;
	@FieldDescribe("设置权重.")
	private Integer weight;
	@FieldDescribe("存储节点名,对应存储名称,阿里云为bucket(桶)名称.")
	private String name;
	@FieldDescribe("是否使用更深的路径.")
	private Boolean deepPath;
	@FieldDescribe("配置名称.")
	private String store;

	public String getStore() {
		return StringUtils.isBlank(this.store) ? DEFAULT_STORE : this.store;
	}

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

	public void setProtocol(StorageProtocol protocol) {
		this.protocol = protocol;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStore(String store) {
		this.store = store;
	}

}
