package com.x.organization.assemble.personal.jaxrs.regist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wx.pwd.CheckStrength;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutInteger;

class ActionCheckPassword extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCheckPassword.class);

	ActionResult<WrapOutInteger> execute(String password) throws Exception {
		ActionResult<WrapOutInteger> result = new ActionResult<>();
		WrapOutInteger wrap = new WrapOutInteger();
		wrap.setValue(CheckStrength.checkPasswordStrength(password));
		result.setData(wrap);
		return result;
	}

}
