package com.x.base.core.project.config;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;

/**
 * Created by fancyLou on 2020-07-24. Copyright © 2020 O2. All rights reserved.
 */
public class WeLink extends ConfigObject {

	@FieldDescribe("是否启用")
	private Boolean enable;

	@FieldDescribe("WeLink应用的client_id")
	private String clientId;

	@FieldDescribe("WeLink应用的client_secret")
	private String clientSecret;

	@FieldDescribe("回调信号触发同步检查,默认每10分钟运行一次,如果期间内有WeLink回调信号接收到,那么触发同步任务进行人员同步.")
	private String syncCron;

	@FieldDescribe("强制拉入同步cron,默认在每天的8点和12点强制进行同步.")
	private String forceSyncCron;

	@FieldDescribe("WeLink api服务器地址")
	private String oapiAddress;

	@FieldDescribe("是否启用消息推送")
	private Boolean messageEnable;

	@FieldDescribe("WeLink消息打开工作的url地址，如：https://sample.o2oa.net/x_desktop/")
	private String workUrl = "";

	@FieldDescribe("WeLink消息处理完成后跳转到特定的门户页面的Id")
	private String messageRedirectPortal = "";

	public static WeLink defaultInstance() {
		return new WeLink();
	}

	public static final Boolean default_enable = false;
	public static final String default_clientId = "";
	public static final String default_clientSecret = "";
	public static final String default_syncCron = "10 0/10 * * * ?";
	public static final String default_forceSyncCron = "10 45 8,12 * * ?";
	public static final String default_oapiAddress = "https://open.welink.huaweicloud.com/api";
	public static final Boolean default_messageEnable = false;
	public static final String default_workUrl = "";
	public static final String default_messageRedirectPortal = "";

	public WeLink() {
		this.enable = default_enable;
		this.clientId = default_clientId;
		this.clientSecret = default_clientSecret;
		this.syncCron = default_syncCron;
		this.forceSyncCron = default_forceSyncCron;
		this.oapiAddress = default_oapiAddress;
		this.messageEnable = default_messageEnable;
		this.workUrl = default_workUrl;
		this.messageRedirectPortal = default_messageRedirectPortal;
	}

	public static String WeLink_Auth_Head_Key = "x-wlk-Authorization";

	private static String cachedAccessToken;
	private static Date cachedAccessTokenDate;

	public static class AccessTokenReq {
		private String client_id;
		private String client_secret;

		public String getClient_id() {
			return client_id;
		}

		public void setClient_id(String client_id) {
			this.client_id = client_id;
		}

		public String getClient_secret() {
			return client_secret;
		}

		public void setClient_secret(String client_secret) {
			this.client_secret = client_secret;
		}
	}

	public static class AccessTokenResp {

		private String access_token;
		private String code;
		private String message;
		private Integer expires_in;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public Integer getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(Integer expires_in) {
			this.expires_in = expires_in;
		}
	}

	/**
	 * 获取WeLink AccessToken
	 * 
	 * @return
	 * @throws Exception
	 */
	public String accessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cachedAccessToken) && (null != cachedAccessTokenDate))
				&& (cachedAccessTokenDate.after(new Date()))) {
			return cachedAccessToken;
		} else {
			String address = this.getOapiAddress() + "/auth/v2/tickets";
			AccessTokenReq req = new AccessTokenReq();
			req.setClient_id(this.getClientId());
			req.setClient_secret(this.getClientSecret());
			AccessTokenResp resp = HttpConnection.postAsObject(address, null, XGsonBuilder.instance().toJson(req),
					AccessTokenResp.class);
			if (!resp.getCode().equals("0")) {
				throw new ExceptionWeLinkAccessToken(resp.getCode(), resp.getMessage());
			}
			cachedAccessToken = resp.getAccess_token();
			Integer second = resp.expires_in;// 过期时间 秒
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, (second - 300));
			cachedAccessTokenDate = cal.getTime();
			return cachedAccessToken;
		}
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getClientId() {
		return StringUtils.isEmpty(this.clientId) ? default_clientId : this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return StringUtils.isEmpty(this.clientSecret) ? default_clientSecret : this.clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getSyncCron() {
		return StringUtils.isEmpty(this.syncCron) ? default_syncCron : this.syncCron;
	}

	public void setSyncCron(String syncCron) {
		this.syncCron = syncCron;
	}

	public String getForceSyncCron() {
		return StringUtils.isEmpty(this.forceSyncCron) ? default_forceSyncCron : this.forceSyncCron;
	}

	public void setForceSyncCron(String forceSyncCron) {
		this.forceSyncCron = forceSyncCron;
	}

	public String getOapiAddress() {
		return StringUtils.isEmpty(this.oapiAddress) ? default_oapiAddress : this.oapiAddress;
	}

	public void setOapiAddress(String oapiAddress) {
		this.oapiAddress = oapiAddress;
	}

	public Boolean getMessageEnable() {
		return BooleanUtils.isTrue(this.messageEnable);
	}

	public void setMessageEnable(Boolean messageEnable) {
		this.messageEnable = messageEnable;
	}

	public String getWorkUrl() {
		return StringUtils.isEmpty(this.workUrl) ? default_workUrl : this.workUrl;
	}

	public void setWorkUrl(String workUrl) {
		this.workUrl = workUrl;
	}

	public String getMessageRedirectPortal() {
		return StringUtils.isEmpty(this.messageRedirectPortal) ? default_messageRedirectPortal
				: this.messageRedirectPortal;
	}

	public void setMessageRedirectPortal(String messageRedirectPortal) {
		this.messageRedirectPortal = messageRedirectPortal;
	}
}
