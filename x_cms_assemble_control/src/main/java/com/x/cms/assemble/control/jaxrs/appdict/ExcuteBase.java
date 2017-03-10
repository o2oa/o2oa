package com.x.cms.assemble.control.jaxrs.appdict;

import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.AppDict;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected static Ehcache cache = ApplicationCache.instance().getCache( AppDict.class);
	
	protected LogService logService = new LogService();
	
}
