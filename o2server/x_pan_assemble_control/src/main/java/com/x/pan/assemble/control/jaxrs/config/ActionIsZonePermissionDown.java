package com.x.pan.assemble.control.jaxrs.config;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileConfig3;
import org.apache.commons.lang3.StringUtils;

class ActionIsZonePermissionDown extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.setValue(business.getSystemConfig().getReadPermissionDown());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {


	}
}
