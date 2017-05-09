package com.x.cms.assemble.control.jaxrs.viewcategory;

import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.ViewCategory;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( ViewCategory.class);
	
	protected LogService logService = new LogService();
	
}
