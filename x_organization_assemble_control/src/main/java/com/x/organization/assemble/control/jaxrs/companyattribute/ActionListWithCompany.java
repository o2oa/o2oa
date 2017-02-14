package com.x.organization.assemble.control.jaxrs.companyattribute;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutCompanyAttribute;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;

public class ActionListWithCompany extends ActionBase {

	protected List<WrapOutCompanyAttribute> execute(Business business, String companyId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Company o = emc.find(companyId, Company.class, ExceptionWhen.not_found);
		List<String> ids = business.companyAttribute().listWithCompany(o.getId());
		List<WrapOutCompanyAttribute> wraps = outCopier.copy(emc.list(CompanyAttribute.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}