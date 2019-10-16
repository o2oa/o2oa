package com.x.calendar.assemble.control.jaxrs.test;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.assemble.control.schedule.AlarmTrigger;

public class ActionTest1 extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(ActionTest1.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		ThisApplication.context().scheduleLocal(AlarmTrigger.class,1);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}