package com.x.cms.assemble.control.jaxrs.viewcategory;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.ViewCategory;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( ViewCategory.class);
	
	protected LogService logService = new LogService();
	
}
