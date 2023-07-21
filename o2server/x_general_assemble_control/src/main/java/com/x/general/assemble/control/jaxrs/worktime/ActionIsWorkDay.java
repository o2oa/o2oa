package com.x.general.assemble.control.jaxrs.worktime;

import java.util.Date;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.DateTools;

public class ActionIsWorkDay extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String date) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Date dateObject = DateTools.parse(date);
		Wo wo = new Wo();
		wo.setValue(Config.workTime().isWorkDay(dateObject));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}
