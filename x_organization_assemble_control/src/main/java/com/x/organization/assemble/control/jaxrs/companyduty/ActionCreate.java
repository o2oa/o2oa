package com.x.organization.assemble.control.jaxrs.companyduty;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInCompanyDuty;
import com.x.organization.core.entity.CompanyDuty;

public class ActionCreate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, WrapInCompanyDuty wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (!business.companyEditAvailable(effectivePerson, wrapIn.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(CompanyDuty.class);
		CompanyDuty o = new CompanyDuty();
		inCopier.copy(wrapIn, o);
		emc.persist(o, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(CompanyDuty.class);
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}
}
