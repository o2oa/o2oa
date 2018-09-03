package o2.base.core.project.config;

import java.io.File;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class Token {

	private static final String surfix = "o2collect";

	public static final String initPassword = "zone2009";

	public static final String defaultInitialManager = "xadmin";

	public static Token defaultInstance() {
		Token o = new Token();
		return o;
	}

	/** 加密用的key,用于加密口令 */
	private String key;

	/** 管理员密码,用于管理员登录,内部服务器口令以及http加密 */
	private String password;

	/** 初始管理员名称 */
	// private String initialManager;

	/** ssl密码 */
	private String ssl;

	/** 前面的代码是 key+surfix 之前是o2server */
	public String getKey() {
		String val = Objects.toString(key, "") + surfix;
		return StringUtils.substring(val, 0, 8);
	}

	public String getCipher() {
		return this.getPassword() + surfix;
	}

	public String getPassword() {
		return Objects.toString(this.password, initPassword);
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSsl() {
		return ssl;
	}

	public void setSsl(String ssl) {
		this.ssl = ssl;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_TOKEN);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
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

}