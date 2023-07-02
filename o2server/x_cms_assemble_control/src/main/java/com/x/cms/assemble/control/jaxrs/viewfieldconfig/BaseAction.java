package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class BaseAction extends StandardJaxrsAction {
	
	static Cache.CacheCategory cacheCategory = new Cache.CacheCategory( ViewFieldConfig.class);
	
	protected LogService logService = new LogService();

}
