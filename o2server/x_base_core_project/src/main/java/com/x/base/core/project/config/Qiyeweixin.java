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

public class Qiyeweixin extends ConfigObject {

	@FieldDescribe("是否启用.")
	private Boolean enable;
	@FieldDescribe("拉入同步cron,默认每10分钟同步一次.")
	private String syncCron;
	@FieldDescribe("强制拉入同步cron,默认在每天的8点和12点强制进行同步.")
	private String forceSyncCron;
	@FieldDescribe("api服务器地址")
	private String apiAddress;
	@FieldDescribe("企业微信corpId")
	private String corpId;
	@FieldDescribe("企业微信corpSecret")
	private String corpSecret;
	@FieldDescribe("企业微信agentId")
	private String agentId;
	@FieldDescribe("回调token")
	private String token = "";
	@FieldDescribe("回调encodingAesKey")
	private String encodingAesKey = "";
	@FieldDescribe("推送消息到企业微信")
	private Boolean messageEnable;

	public static Qiyeweixin defaultInstance() {
		return new Qiyeweixin();
	}

	public static final Boolean default_enable = false;
	public static final String default_syncCron = "10 0/10 * * * ?";
	public static final String default_forceSyncCron = "10 45 8,12 * * ?";
	public static final String default_apiAddress = "https://qyapi.weixin.qq.com";
	public static final String default_corpId = "";
	public static final String default_corpSecret = "";
	public static final String default_agentId = "";
	public static final Boolean default_messageEanble = true;

	public Qiyeweixin() {
		this.enable = default_enable;
		this.syncCron = default_syncCron;
		this.forceSyncCron = default_forceSyncCron;
		this.apiAddress = default_apiAddress;
		this.corpId = default_corpId;
		this.corpSecret = default_corpSecret;
		this.agentId = default_agentId;
		this.messageEnable = default_messageEanble;

	}

	private static String cachedCorpAccessToken;
	private static Date cachedCorpAccessTokenDate;

	private static String cachedJsapiTicket;
	private static Date cachedJsapiTicketDate;

	public String getAgentId() {
		return StringUtils.isEmpty(this.agentId) ? default_agentId : this.agentId;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public Boolean getMessageEnable() {
		return BooleanUtils.isTrue(this.messageEnable);
	}

	public String getSyncCron() {
		return StringUtils.isEmpty(this.syncCron) ? default_syncCron : this.syncCron;
	}

	public String getForceSyncCron() {
		return StringUtils.isEmpty(this.forceSyncCron) ? default_forceSyncCron : this.forceSyncCron;
	}

	public String getCorpId() {
		return StringUtils.isEmpty(corpId) ? default_corpId : this.corpId;
	}

	public String getCorpSecret() {
		return StringUtils.isEmpty(corpSecret) ? default_corpSecret : this.corpSecret;
	}

	public String getApiAddress() {
		return StringUtils.isEmpty(this.apiAddress) ? default_apiAddress : this.apiAddress;
	}

	public String corpAccessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cachedCorpAccessToken) && (null != cachedCorpAccessTokenDate))
				&& (cachedCorpAccessTokenDate.after(new Date()))) {
			return cachedCorpAccessToken;
		} else {
			String address = default_apiAddress + "/cgi-bin/gettoken?corpid=" + this.getCorpId() + "&corpsecret="
					+ this.getCorpSecret();
			CorpAccessTokenResp resp = HttpConnection.getAsObject(address, null, CorpAccessTokenResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionQiyeweixinCorpAccessToken(resp.getErrcode(), resp.getErrmsg());
			}
			cachedCorpAccessToken = resp.getAccess_token();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 90);
			cachedCorpAccessTokenDate = cal.getTime();
			return cachedCorpAccessToken;
		}
	}

	public String getJsapiTicket() throws Exception {
		if ((StringUtils.isNotEmpty(cachedJsapiTicket) && (null != cachedJsapiTicketDate))
				&& (cachedJsapiTicketDate.after(new Date()))) {
			return cachedJsapiTicket;
		} else {
			String address = default_apiAddress + "/get_jsapi_ticket?access_token=" + this.corpAccessToken()
					+ "&type=jsapi";
			JsapiTicketResp resp = HttpConnection.getAsObject(address, null, JsapiTicketResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionZhengwuDingdingJsapiTicket(resp.getErrcode(), resp.getErrmsg());
			}
			cachedJsapiTicket = resp.ticket;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 90);
			cachedJsapiTicketDate = cal.getTime();
			return cachedJsapiTicket;
		}
	}

	public void setApiAddress(String oapiAddress) {
		this.apiAddress = oapiAddress;
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

	public static class AppAccessTokenResp {

		private Integer retCode;
		private String retMessage;
		private RetData retData;

		public static class RetData {
			private String token;

			public String getToken() {
				return token;
			}

			public void setToken(String token) {
				this.token = token;
			}

		}

		public Integer getRetCode() {
			return retCode;
		}

		public void setRetCode(Integer retCode) {
			this.retCode = retCode;
		}

		public String getRetMessage() {
			return retMessage;
		}

		public void setRetMessage(String retMessage) {
			this.retMessage = retMessage;
		}

		public RetData getRetData() {
			return retData;
		}

		public void setRetData(RetData retData) {
			this.retData = retData;
		}
	}

	public class JsapiTicketResp {
		// {
		// "errcode": 0,
		// "errmsg": "ok",
		// "ticket": "dsf8sdf87sd7f87sd8v8ds0vs09dvu09sd8vy87dsv87",
		// "expires_in": 7200
		// }
		private Integer errcode;
		private String errmsg;
		private String ticket;
		private Integer expires_in;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public String getTicket() {
			return ticket;
		}

		public void setTicket(String ticket) {
			this.ticket = ticket;
		}

		public Integer getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(Integer expires_in) {
			this.expires_in = expires_in;
		}

	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public void setCorpSecret(String corpSecret) {
		this.corpSecret = corpSecret;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
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

	public void setSyncCron(String syncCron) {
		this.syncCron = syncCron;
	}

	public void setForceSyncCron(String forceSyncCron) {
		this.forceSyncCron = forceSyncCron;
	}

	public void setMessageEnable(Boolean messageEnable) {
		this.messageEnable = messageEnable;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_QIYEWEIXIN);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

}
