package com.x.organization.assemble.control.alpha.jaxrs.department;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Department;

public class ActionGet extends ActionBase {

	protected WrapOutDepartment execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Department o = emc.find(id, Department.class, ExceptionWhen.not_found);
		WrapOutDepartment wrap = outCopier.copy(o);
		wrap.setDepartmentSubDirectCount(business.department().countSubDirect(id));
		wrap.setIdentitySubDirectCount(business.identity().countSubDirectWithDepartment(id));
		return wrap;
	}

}
