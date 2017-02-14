package com.x.organization.assemble.control.jaxrs.company;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutCompany;
import com.x.organization.core.entity.Company;

public class ActionGet extends ActionBase {

	protected WrapOutCompany execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Company company = emc.find(id, Company.class, ExceptionWhen.not_found);
		WrapOutCompany wrap = outCopier.copy(company);
		wrap.setCompanySubDirectCount(business.company().countSubDirect(company.getId()));
		wrap.setDepartmentSubDirectCount(business.department().countTopWithCompany(company.getId()));
		return wrap;
	}

}