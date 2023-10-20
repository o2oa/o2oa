package com.x.organization.assemble.authentication.jaxrs.dingding;

import java.security.MessageDigest;
import java.util.Formatter;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionInfo extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionInfo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		String nonceStr = "o2oa";
		long timeStamp = System.currentTimeMillis() / 1000;
		String jsticket = Config.dingding().getJsapiTicket();
		String signature = this.getSignature(effectivePerson, jsticket, nonceStr, timeStamp, wi.getUrl());
		String agentid = Config.dingding().getAgentId();
		Wo wo = new Wo();
		wo.setJsticket(jsticket);
		wo.setAgentid(agentid);
		wo.setCorpId(Config.dingding().getCorpId());
		wo.setNonceStr(nonceStr);
		wo.setSignature(signature);
		wo.setTimeStamp(timeStamp);
		result.setData(wo);
		return result;
	}

	private String getSignature(EffectivePerson effectivePerson, String ticket, String nonceStr, long timeStamp,
			String url) throws Exception {
		String plain = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + String.valueOf(timeStamp)
				+ "&url=" + url;
		if (logger.isDebugEnabled()) {
			logger.debug("person {} , signature stirng:{}", effectivePerson, plain);
		}
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		sha1.reset();
		sha1.update(plain.getBytes("UTF-8"));
		return bytesToHex(sha1.digest());
	}

	private String bytesToHex(byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -5190514898231735375L;
		@FieldDescribe("签名地址")
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

	public static class Wo extends GsonPropertyObject {
		private String jsticket;
		private String signature;
		private String nonceStr;
		private Long timeStamp;
		private String corpId;
		private String agentid;

		public String getJsticket() {
			return jsticket;
		}

		public void setJsticket(String jsticket) {
			this.jsticket = jsticket;
		}

		public String getSignature() {
			return signature;
		}

		public void setSignature(String signature) {
			this.signature = signature;
		}

		public String getNonceStr() {
			return nonceStr;
		}

		public void setNonceStr(String nonceStr) {
			this.nonceStr = nonceStr;
		}

		public String getAgentid() {
			return agentid;
		}

		public void setAgentid(String agentid) {
			this.agentid = agentid;
		}

		public Long getTimeStamp() {
			return timeStamp;
		}

		public void setTimeStamp(Long timeStamp) {
			this.timeStamp = timeStamp;
		}

		public String getCorpId() {
			return corpId;
		}

		public void setCorpId(String corpId) {
			this.corpId = corpId;
		}


	}

}