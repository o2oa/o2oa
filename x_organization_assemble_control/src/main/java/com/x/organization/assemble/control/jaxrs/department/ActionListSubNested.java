package com.x.organization.assemble.control.jaxrs.department;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Department;

public class ActionListSubNested extends ActionBase {

	protected List<WrapOutDepartment> execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Department department = emc.find(id, Department.class, ExceptionWhen.not_found);
		List<String> ids = business.department().listSubNested(department.getId());
		List<WrapOutDepartment> wraps = outCopier.copy(emc.list(Department.class, ids));
		for (WrapOutDepartment o : wraps) {
			o.setDepartmentSubDirectCount(business.department().countSubDirect(o.getId()));
			o.setIdentitySubDirectCount(business.identity().countSubDirectWithDepartment(o.getId()));
		}
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}
