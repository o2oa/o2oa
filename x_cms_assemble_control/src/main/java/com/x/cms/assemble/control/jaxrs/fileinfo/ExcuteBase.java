package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.FileInfoServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.FileInfo;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( FileInfo.class);
	
	protected LogService logService = new LogService();
	protected FileInfoServiceAdv fileInfoServiceAdv = new FileInfoServiceAdv();
	
}
