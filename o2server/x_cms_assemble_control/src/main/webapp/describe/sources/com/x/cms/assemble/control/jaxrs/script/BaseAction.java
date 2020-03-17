package com.x.cms.assemble.control.jaxrs.script;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.Script;

import net.sf.ehcache.Ehcache;


class BaseAction extends StandardJaxrsAction {
	public LogService logService = new LogService();
	public Ehcache cache = ApplicationCache.instance().getCache(Script.class);
}
