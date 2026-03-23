package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class ActionCanRead extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCanRead.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		wo.setValue(this.isReader(id, effectivePerson));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}
