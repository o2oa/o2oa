package com.x.query.assemble.designer.jaxrs.view;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.View;
import com.x.query.core.entity.plan.CmsPlan;
import com.x.query.core.entity.plan.Plan;
import com.x.query.core.entity.plan.ProcessPlatformPlan;
import com.x.query.core.entity.plan.Runtime;

class ActionSimulate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSimulate.class);

	ActionResult<Plan> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		logger.debug("receive:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Plan> result = new ActionResult<>();
			Business business = new Business(emc);
			WiRuntime wi = this.convertToWrapIn(jsonElement, WiRuntime.class);
			/** 有可能前台不传任何参数 */
			if (null == wi) {
				wi = new WiRuntime();
			}
			wi.person = effectivePerson.getDistinguishedName();
			wi.identityList = business.organization().identity().listWithPerson(wi.person);
			wi.unitList = business.organization().unit().listWithPerson(wi.person);
			wi.unitAllList = business.organization().unit().listWithPersonSupNested(wi.person);
			wi.groupList = business.organization().group().listWithPerson(wi.person);
			wi.roleList = business.organization().role().listWithPerson(wi.person);
			View view = emc.find(id, View.class);
			if (null == view) {
				throw new ExceptionViewNotExist(id);
			}

			switch (StringUtils.trimToEmpty(view.getType())) {
			case View.TYPE_CMS:
				CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
				cmsPlan.runtime = wi;
				cmsPlan.access();
				result.setData(cmsPlan);
				break;

			case View.TYPE_PROCESSPLATFORM:
				ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(), ProcessPlatformPlan.class);
				processPlatformPlan.runtime = wi;
				processPlatformPlan.access();
				result.setData(processPlatformPlan);
				break;

			default:
				break;
			}
			return result;
		}
	}

	public static class WiRuntime extends Runtime {

	}

}