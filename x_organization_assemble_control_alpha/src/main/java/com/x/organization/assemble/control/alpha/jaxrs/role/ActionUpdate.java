package com.x.organization.assemble.control.alpha.jaxrs.role;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapin.WrapInRole;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

public class ActionUpdate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id, WrapInRole wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (!business.roleEditAvailable(effectivePerson)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		Role o = emc.find(id, Role.class, ExceptionWhen.not_found);
		inCopier.copy(wrapIn, o);
		emc.beginTransaction(Role.class);
		emc.check(o, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Role.class);
		ApplicationCache.notify(Person.class);
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}

}
