package com.x.cms.assemble.control.jaxrs.form;

import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.Form;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( Form.class);
	
	protected LogService logService = new LogService();
}
