package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import com.wx.pwd.CheckStrength;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutInteger;

class ActionCheckPassword extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCheckPassword.class);

	ActionResult<WrapOutInteger> execute(String password) throws Exception {
		ActionResult<WrapOutInteger> result = new ActionResult<>();
		WrapOutInteger wrap = new WrapOutInteger();
		wrap.setValue(CheckStrength.checkPasswordStrength(password));
		result.setData(wrap);
		return result;
	}

}
