package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;

class ActionResetPassword extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			/** 排除xadmin */
			if (Config.token().isInitialManager(flag)) {
				throw new ExceptionDenyResetInitialManagerPassword();
			} else {
				Person o = business.person().pick(flag);
				if (null == o) {
					throw new ExceptionPersonNotExist(flag);
				}
				o = emc.find(o.getId(), Person.class);
				if (!business.editable(effectivePerson, o)) {
					throw new ExceptionDenyEditPerson(effectivePerson, flag);
				}
				business.person().setPassword(o, this.initPassword(business, o));
				emc.beginTransaction(Person.class);
				emc.check(o, CheckPersistType.all);
				emc.commit();
				ApplicationCache.notify(Person.class);
				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
			}

			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
