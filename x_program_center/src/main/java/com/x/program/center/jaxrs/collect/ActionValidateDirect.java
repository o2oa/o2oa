package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;
import com.x.program.center.jaxrs.collect.wrapin.WrapInCollect;

class ActionValidateDirect extends ActionBase {

	ActionResult<WrapOutBoolean> execute(JsonElement jsonElement) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapInCollect wrapIn = this.convertToWrapIn(jsonElement, WrapInCollect.class);
		if (!this.connect()) {
			throw new UnableConnectException();
		}
		String name = wrapIn.getName();
		String password = wrapIn.getPassword();
		if (StringUtils.isEmpty(name)) {
			throw new NameEmptyException();
		}
		if (StringUtils.isEmpty(password)) {
			throw new PasswordEmptyException();
		}
		if (!this.validate(name, password)) {
			throw new InvalidCredentialException();
		}
		result.setData(WrapOutBoolean.trueInstance());
		return result;
	}

}
