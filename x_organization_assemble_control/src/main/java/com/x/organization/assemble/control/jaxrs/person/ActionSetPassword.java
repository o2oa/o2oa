package com.x.organization.assemble.control.jaxrs.person;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapInString;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;

class ActionSetPassword extends ActionBase {

	protected ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String name, WrapInString wrapIn)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			String personId = business.person().getWithName(name, null);
			if (StringUtils.isEmpty(personId)) {
				throw new Exception("person{name:" + name + "} not existed.");
			}
			Person person = emc.find(personId, Person.class, ExceptionWhen.not_found);
			if (!business.personUpdateAvailable(effectivePerson, person)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
			}
			emc.beginTransaction(Person.class);
			if (StringUtils.isNotEmpty(wrapIn.getValue())) {
				business.person().setPassword(person, wrapIn.getValue());
			}
			emc.check(person, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Person.class);
			WrapOutId wrap = new WrapOutId(person.getId());
			result.setData(wrap);
			return result;
		}
	}
}
