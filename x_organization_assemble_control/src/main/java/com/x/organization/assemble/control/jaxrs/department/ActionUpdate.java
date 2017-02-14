package com.x.organization.assemble.control.jaxrs.department;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInDepartment;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;

public class ActionUpdate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id, WrapInDepartment wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Department department = emc.find(id, Department.class, ExceptionWhen.not_found);
		inCopier.copy(wrapIn, department);
		/* 处理所属公司,如果有上级部门，那么和上级部门同属一个公司，忽略写入的公司值 */
		if (StringUtils.isNotEmpty(department.getSuperior())) {
			Department superior = emc.find(department.getSuperior(), Department.class, ExceptionWhen.not_found);
			department.setCompany(superior.getCompany());
		}
		if (!business.companyEditAvailable(effectivePerson, department.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		emc.beginTransaction(Department.class);
		business.department().adjust(department);
		emc.check(department, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Department.class);
		ApplicationCache.notify(Company.class);
		ApplicationCache.notify(Person.class);
		ApplicationCache.notify(Identity.class);
		WrapOutId wrap = new WrapOutId(department.getId());
		return wrap;
	}

}
