package com.x.organization.assemble.authentication.jaxrs.test;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest7 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest7.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		String name = "张三你好";
		Wo wo = new Wo();
		wo.setText(name);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoText {

	}

}