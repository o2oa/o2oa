package com.x.base.core.project.config;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;

public class Dingding extends ConfigObject {

	@FieldDescribe("是否启用")
	private Boolean enable;

	@FieldDescribe("钉钉corpId")
	private String corpId;

	@FieldDescribe("agentId")
	private String agentId;

	@FieldDescribe("应用的key,唯一标识")
	private String appKey;

	@FieldDescribe("应用的密钥")
	private String appSecret;

	@FieldDescribe("回调信号触发同步检查,默认每10分钟运行一次,如果期间内有钉钉回调信号接收到,那么触发同步任务进行人员同步.")
	private String syncCron;

	@FieldDescribe("强制拉入同步cron,默认在每天的8点和12点强制进行同步.")
	private String forceSyncCron;

	@FieldDescribe("是否仅同步人员(新用户创建和根据手机号码绑定钉钉ID).")
	private Boolean syncPersonOnly;

	@FieldDescribe("oapi服务器地址")
	private String oapiAddress;

	@FieldDescribe("回调token")
	private String token = "";

	@FieldDescribe("回调encodingAesKey")
	private String encodingAesKey = "";

	@FieldDescribe("钉钉消息打开工作的url地址，如：https://sample.o2oa.net/x_desktop/")
	private String workUrl = "";

	@FieldDescribe("钉钉消息处理完成后跳转到特定的门户页面的Id")
	private String messageRedirectPortal = "";

	@FieldDescribe("是否启用消息推送")
	private Boolean messageEnable;

	@FieldDescribe("是否开启钉钉扫码登录")
	private Boolean scanLoginEnable;

	@FieldDescribe("钉钉扫码登录的AppId")
	private String scanLoginAppId;

	@FieldDescribe("钉钉扫码登录的appSecret")
	private String scanLoginAppSecret;

	@FieldDescribe("是否启用考勤信息")
	private Boolean attendanceSyncEnable;

	@FieldDescribe("是否同步关联组织(上下级组织)")
	private Boolean syncUnionOrgEnable;

	public static Dingding defaultInstance() {
		return new Dingding();
	}

	public static final String dingdingLogo = "https://res.o2oa.net/app/dingding-logo.png";
	public static final Boolean default_enable = false;
	public static final String default_appKey = "";
	public static final String default_corpId = "";
	public static final String default_appSecret = "";
	public static final String default_agentId = "";
	public static final String default_syncCron = "10 0/10 * * * ?";
	public static final String default_forceSyncCron = "10 45 8,12 * * ?";
	public static final String default_oapiAddress = "https://oapi.dingtalk.com";
	public static final String default_workUrl = "";
	public static final String default_messageRedirectPortal = "";
	public static final Boolean default_messageEnable = true;
	public static final Boolean default_scanLoginEnable = false;
	public static final String default_scanLoginAppId = "";
	public static final String default_scanLoginAppSecret = "";
	public static final Boolean default_attendanceSyncEnable = false;
	public static final Boolean default_syncUnionOrgEnable = true;

	public Dingding() {
		this.enable = default_enable;
		this.corpId = default_corpId;
		this.appKey = default_appKey;
		this.appSecret = default_appSecret;
		this.agentId = default_agentId;
		this.syncCron = default_syncCron;
		this.forceSyncCron = default_forceSyncCron;
		this.oapiAddress = default_oapiAddress;
		this.workUrl = default_workUrl;
		this.messageRedirectPortal = default_messageRedirectPortal;
		this.messageEnable = default_messageEnable;
		this.scanLoginEnable = default_scanLoginEnable;
		this.scanLoginAppId = default_scanLoginAppId;
		this.scanLoginAppSecret = default_scanLoginAppSecret;
		this.attendanceSyncEnable = default_attendanceSyncEnable;
		this.syncUnionOrgEnable = default_syncUnionOrgEnable;
		this.syncPersonOnly = false;
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

	public Boolean getSyncPersonOnly() {
		return BooleanUtils.isTrue(syncPersonOnly);
	}

	public void setSyncPersonOnly(Boolean syncPersonOnly) {
		this.syncPersonOnly = syncPersonOnly;
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
			String address = getOapiAddress() + "/get_jsapi_ticket?access_token=" + corpAccessToken()
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

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_DINGDING);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_DINGDING);
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public void setSyncCron(String syncCron) {
		this.syncCron = syncCron;
	}

	public void setForceSyncCron(String forceSyncCron) {
		this.forceSyncCron = forceSyncCron;
	}

	public void setOapiAddress(String oapiAddress) {
		this.oapiAddress = oapiAddress;
	}

	public void setMessageEnable(Boolean messageEnable) {
		this.messageEnable = messageEnable;
	}

	public String getWorkUrl() {
		return workUrl;
	}

	public void setWorkUrl(String workUrl) {
		this.workUrl = workUrl;
	}

	public String getMessageRedirectPortal() {
		return messageRedirectPortal;
	}

	public void setMessageRedirectPortal(String messageRedirectPortal) {
		this.messageRedirectPortal = messageRedirectPortal;
	}

	public Boolean getScanLoginEnable() {
		return scanLoginEnable;
	}

	public void setScanLoginEnable(Boolean scanLoginEnable) {
		this.scanLoginEnable = scanLoginEnable;
	}

	public String getScanLoginAppId() {
		return scanLoginAppId;
	}

	public void setScanLoginAppId(String scanLoginAppId) {
		this.scanLoginAppId = scanLoginAppId;
	}

	public String getScanLoginAppSecret() {
		return scanLoginAppSecret;
	}

	public void setScanLoginAppSecret(String scanLoginAppSecret) {
		this.scanLoginAppSecret = scanLoginAppSecret;
	}

	public Boolean getAttendanceSyncEnable() {
		return attendanceSyncEnable;
	}

	public void setAttendanceSyncEnable(Boolean attendanceSyncEnable) {
		this.attendanceSyncEnable = attendanceSyncEnable;
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

	public Boolean getSyncUnionOrgEnable() {
		return syncUnionOrgEnable;
	}

	public void setSyncUnionOrgEnable(Boolean syncUnionOrgEnable) {
		this.syncUnionOrgEnable = syncUnionOrgEnable;
	}
}
