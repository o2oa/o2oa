package com.x.program.init.jaxrs.server;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.init.Missions;

class ActionExecuteStatus extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionExecuteStatus.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) {

		ActionResult<Wo> result = new ActionResult<>();
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		Missions.ExecuteStatus executeStatus = Missions.getExecuteStatus();
		Wo wo = new Wo();
		wo.setMessages(executeStatus.getMessages());
		wo.setStatus(executeStatus.getStatus());
		wo.setFailureMessage(executeStatus.getFailureMessage());
		result.setData(wo);
		return result;
	}

	public static class Wo extends Missions.ExecuteStatus {

		private static final long serialVersionUID = 2739113567324714648L;

	}

}