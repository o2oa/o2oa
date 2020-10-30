package com.x.cms.assemble.control.jaxrs.commend;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;

public class BaseAction extends StandardJaxrsAction {

	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(DocumentCommend.class, Document.class);
	
}
