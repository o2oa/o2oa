package com.x.program.center.jaxrs.dingding;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.Business;
import com.x.program.center.dingding.SyncOrganization;
import com.x.program.center.dingding.SyncOrganization.PullResult;

class ActionPullSync extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			if (BooleanUtils.isTrue(Config.dingding().getEnable())) {
				Business business = new Business(emc);
				SyncOrganization o = new SyncOrganization();
				PullResult pullResult = o.execute(business);
				wo = XGsonBuilder.convert(pullResult, Wo.class);
			}else{
				throw new ExceptionNotPullSync();
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends PullResult {
	}

}
