package com.x.message.assemble.communicate.jaxrs.org;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.Org;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			/*
			if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.Manager, OrganizationDefinition.MessageManager))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}*/
			
			ActionResult<Wo> result = new ActionResult<>();
			Org org = emc.find(id, Org.class);
			if (null == org) {
				throw new ExceptionEntityNotExist(id, Org.class);
			}
			Wo wo = Wo.copier.copy(org);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Org {

		private static final long serialVersionUID = -1681442801874856071L;

		public static WrapCopier<Org, Wo> copier = WrapCopierFactory.wo(Org.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}
