package com.x.program.center.jaxrs.collect;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.server.Config;

class ActionValidate extends ActionBase {

	ActionResult<WrapOutBoolean> execute() throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		if (!this.connect()) {
			throw new UnableConnectException();
		}
		if (!this.validate(Config.collect().getName(), Config.collect().getPassword())) {
			throw new InvalidCredentialException();
		}
		result.setData(WrapOutBoolean.trueInstance());
		return result;
	}

}
