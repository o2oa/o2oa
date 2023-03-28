package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;

public class AndFx extends ConfigObject {

	@FieldDescribe("是否启用.")
	private Boolean enable;
	@FieldDescribe("强制拉入同步cron,默认在每天的8点和12点强制进行同步.")
	private String forceSyncCron;
	@FieldDescribe("移动办公认证api服务地址")
	private String ssoApi;
	@FieldDescribe("移动办公统一认证sourceId")
	private String sourceId;
	@FieldDescribe("移动办公统一认证sourceKey")
	private String sourceKey;
	@FieldDescribe("移动办公客户端ID")
	private String clientId;
	@FieldDescribe("移动办公企业ID")
	private String enterId;
	@FieldDescribe("移动办公通讯录api服务地址")
	private String addressApi;
	@FieldDescribe("移动办公通讯录appKey")
	private String addressAppKey;
	@FieldDescribe("移动办公通讯录appSecret")
	private String addressAppSecret;
	@FieldDescribe("移动办公消息打开工作的url地址，如：https://sample.o2oa.net/x_desktop/")
	private String workUrl = "";
	@FieldDescribe("移动办公消息处理完成后跳转到特定的门户页面的Id")
	private String messageRedirectPortal = "";
	@FieldDescribe("推送消息到移动办公")
	private Boolean messageEnable;

	public static AndFx defaultInstance() {
		return new AndFx();
	}

	public static final Boolean default_enable = false;
	public static final String default_forceSyncCron = "10 45 8,12 * * ?";
	public static final String default_ssoApi = "https://token.cmpassport.com:8300/uniapi/uniTokenValidate";
	public static final String default_sourceId = "001311";
	public static final String default_sourceKey = "wHKFp0zRCXdkSXxl";
	public static final String default_clientId = "5d3fdc22dbdd7a668be5fbff";
	public static final String default_enterId = "100000050008";
	public static final String default_addressApi = "https://test.oa.cmzq-office.com";
	public static final String default_addressAppKey = "hasuec12469b31b2d1es86syuvdxd7s6";
	public static final String default_addressAppSecret = "15v6675ssua8n9rtyphfb4ijgd9b6u6yh";
	public static final String default_workUrl = "";
	public static final String default_messageRedirectPortal = "";
	public static final Boolean default_messageEnable = false;

	public AndFx() {
		this.enable = default_enable;
		this.forceSyncCron = default_forceSyncCron;
		this.ssoApi = default_ssoApi;
		this.sourceId = default_sourceId;
		this.sourceKey = default_sourceKey;
		this.clientId = default_clientId;
		this.enterId = default_enterId;
		this.addressApi = default_addressApi;
		this.addressAppKey = default_addressAppKey;
		this.addressAppSecret = default_addressAppSecret;
		this.messageEnable = default_messageEnable;
		this.workUrl = default_workUrl;
		this.messageRedirectPortal = default_messageRedirectPortal;
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
}
