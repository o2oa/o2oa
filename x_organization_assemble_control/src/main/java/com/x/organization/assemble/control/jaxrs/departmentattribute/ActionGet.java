package com.x.organization.assemble.control.jaxrs.departmentattribute;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutDepartmentAttribute;
import com.x.organization.core.entity.DepartmentAttribute;

public class ActionGet extends ActionBase {

	protected WrapOutDepartmentAttribute execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		DepartmentAttribute o = emc.find(id, DepartmentAttribute.class, ExceptionWhen.not_found);
		WrapOutDepartmentAttribute wrap = outCopier.copy(o);
		return wrap;
	}

}
