package com.x.base.core.project.config;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DefaultCharset;

public class Token extends ConfigObject {

	private static final String surfix = "o2platform";

	// private static final String icon_initialManager =
	// "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\u003d\u003d";

	public static final String defaultInitialManager = "xadmin";

	public static final String defaultInitialManagerDistinguishedName = "xadmin@o2oa@P";

	public static final String initPassword = "o2";

	public static final String defaultSslKeyStorePassword = "12345678";
	public static final String defaultSslKeyManagerPassword = "12345678";

	public static Token defaultInstance() {
		Token o = new Token();
		return o;
	}

	/** 加密用的key,用于加密口令 */
	@FieldDescribe("加密用口令的密钥,修改后会导致用户口令验证失败.")
	private String key;

	/** 管理员密码,用于管理员登录,内部服务器口令以及http加密 */
	@FieldDescribe("初始管理员密码,用于内部数据库和FTP文件服务器,以及http的token加密.")
	private String password;

	@FieldDescribe("ssl密码")
	private String sslKeyStorePassword;

	@FieldDescribe("ssl管理密码")
	private String sslKeyManagerPassword;

	/** 用于Sso登录的配置 */
	@FieldDescribe("sso登录配置")
	private List<Sso> ssos = new ArrayList<>();

	/** 初始管理员名称 */
	@FieldDescribe("初始管理员名称,目前不可更改.")
	private String initialManager;

	/** 初始管理员名称DistinguishedName */
	@FieldDescribe("初始管理员DistinguishedName,不可更改.")
	private String initialManagerDistinguishedName;

	@FieldDescribe("oauth单点登录配置")
	private List<Oauth> oauths = new ArrayList<>();

	@FieldDescribe("微信配置")
	private Qyweixin qyweixin;

	@FieldDescribe("钉钉配置")
	private Dingding dingding;

	/** 前面的代码是 key+surfix 结果是nullo2platform */
	public String getKey() {
		String val = Objects.toString(key, "") + surfix;
		return StringUtils.substring(val, 0, 8);
	}

	public void setKey(String key) {
		if (StringUtils.equals(key, StringUtils.substring(surfix, 0, 8))) {
			this.key = null;
		} else {
			this.key = key;
		}
	}

	public String getCipher() {
		return this.getPassword() + surfix;
	}

	public String getPassword() {
		return StringUtils.isEmpty(this.password) ? initPassword : this.password;
	}

	public void setPassword(String password) {
		if (StringUtils.equals(password, initPassword)) {
			this.password = null;
		} else {
			this.password = password;
		}
	}

	public String getInitialManager() {
		return StringUtils.isEmpty(this.initialManager) ? defaultInitialManager : this.initialManager;
	}

	public void setInitialManager(String initialManager) {
		if (StringUtils.equals(initialManager, defaultInitialManager)) {
			this.initialManager = null;
		} else {
			this.initialManager = initialManager;
		}
	}

	public String getInitialManagerDistinguishedName() {
		return StringUtils.isEmpty(this.initialManagerDistinguishedName) ? defaultInitialManagerDistinguishedName
				: this.initialManagerDistinguishedName;
	}

	public void setInitialManagerDistinguishedName(String initialManagerDistinguishedName) {
		if (StringUtils.equals(initialManagerDistinguishedName, defaultInitialManagerDistinguishedName)) {
			this.initialManager = null;
		} else {
			this.initialManagerDistinguishedName = initialManagerDistinguishedName;
		}
	}

	public String getSslKeyStorePassword() {
		return StringUtils.isEmpty(this.sslKeyStorePassword) ? defaultSslKeyStorePassword : this.sslKeyStorePassword;
	}

	public void setSslKeyStorePassword(String sslKeyStorePassword) {
		if (StringUtils.equals(sslKeyStorePassword, defaultSslKeyStorePassword)) {
			this.sslKeyStorePassword = null;
		} else {
			this.sslKeyStorePassword = sslKeyStorePassword;
		}
	}

	public String getSslKeyManagerPassword() {
		return StringUtils.isEmpty(this.sslKeyManagerPassword) ? defaultSslKeyManagerPassword
				: this.sslKeyManagerPassword;
	}

	public void setSslKeyManagerPassword(String sslKeyManagerPassword) {
		if (StringUtils.equals(sslKeyManagerPassword, defaultSslKeyManagerPassword)) {
			this.sslKeyManagerPassword = null;
		} else {
			this.sslKeyManagerPassword = sslKeyManagerPassword;
		}
	}

	public List<Oauth> getOauths() {
		if (null == this.oauths) {
			return new ArrayList<Oauth>();
		}
		return this.oauths;
	}

