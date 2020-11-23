package com.x.general.assemble.control.jaxrs.worktime;

import java.util.Date;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.DateTools;

public class ActionInDefinedWorkDay extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String date) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Date value = DateTools.parse(date);
		Wo wo = new Wo();
		wo.setValue(Config.workTime().inDefinedWorkday(value));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}
