package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectExtFieldRelePersistService;
import com.x.teamwork.assemble.control.service.ProjectExtFieldReleQueryService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.core.entity.ProjectExtFieldRele;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache projectExtFieldReleCache = ApplicationCache.instance().getCache( ProjectExtFieldRele.class );
	
	protected 	ProjectExtFieldReleQueryService projectExtFieldReleQueryService = new ProjectExtFieldReleQueryService();
	
	protected 	ProjectExtFieldRelePersistService projectExtFieldRelePersistService = new ProjectExtFieldRelePersistService();
	
	protected 	ProjectQueryService projectQueryService = new ProjectQueryService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
