package com.x.program.center.jaxrs.agent;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.core.entity.Agent;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Agent agent = emc.flag(flag, Agent.class );
			if (null == agent) {
				throw new ExceptionAgentNotExist(flag);
			}
			Wo wo = Wo.copier.copy(agent);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Agent {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Agent, Wo> copier = WrapCopierFactory.wo(Agent.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));
	}

}