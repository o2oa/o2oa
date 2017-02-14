package com.x.organization.assemble.control.jaxrs.companyattribute;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInCompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute;

public class ActionCreate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, WrapInCompanyAttribute wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (!business.companyEditAvailable(effectivePerson, wrapIn.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(CompanyAttribute.class);
		CompanyAttribute o = new CompanyAttribute();
		inCopier.copy(wrapIn, o);
		emc.persist(o, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(CompanyAttribute.class);
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}

}