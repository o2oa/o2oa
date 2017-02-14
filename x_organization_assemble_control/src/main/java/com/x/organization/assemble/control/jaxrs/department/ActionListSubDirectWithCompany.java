package com.x.organization.assemble.control.jaxrs.department;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.Department;

public class ActionListSubDirectWithCompany extends ActionBase {

	protected List<WrapOutDepartment> execute(Business business, String companyId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		/* 检查公司是否存在 */
		Company company = emc.find(companyId, Company.class, ExceptionWhen.not_found);
		List<WrapOutDepartment> wraps = outCopier
				.copy(emc.list(Department.class, business.department().listTopWithCompany(company.getId())));
		for (WrapOutDepartment o : wraps) {
			o.setDepartmentSubDirectCount(business.department().countSubDirect(o.getId()));
			o.setIdentitySubDirectCount(business.identity().countSubDirectWithDepartment(o.getId()));
		}
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}
