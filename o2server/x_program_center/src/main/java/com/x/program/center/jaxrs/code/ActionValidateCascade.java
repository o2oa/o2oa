package com.x.program.center.jaxrs.code;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.ThisApplication;

class ActionValidateCascade extends BaseAction {
	ActionResult<Wo> execute(String mobile, String answer) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo;
		String customSms = ThisApplication.context().applications().findApplicationName(CUSTOM_SMS_APPLICATION);
		if(StringUtils.isNotBlank(customSms) && Config.customConfig(CUSTOM_SMS_CONFIG_NAME) != null){
			ActionResponse resp = ThisApplication.context().applications()
					.getQuery(customSms, Applications.joinQueryUri("sms", "validate", "mobile", mobile, "answer", answer, "token", "(0)"));
			wo = resp.getData(Wo.class);
		}else {
			ActionResponse resp = ConnectionAction.get(Config.collect().url()
					+ "/o2_collect_assemble/jaxrs/code/validate/mobile/" + mobile + "/answer/" + answer, null);
			wo = resp.getData(Wo.class);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

}
