package com.x.organization.assemble.control.jaxrs.function;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.WrapInString;
import com.x.base.core.project.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;

public class ActionSetPassword {

	protected WrapOutId execute(Business business, String name, WrapInString wrapIn) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		String personId = business.person().getWithName(name, null);
		if (StringUtils.isEmpty(personId)) {
			personId = business.person().getWithUnique(name, null);
		}
		if (StringUtils.isEmpty(personId)) {
			throw new Exception("can not find person:" + name);
		} else {
			if (StringUtils.isEmpty(wrapIn.getValue())) {
				throw new Exception("new password is empty");
			}
			Person person = emc.find(personId, Person.class, ExceptionWhen.not_found);
			emc.beginTransaction(Person.class);
			business.person().setPassword(person, wrapIn.getValue());
			emc.check(person, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Person.class);
			WrapOutId wrap = new WrapOutId(person.getId());
			return wrap;
		}
	}

}
