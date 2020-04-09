package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionValidate extends BaseAction {

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		if (!this.connect()) {
			wo.setValue(false);
			// throw new ExceptionUnableConnect();
		}
		if (!this.validate(Config.collect().getName(), Config.collect().getPassword())) {
			wo.setValue(false);
			// throw new ExceptionInvalidCredential();
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}
