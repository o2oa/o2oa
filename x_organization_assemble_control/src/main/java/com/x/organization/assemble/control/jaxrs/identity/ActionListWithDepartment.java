package com.x.organization.assemble.control.jaxrs.identity;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;

public class ActionListWithDepartment extends ActionBase {

	protected List<WrapOutIdentity> execute(Business business, String departmentId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Department department = emc.find(departmentId, Department.class, ExceptionWhen.not_found);
		List<String> ids = business.identity().listSubDirectWithDepartment(department.getId());
		List<WrapOutIdentity> wraps = outCopier.copy(emc.list(Identity.class, ids));
		SortTools.asc(wraps, false, "name");
		this.fillOnlineStatus(business, wraps);
		return wraps;
	}

}
