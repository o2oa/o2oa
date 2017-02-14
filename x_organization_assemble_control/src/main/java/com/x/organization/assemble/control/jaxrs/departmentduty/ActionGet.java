package com.x.organization.assemble.control.jaxrs.departmentduty;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutDepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty;

public class ActionGet extends ActionBase {

	protected WrapOutDepartmentDuty execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		DepartmentDuty o = emc.find(id, DepartmentDuty.class, ExceptionWhen.not_found);
		WrapOutDepartmentDuty wrap = outCopier.copy(o);
		return wrap;
	}

}
