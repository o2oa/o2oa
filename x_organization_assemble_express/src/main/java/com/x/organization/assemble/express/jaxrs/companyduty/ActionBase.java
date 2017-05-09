package com.x.organization.assemble.express.jaxrs.companyduty;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.CompanyDuty;

import net.sf.ehcache.Ehcache;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<CompanyDuty, WrapOutCompanyDuty> companyDutyOutCopier = BeanCopyToolsBuilder
			.create(CompanyDuty.class, WrapOutCompanyDuty.class, null, WrapOutCompanyDuty.Excludes);

	Ehcache cache = ApplicationCache.instance().getCache(CompanyDuty.class);

}
