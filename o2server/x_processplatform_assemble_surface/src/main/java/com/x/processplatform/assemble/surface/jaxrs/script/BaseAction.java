package com.x.processplatform.assemble.surface.jaxrs.script;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.core.entity.element.Script;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected Ehcache CACHE = ApplicationCache.instance().getCache(Script.class);

}
