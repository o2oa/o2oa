package com.x.base.core.project.config;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

/**
 * 回调要求本地网络端口更换到80、 443、 8080 这三个端口再试，核实使用其它端口会被限制不能连接
 */
public class Exmail extends ConfigObject {

	@FieldDescribe("是否启用.")
	private Boolean enable;
	@FieldDescribe("腾讯企业邮corpId")
	private String corpId;
	@FieldDescribe("新邮件提醒secret")
	private String newRemindSecret;
	@FieldDescribe("单点登录secret")
	private String ssoSecret;
	@FieldDescribe("corpAccessToken获取地址")
	private String corpAccessTokenAddress;
	@FieldDescribe("新邮件数量获取地址")
	private String newCountAddress;
	@FieldDescribe("单点登录获取地址")
	private String ssoAddress;
	@FieldDescribe("回调token")
	private String token;
	@FieldDescribe("回调encodingAesKey")
	private String encodingAesKey;
	@FieldDescribe("存储邮件数量个人属性值.")
	private String personAttributeNewCountName;
	@FieldDescribe("存储邮件标题个人属性值.")
	private String personAttributeTitleName;

	public static Exmail defaultInstance() {
		return new Exmail();
	}

	public static final Boolean default_enable = false;
	public static final String default_corpId = "";
	public static final String default_corpSecret = "";
	public static final String default_newRemindSecret = "";
	public static final String default_ssoSecret = "";
	public static final String default_corpAccessTokenAddress = "https://api.exmail.qq.com/cgi-bin/gettoken";
	public static final String default_newCountAddress = "https://api.exmail.qq.com/cgi-bin/mail/newcount";
	public static final String default_ssoAddress = "https://api.exmail.qq.com/cgi-bin/service/get_login_url";
	public static final String default_personAttributeNewCountName = "exmailNewCount";
	public static final String default_personAttributeTitleName = "exmailTitle";

	public Exmail() {
		this.enable = default_enable;
		this.corpId = default_corpId;
		this.corpAccessTokenAddress = default_corpAccessTokenAddress;
		this.newCountAddress = default_newCountAddress;
		this.ssoAddress = default_ssoAddress;
		this.personAttributeNewCountName = default_personAttributeNewCountName;
		this.personAttributeTitleName = default_personAttributeTitleName;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public String getCorpId() {
		return StringUtils.isEmpty(corpId) ? default_corpId : this.corpId;
	}

	public String getNewRemindSecret() {
		return StringUtils.isEmpty(newRemindSecret) ? default_newRemindSecret : this.newRemindSecret;
	}

	public String getSsoSecret() {
		return StringUtils.isEmpty(ssoSecret) ? default_ssoSecret : this.ssoSecret;
	}

	public String getCorpAccessTokenAddress() {
		return StringUtils.isEmpty(this.corpAccessTokenAddress) ? default_corpAccessTokenAddress
				: this.corpAccessTokenAddress;
	}

	public String getNewCountAddress() {
		return StringUtils.isEmpty(this.newCountAddress) ? default_newCountAddress : this.newCountAddress;
	}

	public String getSsoAddress() {
		return StringUtils.isEmpty(this.ssoAddress) ? default_ssoAddress : this.ssoAddress;
	}

	public String getPersonAttributeNewCountName() {
		return StringUtils.isEmpty(this.personAttributeNewCountName) ? default_personAttributeNewCountName
				: this.personAttributeNewCountName;
	}

	public String getPersonAttributeTitleName() {
		return StringUtils.isEmpty(this.personAttributeTitleName) ? default_personAttributeTitleName
				: this.personAttributeTitleName;
	}

	private static String cachedNewRemindAccessToken;
	private static Date cachedNewRemindAccessTokenDate;

	public String newRemindAccessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cachedNewRemindAccessToken) && (null != cachedNewRemindAccessTokenDate))
				&& (cachedNewRemindAccessTokenDate.after(new Date()))) {
			return cachedNewRemindAccessToken;
		} else {
			String address = getCorpAccessTokenAddress() + "?corpid=" + this.getCorpId() + "&corpsecret="
					+ this.getNewRemindSecret();
			NewRemindAccessTokenResp resp = HttpConnection.getAsObject(address, null, NewRemindAccessTokenResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionExmailNewRemindAccessToken(resp.getErrcode(), resp.getErrmsg());
			}
			cachedNewRemindAccessToken = resp.getAccess_token();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 90);
			cachedNewRemindAccessTokenDate = cal.getTime();
			return cachedNewRemindAccessToken;
		}
	}

	private static String cachedSsoAccessToken;
	private static Date cachedSsoAccessTokenDate;

	public String ssoAccessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cachedSsoAccessToken) && (null != cachedSsoAccessTokenDate))
				&& (cachedSsoAccessTokenDate.after(new Date()))) {
			return cachedSsoAccessToken;
		} else {
			String address = getCorpAccessTokenAddress() + "?corpid=" + this.getCorpId() + "&corpsecret="
					+ this.getSsoSecret();
			SsoAccessTokenResp resp = HttpConnection.getAsObject(address, null, SsoAccessTokenResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionExmailSsoAccessToken(resp.getErrcode(), resp.getErrmsg());
			}
			cachedSsoAccessToken = resp.getAccess_token();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 90);
			cachedSsoAccessTokenDate = cal.getTime();
			return cachedSsoAccessToken;
		}
	}

	public static class SsoAccessTokenResp extends GsonPropertyObject {

		private Integer errcode;
		private String access_token;
		private String errmsg;
		private Integer expires_in;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public Integer getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(Integer expires_in) {
			this.expires_in = expires_in;
		}

	}

	public static class NewRemindAccessTokenResp extends GsonPropertyObject {

		private Integer errcode;
		private String access_token;
		private String errmsg;
		private Integer expires_in;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public Integer getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(Integer expires_in) {
			this.expires_in = expires_in;
		}

	}

	public static class CorpAccessTokenResp extends GsonPropertyObject {

		// {"":7200,"":"ok","":"1601c97b17893fbfa4218ce2151a0692","":0}

		private Integer errcode;
		private String access_token;
		private String errmsg;
		private Integer expires_in;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public Integer getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(Integer expires_in) {
			this.expires_in = expires_in;
		}

	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public void setCorpAccessTokenAddress(String corpAccessTokenAddress) {
		this.corpAccessTokenAddress = corpAccessTokenAddress;
	}

	public void setPersonAttributeNewCountName(String personAttributeNewCountName) {
		this.personAttributeNewCountName = personAttributeNewCountName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEncodingAesKey() {
		return encodingAesKey;
	}

	public void setEncodingAesKey(String encodingAesKey) {
		this.encodingAesKey = encodingAesKey;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_EXMAIL);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

}
