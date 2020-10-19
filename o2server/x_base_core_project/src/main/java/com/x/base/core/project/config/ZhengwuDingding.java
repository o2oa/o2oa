package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.tools.ListTools;

public class ZhengwuDingding extends ConfigObject {

	@FieldDescribe("是否启用.")
	private Boolean enable;
	@FieldDescribe("政务钉钉appId")
	private String appId;
	@FieldDescribe("政务钉钉appSecret")
	private String appSecret;
	@FieldDescribe("政务钉钉agentId")
	private String agentId;
	@FieldDescribe("回调信号触发同步检查,默认每10分钟运行一次,如果期间内有政务钉钉回调信号接收到,那么触发同步任务进行人员同步.")
	private String syncCron;
	@FieldDescribe("强制拉入同步cron,默认在每天的8点和12点强制进行同步.")
	private String forceSyncCron;
	@FieldDescribe("oapi服务器地址")
	private String oapiAddress;
	@FieldDescribe("政务钉钉corpId")
	private String corpId;
	@FieldDescribe("政务钉钉corpSecret")
	private String corpSecret;
	@FieldDescribe("title分隔符")
	private List<String> titleSplit = new ArrayList<>();
	@FieldDescribe("title对应个人属性名称")
	private String personAttributeTitleName;
	@FieldDescribe("政务钉钉nonce")
	private String nonce;
	@FieldDescribe("推送消息到政务钉钉消息")
	private Boolean messageEnable;

	public static ZhengwuDingding defaultInstance() {
		return new ZhengwuDingding();
	}

	public static final Boolean default_enable = false;
	public static final String default_appId = "";
	public static final String default_appSecret = "";
	public static final String default_agentId = "";
	public static final String default_syncCron = "10 0/10 * * * ?";
	public static final String default_forceSyncCron = "10 45 8,12 * * ?";
	public static final String default_oapiAddress = "https://oapi.dingtalk.com";
	public static final List<String> default_titleSplit = ListTools.toList(",", "、", "，", " ", "　");
	public static final String default_personAttributeTitleName = "职务";
	public static final String default_corpId = "";
	public static final String default_corpSecret = "";
	public static final String default_nonce = "o2oa";
	public static final Boolean default_messageEnable = true;

	public ZhengwuDingding() {
		this.enable = default_enable;
		this.appSecret = default_appSecret;
		this.agentId = default_agentId;
		this.syncCron = default_syncCron;
		this.forceSyncCron = default_forceSyncCron;
		this.oapiAddress = default_oapiAddress;
		this.corpId = default_corpId;
		this.corpSecret = default_corpSecret;
		this.titleSplit = default_titleSplit;
		this.personAttributeTitleName = default_personAttributeTitleName;
		this.nonce = default_nonce;
		this.messageEnable = default_messageEnable;

	}

	private static String cachedAppAccessToken;
	private static Date cachedAppAccessTokenDate;

	private static String cachedCorpAccessToken;
	private static Date cachedCorpAccessTokenDate;

	private static String cachedJsapiTicket;
	private static Date cachedJsapiTicketDate;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public String getAgentId() {
		return StringUtils.isEmpty(this.agentId) ? default_agentId : agentId;
	}

	public Boolean getMessageEnable() {
		return BooleanUtils.isTrue(this.messageEnable);
	}

	public String getPersonAttributeTitleName() {
		return StringUtils.isEmpty(this.personAttributeTitleName) ? default_personAttributeTitleName
				: this.personAttributeTitleName;
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

	public String getNonce() {
		return StringUtils.isEmpty(nonce) ? default_nonce : this.nonce;
	}

	public List<String> getTitleSplit() {
		return ListTools.isEmpty(titleSplit) ? new ArrayList<>(default_titleSplit) : this.titleSplit;
	}

	public String getAppSecret() {
		return StringUtils.isEmpty(appSecret) ? default_appSecret : this.appSecret;
	}

	public String getAppId() {
		return StringUtils.isEmpty(appId) ? default_appId : this.appId;
	}

	public String getOapiAddress() {
		return StringUtils.isEmpty(this.oapiAddress) ? default_oapiAddress : this.oapiAddress;
	}

	public String appAccessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cachedAppAccessToken) && (null != cachedAppAccessTokenDate))
				&& (cachedAppAccessTokenDate.after(new Date()))) {
			return cachedAppAccessToken;
		} else {
			String address = this.getOapiAddress() + "/gettoken?appid=" + this.getAppId() + "&appsecret="
					+ this.getAppSecret();
			AppAccessTokenResp resp = HttpConnection.getAsObject(address, null, AppAccessTokenResp.class);
			if (resp.getRetCode() != 0) {
				throw new ExceptionZhengwuDingdingAppAccessToken(resp.getRetCode(), resp.getRetMessage());
			}
			cachedAppAccessToken = resp.getRetData().getToken();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 90);
			cachedAppAccessTokenDate = cal.getTime();
			return cachedAppAccessToken;
		}
	}

	public String corpAccessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cachedCorpAccessToken) && (null != cachedCorpAccessTokenDate))
				&& (cachedCorpAccessTokenDate.after(new Date()))) {
			return cachedCorpAccessToken;
		} else {
			String address = default_oapiAddress + "/gettoken?corpid=" + this.getCorpId() + "&corpsecret="
					+ this.getCorpSecret();
			CorpAccessTokenResp resp = HttpConnection.getAsObject(address, null, CorpAccessTokenResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionZhengwuDingdingCorpAccessToken(resp.getErrcode(), resp.getErrmsg());
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
			String address = default_oapiAddress + "/get_jsapi_ticket?access_token=" + this.corpAccessToken()
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

	public void setOapiAddress(String oapiAddress) {
		this.oapiAddress = oapiAddress;
	}

	public static class CorpAccessTokenResp {

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

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public void setTitleSplit(List<String> titleSplit) {
		this.titleSplit = titleSplit;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public void setCorpSecret(String corpSecret) {
		this.corpSecret = corpSecret;
	}

	public void setPersonAttributeTitleName(String personAttributeTitleName) {
		this.personAttributeTitleName = personAttributeTitleName;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
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

}
