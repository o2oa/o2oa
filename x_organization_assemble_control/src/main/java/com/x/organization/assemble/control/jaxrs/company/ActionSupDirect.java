package com.x.organization.assemble.control.jaxrs.company;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutCompany;
import com.x.organization.core.entity.Company;

public class ActionSupDirect extends ActionBase {

	protected WrapOutCompany execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Company company = emc.find(id, Company.class, ExceptionWhen.not_found);
		WrapOutCompany wrap = null;
		if (StringUtils.isNotEmpty(company.getSuperior())) {
			Company superior = emc.find(company.getSuperior(), Company.class, ExceptionWhen.not_found);
			wrap = outCopier.copy(superior);
			wrap.setCompanySubDirectCount(business.company().countSubDirect(superior.getId()));
			wrap.setDepartmentSubDirectCount(business.department().countTopWithCompany(superior.getId()));
		}
		return wrap;
	}

}
