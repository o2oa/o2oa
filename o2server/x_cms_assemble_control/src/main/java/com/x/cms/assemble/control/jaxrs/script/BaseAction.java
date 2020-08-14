package com.x.cms.assemble.control.jaxrs.script;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.Script;

class BaseAction extends StandardJaxrsAction {
	public LogService logService = new LogService();
	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(Script.class, Document.class);

}
