package com.x.organization.assemble.control.jaxrs.companyattribute;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutCompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute;

public class ActionGet extends ActionBase {

	protected WrapOutCompanyAttribute execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		CompanyAttribute o = emc.find(id, CompanyAttribute.class, ExceptionWhen.not_found);
		WrapOutCompanyAttribute wrap = outCopier.copy(o);
		return wrap;
	}

}