package com.x.program.center.jaxrs.agent;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.program.center.core.entity.Agent;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Agent agent = emc.flag(flag, Agent.class);
			if (null == agent) {
				throw new ExceptionAgentNotExist(flag);
			}
			emc.beginTransaction(Agent.class);
			emc.remove(agent, CheckRemoveType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(agent.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}