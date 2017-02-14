package com.x.organization.assemble.control.jaxrs.company;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInCompany;
import com.x.organization.core.entity.Company;

public class ActionCreate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, WrapInCompany wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (!business.companyEditAvailable(effectivePerson, wrapIn.getSuperior())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		Company company = inCopier.copy(wrapIn);
		emc.beginTransaction(Company.class);
		business.company().adjustLevel(company);
		emc.persist(company, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Company.class);
		WrapOutId wrap = new WrapOutId(company.getId());
		return wrap;
	}

}