package com.x.processplatform.assemble.surface.jaxrs.application;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

abstract class BaseAction extends StandardJaxrsAction {
	
    Cache.CacheCategory cacheCategory = new Cache.CacheCategory(Application.class, Process.class);

}