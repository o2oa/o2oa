package com.x.organization.assemble.control.jaxrs.group;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInGroup;
import com.x.organization.core.entity.Group;

public class ActionUpdate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id, WrapInGroup wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (!business.groupEditAvailable(effectivePerson)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		Group o = emc.find(id, Group.class, ExceptionWhen.not_found);
		inCopier.copy(wrapIn, o);
		emc.beginTransaction(Group.class);
		emc.check(o, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Group.class);
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}

}
