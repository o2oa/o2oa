package com.x.organization.assemble.express.jaxrs.department;

import java.util.List;

import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Department;

public class ActionList {

	public List<WrapOutDepartment> execute(Business business) throws Exception {
		List<String> ids = business.department().list();
		List<WrapOutDepartment> wraps = business.department()
				.wrap(business.entityManagerContainer().list(Department.class, ids));
		return wraps;
	}

}
