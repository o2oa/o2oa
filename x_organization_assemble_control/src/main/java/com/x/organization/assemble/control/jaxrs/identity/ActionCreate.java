package com.x.organization.assemble.control.jaxrs.identity;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInIdentity;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;

public class ActionCreate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, WrapInIdentity wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Identity identity = inCopier.copy(wrapIn);
		Department department = emc.find(identity.getDepartment(), Department.class);
		if (!business.companyEditAvailable(effectivePerson, department.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(Identity.class);
		emc.persist(identity, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Identity.class);
		WrapOutId wrap = new WrapOutId(identity.getId());
		return wrap;
	}

}
