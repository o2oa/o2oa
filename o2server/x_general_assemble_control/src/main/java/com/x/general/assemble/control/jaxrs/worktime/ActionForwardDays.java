package com.x.general.assemble.control.jaxrs.worktime;

import java.util.Date;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.tools.DateTools;

public class ActionForwardDays extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String start, long days) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Date dateOfStart = DateTools.parse(start);
		Wo wo = new Wo();
		wo.setValue(DateTools
				.format(Config.workTime().forwardMinutes(dateOfStart, days * Config.workTime().minutesOfWorkDay())));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapString {

	}

}
