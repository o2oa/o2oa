package com.x.organization.assemble.control.jaxrs.companyattribute;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInCompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute;

public class ActionUpdate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id,
			WrapInCompanyAttribute wrapIn) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		CompanyAttribute o = emc.find(id, CompanyAttribute.class, ExceptionWhen.not_found);
		if (!business.companyEditAvailable(effectivePerson, o.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(CompanyAttribute.class);
		inCopier.copy(wrapIn, o);
		emc.check(o, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(CompanyAttribute.class);
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}

}