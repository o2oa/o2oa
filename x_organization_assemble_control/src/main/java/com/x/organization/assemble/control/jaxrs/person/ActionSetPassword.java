package com.x.organization.assemble.control.jaxrs.person;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapInString;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;

public class ActionSetPassword extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String name, WrapInString wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		String personId = business.person().getWithName(name);
		if (StringUtils.isEmpty(personId)) {
			throw new Exception("person{name:" + name + "} not existed.");
		}
		Person person = emc.find(personId, Person.class, ExceptionWhen.not_found);
		if (!business.personUpdateAvailable(effectivePerson, person)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(Person.class);
		if (StringUtils.isNotEmpty(wrapIn.getValue())) {
			business.setPassword(person, wrapIn.getValue());
		}
		emc.check(person, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Person.class);
		WrapOutId wrap = new WrapOutId(person.getId());
		return wrap;
	}
}
