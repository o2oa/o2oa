package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.element.ViewFieldConfig;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	static Ehcache cache = ApplicationCache.instance().getCache( ViewFieldConfig.class);
	
	protected LogService logService = new LogService();
	protected BeanCopyTools<ViewFieldConfig, WrapOutViewFieldConfig> copier = BeanCopyToolsBuilder.create(ViewFieldConfig.class, WrapOutViewFieldConfig.class, null, WrapOutViewFieldConfig.Excludes);
	
}
