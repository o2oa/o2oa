package com.x.program.center.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapPair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Agent;
import com.x.program.center.core.entity.Invoke;
import com.x.program.center.core.entity.wrap.WrapAgent;
import com.x.program.center.core.entity.wrap.WrapInvoke;
import com.x.program.center.core.entity.wrap.WrapServiceModule;

class ActionPrepareCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPrepareCreate.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			List<Wo> wos = this.adjustForCreate(business, wi);
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> adjustForCreate(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		for (WrapAgent wrap : wi.getAgentList()) {
			Agent exist_Agent = business.entityManagerContainer().find(wrap.getId(), Agent.class);
			if (null != exist_Agent) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}
		for (WrapInvoke wrap : wi.getInvokeList()) {
			Invoke exist_invoke = business.entityManagerContainer().find(wrap.getId(), Invoke.class);
			if (null != exist_invoke) {
				wos.add(new Wo(wrap.getId(), JpaObject.createId()));
			}
		}

		return wos;
	}

	public static class Wi extends WrapServiceModule {

	}

	public static class Wo extends WrapPair {
		public Wo(String value, String replaceValue) {
			this.setFirst(value);
			this.setSecond(replaceValue);
		}
	}

}