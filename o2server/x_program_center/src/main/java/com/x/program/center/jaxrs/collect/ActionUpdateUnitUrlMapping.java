package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.Business;

class ActionUpdateUnitUrlMapping extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, WrapInUrlMapping wrapIn) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean rFlag = false;
		String urlMapping = wrapIn.getUrlMapping();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String unitName = business.getUnitName();
			if (StringUtils.isNotEmpty(unitName)) {
				rFlag = updateUnitMapping(unitName,urlMapping);
			}
		}
		Wo wo = new Wo();
		wo.setValue(rFlag);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}
}
