package com.x.program.center.jaxrs.agent;

import java.util.Date;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.organization.core.express.Organization;
import com.x.program.center.ThisApplication;
import com.x.program.center.WebservicesClient;
import com.x.program.center.core.entity.Agent;
import com.x.program.center.schedule.TriggerAgent;
import com.x.program.center.schedule.TriggerAgent.Resources;

class ActionExecute extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Agent agent = emc.flag(flag, Agent.class);
			if (null == agent) {
				throw new ExceptionAgentNotExist(flag);
			}
			agent.setLastStartTime(new Date());
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			ScriptContext newContext = new SimpleScriptContext();
			Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);
			TriggerAgent.Resources resources = new TriggerAgent.Resources();
			resources.setEntityManagerContainer(emc);
			resources.setContext(ThisApplication.context());
			resources.setOrganization(new Organization(ThisApplication.context()));
			resources.setWebservicesClient(new WebservicesClient());
			resources.setApplications(ThisApplication.context().applications());
			engineScope.put(Resources.RESOURCES_BINDING_NAME, resources);
			Object o = engine.eval(agent.getText(), newContext);
			agent.setLastEndTime(new Date());
			Wo wo = new Wo();
			wo.setValue(gson.toJson(o));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapString {

	}

}