package com.x.organization.assemble.authentication.jaxrs.test;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ZhengwuDingding.AppAccessTokenResp;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest7 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest7.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		String body = "{\r\n" + "   \"agentId\":\"184707353\",\r\n" + "    \"touser\": \"10001461928\",\r\n"
				+ "    \"toparty\": \"\",\r\n" + "    \"msgtype\": \"text\",\r\n" + "   \"context\":\"消息内容,o2oa\"\r\n"
				+ "}";

		String address = Config.zhengwuDingding().getOapiAddress() + "/ent_message/send?access_token="
				+ Config.zhengwuDingding().appAccessToken();
		AppAccessTokenResp resp = HttpConnection.postAsObject(address, null, body, AppAccessTokenResp.class);
		if (resp.getRetCode() != 0) {
			System.out.println("err" + resp.getRetCode() + ", message:" + resp.getRetMessage());
		}
		Wo wo = new Wo();
		wo.setValue(resp.getRetMessage());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapString {

	}

}