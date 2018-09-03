package com.x.base.core.project.config;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageProtocol;
import com.x.base.core.entity.StorageType;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class StorageServer extends GsonPropertyObject {

	private static final Integer default_port = 20040;
	private static final String default_passivePorts = "29000-30000";
	/** 2的八次方最小的质数 */
	private static final String default_name = "251";

	public static StorageServer defaultInstance() {
		return new StorageServer();
	}

	public StorageServer() {
		this.enable = true;
		this.port = default_port;
		this.sslEnable = false;
		this.name = default_name;
		this.accounts = new CopyOnWriteArrayList<Account>();
	}

	private Boolean enable;
	private Integer port;
	private Boolean sslEnable;
	private String name;
	private CopyOnWriteArrayList<Account> accounts;
	private String passivePorts;

	public CopyOnWriteArrayList<Account> getCalculatedAccounts() throws Exception {
		if (ListTools.isEmpty(accounts)) {
			accounts = new CopyOnWriteArrayList<>();
			for (StorageType o : StorageType.values()) {
				Account account = new Account();
				account.setProtocol(StorageProtocol.ftp);
				// account.setName(o.toString());
				account.setUsername(o.toString());
				account.setWeight(100);
				account.setPassword(Config.token().getPassword());
				accounts.add(account);
			}
		}
		return accounts;
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
