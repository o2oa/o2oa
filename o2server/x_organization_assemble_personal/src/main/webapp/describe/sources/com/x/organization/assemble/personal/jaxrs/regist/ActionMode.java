package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionMode extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionMode.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(Config.person().getRegister());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapString {
	}

}
