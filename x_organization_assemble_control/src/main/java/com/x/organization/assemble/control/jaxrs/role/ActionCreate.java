package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInRole;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

public class ActionCreate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, WrapInRole wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (!business.roleEditAvailable(effectivePerson)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		Role role = inCopier.copy(wrapIn);
		emc.beginTransaction(Role.class);
		emc.persist(role, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Role.class);
		ApplicationCache.notify(Person.class);
		WrapOutId wrap = new WrapOutId(role.getId());
		return wrap;
	}

}
