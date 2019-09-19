package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.PasswordTools;

class ActionCheckPassword extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCheckPassword.class);

	ActionResult<Wo> execute(String password) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(PasswordTools.checkPasswordStrength(password));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapInteger {

	}

}
