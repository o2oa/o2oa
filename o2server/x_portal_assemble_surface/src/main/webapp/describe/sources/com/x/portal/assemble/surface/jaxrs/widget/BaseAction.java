package com.x.portal.assemble.surface.jaxrs.widget;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.core.entity.Widget;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	Ehcache widgetCache = ApplicationCache.instance().getCache(Widget.class);

}
