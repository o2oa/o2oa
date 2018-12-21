package com.x.portal.assemble.surface.jaxrs.page;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.core.entity.Page;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	Ehcache pageCache = ApplicationCache.instance().getCache(Page.class);

}
