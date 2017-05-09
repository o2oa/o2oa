package com.x.organization.assemble.control.alpha.jaxrs.identity;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapin.WrapInIdentity;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;

public class ActionUpdate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id, WrapInIdentity wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Identity identity = emc.find(id, Identity.class, ExceptionWhen.not_found);
		inCopier.copy(wrapIn, identity);
		Department department = emc.find(identity.getDepartment(), Department.class);
		if (!business.companyEditAvailable(effectivePerson, department.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(Identity.class);
		emc.check(identity, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Identity.class);
		WrapOutId wrap = new WrapOutId(identity.getId());
		return wrap;
	}

}
