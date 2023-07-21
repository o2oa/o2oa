package com.x.general.assemble.control.jaxrs.worktime;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapInteger;

public class ActionMinutesOfWorkDay extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue((int) Config.workTime().minutesOfWorkDay());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapInteger {

	}

}
