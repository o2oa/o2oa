package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionDisconnect extends BaseAction {

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		if (BooleanUtils.isNotTrue(Config.miscellaneous().getConfigApiEnable())) {
			throw new ExceptionModifyConfig();
		}
		wo.setValue(true);
		Config.collect().setEnable(false);
		Config.collect().save();
		Config.flush();
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

}
