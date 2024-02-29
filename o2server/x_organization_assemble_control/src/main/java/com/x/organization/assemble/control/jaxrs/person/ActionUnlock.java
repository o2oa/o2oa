package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.enums.PersonStatusEnum;

class ActionUnlock extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUnlock.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Person person = business.person().pick(flag);
			if (null == person) {
				throw new ExceptionPersonNotExist(flag);
			}
			if (!effectivePerson.isSecurityManager() && !this.editable(business, effectivePerson, person)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			emc.beginTransaction(Person.class);
			Person entityPerson = emc.find(person.getId(), Person.class);
			entityPerson.setFailureCount(0);
			entityPerson.setStatus(PersonStatusEnum.NORMAL.getValue());
			entityPerson.setStatusDes("");
			entityPerson.setLockExpireTime(null);
			emc.check(entityPerson, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Person.class);

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

}
