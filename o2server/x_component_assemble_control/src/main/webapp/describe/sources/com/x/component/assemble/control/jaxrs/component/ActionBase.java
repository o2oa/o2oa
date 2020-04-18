package com.x.component.assemble.control.jaxrs.component;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.component.core.entity.Component;

import net.sf.ehcache.Ehcache;

class ActionBase extends StandardJaxrsAction {

	Ehcache cache = ApplicationCache.instance().getCache(Component.class);

}
