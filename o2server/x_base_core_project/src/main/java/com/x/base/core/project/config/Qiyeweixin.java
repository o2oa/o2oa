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
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;

public class Qiyeweixin extends ConfigObject {

	@FieldDescribe("是否启用.")
	private Boolean enable;
	@FieldDescribe("回调信号触发同步检查,默认每10分钟运行一次,如果期间内有企业微信回调信号接收到,那么触发同步任务进行人员同步.")
	private String syncCron;
	@FieldDescribe("强制拉入同步cron,默认在每天的8点和12点强制进行同步.")
	private String forceSyncCron;
	@FieldDescribe("api服务器地址")
	private String apiAddress;
	@FieldDescribe("oauth2请求地址")
	private String oauth2Address;
	@FieldDescribe("qrConnect请求地址")
	private String qrConnectAddress;
	@FieldDescribe("企业微信corpId")
	private String corpId;
	@FieldDescribe("企业微信同步通讯录Secret")
	private String syncSecret;
	@FieldDescribe("企业微信corpSecret")
	private String corpSecret;
	@FieldDescribe("企业微信agentId")
	private String agentId;
	@FieldDescribe("回调token")
	private String token = "";
	@FieldDescribe("回调encodingAesKey")
	private String encodingAesKey = "";
	@FieldDescribe("企业微信消息打开工作的url地址，如：https://sample.o2oa.net/x_desktop/")
	private String workUrl = "";
	@FieldDescribe("企业微信消息处理完成后跳转到特定的门户页面的Id")
	private String messageRedirectPortal = "";
	@FieldDescribe("推送消息到企业微信")
	private Boolean messageEnable;
	@FieldDescribe("是否启用用户绑定，私有化绑定用户用的，和同步冲突")
	private Boolean bindEnable;

	@FieldDescribe("企业微信扫码登录")
	private Boolean scanLoginEnable;
	@FieldDescribe("是否启用考勤信息")
	private Boolean attendanceSyncEnable;
	@FieldDescribe("企业微信考勤打卡应用id")
	private String attendanceSyncAgentId;
	@FieldDescribe("企业微信考勤打卡应用secret")
	private String attendanceSyncSecret;

	public static Qiyeweixin defaultInstance() {
		return new Qiyeweixin();
	}

	public static final Boolean default_enable = false;
	public static final String default_syncCron = "10 0/10 * * * ?";
	public static final String default_forceSyncCron = "10 45 8,12 * * ?";
	public static final String default_apiAddress = "https://qyapi.weixin.qq.com";
	public static final String default_oAuth2Address = "https://open.weixin.qq.com";
	public static final String default_qrConnectAddress = "https://open.work.weixin.qq.com";
	public static final String default_corpId = "";
	public static final String default_corpSecret = "";
	public static final String default_syncSecret = "";
	public static final String default_agentId = "";
	public static final String default_workUrl = "";
	public static final String default_messageRedirectPortal = "";
	public static final Boolean default_messageEanble = false;
	public static final Boolean default_bindEnable = false;
	public static final Boolean default_scanLoginEnable = false;
	public static final Boolean default_attendanceSyncEnable = false;

	public Qiyeweixin() {
		this.enable = default_enable;
		this.syncCron = default_syncCron;
		this.forceSyncCron = default_forceSyncCron;
		this.apiAddress = default_apiAddress;
		this.oauth2Address = default_oAuth2Address;
		this.qrConnectAddress = default_qrConnectAddress;
		this.corpId = default_corpId;
		this.syncSecret = default_syncSecret;
		this.corpSecret = default_corpSecret;
		this.agentId = default_agentId;
		this.messageEnable = default_messageEanble;
		this.workUrl = default_workUrl;
		this.messageRedirectPortal = default_messageRedirectPortal;
		this.scanLoginEnable = default_scanLoginEnable;
		this.attendanceSyncEnable = default_attendanceSyncEnable;
		this.attendanceSyncAgentId = "";
		this.attendanceSyncSecret = "";
		this.bindEnable = default_bindEnable;
	}

	private static String cacheAttendanceAccessToken;
	private static Date cacheAttendanceAccessTokenDate;

	private static String cachedCorpAccessToken;
	private static Date cachedCorpAccessTokenDate;

	private static String cachedSyncAccessToken;
	private static Date cachedSyncAccessTokenDate;

	private static String cachedJsapiTicket;
	private static Date cachedJsapiTicketDate;

	private static String cachedAppJsapiTicket;
	private static Date cachedAppJsapiTicketDate;


	public String getSyncSecret() {
		return StringUtils.isEmpty(syncSecret) ? default_syncSecret : this.syncSecret;
	}

