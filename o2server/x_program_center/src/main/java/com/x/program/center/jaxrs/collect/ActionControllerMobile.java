package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionControllerMobile extends BaseAction {

	ActionResult<Wo> execute(String name, String mobile) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (BooleanUtils.isNotTrue(this.connect())) {
			throw new ExceptionUnableConnect();
		}
		Wo wo = new Wo();
		wo.setValue(this.controllerMobile(name, mobile));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

}
