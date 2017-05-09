package com.x.organization.assemble.control.alpha.jaxrs.departmentattribute;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutDepartmentAttribute;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentAttribute;

public class ActionListWithDepartment extends ActionBase {

	protected List<WrapOutDepartmentAttribute> execute(Business business, String departmentId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		/* 检查部门是否存在 */
		Department department = emc.find(departmentId, Department.class, ExceptionWhen.not_found);
		List<String> ids = (business.departmentAttribute().listWithDepartment(department.getId()));
		List<WrapOutDepartmentAttribute> wraps = outCopier.copy(emc.list(DepartmentAttribute.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}
