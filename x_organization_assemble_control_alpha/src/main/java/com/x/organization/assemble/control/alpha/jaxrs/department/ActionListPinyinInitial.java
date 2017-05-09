package com.x.organization.assemble.control.alpha.jaxrs.department;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Department;

public class ActionListPinyinInitial extends ActionBase {

	protected List<WrapOutDepartment> execute(Business business, String key) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.department().listPinyinInitial(key);
		List<WrapOutDepartment> wraps = outCopier.copy(emc.list(Department.class, ids));
		for (WrapOutDepartment o : wraps) {
			o.setDepartmentSubDirectCount(business.department().countSubDirect(o.getId()));
			o.setIdentitySubDirectCount(business.identity().countSubDirectWithDepartment(o.getId()));
		}
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}