package com.x.base.core.project.config;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;

public class Token extends ConfigObject {

	private static final long serialVersionUID = 397619753053929409L;

	private static final String surfix = "o2platform";

	public static final String defaultInitialManager = "xadmin";

	public static final String defaultInitialManagerDistinguishedName = "xadmin@o2oa@P";

	public static final String initPassword = "o2oa@2022";

	public static final String defaultSslKeyStorePassword = "123456";
	public static final String defaultSslKeyManagerPassword = "123456";

	public static final Boolean DEFAULT_RSAENABLE = false;

	// 此对象临时计算无需存储
	private transient String _cipher = "";
	// 此对象临时计算无需存储
	private transient String _password = "";

	public static Token defaultInstance() {
		return new Token();
	}

	public Token() {
		this.key = "";
		this.password = "";
		this.sslKeyStorePassword = defaultSslKeyStorePassword;
		this.sslKeyManagerPassword = defaultSslKeyManagerPassword;

		this.rsaEnable = DEFAULT_RSAENABLE;
	}

	// 加密用的key,用于加密口令
	@FieldDescribe("加密用口令的密钥,修改后会导致用户口令验证失败.")
	private String key;

	@FieldDescribe("初始管理员密码,用于内部数据库和FTP文件服务器,以及http的token加密.")
	private String password;

	@FieldDescribe("ssl密码")
	private String sslKeyStorePassword;

	@FieldDescribe("ssl管理密码")
	private String sslKeyManagerPassword;

	@FieldDescribe("LDAP认证配置")
	private LdapAuth ldapAuth;

	@FieldDescribe("sso登录配置")
	private List<Sso> ssos = new ArrayList<>();

	@FieldDescribe("oauth单点登录配置")
	private List<Oauth> oauths = new ArrayList<>();

	@FieldDescribe("作为客户端单点登录配置")
	private List<OauthClient> oauthClients = new ArrayList<>();

	@FieldDescribe("启用rsa加密.")
	private Boolean rsaEnable = DEFAULT_RSAENABLE;

	public Boolean getRsaEnable() {
		return null == this.rsaEnable ? DEFAULT_RSAENABLE : this.rsaEnable;
	}

