package com.x.cms.assemble.control.jaxrs.viewcategory;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.ViewCategory;


public class BaseAction extends StandardJaxrsAction {
	
	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory( ViewCategory.class);
	
	protected LogService logService = new LogService();
	
}
