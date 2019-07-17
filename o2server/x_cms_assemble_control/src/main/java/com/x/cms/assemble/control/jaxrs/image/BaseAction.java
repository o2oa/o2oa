package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.FileInfoServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.FileInfo;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( FileInfo.class);
	
	protected LogService logService = new LogService();
	
	protected FileInfoServiceAdv fileInfoServiceAdv = new FileInfoServiceAdv();
	
	protected DocumentQueryService documentInfoServiceAdv = new DocumentQueryService();
	
}