	public String getAgentId() {
		return StringUtils.isEmpty(this.agentId) ? default_agentId : this.agentId;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public Boolean getMessageEnable() {
		return BooleanUtils.isTrue(this.messageEnable);
	}
//
//	public String getSyncCron() {
//		return StringUtils.isEmpty(this.syncCron) ? default_syncCron : this.syncCron;
//	}
	// 清空表达式 不执行同步操作
	public String getSyncCron() {
		return this.syncCron;
	}

//	public String getForceSyncCron() {
//		return StringUtils.isEmpty(this.forceSyncCron) ? default_forceSyncCron : this.forceSyncCron;
//	}

	public String getForceSyncCron() {
		return this.forceSyncCron;
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

	public String getOauth2Address() {
		return StringUtils.isEmpty(this.oauth2Address) ? default_oAuth2Address : this.oauth2Address;
	}

	public String getQrConnectAddress() {
		return StringUtils.isEmpty(this.qrConnectAddress) ? default_qrConnectAddress : this.qrConnectAddress;
	}

	public String corpAccessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cachedCorpAccessToken) && (null != cachedCorpAccessTokenDate))
				&& (cachedCorpAccessTokenDate.after(new Date()))) {
			return cachedCorpAccessToken;
		} else {
			String address = getApiAddress() + "/cgi-bin/gettoken?corpid=" + this.getCorpId() + "&corpsecret="
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

	public String attendanceAccessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cacheAttendanceAccessToken) && (null != cacheAttendanceAccessTokenDate))
				&& (cacheAttendanceAccessTokenDate.after(new Date()))) {
			return cacheAttendanceAccessToken;
		} else {
			String address = getApiAddress() + "/cgi-bin/gettoken?corpid=" + this.getCorpId() + "&corpsecret="
					+ this.getAttendanceSyncSecret();
			CorpAccessTokenResp resp = HttpConnection.getAsObject(address, null, CorpAccessTokenResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionQiyeweixinCorpAccessToken(resp.getErrcode(), resp.getErrmsg());
			}
			cacheAttendanceAccessToken = resp.getAccess_token();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 90);

			cacheAttendanceAccessTokenDate = cal.getTime();
			return cacheAttendanceAccessToken;
		}
	}

	public String syncAccessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cachedSyncAccessToken) && (null != cachedSyncAccessTokenDate))
				&& (cachedSyncAccessTokenDate.after(new Date()))) {
			return cachedSyncAccessToken;
		} else {
			String address = getApiAddress() + "/cgi-bin/gettoken?corpid=" + this.getCorpId() + "&corpsecret="
					+ this.getCorpSecret();
			System.out.println("address：" + address);
			CorpAccessTokenResp resp = HttpConnection.getAsObject(address, null, CorpAccessTokenResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionQiyeweixinCorpAccessToken(resp.getErrcode(), resp.getErrmsg());
			}
			cachedSyncAccessToken = resp.getAccess_token();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 90);
			cachedSyncAccessTokenDate = cal.getTime();
			return cachedSyncAccessToken;
		}
	}

	/**
	 * 获取企业的jsapi_ticket
	 * @return
	 * @throws Exception
	 */
	public String getJsapiTicket() throws Exception {
		if ((StringUtils.isNotEmpty(cachedJsapiTicket) && (null != cachedJsapiTicketDate))
				&& (cachedJsapiTicketDate.after(new Date()))) {
			return cachedJsapiTicket;
		} else {
			String address = getApiAddress() + "/cgi-bin/get_jsapi_ticket?access_token=" + this.corpAccessToken();
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

	/**
	 * 获取应用的jsapi_ticket
	 * @return
	 * @throws Exception
	 */
	public String getAppJsapiTicket() throws Exception {
		if ((StringUtils.isNotEmpty(cachedAppJsapiTicket) && (null != cachedAppJsapiTicketDate))
				&& (cachedAppJsapiTicketDate.after(new Date()))) {
			return cachedAppJsapiTicket;
		} else {
			String address = getApiAddress() + "/cgi-bin/ticket/get?access_token=" + this.corpAccessToken() + "&type=agent_config";
			JsapiTicketResp resp = HttpConnection.getAsObject(address, null, JsapiTicketResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionZhengwuDingdingJsapiTicket(resp.getErrcode(), resp.getErrmsg());
			}
			cachedAppJsapiTicket = resp.ticket;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 90);
			cachedAppJsapiTicketDate = cal.getTime();
			return cachedAppJsapiTicket;
		}
	}

	
	public void setApiAddress(String oapiAddress) {
		this.apiAddress = oapiAddress;
	}

	public void setOauth2Address(String oauth2Address) {
		this.oauth2Address = oauth2Address;
	}

	public void setQrConnectAddress(String qrConnectAddress) {
		this.qrConnectAddress = qrConnectAddress;
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

	public Boolean getAttendanceSyncEnable() {
		return attendanceSyncEnable;
	}

	public void setAttendanceSyncEnable(Boolean attendanceSyncEnable) {
		this.attendanceSyncEnable = attendanceSyncEnable;
	}

	public String getAttendanceSyncAgentId() {
		return attendanceSyncAgentId;
	}

	public void setAttendanceSyncAgentId(String attendanceSyncAgentId) {
		this.attendanceSyncAgentId = attendanceSyncAgentId;
	}

	public String getAttendanceSyncSecret() {
		return attendanceSyncSecret;
	}

	public void setAttendanceSyncSecret(String attendanceSyncSecret) {
		this.attendanceSyncSecret = attendanceSyncSecret;
	}

	

	public Boolean getBindEnable() {
		return bindEnable;
	}

	public void setBindEnable(Boolean bindEnable) {
		this.bindEnable = bindEnable;
	}

	
	public void setSyncSecret(String syncSecret) {
		this.syncSecret = syncSecret;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_QIYEWEIXIN);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_QIYEWEIXIN);
	}

	// 企业微信的logo 企业微信扫码登录的时候显示用
//	public static final String qywxLogo = "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";
	public static final String qywxLogo = "https://res.o2oa.net/app/qywx-logo.png";
}
