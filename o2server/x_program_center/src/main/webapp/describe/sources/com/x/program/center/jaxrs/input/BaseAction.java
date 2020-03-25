package com.x.program.center.jaxrs.input;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	protected Ehcache inputCache = ApplicationCache.instance().getCache(BaseAction.class.getName(), 100,
			ApplicationCache.MINUTES_20, ApplicationCache.MINUTES_20);

	public enum Method {
		cover, create, ignore;
	}

}