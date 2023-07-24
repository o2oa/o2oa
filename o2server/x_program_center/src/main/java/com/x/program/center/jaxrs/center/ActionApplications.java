package com.x.program.center.jaxrs.center;

import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionApplications extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionApplications.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = gson.fromJson(Config.resource_node_applications(), Wo.class);
		result.setData(wo);
		return result;
	}

	public static class Wo extends Applications {

		private static final long serialVersionUID = -7318837875311504064L;

	}

}