package com.x.organization.assemble.express.jaxrs.departmentduty;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty;

import net.sf.ehcache.Ehcache;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<DepartmentDuty, WrapOutDepartmentDuty> departmentDutyOutCopier = BeanCopyToolsBuilder
			.create(DepartmentDuty.class, WrapOutDepartmentDuty.class, null, WrapOutDepartmentDuty.Excludes);

	Ehcache cache = ApplicationCache.instance().getCache(DepartmentDuty.class);

}
