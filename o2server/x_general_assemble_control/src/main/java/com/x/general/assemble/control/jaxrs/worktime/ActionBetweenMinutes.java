package com.x.general.assemble.control.jaxrs.worktime;

import java.util.Date;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.tools.DateTools;

public class ActionBetweenMinutes extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String start, String end) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Date dateOfStart = DateTools.parse(start);
		Date dateOfEnd = DateTools.parse(end);
		Wo wo = new Wo();
		wo.setValue((int) Config.workTime().betweenMinutes(dateOfStart, dateOfEnd));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapInteger {

	}

}
