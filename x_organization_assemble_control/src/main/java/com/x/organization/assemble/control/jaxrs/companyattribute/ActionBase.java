package com.x.organization.assemble.control.jaxrs.companyattribute;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.organization.assemble.control.wrapin.WrapInCompanyAttribute;
import com.x.organization.assemble.control.wrapout.WrapOutCompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute;

public class ActionBase {
	
	protected BeanCopyTools<CompanyAttribute, WrapOutCompanyAttribute> outCopier = BeanCopyToolsBuilder
			.create(CompanyAttribute.class, WrapOutCompanyAttribute.class, null, WrapOutCompanyAttribute.Excludes);

	protected BeanCopyTools<WrapInCompanyAttribute, CompanyAttribute> inCopier = BeanCopyToolsBuilder
			.create(WrapInCompanyAttribute.class, CompanyAttribute.class, null, WrapInCompanyAttribute.Excludes);
}
