package com.x.organization.assemble.express.jaxrs.department;

import com.x.base.core.cache.ApplicationCache;
import com.x.organization.core.entity.Department;

import net.sf.ehcache.Ehcache;

public class ActionBase {
	protected static Ehcache cache = ApplicationCache.instance().getCache(Department.class);

}