	// 前面的代码是 key+surfix 结果是nullo2platform
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
		if (StringUtils.isEmpty(this._cipher)) {
			this._cipher = DigestUtils.md5Hex(this.getPassword());
		}
		return this._cipher;
	}

	public String getPassword() {
		if (StringUtils.isEmpty(this._password)) {
			this._password = StringUtils.isEmpty(this.password) ? initPassword : Crypto.plainText(this.password);
		}
		return this._password;
	}

	public void setPassword(String password)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		this.password = Crypto.formattedDefaultEncrypt(password);
	}

	public String getInitialManager() {
		return defaultInitialManager;
	}

	public String getInitialManagerDistinguishedName() {
		return defaultInitialManagerDistinguishedName;
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
			return new ArrayList<>();
		}
		return this.oauths;
	}

	public List<Sso> getSsos() {
		if (null == this.ssos) {
			return new ArrayList<>();
		}
		return this.ssos;
	}

	public LdapAuth getLdapAuth() {
		return this.ldapAuth == null ? LdapAuth.defaultInstance() : this.ldapAuth;
	}

	public void setLdapAuth(LdapAuth ldapAuth) {
		this.ldapAuth = ldapAuth;
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
		BaseTools.executeSyncFile(Config.PATH_CONFIG_TOKEN);
	}

	public boolean isInitialManager(String name) throws Exception {
		if (BooleanUtils.isTrue(Config.ternaryManagement().getEnable())) {
			return Config.ternaryManagement().isTernaryManagement(name);
		} else {
			return StringUtils.equals(this.getInitialManager(), name)
					|| StringUtils.equals(this.getInitialManagerDistinguishedName(), name);
		}
	}

	public boolean verifyPassword(String name, String password) throws Exception {
		if (BooleanUtils.isTrue(Config.ternaryManagement().getEnable())) {
			return Config.ternaryManagement().verifyPassword(name, password);
		} else {
			return StringUtils.equals(this.getPassword(), password);
		}
	}

	public InitialManager initialManagerInstance() {
		InitialManager o = new InitialManager();
		String name = this.getInitialManager();
		o.name = name;
		o.id = name;
		o.employee = name;
		o.display = name;
		o.mail = name + "@o2oa.net";
		o.setDistinguishedName(defaultInitialManagerDistinguishedName);
		o.weixin = "";
		o.qq = "";
		o.weibo = "";
		o.mobile = "";
		o.roleList = new ArrayList<>();
		o.roleList.add(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.Manager));
		o.roleList.add(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.OrganizationManager));
		o.roleList.add(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.MeetingManager));
		o.roleList.add(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.ProcessPlatformManager));
		return o;
	}

	public class InitialManager extends GsonPropertyObject {

		private static final long serialVersionUID = 6295964037824026773L;

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
		private String pinyin;
		private String pinyinInitial;
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

		public String getPinyin() {
			return pinyin;
		}

		public void setPinyin(String pinyin) {
			this.pinyin = pinyin;
		}

		public String getPinyinInitial() {
			return pinyinInitial;
		}

		public void setPinyinInitial(String pinyinInitial) {
			this.pinyinInitial = pinyinInitial;
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

	public static class Oauth extends ConfigObject {

		public static Oauth defaultInstance() {
			return new Oauth();
		}

		public Oauth() {
			this.enable = false;
			this.clientId = "";
			this.mapping = new LinkedHashMap<>();

		}

		@FieldDescribe("是否启用")
		private Boolean enable;

		@FieldDescribe("客户端名称")
		private String clientId;

		@FieldDescribe("密钥")
		private String clientSecret;

		@FieldDescribe("登录地址")
		private String loginUrl;

		@FieldDescribe("返回值")
		private Map<String, String> mapping;

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public Map<String, String> getMapping() {
			if (null == mapping) {
				return new LinkedHashMap<>();
			}
			return mapping;
		}

		public void setMapping(Map<String, String> mapping) {
			this.mapping = mapping;
		}

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getClientSecret() {
			return clientSecret;
		}

		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}

		public String getLoginUrl() {
			return loginUrl;
		}

		public void setLoginUrl(String loginUrl) {
			this.loginUrl = loginUrl;
		}

	}

	public static class OauthClient extends ConfigObject {

		public static OauthClient defaultInstance() {
			return new OauthClient();
		}

		public static final String default_authParameter = "client_id={$client_id}&redirect_uri={$redirect_uri}";
		public static final String default_infoParameter = "access_token={$access_token}";
		public static final String default_tokenParameter = "client_id={$client_id}&client_secret={$client_secret}&redirect_uri={$redirect_uri}&grant_type=authorization_code&code={$code}";

		public OauthClient() {
			this.enable = false;
			this.name = "";
			this.displayName = "";
			this.icon = "";
			this.clientId = "";
			this.clientSecret = "";
			this.authAddress = "";
			this.authParameter = default_authParameter;
			this.authMethod = "GET";
			this.tokenAddress = "";
			this.tokenParameter = default_tokenParameter;
			this.tokenMethod = "POST";
			this.tokenType = "json";
			this.infoAddress = "";
			this.infoParameter = default_infoParameter;
			this.infoMethod = "GET";
			this.infoType = "json";
			this.infoCredentialField = "openId";
			this.infoScriptText = "";
			this.bindingEnable = false;
			this.bindingField = "";
		}

		@FieldDescribe("是否启用.")
		private Boolean enable = false;
		@FieldDescribe("名称.")
		private String name = "";
		@FieldDescribe("显示名称.")
		private String displayName = "";
		@FieldDescribe("图标.")
		private String icon = "";
		@FieldDescribe("用户oauth2认证的client_id.")
		private String clientId = "";
		@FieldDescribe("用户oauth2认证的client_secret.")
		private String clientSecret = "";
		@FieldDescribe("认证后的跳转地址.")
		private String authAddress = "";
		@FieldDescribe("请求密钥方法参数.")
		private String authParameter = "";
		@FieldDescribe("请求密钥方法.一般为GET")
		private String authMethod = "GET";
		@FieldDescribe("请求令牌网址.")
		private String tokenAddress = "";
		@FieldDescribe("请求令牌方法参数.")
		private String tokenParameter = "";
		@FieldDescribe("请求令牌方法.一般为POST")
		private String tokenMethod = "POST";
		@FieldDescribe("token信息格式.json格式或者form格式")
		private String tokenType = "json";
		@FieldDescribe("请求信息网址.")
		private String infoAddress = "";
		@FieldDescribe("请求信息方法参数.")
		private String infoParameter = "";
		@FieldDescribe("请求信息方法.一般为GET")
		private String infoMethod = "GET";
		@FieldDescribe("info信息格式.json格式或者form格式或者script格式")
		private String infoType = "json";
		@FieldDescribe("info信息中用于标识个人的字段.")
		private String infoCredentialField = "username";
		@FieldDescribe("info信息中用于标识个人的字段.")
		private String infoScriptText = "";
		@FieldDescribe("是否允许绑定到用户,如果允许,用户可以自行绑定.")
		private Boolean bindingEnable = false;
		@FieldDescribe("绑定字段,对端的用户标识,一般为openId绑定到个人字段,可选值为open1Id,open2Id,open3Id,open4Id,open5Id")
		private String bindingField = "";

		public String getDisplayName() {
			return displayName;
		}

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String getClientSecret() {
			return clientSecret;
		}

		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}

		public String getAuthAddress() {
			return authAddress;
		}

		public void setAuthAddress(String authAddress) {
			this.authAddress = authAddress;
		}

		public String getTokenAddress() {
			return tokenAddress;
		}

		public void setTokenAddress(String tokenAddress) {
			this.tokenAddress = tokenAddress;
		}

		public String getInfoAddress() {
			return infoAddress;
		}

		public void setInfoAddress(String infoAddress) {
			this.infoAddress = infoAddress;
		}

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAuthMethod() {
			return authMethod;
		}

		public void setAuthMethod(String authMethod) {
			this.authMethod = authMethod;
		}

		public String getTokenMethod() {
			return tokenMethod;
		}

		public void setTokenMethod(String tokenMethod) {
			this.tokenMethod = tokenMethod;
		}

		public String getInfoMethod() {
			return infoMethod;
		}

		public void setInfoMethod(String infoMethod) {
			this.infoMethod = infoMethod;
		}

		public String getInfoType() {
			return infoType;
		}

		public void setInfoType(String infoType) {
			this.infoType = infoType;
		}

		public String getInfoCredentialField() {
			return infoCredentialField;
		}

		public void setInfoCredentialField(String infoCredentialField) {
			this.infoCredentialField = infoCredentialField;
		}

		public String getAuthParameter() {
			return authParameter;
		}

		public void setAuthParameter(String authParameter) {
			this.authParameter = authParameter;
		}

		public String getTokenParameter() {
			return tokenParameter;
		}

		public void setTokenParameter(String tokenParameter) {
			this.tokenParameter = tokenParameter;
		}

		public String getInfoParameter() {
			return infoParameter;
		}

		public void setInfoParameter(String infoParameter) {
			this.infoParameter = infoParameter;
		}

		public String getTokenType() {
			return tokenType;
		}

		public void setTokenType(String tokenType) {
			this.tokenType = tokenType;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getInfoScriptText() {
			return infoScriptText;
		}

		public void setInfoScriptText(String infoScriptText) {
			this.infoScriptText = infoScriptText;
		}

		public Boolean getBindingEnable() {
			return bindingEnable;
		}

		public void setBindingEnable(Boolean bindingEnable) {
			this.bindingEnable = bindingEnable;
		}

		public String getBindingField() {
			return bindingField;
		}

		public void setBindingField(String bindingField) {
			this.bindingField = bindingField;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

	}

	public static class Sso extends ConfigObject {

		public static Sso defaultInstance() {
			return new Sso();
		}

		public Sso() {
			this.enable = false;
			this.client = "";
			this.key = "";
		}

		@FieldDescribe("是否启用")
		private Boolean enable;

		@FieldDescribe("名称")
		private String client;

		@FieldDescribe("密钥")
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

	public static class LdapAuth extends ConfigObject {

		public static LdapAuth defaultInstance() {
			return new LdapAuth();
		}

		public LdapAuth() {
			this.enable = false;
			this.ldapUrl = "";
			this.bindDnUser = "";
			this.bindDnPwd = "";
			this.baseDn = "";
			this.userDn = "";
		}

		@FieldDescribe("是否启用")
		private Boolean enable;

		@FieldDescribe("LDAP服务器如：ldap://127.0.0.1:389")
		private String ldapUrl;

		@FieldDescribe("BindDn用户(需有管理权限的用户)，如：cn=root")
		private String bindDnUser;

		@FieldDescribe("BindDn用户的密码")
		private String bindDnPwd;

		@FieldDescribe("LDAP查询的根名称如：dc=zone,DC=COM")
		private String baseDn;

		@FieldDescribe("认证用户绑定属性：uid、手机号、员工编码或邮箱(需确保在baseDn下查找到的数据是唯一的并且在o2能查到关联人员)")
		private String userDn;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getLdapUrl() {
			return ldapUrl;
		}

		public void setLdapUrl(String ldapUrl) {
			this.ldapUrl = ldapUrl;
		}

		public String getBaseDn() {
			return baseDn;
		}

		public void setBaseDn(String baseDn) {
			this.baseDn = baseDn;
		}

		public String getUserDn() {
			return userDn;
		}

		public void setUserDn(String userDn) {
			this.userDn = userDn;
		}

		public String getBindDnUser() {
			return bindDnUser;
		}

		public void setBindDnUser(String bindDnUser) {
			this.bindDnUser = bindDnUser;
		}

		public String getBindDnPwd() {
			return StringUtils.isBlank(this.bindDnPwd) ? this.bindDnPwd : Crypto.plainText(this.bindDnPwd);
		}

		public void setBindDnPwd(String bindDnPwd) {
			this.bindDnPwd = bindDnPwd;
		}
	}

	public List<OauthClient> getOauthClients() {
		return oauthClients;
	}

	public void setOauthClients(List<OauthClient> oauthClients) {
		this.oauthClients = oauthClients;
	}

}
