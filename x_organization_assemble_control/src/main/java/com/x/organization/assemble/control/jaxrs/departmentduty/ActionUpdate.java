package com.x.organization.assemble.control.jaxrs.departmentduty;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInDepartmentDuty;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentDuty;

public class ActionUpdate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id,
			WrapInDepartmentDuty wrapIn) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		DepartmentDuty o = emc.find(id, DepartmentDuty.class, ExceptionWhen.not_found);
		inCopier.copy(wrapIn, o);
		Department department = emc.find(o.getDepartment(), Department.class);
		if (!business.companyEditAvailable(effectivePerson, department.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(DepartmentDuty.class);
		emc.check(o, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(DepartmentDuty.class);
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}

}
