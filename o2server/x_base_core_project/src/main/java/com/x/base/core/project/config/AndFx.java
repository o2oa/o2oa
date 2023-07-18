package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;

public class AndFx extends ConfigObject {

	@FieldDescribe("是否启用.")
	private Boolean enable;
	@FieldDescribe("人员组织同步cron表达式,例如在每天的8点和12点进行同步：10 45 8,12 * * ?.")
	private String forceSyncCron;
	@FieldDescribe("是否开启人员组织同步,默认false")
	private Boolean forceSyncEnable;
	@FieldDescribe("是否同步离职人员,默认false（即人员离职需要在O2手工删除）")
	private Boolean syncDeleteUser;
	@FieldDescribe("认证api服务地址")
	private String ssoApi;
	@FieldDescribe("统一认证sourceId")
	private String sourceId;
	@FieldDescribe("统一认证sourceKey")
	private String sourceKey;
	@FieldDescribe("客户端ID")
	private String clientId;
	@FieldDescribe("企业ID")
	private String enterId;
	@FieldDescribe("通讯录api服务地址")
	private String addressApi;
	@FieldDescribe("通讯录appKey")
	private String addressAppKey;
	@FieldDescribe("通讯录appSecret")
	private String addressAppSecret;
	@FieldDescribe("消息推送api服务地址")
	private String msgApi;
	@FieldDescribe("消息推送appKey")
	private String msgAppKey;
	@FieldDescribe("消息推送appSecret")
	private String msgAppSecret;
	@FieldDescribe("消息推送发送者手机号")
	private String msgSender;
	@FieldDescribe("消息推送行业消息类型")
	private String msgType;
	@FieldDescribe("消息盒子标题")
	private String msgBoxTitle;
	@FieldDescribe("消息打开工作的url地址，如：https://sample.o2oa.net/x_desktop/")
	private String workUrl = "";
	@FieldDescribe("消息处理完成后跳转到特定的门户页面的Id")
	private String messageRedirectPortal = "";
	@FieldDescribe("启用消息推送.")
	private Boolean messageEnable;

	public static AndFx defaultInstance() {
		return new AndFx();
	}

	public static final Boolean default_enable = false;
	public static final String default_forceSyncCron = "10 45 8,12 * * ?";
	public static final Boolean default_forceSyncEnable = false;
	public static final Boolean default_syncDeleteUser = true;
	public static final String default_ssoApi = "https://token.cmpassport.com:8300/uniapi/uniTokenValidate";
	public static final String default_sourceId = "001311";
	public static final String default_sourceKey = "";
	public static final String default_clientId = "5d3fdc22dbdd7a668be5fbff";
	public static final String default_enterId = "100000050008";
	public static final String default_addressApi = "https://test.oa.cmzq-office.com";
	public static final String default_addressAppKey = "hasuec12469b31b2d1es86syuvdxd7s6";
	public static final String default_addressAppSecret = "";
	public static final String default_msgApi = "https://test.yd.cmzq-office.com/v1/origin/corporate/api/corporateApi/corporateMessages";
	public static final String default_msgAppKey = "182461ai23etc63595d26jhsb7e0dcgs";
	public static final String default_msgAppSecret = "";
	public static final String default_msgSender = "10086102021";
	public static final String default_msgType = "102021";
	public static final String default_msgBoxTitle = "智慧办公消息";
	public static final String default_workUrl = "http://demo001.openlc.net/x_desktop/";
	public static final String default_messageRedirectPortal = "";
	public static final Boolean default_messageEnable = false;

	public AndFx() {
		this.enable = default_enable;
		this.forceSyncCron = default_forceSyncCron;
		this.forceSyncEnable = default_forceSyncEnable;
		this.syncDeleteUser = default_syncDeleteUser;
		this.ssoApi = default_ssoApi;
		this.sourceId = default_sourceId;
		this.sourceKey = default_sourceKey;
		this.clientId = default_clientId;
		this.enterId = default_enterId;
		this.addressApi = default_addressApi;
		this.addressAppKey = default_addressAppKey;
		this.addressAppSecret = default_addressAppSecret;
		this.msgApi = default_msgApi;
		this.msgAppKey = default_msgAppKey;
		this.msgAppSecret = default_msgAppSecret;
		this.msgAppSecret = default_msgAppSecret;
		this.msgSender = default_msgSender;
		this.msgType = default_msgType;
		this.msgBoxTitle = default_msgBoxTitle;
		this.workUrl = default_workUrl;
		this.messageRedirectPortal = default_messageRedirectPortal;
		this.messageEnable = default_messageEnable;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getForceSyncCron() {
		return forceSyncCron;
	}

	public void setForceSyncCron(String forceSyncCron) {
		this.forceSyncCron = forceSyncCron;
	}

	public String getSsoApi() {
		return ssoApi;
	}

	public void setSsoApi(String ssoApi) {
		this.ssoApi = ssoApi;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
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

	public Boolean getMessageEnable() {
		return messageEnable;
	}

	public void setMessageEnable(Boolean messageEnable) {
		this.messageEnable = messageEnable;
	}

	public String getAddressAppKey() {
		return addressAppKey;
	}

	public void setAddressAppKey(String addressAppKey) {
		this.addressAppKey = addressAppKey;
	}

	public String getAddressAppSecret() {
		return addressAppSecret;
	}

	public void setAddressAppSecret(String addressAppSecret) {
		this.addressAppSecret = addressAppSecret;
	}

	public String getEnterId() {
		return enterId;
	}

	public void setEnterId(String enterId) {
		this.enterId = enterId;
	}

	public String getAddressApi() {
		return addressApi;
	}

	public void setAddressApi(String addressApi) {
		this.addressApi = addressApi;
	}

	public String getMsgAppKey() {
		return msgAppKey;
	}

	public void setMsgAppKey(String msgAppKey) {
		this.msgAppKey = msgAppKey;
	}

	public String getMsgAppSecret() {
		return msgAppSecret;
	}

	public void setMsgAppSecret(String msgAppSecret) {
		this.msgAppSecret = msgAppSecret;
	}

	public String getMsgSender() {
		return msgSender;
	}

	public void setMsgSender(String msgSender) {
		this.msgSender = msgSender;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgApi() {
		return msgApi;
	}

	public void setMsgApi(String msgApi) {
		this.msgApi = msgApi;
	}

	public String getMsgBoxTitle() {
		return msgBoxTitle;
	}

	public void setMsgBoxTitle(String msgBoxTitle) {
		this.msgBoxTitle = msgBoxTitle;
	}

	public Boolean getForceSyncEnable() {
		return forceSyncEnable;
	}

	public void setForceSyncEnable(Boolean forceSyncEnable) {
		this.forceSyncEnable = forceSyncEnable;
	}

	public Boolean getSyncDeleteUser() {
		return syncDeleteUser;
	}

	public void setSyncDeleteUser(Boolean syncDeleteUser) {
		this.syncDeleteUser = syncDeleteUser;
	}
}
