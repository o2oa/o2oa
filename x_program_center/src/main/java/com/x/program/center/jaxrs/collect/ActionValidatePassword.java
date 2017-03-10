package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.wx.pwd.CheckStrength;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutInteger;
import com.x.program.center.jaxrs.collect.wrapin.WrapInCollect;

class ActionValidatePassword extends ActionBase {

	ActionResult<WrapOutInteger> execute(JsonElement jsonElement) throws Exception {
		ActionResult<WrapOutInteger> result = new ActionResult<>();
		WrapInCollect wrapIn = this.convertToWrapIn(jsonElement, WrapInCollect.class);
		WrapOutInteger wrap = new WrapOutInteger();
		String password = wrapIn.getPassword();
		if (StringUtils.isEmpty(password)) {
			throw new PasswordEmptyException();
		}
		wrap.setValue(CheckStrength.checkPasswordStrength(password));
		result.setData(wrap);
		return result;
	}

}
