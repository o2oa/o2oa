package com.x.organization.assemble.control.jaxrs.identity;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.Identity;

public class ActionDelete extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Identity identity = emc.find(id, Identity.class, ExceptionWhen.not_found);
		Department department = emc.find(identity.getDepartment(), Department.class);
		if (!business.companyEditAvailable(effectivePerson, department.getCompany())) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		/* 由于有关联所以要分段提交，先提交CompanyDuty的成员删除。 */
		emc.beginTransaction(CompanyDuty.class);
		for (String str : business.companyDuty().listWithIdentity(identity.getId())) {
			CompanyDuty o = emc.find(str, CompanyDuty.class);
			o.getIdentityList().remove(id);
		}
		emc.commit();
		/* 由于有关联所以要分段提交，在提交DepartmentDuty的成员删除。 */
		emc.beginTransaction(DepartmentDuty.class);
		for (String str : business.departmentDuty().listWithIdentity(identity.getId())) {
			DepartmentDuty o = emc.find(str, DepartmentDuty.class);
			o.getIdentityList().remove(identity.getId());
		}
		emc.commit();
		/* 最后提交Identity的删除 */
		emc.beginTransaction(Identity.class);
		emc.remove(identity, CheckRemoveType.all);
		emc.commit();
		ApplicationCache.notify(Identity.class);
		ApplicationCache.notify(CompanyDuty.class);
		ApplicationCache.notify(DepartmentDuty.class);
		WrapOutId wrap = new WrapOutId(identity.getId());
		return wrap;
	}

}