	public List<Sso> getSsos() {
		if (null == this.ssos) {
			return new ArrayList<Sso>();
		}
		return this.ssos;
	}

	public void setOauths(List<Oauth> oauths) {
		this.oauths = oauths;
	}

	public void setSsos(List<Sso> ssos) {
		this.ssos = ssos;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_TOKEN);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public boolean isInitialManager(String name) {
		return StringUtils.equals(this.getInitialManager(), name)
				|| StringUtils.equals(this.getInitialManagerDistinguishedName(), name);
	}

	public InitialManager initialManagerInstance() {
		InitialManager o = new InitialManager();
		String name = this.getInitialManager();
		o.name = name;
		o.id = name;
		o.employee = name;
		o.display = name;
		o.mail = name + "@o2oa.io";
		o.setDistinguishedName(defaultInitialManagerDistinguishedName);
		o.weixin = "";
		o.qq = "";
		o.weibo = "";
		o.mobile = "";
		// o.icon = icon_initialManager;
		o.roleList = new ArrayList<String>();
		// o.roleList.add(RoleDefinition.UnitManager);
		// o.roleList.add(RoleDefinition.GroupCreator);
		o.roleList.add(OrganizationDefinition.Manager);
		o.roleList.add(OrganizationDefinition.OrganizationManager);
		o.roleList.add(OrganizationDefinition.MeetingManager);
		// o.roleList.add(RoleDefinition.PersonManager);
		// o.roleList.add(RoleDefinition.ProcessPlatformCreator);
		o.roleList.add(OrganizationDefinition.ProcessPlatformManager);
		return o;
	}

	public class InitialManager extends GsonPropertyObject {
		private String name;
		private String unique;
		private String id;
		private String distinguishedName;
		private String employee;
		private String display;
		private String mail;
		private String weixin;
		private String qq;
		private String weibo;
		private String mobile;
		// private String icon;
		private List<String> roleList;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmployee() {
			return employee;
		}

		public void setEmployee(String employee) {
			this.employee = employee;
		}

		public String getDisplay() {
			return display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}

		public String getMail() {
			return mail;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public String getWeixin() {
			return weixin;
		}

		public void setWeixin(String weixin) {
			this.weixin = weixin;
		}

		public String getQq() {
			return qq;
		}

		public void setQq(String qq) {
			this.qq = qq;
		}

		public String getWeibo() {
			return weibo;
		}

		public void setWeibo(String weibo) {
			this.weibo = weibo;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

		// public String getIcon() {
		// return icon;
		// }
		//
		// public void setIcon(String icon) {
		// this.icon = icon;
		// }

		public String getUnique() {
			return unique;
		}

		public void setUnique(String unique) {
			this.unique = unique;
		}

		public String getDistinguishedName() {
			return distinguishedName;
		}

		public void setDistinguishedName(String distinguishedName) {
			this.distinguishedName = distinguishedName;
		}

	}

	public Oauth findOauth(String clientId) {
		for (Oauth o : this.getOauths()) {
			if (StringUtils.equalsIgnoreCase(clientId, o.getClientId())) {
				return o;
			}
		}
		return null;
	}

	public Sso findSso(String client) {
		for (Sso o : this.getSsos()) {
			if (StringUtils.equalsIgnoreCase(client, o.getClient())) {
				return o;
			}
		}
		return null;
	}

	public class Oauth {

		private String clientId;

		private Map<String, String> mapping;

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public Map<String, String> getMapping() {
			if (null == mapping) {
				return new LinkedHashMap<String, String>();
			}
			return mapping;
		}

		public void setMapping(Map<String, String> mapping) {
			this.mapping = mapping;
		}

	}

	public class Sso {

		private String client;

		private String key;

		public String getClient() {
			return client;
		}

		public void setClient(String client) {
			this.client = client;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

	}

	public class Qyweixin {

		private String corpId;
		private String corpSecret;

		public String getCorpId() {
			return corpId;
		}

		public void setCorpId(String corpId) {
			this.corpId = corpId;
		}

		public String getCorpSecret() {
			return corpSecret;
		}

		public void setCorpSecret(String corpSecret) {
			this.corpSecret = corpSecret;
		}

	}

	public Qyweixin getQyweixin() {
		return qyweixin;
	}

	public void setQyweixin(Qyweixin qyweixin) {
		this.qyweixin = qyweixin;
	}

	public Dingding getDingding() {
		return dingding;
	}

	public void setDingding(Dingding dingding) {
		this.dingding = dingding;
	}

}