package com.x.organization.assemble.control.jaxrs.departmentattribute;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentAttribute;

public class ActionDelete extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		DepartmentAttribute o = emc.find(id, DepartmentAttribute.class, ExceptionWhen.not_found);
		Department department = emc.find(o.getDepartment(), Department.class);
		if (!business.companyEditAvailable(effectivePerson, department.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(DepartmentAttribute.class);
		emc.remove(o, CheckRemoveType.all);
		emc.commit();
		ApplicationCache.notify(DepartmentAttribute.class);
		WrapOutId wrap = new WrapOutId(o.getId());
		return wrap;
	}

}
