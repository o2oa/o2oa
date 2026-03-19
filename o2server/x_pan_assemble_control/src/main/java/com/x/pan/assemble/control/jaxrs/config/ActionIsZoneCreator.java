package com.x.pan.assemble.control.jaxrs.config;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileConfig3;

class ActionIsZoneCreator extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.setValue(true);
			if(!business.controlAble(effectivePerson)) {
				FileConfig3 config = business.getSystemConfig();
				if (config != null && config.getProperties() != null
						&& ListTools.isNotEmpty(config.getProperties().getZoneAdminList())) {
					wo.setValue(ListTools.containsAny(config.getProperties().getZoneAdminList(), business.getUserInfo(effectivePerson.getDistinguishedName())));
				}
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {


	}
}
