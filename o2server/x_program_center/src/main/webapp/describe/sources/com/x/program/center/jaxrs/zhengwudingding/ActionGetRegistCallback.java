package com.x.program.center.jaxrs.zhengwudingding;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ZhengwuDingding.AppAccessTokenResp;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionGetRegistCallback extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetRegistCallback.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		// https://[ip]:[port]/oapi/call_back/get?access_token=ACCESS_TOKEN
		String address = Config.zhengwuDingding().getOapiAddress() + "/call_back/get?access_token="
				+ Config.zhengwuDingding().appAccessToken();
		AppAccessTokenResp resp = HttpConnection.getAsObject(address, null, AppAccessTokenResp.class);
		if (resp.getRetCode() != 0) {
			throw new ExceptionGetRegistCallback(resp.getRetCode(), resp.getRetMessage());
		}
		Wo wo = XGsonBuilder.convert(resp, Wo.class);
		result.setData(wo);
		return result;
	}

	public static class Wo extends GetRegistCallbackRasp {
	}

	public static class GetRegistCallbackRasp {
		private Integer retCode;
		private String retMessage;
		private String url;
		private List<String> call_back_tag = new ArrayList<>();

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

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public List<String> getCall_back_tag() {
			return call_back_tag;
		}

		public void setCall_back_tag(List<String> call_back_tag) {
			this.call_back_tag = call_back_tag;
		}
	}

}
