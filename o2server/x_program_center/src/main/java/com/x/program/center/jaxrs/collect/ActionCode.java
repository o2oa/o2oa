package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionCode extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String mobile) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		if (BooleanUtils.isNotTrue(this.connect())) {
			throw new ExceptionUnableConnect();
		}
		if (!Config.person().isMobile(mobile)) {
			throw new ExceptionInvalidMobile(mobile);
		}
		wo.setValue(this.code(mobile));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

}
