package com.x.organization.assemble.control.alpha.jaxrs.company;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.core.entity.Company;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {
	Ehcache cache = ApplicationCache.instance().getCache(Company.class);
}
