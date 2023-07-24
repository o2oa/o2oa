package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionCheckPassword extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheckPassword.class);

	ActionResult<Wo> execute(String password) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		if (!password.matches(Config.person().getPasswordRegex())) {
			throw new ExceptionInvalidPassword(Config.person().getPasswordRegexHint());
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapString {

	}

}
