package com.x.organization.assemble.control.jaxrs.group;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInGroup;
import com.x.organization.core.entity.Group;

public class ActionCreate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, WrapInGroup wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (!business.groupEditAvailable(effectivePerson)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		Group group = inCopier.copy(wrapIn);
		emc.beginTransaction(Group.class);
		emc.persist(group, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Group.class);
		WrapOutId wrap = new WrapOutId(group.getId());
		return wrap;
	}

}
