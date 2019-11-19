package com.x.program.center.jaxrs.output;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.core.entity.Agent;
import com.x.program.center.core.entity.Invoke;
import com.x.program.center.core.entity.wrap.ServiceModuleEnum;
import com.x.program.center.core.entity.wrap.WrapAgent;
import com.x.program.center.core.entity.wrap.WrapInvoke;
import com.x.program.center.core.entity.wrap.WrapServiceModule;

import java.util.ArrayList;
import java.util.List;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();

			List<Wo> wos = new ArrayList<>();
			Wo wo = Wo.copyFrom(ServiceModuleEnum.AGENT);
			List<WrapAgent> agentList = emc.fetchAll(Agent.class, agentCopier);
			wo.setAgentList(agentList);
			wos.add(wo);
			wo = Wo.copyFrom(ServiceModuleEnum.INVOKE);
			List<WrapInvoke> invokeList = emc.fetchAll(Invoke.class, invokeCopier);
			wo.setInvokeList(invokeList);
			wos.add(wo);

			result.setData(wos);
			return result;
		}
	}

	public static WrapCopier<Agent, WrapAgent> agentCopier = WrapCopierFactory.wo(Agent.class, WrapAgent.class,
			JpaObject.singularAttributeField(Agent.class, true, true), null);

	public static WrapCopier<Invoke, WrapInvoke> invokeCopier = WrapCopierFactory.wo(Invoke.class, WrapInvoke.class,
			JpaObject.singularAttributeField(Invoke.class, true, true), null);


	public static class Wo extends WrapServiceModule {
		public static Wo copyFrom(ServiceModuleEnum serviceModuleEnum){
			Wo wo = new Wo();
			wo.setId(serviceModuleEnum.getValue());
			wo.setName(serviceModuleEnum.getDescription());
			return wo;
		}

	}
}