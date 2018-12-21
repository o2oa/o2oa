package com.x.program.center.jaxrs.code;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionValidateCascade extends BaseAction {
	ActionResult<Wo> execute(String mobile, String answer) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ActionResponse resp = ConnectionAction.get(Config.collect().url()
				+ "/o2_collect_assemble/jaxrs/code/validate/mobile/" + mobile + "/answer/" + answer, null);
		Wo wo = new Wo();
		wo.setValue(resp.getData(Wo.class).getValue());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

}