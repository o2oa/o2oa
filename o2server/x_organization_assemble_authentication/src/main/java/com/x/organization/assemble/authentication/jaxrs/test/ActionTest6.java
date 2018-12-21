package com.x.organization.assemble.authentication.jaxrs.test;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ZhengwuDingding.AppAccessTokenResp;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest6 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest6.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		String body = "	 {\r\n" + "	 \"agentId\":\"184707353\",\r\n" + "	 \"name\":\"用车管理\",\r\n"
				+ "	 \"type\":\"0\",\r\n" + "	 \"urlPrefix\":\"http://60.190.253.249:8001/x_desktop/zhengwuDingdingSso.html\"\r\n" + "	 }";

		String address = Config.zhengwuDingding().getOapiAddress() + "/backlog/register?access_token="
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