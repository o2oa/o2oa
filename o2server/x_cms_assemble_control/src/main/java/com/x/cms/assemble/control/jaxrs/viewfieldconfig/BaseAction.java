package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.ViewFieldConfig;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	static Ehcache cache = ApplicationCache.instance().getCache( ViewFieldConfig.class);
	
	protected LogService logService = new LogService();

}
