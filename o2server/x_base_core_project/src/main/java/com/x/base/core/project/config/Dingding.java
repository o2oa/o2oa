package com.x.base.core.project.config;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.HttpConnection;

public class Dingding extends ConfigObject {

	@FieldDescribe("是否启用")
	private Boolean enable;

	@FieldDescribe("钉钉corpId")
	private String corpId;

	@FieldDescribe("agentId")
	private String agentId;

//	@FieldDescribe("钉钉corpSecret")
//	private String corpSecret;

	@FieldDescribe("组织同步cron,默认每10分钟同步一次.")
	private String syncCron;

	@FieldDescribe("强制拉入同步cron,默认在每天的8点和12点强制进行同步.")
	private String forceSyncCron;

	@FieldDescribe("oapi服务器地址")
	private String oapiAddress;

	@FieldDescribe("是否启用消息推送")
	private Boolean messageEnable;

	@FieldDescribe("应用的key,唯一标识")
	private String appKey;

	@FieldDescribe("应用的密钥")
	private String appSecret;

	public static Dingding defaultInstance() {
		return new Dingding();
	}

	public static final Boolean default_enable = false;
	public static final String default_appKey = "";
	public static final String default_appSecret = "";
	public static final String default_agentId = "";
	public static final String default_syncCron = "10 0/10 * * * ?";
	public static final String default_forceSyncCron = "10 45 8,12 * * ?";
	public static final String default_oapiAddress = "https://oapi.dingtalk.com";
	public static final Boolean default_messageEnable = true;

	public Dingding() {
		this.enable = default_enable;
		this.appKey = default_appKey;
		this.appSecret = default_appSecret;
		this.agentId = default_agentId;
		this.syncCron = default_syncCron;
		this.forceSyncCron = default_forceSyncCron;
		this.oapiAddress = default_oapiAddress;
		this.messageEnable = default_messageEnable;
	}

	private static String cachedCorpAccessToken;
	private static Date cachedCorpAccessTokenDate;

	private static String cachedJsapiTicket;
	private static Date cachedJsapiTicketDate;

	public Boolean getMessageEnable() {
		return BooleanUtils.isTrue(this.messageEnable);
	}

	public String getAgentId() {
		return StringUtils.isEmpty(this.agentId) ? default_agentId : this.agentId;
	}

	public String getOapiAddress() {
		return StringUtils.isEmpty(this.oapiAddress) ? default_oapiAddress : this.oapiAddress;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public String getSyncCron() {
		return StringUtils.isEmpty(this.syncCron) ? default_syncCron : this.syncCron;
	}

	public String getForceSyncCron() {
		return StringUtils.isEmpty(this.forceSyncCron) ? default_forceSyncCron : this.forceSyncCron;
	}

	public String corpAccessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cachedCorpAccessToken) && (null != cachedCorpAccessTokenDate))
				&& (cachedCorpAccessTokenDate.after(new Date()))) {
			return cachedCorpAccessToken;
		} else {
			String address = this.getOapiAddress() + "/gettoken?appkey=" + this.getAppKey() + "&appsecret="
					+ this.getAppSecret();
			AccessTokenResp resp = HttpConnection.getAsObject(address, null, AccessTokenResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionDingdingCorpAccessToken(resp.getErrcode(), resp.getErrmsg());
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
			String address = "https://oapi.dingtalk.com/get_jsapi_ticket?access_token=" + corpAccessToken()
					+ "&type=jsapi";
			JsapiTicketResp resp = HttpConnection.getAsObject(address, null, JsapiTicketResp.class);
			cachedJsapiTicket = resp.getTicket();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, 1);
			cachedJsapiTicketDate = cal.getTime();
			return cachedJsapiTicket;
		}
	}

	public static class AccessTokenResp {

		private String access_token;
		private Integer errcode;
		private String errmsg;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

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
	}

	public static class JsapiTicketResp {

		private String ticket;

		public String getTicket() {
			return ticket;
		}

		public void setTicket(String ticket) {
			this.ticket = ticket;
		}
	}

	public String getAppKey() {
		return appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public String getCorpId() {
		return corpId;
	}

}
