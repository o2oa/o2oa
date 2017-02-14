package com.x.organization.assemble.control.jaxrs.companyduty;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.CompanyDuty;

public class ActionGet extends ActionBase {

	protected WrapOutCompanyDuty execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		CompanyDuty o = emc.find(id, CompanyDuty.class, ExceptionWhen.not_found);
		WrapOutCompanyDuty wrap = outCopier.copy(o);
		return wrap;
	}

}
