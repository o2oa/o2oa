package com.x.organization.assemble.control.jaxrs.company;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapout.WrapOutCompany;
import com.x.organization.core.entity.Company;

public class ActionListLike extends ActionBase {

	protected List<WrapOutCompany> execute(Business business, String key) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> ids = business.company().listLike(key);
		List<WrapOutCompany> wraps = outCopier.copy(emc.list(Company.class, ids));
		for (WrapOutCompany o : wraps) {
			o.setCompanySubDirectCount(business.company().countSubDirect(o.getId()));
			o.setDepartmentSubDirectCount(business.department().countTopWithCompany(o.getId()));
		}
		SortTools.asc(wraps, false, "name");
		return wraps;
	}

}