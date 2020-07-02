package com.x.teamwork.assemble.control.jaxrs.extfield;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.CustomExtFieldRelePersistService;
import com.x.teamwork.assemble.control.service.CustomExtFieldReleQueryService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.SystemConfigQueryService;
import com.x.teamwork.assemble.control.service.TaskQueryService;
import com.x.teamwork.core.entity.CustomExtFieldRele;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache customExtFieldReleCache = ApplicationCache.instance().getCache( CustomExtFieldRele.class );
	
	protected 	CustomExtFieldReleQueryService customExtFieldReleQueryService = new CustomExtFieldReleQueryService();
	
	protected 	CustomExtFieldRelePersistService customExtFieldRelePersistService = new CustomExtFieldRelePersistService();
	
	protected 	ProjectQueryService projectQueryService = new ProjectQueryService();
	protected 	TaskQueryService taskQueryService = new TaskQueryService();
	
	protected 	DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();
	
}
