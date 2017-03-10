package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.jaxrs.personattribute.ActionBase;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

class ActionDelete extends ActionBase {

	WrapOutId execute(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (!business.roleEditAvailable(effectivePerson)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		Role o = emc.find(id, Role.class, ExceptionWhen.not_found);
		emc.beginTransaction(Role.class);
		emc.remove(o, CheckRemoveType.all);
		emc.commit();
		ApplicationCache.notify(Role.class);
		ApplicationCache.notify(Person.class);
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}

}