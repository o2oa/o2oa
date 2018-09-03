package com.x.base.core.project.config;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Qiyeweixin extends GsonPropertyObject {

	@FieldDescribe("企业微信corpId")
	private String corpId;
	@FieldDescribe("企业微信corpSecret")
	private String corpSecret;
	@FieldDescribe("组织与企业微信同步方向pull,push,disable")
	private String syncOrganizationDirection;

	@FieldDescribe("拉入同步间隔")
	private Integer pullSyncOrganizationInterval;
	@FieldDescribe("强制拉入全部同步间隔(分钟)")
	private Integer forcePullSyncOrganizationInterval;

	public static Qiyeweixin defaultInstance() {
		return new Qiyeweixin();
	}

	public static final String default_corpId = "";
	public static final String default_corpSecret = "";
	public static final String default_syncOrganizationDirection = "disable";
	public static final String SYNCORGANIZATIONDIRECTION_PULL = "pull";
	public static final String SYNCORGANIZATIONDIRECTION_PUSH = "push";
	public static final Integer default_pullSyncOrganizationInterval = 30;
	public static final Integer default_forcePullSyncOrganizationInterval = 180;

	public Qiyeweixin() {
		this.corpId = default_corpId;
		this.corpSecret = default_corpSecret;
		this.syncOrganizationDirection = default_syncOrganizationDirection;
	}

	public String getSyncOrganizationDirection() {
		return StringUtils.isEmpty(syncOrganizationDirection) ? default_syncOrganizationDirection
				: syncOrganizationDirection;
	}

	public Integer getForcePullSyncOrganizationInterval() {
		if (null == this.forcePullSyncOrganizationInterval || this.forcePullSyncOrganizationInterval < 0) {
			return default_forcePullSyncOrganizationInterval;
		} else {
			return this.forcePullSyncOrganizationInterval;
		}
	}

	public Integer getPullSyncOrganizationInterval() {
		if (null == this.pullSyncOrganizationInterval || this.pullSyncOrganizationInterval < 0) {
			return default_pullSyncOrganizationInterval;
		}
		return this.pullSyncOrganizationInterval;
	}

	public Integer getFullPullSyncOrganizationInterval() {
		if (null == this.forcePullSyncOrganizationInterval || this.forcePullSyncOrganizationInterval < 0) {
			return default_forcePullSyncOrganizationInterval;
		}
		return this.forcePullSyncOrganizationInterval;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getCorpSecret() {
		return corpSecret;
	}

	public void setCorpSecret(String corpSecret) {
		this.corpSecret = corpSecret;
	}

	public String corp_access_token() throws Exception {
		String address = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpId + "&corpsecret=" + corpSecret;
		AccessTokenReq req = HttpConnection.getAsObject(address, null, AccessTokenReq.class);
		if (req.getErrcode() != 0) {
			throw new ExceptionQyweixinCorpAccessToken(req.getErrmsg());
		}
		return req.getAccess_token();
	}

	public static class AccessTokenReq {

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

	public void setSyncOrganizationDirection(String syncOrganizationDirection) {
		this.syncOrganizationDirection = syncOrganizationDirection;
	}

	public void setPullSyncOrganizationInterval(Integer pullSyncOrganizationInterval) {
		this.pullSyncOrganizationInterval = pullSyncOrganizationInterval;
	}

	public void setForcePullSyncOrganizationInterval(Integer forcePullSyncOrganizationInterval) {
		this.forcePullSyncOrganizationInterval = forcePullSyncOrganizationInterval;
	}

}
