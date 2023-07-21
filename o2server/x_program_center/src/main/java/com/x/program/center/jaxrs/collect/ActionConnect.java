package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionConnect extends BaseAction {

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		if (BooleanUtils.isNotTrue(this.connect())) {
			wo.setValue(false);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

}
