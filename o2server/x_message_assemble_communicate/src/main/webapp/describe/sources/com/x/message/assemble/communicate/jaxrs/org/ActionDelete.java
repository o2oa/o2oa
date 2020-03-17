package com.x.message.assemble.communicate.jaxrs.org;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.Org;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			
			/*
			if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.Manager, OrganizationDefinition.MessageManager))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}*/
			
			ActionResult<Wo> result = new ActionResult<>();
			emc.beginTransaction(Org.class);
			Org org = emc.find(id, Org.class);
			
			if (null == org) {
				throw new ExceptionEntityNotExist(id, Org.class);
			}
			
			emc.remove(org, CheckRemoveType.all);
			emc.commit();
			
			Wo wo = new Wo();
			wo.setId(org.getId());
			result.setData(wo);
			return result;
			
		}
	}

	public static class Wo extends WoId {
		
	}

}
