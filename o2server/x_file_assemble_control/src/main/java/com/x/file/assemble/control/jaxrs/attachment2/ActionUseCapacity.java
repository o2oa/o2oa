package com.x.file.assemble.control.jaxrs.attachment2;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapLong;
import com.x.file.assemble.control.Business;

class ActionUseCapacity extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String queryPerson = effectivePerson.getDistinguishedName();
			if(business.controlAble(effectivePerson) && StringUtils.isNotBlank(person)){
				queryPerson = person;
			}
			Wo wo = new Wo();
			wo.setValue(business.attachment2().getUseCapacity(queryPerson));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapLong {

	}
}
