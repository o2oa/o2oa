package com.x.organization.assemble.express.jaxrs.company;

import com.x.base.core.cache.ApplicationCache;
import com.x.organization.core.entity.Company;

import net.sf.ehcache.Ehcache;

abstract class ActionBase {
	protected static Ehcache cache = ApplicationCache.instance().getCache(Company.class);

}
