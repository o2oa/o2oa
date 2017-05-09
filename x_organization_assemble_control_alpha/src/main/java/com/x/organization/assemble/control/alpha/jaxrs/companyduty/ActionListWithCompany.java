package com.x.organization.assemble.control.alpha.jaxrs.companyduty;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyDuty;

public class ActionListWithCompany extends ActionBase {

	protected List<WrapOutCompanyDuty> execute(Business business, String companyId)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Company o = emc.find(companyId, Company.class, ExceptionWhen.not_found);
		List<String> ids = business.companyDuty().listWithCompany(o.getId());
		List<WrapOutCompanyDuty> wraps = outCopier.copy(emc.list(CompanyDuty.class, ids));
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}
