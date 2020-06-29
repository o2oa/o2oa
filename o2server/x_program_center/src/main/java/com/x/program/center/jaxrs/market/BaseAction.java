package com.x.program.center.jaxrs.market;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.WrapModule;
import com.x.program.center.core.entity.Application;
import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected Ehcache cache = ApplicationCache.instance().getCache(Application.class);

}
