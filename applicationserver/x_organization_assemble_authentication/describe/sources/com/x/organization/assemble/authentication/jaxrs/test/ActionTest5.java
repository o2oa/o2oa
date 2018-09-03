package com.x.organization.assemble.authentication.jaxrs.test;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ZhengwuDingding.AppAccessTokenResp;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest5 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest5.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		String body = "{\r\n" + "    \"title\":\"大学生运动会\",\r\n" + "    \"sponsorUserId\":\"10001461928\",\r\n"
				+ "    \"level\":\"0\",\r\n" + "    \"startTime\":\"1504840278000\",\r\n"
				+ "    \"endTime\":\"1504840278000\",\r\n" + "    \"handleUserIdList\":[10001461928],\r\n"
				+ "    \"callbackUrl\":\"sport/sport-manage\"\r\n" + ",\"agentId\":\"184707353\"}";

		String address = Config.zhengwuDingding().getOapiAddress() + "/backlog/publish?access_token="
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
	// {
	// "agentId":"XXXXX",
	// "name":"中小企业服务",
	// "type":"0",
	// "urlPrefix":"www.zjszw.com"
	// }

	public static class Wo extends WrapString {

	}

}