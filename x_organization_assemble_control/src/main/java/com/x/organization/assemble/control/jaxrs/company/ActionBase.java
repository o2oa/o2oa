package com.x.organization.assemble.control.jaxrs.company;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.organization.assemble.control.wrapin.WrapInCompany;
import com.x.organization.assemble.control.wrapout.WrapOutCompany;
import com.x.organization.core.entity.Company;

public class ActionBase {
	protected static BeanCopyTools<Company, WrapOutCompany> outCopier = BeanCopyToolsBuilder.create(Company.class,
			WrapOutCompany.class, null, WrapOutCompany.Excludes);

	protected static BeanCopyTools<WrapInCompany, Company> inCopier = BeanCopyToolsBuilder.create(WrapInCompany.class,
			Company.class, null, WrapInCompany.Excludes);
}
