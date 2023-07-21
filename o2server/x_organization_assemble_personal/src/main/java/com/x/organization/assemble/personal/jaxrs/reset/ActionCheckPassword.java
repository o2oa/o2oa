package com.x.organization.assemble.personal.jaxrs.reset;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapString;

class ActionCheckPassword extends BaseAction {

	ActionResult<Wo> execute(String password) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		if (!password.matches(Config.person().getPasswordRegex())) {
			wo.setValue(Config.person().getPasswordRegexHint());
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapString {
	}

}
