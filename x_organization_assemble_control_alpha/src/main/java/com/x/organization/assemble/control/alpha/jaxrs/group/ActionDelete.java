package com.x.organization.assemble.control.alpha.jaxrs.group;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Group;

public class ActionDelete extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (!business.groupEditAvailable(effectivePerson)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		Group o = emc.find(id, Group.class, ExceptionWhen.not_found);
		emc.beginTransaction(Group.class);
		emc.remove(o, CheckRemoveType.all);
		emc.commit();
		ApplicationCache.notify(Group.class);
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}

}