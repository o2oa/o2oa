package com.x.organization.assemble.control.alpha.jaxrs.department;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.organization.assemble.control.alpha.wrapin.WrapInDepartment;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Department;

public class ActionBase {
	
	protected static BeanCopyTools<Department, WrapOutDepartment> outCopier = BeanCopyToolsBuilder
			.create(Department.class, WrapOutDepartment.class, null, WrapOutDepartment.Excludes);

	protected static BeanCopyTools<WrapInDepartment, Department> inCopier = BeanCopyToolsBuilder
			.create(WrapInDepartment.class, Department.class, null, WrapInDepartment.Excludes);
}
