package com.x.organization.assemble.control.jaxrs.company;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Company;

public class ActionDelete extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Company company = emc.find(id, Company.class, ExceptionWhen.not_found);
		if (!business.companyEditAvailable(effectivePerson, company.getSuperior())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(Company.class);
		emc.remove(company, CheckRemoveType.all);
		emc.commit();
		ApplicationCache.notify(Company.class);
		WrapOutId wrap = new WrapOutId(company.getId());
		return wrap;
	}

}