package com.x.organization.assemble.control.jaxrs.departmentduty;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutDepartmentDuty;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentDuty;

public class ActionListWithDepartment extends ActionBase {

	protected List<WrapOutDepartmentDuty> execute(Business business, String departmentId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Department department = emc.find(departmentId, Department.class, ExceptionWhen.not_found);
		List<String> ids = business.departmentDuty().listWithDepartment(department.getId());
		List<WrapOutDepartmentDuty> wraps = outCopier.copy(emc.list(DepartmentDuty.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}
