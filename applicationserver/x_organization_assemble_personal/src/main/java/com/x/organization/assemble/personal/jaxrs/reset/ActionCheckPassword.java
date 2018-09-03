package com.x.organization.assemble.personal.jaxrs.reset;

import com.wx.pwd.CheckStrength;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutInteger;

class ActionCheckPassword extends BaseAction {

	ActionResult<WrapOutInteger> execute(String password) throws Exception {
		ActionResult<WrapOutInteger> result = new ActionResult<>();
		WrapOutInteger wrap = new WrapOutInteger();
		wrap.setValue(CheckStrength.checkPasswordStrength(password));
		result.setData(wrap);
		return result;
	}

}
