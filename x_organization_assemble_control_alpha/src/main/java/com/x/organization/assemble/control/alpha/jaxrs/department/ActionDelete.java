package com.x.organization.assemble.control.alpha.jaxrs.department;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;

public class ActionDelete extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Department department = emc.find(id, Department.class, ExceptionWhen.not_found);
		if (!business.companyEditAvailable(effectivePerson, department.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(Department.class);
		emc.remove(department, CheckRemoveType.all);
		emc.commit();
		ApplicationCache.notify(Department.class);
		ApplicationCache.notify(Company.class);
		ApplicationCache.notify(Person.class);
		ApplicationCache.notify(Identity.class);
		WrapOutId wrap = new WrapOutId(department.getId());
		return wrap;
	}

}
