package com.x.organization.assemble.control.jaxrs.companyduty;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.CompanyDuty;

public class ActionDelete extends ActionBase {

	protected WrapOutId execute(Business business,EffectivePerson effectivePerson, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		CompanyDuty o = emc.find(id, CompanyDuty.class, ExceptionWhen.not_found);
		if (!business.companyEditAvailable(effectivePerson, o.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(CompanyDuty.class);
		emc.remove(o, CheckRemoveType.all);
		emc.commit();
		ApplicationCache.notify(CompanyDuty.class);
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}

}
