package com.x.processplatform.assemble.designer.jaxrs.mapping;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Mapping;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();

			Business business = new Business(emc);

			Mapping mapping = emc.flag(flag, Mapping.class);

			if (null == mapping) {
				throw new ExceptionEntityNotExist(flag, Mapping.class);
			}

			Application application = emc.flag(mapping.getApplication(), Application.class);

			if ((null != application) && (!business.editable(effectivePerson, application))) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			emc.beginTransaction(Mapping.class);
			emc.remove(mapping, CheckRemoveType.all);
			emc.commit();

			ApplicationCache.notify(Mapping.class);
			Wo wo = new Wo();
			wo.setId(mapping.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

}