package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.content.DataItem;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected static Ehcache cache = ApplicationCache.instance().getCache( DataItem.class);
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected DocumentInfoServiceAdv documentServiceAdv = new DocumentInfoServiceAdv();
	
}
