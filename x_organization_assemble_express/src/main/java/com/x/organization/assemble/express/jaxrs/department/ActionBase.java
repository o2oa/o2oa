package com.x.organization.assemble.express.jaxrs.department;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.core.entity.Department;

import net.sf.ehcache.Ehcache;

abstract class ActionBase extends StandardJaxrsAction {
	protected static Ehcache cache = ApplicationCache.instance().getCache(Department.class);

}
