package com.x.base.core.project.config;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageProtocol;
import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;

public class StorageServer extends ConfigObject {

	private static final Integer default_port = 20040;
	private static final String default_passivePorts = "29000-30000";
	private static final String default_prefix = "";
	/** 2的八次方最大的质数 */
	private static final String default_name = "251";
	private static final Boolean default_deepPath = false;

	public static StorageServer defaultInstance() {
		return new StorageServer();
	}

	public StorageServer() {
		this.enable = true;
		this.port = default_port;
		this.sslEnable = false;
		this.name = default_name;
		this.accounts = new CopyOnWriteArrayList<Account>();
		this.prefix = default_prefix;
		this.deepPath = default_deepPath;
	}

	@FieldDescribe("是否启用,对于二进制流文件,比如附件,图片等存储在单独的文件服务器中,可以支持多种文件服务器,默认情况下使用ftp服务器作为文件服务器,每个节点可以启动一个文件服务器以提供高性能.")
	private Boolean enable;
	@FieldDescribe("ftp服务器端口,此端口可以不对外开放,仅有ApplicationServer进行访问,并不直接对用户提供服务.")
	private Integer port;
	@FieldDescribe("是否启用ssl传输加密,如果启用将使用config/keystore文件作为密钥文件.使用config/token.json文件中的sslKeyStorePassword字段为密钥密码,sslKeyManagerPassword为管理密码.")
	private Boolean sslEnable;
	@FieldDescribe("名称,多个节点中不能重名,默认为251.")
	private String name;
	@FieldDescribe("二进制流文件是分多个账号分段存储的,可以单独设置每个分类的存储配置,一般不需要设置.")
	private CopyOnWriteArrayList<Account> accounts;
	@FieldDescribe("ftp传输有主动和被动之分,如果使用了被动传输,设置被动端口范围,默认为29000-30000.")
	private String passivePorts;
	@FieldDescribe("路径前缀.")
	private String prefix;
	@FieldDescribe("使用更深的路径")
	private Boolean deepPath;

	public CopyOnWriteArrayList<Account> getCalculatedAccounts() throws Exception {
		if (ListTools.isEmpty(accounts)) {
			accounts = new CopyOnWriteArrayList<>();
			for (StorageType o : StorageType.values()) {
				Account account = new Account();
				account.setProtocol(StorageProtocol.ftp);
				account.setUsername(o.toString());
				account.setWeight(100);
				account.setPassword(Config.token().getPassword());
				accounts.add(account);
			}
		}
		return accounts;
	}

	public String getPrefix() {
		return StringUtils.isBlank(this.prefix) ? default_prefix : this.prefix;
	}

	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	public String getPassivePorts() {
		return StringUtils.isBlank(this.passivePorts) ? default_passivePorts : this.passivePorts;
	}

	public String getName() {
		return StringUtils.isBlank(this.name) ? default_name : this.name;
	}

	public class Account {

		private StorageProtocol protocol;

		private String username;

		private String password;

		private Integer weight;

		public Integer getWeight() {
			return weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public StorageProtocol getProtocol() {
			return protocol;
		}

		public void setProtocol(StorageProtocol protocol) {
			this.protocol = protocol;
		}

	}

	public Integer getPort() {
		if (null != this.port && this.port > 0) {
			return this.port;
		}
		return default_port;
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setAccounts(CopyOnWriteArrayList<Account> accounts) {
		this.accounts = accounts;
	}

	public CopyOnWriteArrayList<Account> getAccounts() {
		if (null == this.accounts) {
			return new CopyOnWriteArrayList<Account>();
		}
		return accounts;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassivePorts(String passivePorts) {
		this.passivePorts = passivePorts;
	}

}
